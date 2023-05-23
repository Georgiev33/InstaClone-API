# InstaClone API

InstaClone API is a backend RESTful API built on Spring Boot that aims to replicate the core features of Instagram. It provides functionality for managing user profiles, stories, posts, comments, chats, searches, notifications, and administrative activities.

## Objective

The objective of creating InstaClone API was to gain practical experience and enhance our knowledge of the Spring framework. We focused on developing a robust backend RESTful API and familiarizing ourselves with Spring Security for user authentication using JWT tokens. Additionally, we explored the implementation of message queues, specifically Kafka, for enabling chat functionality.

## Core Technologies

The InstaClone API is built using the following technologies:

- **Spring Boot**: The primary framework used for building the API.
- **MySQL**: The chosen database for storing application data.
- **Apache Kafka**: Integrated as a message broker for chat functionality.
- **Swagger**: Used to generate comprehensive API documentation.
- **Spring Security**: Implemented for user authentication with JWT tokens.
- **Maven**: The build tool used for the project.
- **MySQL Workbench**: A visual tool for managing MySQL databases.
- **Postman**: A popular API development and testing platform.
- **Trello**: Utilized for project management and collaboration during the development process.
## API Endpoints

The API provides endpoints for various functional modules, including User, Admin, Story, Post, Comment, Chat, Search, and Notification. Detailed information about these endpoints can be found in the API documentation, which is generated and accessible through Swagger.

## Requirements

To run the InstaClone API, you need the following:

- Java 17: Ensure you have Java 17 installed on your system.
- MySQL 8.0: Make sure you have MySQL version 8.0 or later.
- Apache Kafka 3.4.0: Install Kafka version 3.4.0 for the chat functionality.

## Setting Up

To run the InstaClone API on your local system, make sure you have Java 17, MySQL 8.0, Kafka 3.4.0, MySQL Workbench, and Postman installed. Follow these steps to set it up:

1. Clone the repository to your local machine.
2. Open the application.properties file and provide your MySQL database username and password. (You can find the required database script in the design folder.)
3. Ensure your Kafka server is running.
4. Use the command `mvn clean install` to build the application.
5. Once the build is successful, start the application by running `mvn spring-boot:run`.
6. Access the Swagger UI for API documentation at `localhost:8080/swagger-ui.html` after the server starts.

## Database Schema

The following screenshot displays the database schema used in the InstaClone API:

![Database Schema](https://raw.githubusercontent.com/Georgiev33/Instagram/main/design/DATABASE_SCHEMA.png)

The schema illustrates the relationships between different entities within the application and provides insight into the data flow and structure. It showcases all the tables in the database, their columns, and the associations between them.

Please note that this project is a clone and is not affiliated with Instagram.

