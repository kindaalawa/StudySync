# StudySync

StudySync is an educational platform that facilitates course management, scheduling, and interaction between students and instructors. Built using Spring Boot for backend operations, JavaFX for the frontend interface, and managed through a robust SQL database, StudySync aims to streamline the educational process through efficient data handling and user-friendly interfaces.

## Features

- **User Management:** Secure login and user authentication system.
- **Course Enrollment:** Allows users to enroll in courses, manage schedules, and pay course fees.
- **Instructor Interaction:** Facilitates communication and scheduling meetings with instructors.
- **Document Management:** Supports uploading and managing course-related documents.
- **Payment System:** Integrated system for handling course fees, including tracking payment statuses.
- **API Testing:** Utilizes Postman for testing and verifying backend functionality.

## Prerequisites

Before you begin, ensure you have met the following requirements:
- Java JDK 11 or newer
- IntelliJ IDEA for backend development
- JavaFX for frontend development
- TablePlus or any SQL database management tool for handling the database operations
- Postman for API testing

## Installation

To set up the StudySync app locally, follow these steps:

1. **Clone the repository:**
   ```bash
   git clone https://github.com/kindaalawa/studysync.git
   cd studysync
## Installation

### Database Setup
1. **Open TablePlus** and connect to your SQL server.
2. **Create a new database** named `StudySync`.
3. **Run the SQL scripts** provided in the `sql` directory to create the tables.

### Backend Setup
1. **Open IntelliJ IDEA** and import the project.
2. **Configure the `application.properties` file** with your database credentials.
3. **Build and run** the application.

### Frontend Setup
1. **Ensure JavaFX is properly configured** in your IDE.
2. **Navigate** to the `src/main/java/com/yourcompany/studysync` and run the main JavaFX application.
## Testing APIs with Postman

To ensure the API functions correctly, follow these steps to test with Postman:

1. **Open Postman:**
   - Launch Postman and create a new collection for your API tests.

2. **Configure API Requests:**
   - Set up requests corresponding to the API endpoints you have developed. This typically includes methods such as GET, POST, PUT, and DELETE.
   - Include parameters, headers, and request bodies as required by your API.

3. **Send Requests and Analyze Responses:**
   - Execute the requests and verify the responses. Check for status codes, response payloads, and error messages to ensure API reliability and functionality.

4. **Automate Testing:**
   - Utilize Postman's test scripts to automate testing and ensure that your API behaves as expected over time and changes.

## Usage

Here is how you can use StudySync to manage educational courses:

1. **Log in to the system** using your username and password.
2. **Navigate to the courses page** to view available courses or enroll in new ones.
3. **Schedule meetings with instructors** through the Meetings tab.
4. **Upload and download course materials** in the Info section.

For more details on specific functionalities, refer to the user guide provided in the `docs` folder.
