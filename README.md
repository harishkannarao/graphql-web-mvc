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

    ./mvnw clean test-compile exec:java@run-local

### Urls

* Graphql: http://localhost:8080/graphql-web-mvc/graphql
* Graphql Schema: http://localhost:8080/graphql-web-mvc/graphql/schema

### GraphQL with CURL

Mutation with CURL

    echo '{
        "query": "mutation createBookWithAuthor($bookInput: BookInput!, $authorInput: AuthorInput!) {\n  createBookWithAuthor(authorInput: $authorInput, bookInput: $bookInput) {\n    success\n    message\n    book {\n      id\n      name\n      isbn\n      publishedDateTime\n      rating\n      authors(limit: 5) {\n        id\n        name\n      }\n    }\n    author {\n      id\n      name\n      books {\n        id\n        name\n        isbn\n        publishedDateTime\n        rating\n      }\n    }\n  }\n}\n",
        "variables": {
          "bookInput": {
            "id": "1234",
            "name": "sample-book",
            "rating": null,
            "publishedDateTime": "2024-04-25T14:00:00.455Z",
            "isbn": "ISBN-2024-04-25-001"
          },
          "authorInput": {
            "id": "2345",
            "name": "sample-author"
          }
        }
    } ' | tr -d '\n' | curl \
    -X POST \
    -H "Content-Type: application/json" \
    -H "X-Auth-Email: CLOUDFLARE_EMAIL" \
    -H "X-Auth-key: CLOUDFLARE_API_KEY" \
    -s \
    -d @- \
    "http://localhost:8080/graphql-web-mvc/graphql"

    