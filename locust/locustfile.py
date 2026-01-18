"""
Locust Load Test - Orders API
Simula carga de criação de pedidos com pico de 350 TPS
"""

import random
import json
from locust import HttpUser, task, between, events
from locust.runners import MasterRunner

# Lista de produtos para variar os pedidos
PRODUCTS = [
    {"productId": "PROD-123", "productName": "Notebook Dell", "unitPrice": 2999.99},
    {"productId": "PROD-456", "productName": "Mouse Logitech", "unitPrice": 89.90},
    {"productId": "PROD-789", "productName": "Teclado Mecânico", "unitPrice": 549.90},
    {"productId": "PROD-101", "productName": "Monitor LG 27\"", "unitPrice": 1299.99},
    {"productId": "PROD-202", "productName": "Webcam Full HD", "unitPrice": 299.99},
    {"productId": "PROD-303", "productName": "Headset Gamer", "unitPrice": 399.90},
]

# Lista de cidades para variar endereços
CITIES = [
    {"city": "São Paulo", "state": "SP"},
    {"city": "Rio de Janeiro", "state": "RJ"},
    {"city": "Belo Horizonte", "state": "MG"},
    {"city": "Curitiba", "state": "PR"},
    {"city": "Porto Alegre", "state": "RS"},
    {"city": "Brasília", "state": "DF"},
]

class OrdersUser(HttpUser):
    """
    Usuário que cria pedidos aleatórios.
    """
    
    # Tempo de espera entre requisições (0.1 a 0.5 segundos)
    # Isso permite controlar a taxa via número de usuários
    wait_time = between(0.1, 0.5)
    
    def on_start(self):
        """
        Executado quando o usuário inicia.
        """
        self.customer_id = f"CUST-{random.randint(1000, 9999)}"
    
    @task
    def create_order(self):
        """
        Cria um pedido aleatório.
        Weight: 1 (100% das requisições)
        """
        
        # Seleciona 1 a 3 produtos aleatórios
        num_items = random.randint(1, 3)
        selected_products = random.sample(PRODUCTS, num_items)
        
        # Constrói os items do pedido
        items = [
            {
                "productId": product["productId"],
                "productName": product["productName"],
                "quantity": random.randint(1, 5),
                "unitPrice": product["unitPrice"]
            }
            for product in selected_products
        ]
        
        # Seleciona cidade aleatória
        location = random.choice(CITIES)
        
        # Gera CEP aleatório
        zipCode = f"{random.randint(10000, 99999)}-{random.randint(100, 999)}"
        
        # Constrói o payload
        payload = {
            "customerId": self.customer_id,
            "items": items,
            "shippingAddress": {
                "street": f"Rua Exemplo, {random.randint(1, 9999)}",
                "city": location["city"],
                "state": location["state"],
                "zipCode": zipCode,
                "country": "Brasil"
            },
            "currency": "BRL"
        }
        
        # Envia requisição POST
        with self.client.post(
            "/api/v1/orders",
            json=payload,
            catch_response=True,
            name="POST /api/v1/orders"
        ) as response:
            if response.status_code == 201:
                response.success()
            elif response.status_code == 400:
                # Validation error - não contar como falha
                response.failure(f"Validation error: {response.text}")
            elif response.status_code == 500:
                # Server error
                response.failure(f"Server error: {response.text}")
            else:
                response.failure(f"Unexpected status {response.status_code}: {response.text}")


# Event listeners para logging customizado
@events.test_start.add_listener
def on_test_start(environment, **kwargs):
    """
    Executado quando o teste inicia.
    """
    print("=" * 80)
    print("🚀 INICIANDO TESTE DE CARGA - Orders API")
    print("=" * 80)
    if isinstance(environment.runner, MasterRunner):
        print("📊 Modo: Master (distribuído)")
    else:
        print("📊 Modo: Standalone")
    print(f"🎯 Host: {environment.host}")
    print("=" * 80)


@events.test_stop.add_listener
def on_test_stop(environment, **kwargs):
    """
    Executado quando o teste termina.
    """
    print("\n" + "=" * 80)
    print("✅ TESTE DE CARGA CONCLUÍDO!")
    print("=" * 80)
    print("📈 Verifique as métricas em:")
    print("   • Prometheus: http://localhost:9090")
    print("   • Grafana: http://localhost:3000")
    print("   • Locust: http://localhost:8089")
    print("=" * 80)


