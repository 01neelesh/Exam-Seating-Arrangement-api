package com.seating.exam_seating_arrangement_system.api;

import com.seating.exam_seating_arrangement_system.seating.PDFGenerator;
import com.seating.exam_seating_arrangement_system.seating.Room;
import com.seating.exam_seating_arrangement_system.seating.SeatingLogic;
import com.seating.exam_seating_arrangement_system.seating.Student;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(value = "/api/v1/seating")
@Tag(name= "Seating Arrangement",description = "Seating Arrangement API endpoints")
public class SeatingController {

    @Operation(summary = "Welcome endpoint", description = "Returns a welcome message to verify API is working")
    @ApiResponse(responseCode = "200", description = "Welcome message returned successfully")
    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Welcome to the Seating Arrangement System API!");
    }

    @Controller
    public class WebController {
        @GetMapping("/")
        public String home() {
            return "home";
        }
    }


    @PostMapping("/generate-pdf")
    public ResponseEntity<String> generateSeatingArrangement(
            @RequestParam("studentsFile") MultipartFile studentsFile,
            @RequestParam("roomsFile") MultipartFile roomsFile,
            @RequestParam("type") String type) {
        Path tempDirectory;
        try {
            // Create a temporary directory for file handling
            tempDirectory = Files.createTempDirectory("seating-arrangement");

            // Save files in the temporary directory
            File studentFile = saveMultipartFile(studentsFile, tempDirectory);
            File roomFile = saveMultipartFile(roomsFile, tempDirectory);

            // Read data from Excel files
            SeatingLogic seatingLogic = new SeatingLogic();
            List<Student> students = seatingLogic.readStudentsFromExcel(studentFile.getAbsolutePath());
            List<Room> rooms = seatingLogic.readRoomsFromExcel(roomFile.getAbsolutePath());

            // Generate seating arrangements based on type
            Map<String, List<List<String>>> seatingPlan = switch (type.toLowerCase()) {
                case "alphabetical" -> SeatingLogic.allocateAlphabetically(students, rooms);
                case "rollnumber" -> SeatingLogic.allocateByRollNumber(students, rooms);
                default -> throw new IllegalArgumentException("Invalid type: " + type);
            };

            // Generate PDF
            String pdfOutputPath = tempDirectory.resolve("SeatingArrangement.pdf").toString();
            PDFGenerator pdfGenerator = new PDFGenerator();
            pdfGenerator.generateSeatingPDF(seatingPlan, pdfOutputPath);

            return ResponseEntity.ok("PDF generated successfully! Download from: " + pdfOutputPath);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating PDF: " + e.getMessage());
        }
    }

    // Helper method to save a MultipartFile to a temporary file
    private File saveMultipartFile(MultipartFile file, Path tempDirectory) throws Exception {
        Path filePath = tempDirectory.resolve(Objects.requireNonNull(file.getOriginalFilename()));
        Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE);
        return filePath.toFile();
    }
}
