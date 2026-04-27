# Customer Management System

Spring Boot customer management application with a simple browser UI for managing customers, addresses, cities, countries, family-member links, and Excel-based bulk upload.

## Features

- Create, update, view, and delete customers
- Store customer name, date of birth, NIC, mobile numbers, and addresses
- Manage reference data for cities and countries
- Link customers as family members
- Upload customers from an Excel workbook
- Static frontend served by Spring Boot

## Tech Stack

- Java 8
- Spring Boot 2.7.18
- Spring Web
- Spring Data JPA
- MariaDB
- Lombok
- Apache POI
- H2 for tests

## Project Structure

```text
src/main/java/com/convergence/cms
  controller/   REST endpoints
  dto/          Request DTOs
  entity/       JPA entities
  exception/    Global exception handling
  repository/   Spring Data repositories
  service/      Business logic
  util/         Excel parsing helper

src/main/resources
  application.properties
  static/       HTML, CSS, JS frontend
```

## Requirements

- Java 8
- Maven wrapper included in the project
- MariaDB running locally
- Database named `customer_management`

## Configuration

Application settings are in [application.properties](C:\Users\RS%20COMPUTERS\Documents\CMS\src\main\resources\application.properties).

Default local database configuration:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/customer_management
spring.datasource.username=root
spring.datasource.password=root
server.port=8081
```

Update these values to match your local MariaDB setup if needed.

## Run the Project

Start the application:

```powershell
.\mvnw.cmd spring-boot:run
```

Open:

```text
http://localhost:8081
```

## Run Tests

```powershell
.\mvnw.cmd test
```

## Main API Endpoints

### Customers

- `GET /customers`
- `GET /customers/{id}`
- `GET /customers/nic/{nic}`
- `POST /customers`
- `PUT /customers/{id}`
- `DELETE /customers/{id}`
- `POST /customers/upload`

### Cities

- `GET /cities`
- `GET /cities/{id}`
- `POST /cities`
- `DELETE /cities/{id}`

### Countries

- `GET /countries`
- `GET /countries/{id}`
- `POST /countries`
- `DELETE /countries/{id}`

## Customer Payload Example

```json
{
  "name": "Roh",
  "dob": "1980-04-30",
  "nic": "805861444V",
  "mobileNumbers": ["756671273"],
  "addresses": [
    {
      "line1": "no:33",
      "line2": "al hasanath road",
      "city": "Puttalam",
      "country": "India"
    }
  ],
  "familyMemberIds": []
}
```

## Excel Upload Format

The first row must be the header row.

Required columns:

- `name`
- `dob` or `date_of_birth`
- `nic`

Optional columns:

- `mobileNumbers`
- `line1`
- `line2`
- `city`
- `country`
- `familyMemberIds`

Example:

```text
name,date_of_birth,nic,mobileNumbers,line1,line2,city,country
roh,4/30/1980,805861444V,756671273,no:33,al hasanath road,puttalam,india
```

Notes:

- Each row is validated before insert.
- NIC must be unique.
- Duplicate NIC values will return `NIC already exists`.
- Empty or partial extra rows in the workbook can cause validation errors.

## Known Behavior

- Customer NIC is unique.
- City records are saved with a country relationship.
- The frontend shows backend validation errors in a toast message.

## Troubleshooting

### Port already in use

If startup fails because port `8081` is already in use, stop the existing process or change:

```properties
server.port=8081
```

### Database errors on startup or save

Check:

- MariaDB is running
- the `customer_management` database exists
- username and password in `application.properties` are correct

### Excel upload fails

Check:

- the sheet contains the required headers
- each customer row contains `name`, `dob/date_of_birth`, and `nic`
- NIC values do not already exist in the database
- there are no stray partial rows below the valid data

## Author

Project package: `com.convergence.cms`
