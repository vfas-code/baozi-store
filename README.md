# Baozi Store API

API REST simples para a Baozi Store (loja de pão chinês), com cadastro de clientes, produtos e pedidos.

**Tecnologias:** Java 17+, Spring Boot 3, Spring Web, Spring Data JPA, banco H2 (arquivo).

## Estrutura (MVC)

```
src/main/java/com/baozistore
├── BaoziStoreApplication.java
├── model/        Cliente, Produto, Pedido (entidades JPA)
├── repository/   ClienteRepository, ProdutoRepository, PedidoRepository
└── controller/   ClienteController, ProdutoController, PedidoController
```

## Como rodar

- **IntelliJ / Eclipse / VS Code:** abra a pasta como projeto Maven e execute `BaoziStoreApplication`.
- **Terminal:** `mvn spring-boot:run`

A API sobe em `http://localhost:8080`.
Console do banco: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:file:./data/baozidb`, usuário `sa`, sem senha).

## Entidades

| Entidade | Campos |
|---|---|
| Cliente | id (Long), nome (String), clienteDesde (LocalDate) |
| Produto | id (Long), nome (String), preco (BigDecimal), estoque (Boolean) |
| Pedido | id (Long), cliente (Cliente → cliente_id), produto (Produto → produto_id), quantidade (Integer) |

## Endpoints

| Método | Rota | Ação |
|---|---|---|
| POST | /clientes | Cria cliente |
| GET | /clientes | Lista clientes |
| GET | /clientes/{id} | Busca cliente por ID |
| PUT | /clientes/{id} | Atualiza cliente (opcional) |
| DELETE | /clientes/{id} | Apaga cliente |
| POST | /produtos | Cria produto |
| GET | /produtos | Lista produtos |
| GET | /produtos/{id} | Busca produto por ID |
| PUT | /produtos/{id} | Atualiza produto (opcional) |
| DELETE | /produtos/{id} | Apaga produto |
| POST | /pedidos | Cria pedido |
| GET | /pedidos | Lista pedidos |
| GET | /pedidos/{id} | Busca pedido por ID |
| PUT | /pedidos/{id} | Atualiza pedido (opcional) |
| DELETE | /pedidos/{id} | Apaga pedido |

### Exemplos de JSON

Cliente:
```json
{ "nome": "Vinicius1234567", "clienteDesde": "2026-10-12" }
```

Produto:
```json
{ "nome": "Baozi de Porco", "preco": 6.50, "estoque": true }
```

Pedido:
```json
{ "cliente": { "id": 1 }, "produto": { "id": 1 }, "quantidade": 12 }
```

Respostas: `201 Created` no POST, `200 OK` nos GET/PUT, `204 No Content` no DELETE, `404` quando o ID não existe.
Um cliente ou produto que já tem pedidos não pode ser apagado (`409 Conflict`) — apague o pedido primeiro.

## Postman

Importe `postman/BaoziStore.postman_collection.json` e altere a variável `nomeCliente` da coleção para seu Nome+RU.
