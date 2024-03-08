# Spring Boot Graphql Web Mvc
This project demonstrates the Graphql client and server using Spring Boot Web Mvc stack.

# Getting Started

### Required Tools

* Java 21
* Maven 3.5.3
* Docker Engine Latest Version

### Run Build

    ./mvnw clean install

### Run application with PostgresQL DB

    ./mvnw test-compile exec:java@run-local

### Urls

* Graphql: http://localhost:8080/graphql-web-mvc/graphql
* Graphql Schema: http://localhost:8080/graphql-web-mvc/graphql/schema