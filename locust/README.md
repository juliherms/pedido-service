# 🚀 Locust Load Testing - Quick Start

## 📦 O que é Locust?

Locust é uma ferramenta de teste de carga open-source que simula milhares de usuários simultâneos para testar a performance da sua aplicação.

## ⚡ Quick Start (3 Comandos)

### 1️⃣ Iniciar Locust (Interface Web)

```powershell
# Executar script automático
.\scripts\run-load-test.ps1

# OU manualmente:
docker compose up -d locust
```

### 2️⃣ Acessar Interface

Abra: **http://localhost:8089**

Configure:
- **Number of users**: `120` (para ~350 TPS)
- **Spawn rate**: `4` (4 usuários/segundo)
- **Host**: `http://app:8080` (já configurado)

Clique em **Start swarming**

### 3️⃣ Monitorar

- **Locust**: http://localhost:8089 (RPS, latência, erros)
- **Grafana**: http://localhost:3000 (métricas de negócio)
- **Prometheus**: http://localhost:9090 (queries PromQL)

## 🎯 Modo Headless (Automático)

Para executar teste de **30 minutos com 350 TPS** automaticamente:

```powershell
# Opção 1: Editar docker-compose.yml
# Descomentar seção "headless" no serviço locust

# Opção 2: Usar script
.\scripts\run-load-test.ps1 -Mode headless -Users 120 -RunTime "30m"
```

## 📊 Métricas Esperadas

### Sucesso (✅)
- Taxa de sucesso: **> 99.5%**
- RPS sustentado: **~350**
- Latência p95: **< 500ms**
- Latência p99: **< 1s**
- Erros Kafka: **0**

### Atenção (⚠️)
- Taxa de erro: **> 1%** → Verificar recursos
- Latência p95: **> 1s** → Possível gargalo
- RPS instável → Verificar logs

## 🛠️ Comandos Úteis

```powershell
# Ver logs do Locust
docker compose logs -f locust

# Parar teste
docker compose stop locust

# Reiniciar
docker compose restart locust

# Remover e reconstruir
docker compose down locust
docker compose build locust
docker compose up -d locust
```

## 📁 Arquivos Gerados

Após o teste (modo headless):
- `report.html` - Relatório visual completo
- `results_stats.csv` - Estatísticas por timestamp
- `results_failures.csv` - Log de falhas

## 🐛 Troubleshooting

| Problema | Solução |
|----------|---------|
| Locust não inicia | `docker compose build locust` |
| Taxa de erro alta | Reduzir usuários: `--users=60` |
| RPS não atinge 350 | Aumentar usuários: `--users=150` |
| Connection refused | Verificar: `docker compose ps app` |

## 📚 Documentação Completa

Veja: **[LOCUST_LOAD_TEST_GUIDE.md](../LOCUST_LOAD_TEST_GUIDE.md)**

## 🎮 Exemplos de Cenários

### Teste de Stress (500 TPS por 10 min)
```powershell
.\scripts\run-load-test.ps1 -Mode headless -Users 180 -SpawnRate 6 -RunTime "10m"
```

### Teste de Soak (100 TPS por 2 horas)
```powershell
.\scripts\run-load-test.ps1 -Mode headless -Users 40 -SpawnRate 2 -RunTime "2h"
```

### Teste Rápido (50 TPS por 5 min)
```powershell
.\scripts\run-load-test.ps1 -Mode headless -Users 20 -SpawnRate 2 -RunTime "5m"
```

---

**Happy Load Testing! 🚀**


