# Customer Rewards

## Problem statement

This Spring Boot REST API calculates customer reward points from purchase
transactions. A customer earns 2 points for every dollar spent above $100 and
1 point for every dollar spent between $50 and $100. Purchases of $50 or less
earn no points. The API returns points grouped by calendar month and a total.

## Project structure

Application code is under
`src/main/java/com/charter/customer_rewards`:

- `controller` — REST endpoints.
- `service` — reward calculation and application logic.
- `repository` — Spring Data JPA database access.
- `dto` — API response objects.
- `exception` — custom exceptions and global API error handling.
- `model` — JPA entities.

`src/main/resources` contains the application configuration and the
`data.sql` sample dataset. Tests are under `src/test/java`.

## Database setup

The application is configured to use MySQL at `localhost:3306`, with database
`rewardsdb`. Create the database before starting the application:

```sql
CREATE DATABASE rewardsdb;
```

Configure the connection in `src/main/resources/application.properties` (or
override it with Spring Boot datasource properties). Set the database username
and password for your local environment; do not commit credentials. Hibernate
is configured to create or update the tables on application startup.

After the first startup creates the tables, load the reproducible demo dataset:

```sh
mysql -u root -p rewardsdb < src/main/resources/data.sql
```

The script adds Demo John and Demo Kiran with transactions, and Demo Sagar
without transactions. It can be run again without duplicating those sample
rows. Use the generated customer IDs when calling the API.

## Run the application

With Java 17 and MySQL available, start the app from the project root:

```sh
./mvnw spring-boot:run
```

On Windows, use:

```powershell
.\mvnw.cmd spring-boot:run
```

The server listens on port `8081` by default.

## API

### Get a customer's rewards

```http
GET /rewards/{customerId}
```

For example, if Demo John has customer ID `1`:

```sh
curl http://localhost:8081/rewards/1
```

Example response (Demo John's sample transactions):

```json
{
  "customerId": 1,
  "monthlyPoints": {
    "2025-01": 90,
    "2025-02": 25
  },
  "totalPoints": 115
}
```

The month keys are formatted as `YYYY-MM`. A customer with no transactions
receives zero total points and an empty `monthlyPoints` object.

For an unknown customer, the API returns HTTP `404`:

```json
{
  "error": "Customer not found with id: 100",
  "status": 404
}
```

Customer IDs must be positive. Invalid IDs return HTTP `400` with the same
`error` and `status` JSON fields.
