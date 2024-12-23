package com.seating.exam_seating_arrangement_system.seating;


import io.swagger.v3.oas.annotations.media.Schema;

public class Room {
    @Schema(description = "Room number or identifier", example = "101", required = true)
    String roomNumber;
    int totalCapacity;
    int rows;
    int columns;
    int studentsPerSeat;

    public Room(String roomNumber, int totalCapacity, int rows, int columns, int studentsPerSeat) {
        this.roomNumber = roomNumber;
        this.totalCapacity = totalCapacity;
        this.rows = rows;
        this.columns = columns;
        this.studentsPerSeat = studentsPerSeat;
    }
    public int calculateTotalSeats() {
        return rows * columns * studentsPerSeat;
    }


    public int getTotalCapacity() {
        return totalCapacity;
    }
}