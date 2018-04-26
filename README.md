# Encrypted Hazelcast Cache

Spring Boot application example that contains a custom implementation of the Hazelcast Cache Manager.
It encrypts the in-memory data stored by Hazelcast.

Step-by-step guide: https://medium.com/coders-do-read/how-to-implement-an-encrypted-distributed-cache-87be4e506f8e

### Run locally

Prerequisites:
 - Gradle
 - JDK 8

Build project:
```
./gradlew build
```

Start the application:
```
java -jar build/libs/encrypted-hazelcast-app.jar
```

Start the application on specific port:
```
java -jar build/libs/encrypted-hazelcast-app.jar --server.port=8090
```
