# Resume Analyzer

An AI-powered resume analysis web application built with Spring Boot. Users can upload PDF resumes, extract resume text, compare resumes against job descriptions, view resume performance, receive improvement suggestions, and generate AI-powered resume insights using a local Llama 3.2 model through Ollama.

---

## Features

- User registration and login
- Secure user-specific resume management
- PDF resume upload
- PDF text extraction using Apache PDFBox
- Duplicate resume upload prevention
- Resume history management
- View uploaded resume details
- Download original resume PDF
- Delete uploaded resumes
- Job description matching
- Job match scores
- Matching skills detection
- Missing skills detection
- Recommended jobs
- Resume dashboard with key metrics
- Common missing skills
- Resume improvement suggestions
- Standard ATS-style resume analysis
- Overall resume score
- Skills score
- Experience score
- Education score
- Format score
- Keyword score
- Strengths and weaknesses
- Resume recommendations
- AI-powered resume analysis
- Local Llama 3.2 AI through Ollama
- AI resume score
- AI ATS compatibility score
- AI-generated resume summary
- AI-generated recommendations
- AI report viewing
- AI report printing
- AI report downloading
- Responsive web interface
- Session-based authentication
- User data isolation

---

## Technology Stack

### Backend

- Java 17
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Spring Boot Actuator
- Apache PDFBox 3.0.6
- Spring AI
- Ollama
- Maven

### Database

- MySQL 8
- Database name: `resume_analyzer`

### AI

- Ollama
- Llama 3.2

### Frontend

- HTML5
- CSS3
- JavaScript

---

# System Requirements

Install the following before running the application:

1. Java 17
2. MySQL 8
3. Git
4. Ollama
5. Llama 3.2 model
6. Windows, Linux, or macOS

The project includes the Maven Wrapper, so a separate Maven installation is not required.

---

# Project Architecture

The application uses the following architecture:

```text
Browser
   |
   v
HTML / CSS / JavaScript
   |
   v
Spring Boot REST APIs
   |
   +-------------------+
   |                   |
   v                   v
MySQL Database       Ollama
                       |
                       v
                   Llama 3.2