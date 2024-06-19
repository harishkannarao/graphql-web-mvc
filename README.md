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
* Graphiql: http://localhost:8080/graphql-web-mvc/graphiql

### Verify CORS

    curl -I -X OPTIONS -H "Origin: https://www.example.com" -H 'Access-Control-Request-Method: GET' "http://localhost:8080/graphql-web-mvc/graphql" 2>&1 | grep -i 'Access-Control-Allow-Origin'

### GraphQL with CURL

Query with CURL

    echo '{
        "query": "
            query ListBooks($bookIds: [String!]!, $authorLimit: Int!) {
                listBooks(bookIds: $bookIds) {
                    id
                    name
                    rating
                    isbn
                    publishedDateTime
                    authors(limit: $authorLimit) {
                        id
                        name
                        books {
                            id
                            name
                            rating
                            isbn
                            publishedDateTime
                        }
                    }
                }
            }
        ",
        "variables": {
          "bookIds": [
                "1234"
          ],
          "authorLimit": 3
        }
    } ' | tr -d '\n' | curl \
    -X POST \
    -H "Content-Type: application/json" \
    -s \
    -d @- \
    "http://localhost:8080/graphql-web-mvc/graphql" \
    | jq .

Mutation with CURL

    echo '{
        "query": "
            mutation CreateBookAuthorNuclear(
                $bookInput: BookInput!,
                $authorInput: AuthorInput!
            ) {
                createBookWithAuthor(authorInput: $authorInput, bookInput: $bookInput) {
                    success,
                    message,
                    book {
                        id
                        name,
                        isbn,
                        publishedDateTime,
                        rating,
                        authors {
                            id
                            name
                        }
                    }
                    author {
                        id,
                        name,
                        books {
                            id,
                            name,
                            isbn,
                            publishedDateTime,
                            rating
                        }
                    }
                }
            }
        ",
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
    -s \
    -d @- \
    "http://localhost:8080/graphql-web-mvc/graphql" \
    | jq .