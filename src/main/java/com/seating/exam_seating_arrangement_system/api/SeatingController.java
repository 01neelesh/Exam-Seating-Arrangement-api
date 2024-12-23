package com.seating.exam_seating_arrangement_system.api;

import com.seating.exam_seating_arrangement_system.seating.PDFGenerator;
import com.seating.exam_seating_arrangement_system.seating.Room;
import com.seating.exam_seating_arrangement_system.seating.SeatingLogic;
import com.seating.exam_seating_arrangement_system.seating.Student;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping("/api/v1/seating")
@Tag(name = "Seating Arrangement", description = "API for managing exam seating arrangements")
public class SeatingController {
    private static final Logger logger = LoggerFactory.getLogger(SeatingController.class);
    private final SeatingLogic seatingLogic;
    private final PDFGenerator pdfGenerator;

    public SeatingController() {
        this.seatingLogic = new SeatingLogic();
        this.pdfGenerator = new PDFGenerator();
    }

    @Operation(
            summary = "Welcome endpoint",
            description = "Returns a welcome message to verify API is working"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Welcome message returned successfully"
    )
    @GetMapping("/")
    public ResponseEntity<String> home() {
        logger.info("Accessing home endpoint");
        return ResponseEntity.ok("Welcome to the Seating Arrangement System API!");
    }
    @Controller
    public class WebController {
        @GetMapping("/")
        public String home() {
            return "home";
        }
    }

    @Operation(
            summary = "Generate seating arrangement PDF",
            description = """
            Generates a PDF file containing exam seating arrangements based on input Excel files.
            
            Required Excel File Formats:
            1. Students File (columns): Name, RollNumber, Class
            2. Rooms File (columns): RoomNumber, Rows, Columns, Students per unit seat, capacity
            
            Arrangement Types:
            - alphabetical: Arranges students alphabetically by name
            - rollnumber: Arranges students by roll number
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "PDF generated successfully",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input - File format incorrect or missing required columns",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping(
            value = "/generate-pdf",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> generateSeatingArrangement(
            @Parameter(
                    description = "Excel file containing student details",
                    required = true
            )
            @RequestParam("studentsFile") MultipartFile studentsFile,

            @Parameter(
                    description = "Excel file containing room details",
                    required = true
            )
            @RequestParam("roomsFile") MultipartFile roomsFile,

            @Parameter(
                    description = "Type of arrangement (alphabetical/rollnumber)",
                    required = true,
                    schema = @Schema(allowableValues = {"alphabetical", "rollnumber"})
            )
            @RequestParam("type") String type
    ) {
        try {
            // Validate file types
            validateFiles(studentsFile, roomsFile);

            // Create temporary directory and save files
            Path tempDirectory = createTempDirectory();
            File studentFile = saveMultipartFile(studentsFile, tempDirectory);
            File roomFile = saveMultipartFile(roomsFile, tempDirectory);

            // Process data and generate seating arrangement
            Map<String, List<List<String>>> seatingPlan = processSeatingArrangement(
                    studentFile,
                    roomFile,
                    type
            );

            // Generate and save PDF
            String pdfPath = generatePDF(seatingPlan, tempDirectory);

            logger.info("Successfully generated seating arrangement PDF");
            return ResponseEntity.ok("PDF generated successfully! Download from: " + pdfPath);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid input: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error generating seating arrangement", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating PDF: " + e.getMessage());
        }
    }

    private void validateFiles(MultipartFile studentsFile, MultipartFile roomsFile) {
        if (!isExcelFile(studentsFile) || !isExcelFile(roomsFile)) {
            throw new IllegalArgumentException("Invalid file format. Only Excel files (.xlsx, .xls) are allowed.");
        }
    }

    private boolean isExcelFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.equals("application/vnd.ms-excel") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        );
    }

    private Path createTempDirectory() throws Exception {
        Path tempDirectory = Files.createTempDirectory("seating-arrangement");
        logger.debug("Created temporary directory: {}", tempDirectory);
        return tempDirectory;
    }

    private File saveMultipartFile(MultipartFile file, Path tempDirectory) throws Exception {
        Path filePath = tempDirectory.resolve(Objects.requireNonNull(file.getOriginalFilename()));
        Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE);
        logger.debug("Saved file: {}", filePath);
        return filePath.toFile();
    }

    private Map<String, List<List<String>>> processSeatingArrangement(
            File studentFile,
            File roomFile,
            String type
    ) throws Exception {
        List<Student> students = seatingLogic.readStudentsFromExcel(studentFile.getAbsolutePath());
        List<Room> rooms = seatingLogic.readRoomsFromExcel(roomFile.getAbsolutePath());

        logger.info("Processing seating arrangement for {} students and {} rooms",
                students.size(), rooms.size());

        return switch (type.toLowerCase()) {
            case "alphabetical" -> SeatingLogic.allocateAlphabetically(students, rooms);
            case "rollnumber" -> SeatingLogic.allocateByRollNumber(students, rooms);
            default -> throw new IllegalArgumentException(
                    "Invalid arrangement type. Must be either 'alphabetical' or 'rollnumber'");
        };
    }

    private String generatePDF(
            Map<String, List<List<String>>> seatingPlan,
            Path tempDirectory
    ) throws Exception {
        String pdfOutputPath = tempDirectory.resolve("SeatingArrangement.pdf").toString();
        pdfGenerator.generateSeatingPDF(seatingPlan, pdfOutputPath);
        logger.debug("Generated PDF at: {}", pdfOutputPath);
        return pdfOutputPath;
    }
}