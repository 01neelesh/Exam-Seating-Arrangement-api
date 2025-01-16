# Exam Seating Arrangement System

This project is designed to help educational institutions streamline and automate the process of creating seating arrangements for exams. The application generates optimized seating plans based on student and room data provided via Excel sheets.

## Features
- **Upload Excel Files**: Accepts student and room data through drag-and-drop.
- **Dynamic Seating Arrangements**: Generates PDF seating plans dynamically.
- **Backend**: Java Spring Boot for API handling.
- **Frontend**: Dynamic and user-friendly UI for interaction.

## Tech Stack
- **Backend**: Spring Boot, Maven, Java
- **Frontend**: HTML, CSS, JavaScript 
- **Database**:  MySQL
- **PDF Generation**: Apache POI and iText
- **Deployment**: Railway

## Prerequisites
- Java 17 or higher installed on your system
- Maven installed
- Railway account for deployment

## Setup and Deployment

### Local Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/01neelesh/Exam-Seating-Arrangement-api.git
   cd exam-seating-arrangement-system
   ```
2. Install dependencies:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
4. Access the app locally at `http://localhost:8080`.

### Create JAR File
1. Build the JAR file:
   ```bash
   mvn clean package
   ```
2. Locate the JAR in the `target/` directory.

### Deploy to Railway
1. Upload the JAR file or connect your GitHub repository to Railway.
2. Set environment variables in Railway.
3. Test the deployed application at the Railway-provided URL.

## Frontend Interaction
1. **Upload Data**:
    - Drag and drop Excel files containing student and room data.
    - Validate the file format before submission.

2. **Generate PDF**:
    - Click on the "Generate Seating Plan" button.
    - The system dynamically creates a PDF seating plan.

3. **Download Results**:
    - View the generated seating plan and download it as a PDF.

## Screenshots
1. **Upload Page**:
    - Simple drag-and-drop interface for Excel files.
2. **Generated Seating Plan**:
    - Dynamic seating layout with annotations like `R1C1S1`.

## Suggestions for Improvements
1. Add **email notifications** for administrators with seating plan attachments.
2. Allow **custom seating logic**, such as assigning alternate seats or specific rows to students with special needs.
3. Use **React** for a smoother, single-page application (SPA) experience.
4. Implement **role-based access** to restrict features to admins and users.

## License
This project is licensed under the MIT License.

## Contributing
Feel free to fork this repository, make changes, and submit pull requests!
