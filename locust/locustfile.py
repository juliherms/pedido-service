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
    {"idProduto": "PROD-123", "nomeProduto": "Notebook Dell", "valorUnitario": 2999.99},
    {"idProduto": "PROD-456", "nomeProduto": "Mouse Logitech", "valorUnitario": 89.90},
    {"idProduto": "PROD-789", "nomeProduto": "Teclado Mecânico", "valorUnitario": 549.90},
    {"idProduto": "PROD-101", "nomeProduto": "Monitor LG 27\"", "valorUnitario": 1299.99},
    {"idProduto": "PROD-202", "nomeProduto": "Webcam Full HD", "valorUnitario": 299.99},
    {"idProduto": "PROD-303", "nomeProduto": "Headset Gamer", "valorUnitario": 399.90},
]

# Lista de cidades para variar endereços
CITIES = [
    {"cidade": "São Paulo", "estado": "SP"},
    {"cidade": "Rio de Janeiro", "estado": "RJ"},
    {"cidade": "Belo Horizonte", "estado": "MG"},
    {"cidade": "Curitiba", "estado": "PR"},
    {"cidade": "Porto Alegre", "estado": "RS"},
    {"cidade": "Brasília", "estado": "DF"},
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
                "idProduto": product["idProduto"],
                "nomeProduto": product["nomeProduto"],
                "quantidade": random.randint(1, 5),
                "valorUnitario": product["valorUnitario"]
            }
            for product in selected_products
        ]
        
        # Seleciona cidade aleatória
        location = random.choice(CITIES)
        
        # Gera CEP aleatório
        zipCode = f"{random.randint(10000, 99999)}-{random.randint(100, 999)}"
        
        # Constrói o payload
        payload = {
            "clienteId": self.customer_id,
            "itens": items,
            "enderecoEntregaRequest": {
                "rua": f"Rua Exemplo, {random.randint(1, 9999)}",
                "cidade": location["cidade"],
                "estado": location["estado"],
                "cep": zipCode,
                "pais": "Brasil"
            }
        }
        
        # Envia requisição POST
        with self.client.post(
            "/api/v1/pedidos",
            json=payload,
            catch_response=True,
            name="POST /api/v1/pedidos"
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
    print("INICIANDO TESTE DE CARGA - Orders API")
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
    print("TESTE DE CARGA CONCLUÍDO!")
    print("=" * 80)
    print("Verifique as métricas em:")
    print("   • Prometheus: http://localhost:9090")
    print("   • Grafana: http://localhost:3000")
    print("   • Locust: http://localhost:8089")
    print("=" * 80)


