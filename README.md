# Parking Violation Reporter – Backend

**Java Spring Boot** REST API with **MySQL**, **Redis**, **JWT security**, and **custom JTS geolocation**.

Client: [github.com/giovanny-valencia/parkingReportingClient](https://github.com/giovanny-valencia/parkingReportingClient)

---

## Overview
Backend monolith for:
- User registration & login (JWT)
- Validating user location (lat/long) against **supported city boundaries** (JTS "Point-in-Polygon")
- Processing user-generated **parking violation reports**
- Fetching active reports per jurisdiction
- Officer report resolution

---

## Live Demo (Working)
- **User registration & login**
- **Location validation** (bounding box → JTS point-in-polygon)

> *Note: Report submission & fetch are under active rewrite. New design follows modular, single-responsibility principles.*

---

## Features (Implemented)
- JWT-based registration & login
- Endpoint authorization
- Rate limiting with **Redis**
- **JTS** city boundary validation
- Full server-side validation
- Unit tested with **JUnit 5** & **Mockito**

---

## Features (In Progress)
- Report submission with **AWS S3** image upload
- Fetching active reports
- Officer report in progress (heartbeat)
- Officer report resolution

---

## Tech Stack
| Tech | Purpose |
|------|--------|
| **Java 17** + **Spring Boot** | Backend framework |
| **Spring Security** | JWT auth |
| **MySQL** (Flyway) | Persistence |
| **Redis** | Rate limiting |
| **AWS S3** | Image storage |
| **JTS** | Geospatial logic |
| **JUnit 5**, **Mockito** | Testing |
