# Encrypted Hazelcast Cache

Spring Boot application example that contains a custom implementation of the Hazelcast Cache Manager.
It encrypts the in-memory data stored by Hazelcast.

### Run locally

Prerequisites:
 - Gradle
 - JDK 8

Build project

```
./gradlew build
```
Start the application:

```
java -jar build/libs/encrypted-hazelcast-app.jar
```

Start the application on different port:

```
java -jar build/libs/encrypted-hazelcast-app.jar --server.port=8090
```
