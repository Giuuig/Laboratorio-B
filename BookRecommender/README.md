# BookRecommender

- Java 17 + Maven
- JavaFX UI
- Concurrent socket server (`ServerBR`) embedded in the same JVM
- PostgreSQL via JDBC. On first run the DB `bookrecommender` is created and schema applied automatically.
- Simple JSON protocol (Gson).

## Run

1. Ensure PostgreSQL is running locally and that the `app.properties` credentials are valid (default: user=postgres, password=postgres).
2. `mvn clean javafx:run`

The server auto-starts on port `5555` inside the app.