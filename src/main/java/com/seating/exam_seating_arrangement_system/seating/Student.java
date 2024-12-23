package com.seating.exam_seating_arrangement_system.seating;

import io.swagger.v3.oas.annotations.media.Schema;

public class Student {
    @Schema(description = "Student's roll number", example = "2021CS001", required = true)
    String name;
    @Schema(description = "Student's full name", example = "John Doe", required = true)
    String rollNumber;
    @Schema(description = "Student's class/section", example = "CS-A", required = true)
    String className;

    public Student(String rollNumber, String name, String className) {
        this.rollNumber = rollNumber;
        this.name = name;
        this.className = className;
    }
    @Override
    public String toString() {
        return "Student{" +
                "rollNumber='" + rollNumber + '\'' +
                ", name='" + name + '\'' +
                ", className='" + className + '\'' +
                '}';
    }


}