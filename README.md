# Product Service REST API

A simple REST service for managing products, built with Spring Boot.

### Technology Stack

- Java 17
- Spring Boot
- Spring MVC Spring Data JPA
- PostgreSQL
- Maven
- Docker (optional)
- OpenAPI (Swagger)
- Spring Retry
- Spring Validation
- Spring Aspects

### Features

- Create, view and list products
- Automatic EUR to USD conversion using HNB API
- Data validation
- Pagination and sorting capabilities

## Local Setup

### Prerequisites

- JDK 17+
- Maven 3.6+
- PostgreSQL 14+
- Git
- Docker (optional)

## Database Setup

You can create the PostgreSQL database using either the command line or pgAdmin:

### Option 1: Using Command Line:

```bash anotate
createdb product_db
```

### Option 2: Using pgAdmin

1. Open pgAdmin and connect to your PostgreSQL server
2. Right-click on "Databases" in the left sidebar and select "Create" > "Database..."
3. In the "Create - Database" dialog:
    - Enter "product_db" as the Database name
    - Select the appropriate owner (usually "postgres")
    - Leave other settings at their defaults
4. Click "Save" to create the database

### Adjust Application Properties

If you need to use different database settings, adjust the application.properties file:

```properties
propertiesspring.datasource.url=jdbc:postgresql://localhost:5432/product_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

## Running the Application

### Option 1: Using Maven

1. Clone the repository:

```bash
git clone https://github.com/hi-im-phillip/assignment-product-rest.git
cd product-rest
```

2. Build the project:

```bash
mvn clean install
```

If Maven is not in your PATH, use the Maven Wrapper instead:
bash

#### Windows

```cmd
mvnw.cmd clean install
```

#### macOS/Linux

```bash
./mvnw clean install
```

3. Run the application:

```bash
mvn spring-boot:run
```

#### Or with Maven Wrapper

#### Windows

```cmd
mvnw.cmd spring-boot:run
```

#### macOS/Linux

```bash
./mvnw spring-boot:run
```

### Option 2: Using Docker Compose

#### Clone the repository:

```bash
git clone https://github.com/hi-im-phillip/assignment-product-rest.git
cd product-rest
```

#### Update the Docker Compose context (if using a remote repository):

If using remote repository, please prepare token. More info on:
https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens

Update the `docker-compose.yml` file to include your GitHub token:

```yaml
services:
  product-service:
    build:
      context: .UPDATE ME  # here goes your token with git repo url
      dockerfile: Dockerfile

```

Build and run the containers:

```bash
docker-compose up -d
```

The application will be available at http://localhost:8080

## API Documentation

#### The API documentation is available using Swagger UI:

http://localhost:8080/swagger-ui.html

## Price Validation

The application includes a price validation feature that can be enabled or disabled through the conditionals.properties
file:

```properties
price-validate.enabled=true
```