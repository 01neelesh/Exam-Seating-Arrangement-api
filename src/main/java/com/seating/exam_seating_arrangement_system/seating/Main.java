package com.seating.exam_seating_arrangement_system.seating;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class Main {
        private static final Logger logger = LoggerFactory.getLogger(Main.class);

        public static void main(String[] args) {

                try {
                        // File paths

                        String studentExcelPath = "students.xlsx";      // Path to the student Excel file
                        String roomExcelPath = "roomMatrix.xlsx";       // Path to the room matrix Excel file
                        String rollNumberPdfPath = "RollNumberSeating.pdf"; // PDF for Roll Number-wise arrangement
                        String alphabeticalPdfPath = "AlphabeticalSeating.pdf"; // PDF for Alphabetical arrangement

                        // Create SeatingLogic instance
                        SeatingLogic seatingLogic = new SeatingLogic();

                        logger.info("Reading student and room data from Excel files....");

                        // Read students and rooms from Excel files
                        List<Student> students = seatingLogic.readStudentsFromExcel(studentExcelPath);
                        List<Room> rooms = seatingLogic.readRoomsFromExcel(roomExcelPath);

                        // Allocate seats Roll Number-wise
                        logger.info("allocating seats roll number...");
                        Map<String, List<List<String>>> rollNumberSeating = SeatingLogic.allocateByRollNumber(students, rooms);

                        // Allocate seats Alphabetically
                        logger.info("allocating seats alphabetically ");
                        Map<String, List<List<String>>> alphabeticalSeating = SeatingLogic.allocateAlphabetically(students, rooms);

                        // Generate PDFs
                        logger.info("generating pdfs...");
                        PDFGenerator pdfGenerator = new PDFGenerator();
                        pdfGenerator.generateSeatingPDF(rollNumberSeating, rollNumberPdfPath);
                        pdfGenerator.generateSeatingPDF(alphabeticalSeating, alphabeticalPdfPath);

                        // Summary message

                        logger.info("Seating arrangement PDFs generated successfully!");
                        logger.info("Files saved as:");
                    logger.info("1. Roll Number-wise: {}", rollNumberPdfPath);
                    logger.info("2. Alphabetical: {}", alphabeticalPdfPath);
                } catch (Exception e) {
                        logger.error("An error occurred while processing seating arrangements: ", e);
                }
        }
}
