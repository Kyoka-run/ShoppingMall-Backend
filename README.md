# ShoppingMall-Backend

## Overview
ShoppingMall-Backend is a Spring Boot-based backend system for a simulated e-commerce platform. This project is designed to handle essential e-commerce functionalities including product management, user registration and authentication, order processing, and security implementations using JWT and Spring Security.

## Features
- **Product Management**: Create, update, and delete products along with detailed descriptions and pricing.
- **User Authentication**: Secure registration and login processes using JWT for token generation and validation.
- **Order Processing**: Management of customer orders from placement through processing to delivery status updates.
- **Security**: Implementation of role-based access control (RBAC) with Spring Security to manage different user permissions effectively.

## Technologies
- **Spring Boot**: Simplifies the bootstrapping and development of new Spring Applications.
- **H2 Database**: In-memory database for rapid development and testing.
- **Spring Security and JWT**: Provides authentication and authorization mechanisms.
- **JUnit and Mockito**: For unit testing and ensuring code quality.

## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites
- JDK 1.8 or later
- Maven 3.2+

### Running the application locally
1. Clone the repository:
   	```bash
  	 git clone https://github.com/Kyoka-run/ShoppingMall-Backend.git

2.Navigate into the project directory:
 	```bash
	cd ShoppingMall-Backend
3.Run the application using Maven
	 ```bash
	mvn spring-boot:run
