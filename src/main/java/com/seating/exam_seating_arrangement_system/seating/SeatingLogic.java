package com.seating.exam_seating_arrangement_system.seating;


import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class SeatingLogic {

    private static final Logger logger = LoggerFactory.getLogger(SeatingLogic.class);
    // Allocate students to rooms by roll number
    public static Map<String, List<List<String>>> allocateByRollNumber(List<Student> students, List<Room> rooms) {
        return allocateSeats(students, rooms, "rollNumber");
    }

    // Allocate students to rooms alphabetically
    public static Map<String, List<List<String>>> allocateAlphabetically(List<Student> students, List<Room> rooms) {
        // Sort students alphabetically by name
        students.sort(Comparator.comparing(student -> student.name));
        return allocateSeats(students, rooms, "name");
    }

    // Common method to allocate seats
    private static Map<String, List<List<String>>> allocateSeats(List<Student> students, List<Room> rooms, String displayType) {
        Map<String, List<List<String>>> roomSeating = new LinkedHashMap<>();
        int currentStudentIndex = 0;

        for (Room room : rooms) {
            List<List<String>> roomLayout = new ArrayList<>();
            int totalStudentsPerRow = room.columns * room.studentsPerSeat;

            for (int r = 0; r < room.rows; r++) {
                List<String> row = new ArrayList<>();
                for (int c = 0; c < totalStudentsPerRow && currentStudentIndex < students.size(); c++) {
                    Student student = students.get(currentStudentIndex++);
                    String seatLabel = "SeatR" + (r + 1) + "C" + ((c / room.studentsPerSeat) + 1);
                    String seatContent = displayType.equals("rollNumber")
                            ? seatLabel + "\nRoll No: " + student.rollNumber
                            : seatLabel + "\nName: " + student.name;
                    row.add(seatContent);
                }
                roomLayout.add(row);
            }

            roomSeating.put(room.roomNumber, roomLayout);

            if (currentStudentIndex >= students.size()) break; // All students are seated
        }

        if (currentStudentIndex < students.size()) {
            int remainingStudents = students.size() - currentStudentIndex;
            logger.warn("Not enough room capacity for " + remainingStudents + " students.");
        }

        return roomSeating;
    }

    public List<Student> readStudentsFromExcel(String studentExcelPath) throws IOException {
        List<Student> students = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(studentExcelPath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                String rollNumber = row.getCell(0).getCellType() == CellType.NUMERIC
                        ? String.valueOf((int) row.getCell(0).getNumericCellValue())
                        : row.getCell(0).getStringCellValue();

                String name = row.getCell(1).getStringCellValue();
                String className = row.getCell(2).getStringCellValue();

                students.add(new Student(rollNumber, name, className));
            }
        } catch (IOException e) {
            logger.error("Error reading student Excel file: " + studentExcelPath, e);
            throw e;
        }
        return students;
    }

    // Read room data from Excel
    public List<Room> readRoomsFromExcel(String roomExcelPath) throws IOException {
        List<Room> rooms = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(roomExcelPath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                String roomNumber = row.getCell(0).getCellType() == CellType.NUMERIC
                        ? String.valueOf((int) row.getCell(0).getNumericCellValue())
                        : row.getCell(0).getStringCellValue();

                int rows = (int) row.getCell(1).getNumericCellValue();
                int columns = (int) row.getCell(2).getNumericCellValue();
                int totalCapacity = (int) row.getCell(3).getNumericCellValue();
                int studentsPerSeat = (int) row.getCell(4).getNumericCellValue();

                rooms.add(new Room(roomNumber, totalCapacity, rows, columns, studentsPerSeat));
            }
        } catch (IOException e) {
            logger.error("Error reading room Excel file: " + roomExcelPath, e);
            throw e;
        }
        return rooms;
    }
}
