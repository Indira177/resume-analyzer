# Resume Analyzer

A Spring Boot resume analysis application that allows users to upload PDF resumes, extract resume text, compare resumes against job descriptions, view resume performance, and generate AI-powered resume insights using a local Llama 3.2 model through Ollama.

## Features

- User registration and login
- Secure resume upload
- PDF resume text extraction using Apache PDFBox
- Resume history management
- Job description matching
- Match score, matching skills, and missing skills
- Resume dashboard with key metrics
- Resume improvement suggestions
- Normal resume performance analysis
- AI-powered resume analysis using local Llama 3.2
- AI report download and print
- Resume details and extracted text view
- Responsive professional web interface

## Technology Stack

### Backend

- Java 17
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Spring Boot Actuator
- Apache PDFBox 3.0.6
- Spring AI with Ollama
- Maven

### Database

- MySQL 8
- Database: `resume_analyzer`

### AI

- Ollama
- Llama 3.2

### Frontend

- HTML5
- CSS3
- JavaScript

## Requirements

Install the following before running the application:

1. Java 17
2. MySQL 8
3. Maven (the project also includes Maven Wrapper)
4. Ollama
5. Llama 3.2 model in Ollama

## AI Setup

Make sure Ollama is running locally on:

```text
http://localhost:11434
```

Pull the model:

```text
ollama pull llama3.2:latest
```

The application is configured to use:

```text
llama3.2:latest
```

## Database Setup

Create the MySQL database:

```sql
CREATE DATABASE resume_analyzer;
```

The sellable package does **not** include a real `application.properties` file containing database credentials.

### Buyer Configuration

After extracting the project:

1. Open:
   `src/main/resources/`

2. Copy:
   `application.properties.example`

3. Rename the copied file to:
   `application.properties`

4. Open `application.properties` and enter the customer's own MySQL username and password.

5. Make sure MySQL is running and the database `resume_analyzer` exists.

6. Keep the customer's `application.properties` local. Do not commit or share it when publishing the project.

The example configuration file is provided as a safe template for installation.

## Application Configuration

The AI configuration currently uses:

```properties
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=llama3.2:latest
spring.ai.ollama.chat.options.temperature=0.3
```

Update the database properties to match the environment where the application is installed.

## Run the Application

From the project root:

### Windows

```text
mvnw.cmd spring-boot:run
```

or, when Maven is installed:

```text
mvn spring-boot:run
```

The application runs on:

```text
http://localhost:8080
```

## Main API Areas

### Authentication

```text
/api/auth/login
/api/auth/register
/api/auth/logout
```

### Resume

```text
/api/resumes/upload
/api/resumes/my-resumes
```

### Job Matching

```text
/api/job-descriptions
/api/job-match
/api/job-match/ranking/{resumeId}
```

### AI Analysis

```text
/api/ai/analyze/{resumeId}
```

### Health Check

```text
/api/health
```

## How to Use

1. Open `http://localhost:8080`.
2. Register a new account or log in.
3. Choose a PDF resume.
4. Click **Upload & Analyze**.
5. Review the Dashboard metrics.
6. Open **Recommended Jobs** to review matching jobs.
7. Open **Improvements** to review missing skills.
8. Open **My Resumes** to manage uploaded resumes.
9. Use **Normal Analyze** for the standard resume report.
10. Use **AI Analyze** for the Llama 3.2 powered analysis.
11. Print or download reports when required.

## Project Structure

```text
resume-analyzer/
├── .mvn/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.resumeanalyzer/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── entity/
│   │   │       ├── repository/
│   │   │       └── service/
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── index.html
│   │       │   ├── script.js
│   │       │   └── style.css
│   │       ├── templates/
│   │       └── application.properties
│   └── test/
├── pom.xml
├── mvnw
└── mvnw.cmd
```

## Customer Installation Summary

For a new installation, the customer needs:

- Java 17
- MySQL 8
- Ollama
- Llama 3.2 model (`llama3.2:latest`)

Installation flow:

1. Extract the project ZIP.
2. Create the `resume_analyzer` MySQL database.
3. Copy `src/main/resources/application.properties.example` to `application.properties`.
4. Enter the customer's own MySQL credentials.
5. Start Ollama and make sure `llama3.2:latest` is available.
6. Start Spring Boot using `mvnw.cmd spring-boot:run`.
7. Open `http://localhost:8080`.

## Production / Delivery Notes

Before handing the project to a customer:

- Set customer-specific MySQL credentials.
- Do not ship real user passwords.
- Do not ship private resume files unless they are explicitly intended as demo data.
- Make sure Ollama is installed and the required Llama 3.2 model is available.
- Verify the application on a clean machine before delivery.
- Package the source project without unnecessary IDE/build output files.

## Demo Checklist

Before a final demonstration, verify:

- Login works
- Resume upload works
- Dashboard metrics update
- Recommended jobs load
- Improvements load
- Resume history loads
- View resume works
- Normal Analyze works
- AI Analyze works
- Print report works
- Download report works
- Logout works
- Refresh/login persistence works

## Support / Customization

The application can be extended with additional job descriptions, authentication improvements, deployment configuration, cloud database support, external AI providers, resume scoring enhancements, and additional reporting features.
