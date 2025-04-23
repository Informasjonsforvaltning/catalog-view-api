# Catalog View API

This application provides an API for viewing your company catalog in the registration solution. Through this endpoint
you will be able to retrieve all information about resources that are registered in the registration solution of your
company. Currently, it is only possible to use this endpoint against the term catalog.

The API is protected with [Maskinporten](https://samarbeid.digdir.no/maskinporten/maskinporten/25).

For a broader understanding of the system’s context, refer to
the [architecture documentation](https://github.com/Informasjonsforvaltning/architecture-documentation) wiki. For more
specific context on this application, see the **Registration** subsystem section.

## Getting Started

These instructions will give you a copy of the project up and running on your local machine for development and testing
purposes.

### Prerequisites

Ensure you have the following installed:

- Java 21
- Maven
- Docker

### Running locally

Clone the repository

```sh
git clone https://github.com/Informasjonsforvaltning/catalog-view-api.git
cd catalog-view-api
```

Start MongoDB, Elasticsearch and the application (either through your IDE using the dev profile, or via CLI):

```sh
docker compose up -d
mvn spring-boot:run -Dspring-boot.run.profiles=develop
```

### API Documentation (OpenAPI)

The API documentation is available at ```openapi.yaml```.

### Running tests

```sh
mvn verify
```
