# Support Backend

Spring Boot API for the AI voice customer-support agent project.
Owns customers, orders, and tickets. The ElevenAgents voice agent
calls this API through webhook tools — it never touches the database
directly.

## 1. Start Postgres (Docker)

From this folder:

```bash
docker compose up -d
```

This starts a Postgres 16 container named `support-db` on port 5432,
database `support`, user `postgres`, password `devpass`, with a
persistent volume so data survives restarts.

Check it's running:

```bash
docker ps
```

## 2. Connect with DBeaver (optional, to browse data visually)

New connection → PostgreSQL:
- Host: `localhost`
- Port: `5432`
- Database: `support`
- Username: `postgres`
- Password: `devpass`

## 3. Install Maven (if you don't have it)

```bash
sudo apt update && sudo apt install -y maven
```

## 4. Run the Spring Boot app

```bash
mvn spring-boot:run
```

The app starts on **http://localhost:8080** and automatically:
- Creates all tables (`ddl-auto=update`)
- Seeds two sample customers and two sample orders (only if the DB is
  empty) — see `DataSeeder.java`

Seed data:
| Customer | Phone | Order | Status |
|---|---|---|---|
| Rahul Sharma | +919876543210 | ORD123 | DELAYED |
| Priya Verma | +919812345678 | ORD124 | SHIPPED |

## 5. Test the API

```bash
# Get order status
curl http://localhost:8080/api/orders/ORD123

# Get customer by phone
curl http://localhost:8080/api/customers/by-phone/+919876543210

# Create a ticket
curl -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{
        "customerPhone": "+919876543210",
        "orderCode": "ORD123",
        "subject": "Order delayed",
        "description": "Customer reports order has not arrived",
        "priority": "HIGH"
      }'

# Get a ticket back
curl http://localhost:8080/api/tickets/TK1001

# Close a ticket
curl -X POST http://localhost:8080/api/tickets/TK1001/close
```

Note: ticket codes are generated as `TK` + `1000 + database id`, so
your first created ticket will be `TK1001`, not `TK1024` — just an
implementation detail, feel free to change the offset in
`TicketService.createTicket()`.

## 6. API reference

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/orders/{orderCode}` | Look up an order |
| GET | `/api/customers/by-phone/{phone}` | Look up a customer |
| GET | `/api/tickets/{ticketCode}` | Look up a ticket |
| POST | `/api/tickets` | Create a ticket |
| PUT | `/api/tickets/{ticketCode}/status` | Update ticket status |
| POST | `/api/tickets/{ticketCode}/close` | Close a ticket |
| POST | `/api/tickets/{ticketCode}/escalate` | Escalate a ticket |

All errors return JSON like:
```json
{
  "timestamp": "...",
  "status": 404,
  "error": "Not Found",
  "message": "No order found with code ORD999"
}
```

## Next step: exposing this to ElevenAgents

Once this is running locally, you'll need `ngrok` (or similar) to
expose `localhost:8080` to the internet so ElevenAgents' webhook tools
can reach it:

```bash
ngrok http 8080
```

Then in the ElevenAgents dashboard, add Webhook Tools pointing at:
`https://<your-ngrok-id>.ngrok-free.app/api/orders/{orderCode}` etc.

This is Phase 4 of the build plan — don't do it until this API is
confirmed working locally via curl first.
