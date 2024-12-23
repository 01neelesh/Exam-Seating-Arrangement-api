package com.seating.exam_seating_arrangement_system.seating;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PDFGenerator {

    public void generateSeatingPDF(Map<String, List<List<String>>> seatingPlan, String filePath) {
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();


            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);

            for (Map.Entry<String, List<List<String>>> entry : seatingPlan.entrySet()) {
                String roomNumber = entry.getKey();
                List<List<String>> roomLayout = entry.getValue();

                // Add room header
                Paragraph header = new Paragraph("Room: " + roomNumber, new Font(baseFont, 16, Font.BOLD));
                header.setSpacingAfter(10);
                document.add(header);

                Paragraph subHeader = new Paragraph("Whiteboard this side", new Font(baseFont, 12));
                subHeader.setSpacingAfter(20);
                document.add(subHeader);

                // Create table
                PdfPTable table = new PdfPTable(roomLayout.get(0).size()); // Columns = size of a row
                table.setWidthPercentage(100);

                // Set equal column widths
                float[] columnWidths = new float[roomLayout.get(0).size()];
                Arrays.fill(columnWidths, 1f);
                table.setWidths(columnWidths);

                // Add rows and cells
                for (List<String> row : roomLayout) {
                    for (String seatContent : row) {
                        PdfPCell cell = new PdfPCell();
                        String[] lines = seatContent.split("\n");

                        Paragraph cellPara = new Paragraph();
                        cellPara.add(new Chunk(lines[0] + "\n", new Font(baseFont, 8))); // Seat label
                        for (int i = 1; i < lines.length; i++) {
                            cellPara.add(new Chunk(lines[i] + "\n", new Font(baseFont, 10))); // Student details
                        }

                        cell.addElement(cellPara);
                        cell.setMinimumHeight(75);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setPadding(5);
                        table.addCell(cell);
                    }
                }

                document.add(table);
                document.newPage(); // New page for each room
            }

            document.close();
            System.out.println("PDF generated successfully at: " + filePath);

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }
    }
}
