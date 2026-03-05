package com.mentorlink.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Component
public class ExcelProcessor {

    /**
     * Parse student Excel: columns [Email, FullName, RollNumber, Department, YearOfStudy]
     */
    public List<StudentUploadRow> parseStudents(MultipartFile file) throws Exception {
        List<StudentUploadRow> rows = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook wb = new XSSFWorkbook(is)) {
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String email = getCellString(row, 0);
                if (email == null || email.isBlank()) continue;
                rows.add(StudentUploadRow.builder()
                        .email(email.trim())
                        .fullName(getCellString(row, 1))
                        .rollNumber(getCellString(row, 2))
                        .department(getCellString(row, 3))
                        .yearOfStudy(getCellInt(row, 4))
                        .build());
            }
        }
        return rows;
    }

    /**
     * Parse leftover students Excel for auto-group allocation.
     * Columns: [Email] or [Email, RollNumber]. Either column can identify the student.
     * Used after group formation deadline: admin uploads list of students who haven't formed groups.
     */
    public List<com.mentorlink.util.LeftoverStudentRow> parseLeftoverStudents(MultipartFile file) throws Exception {
        List<com.mentorlink.util.LeftoverStudentRow> rows = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook wb = new XSSFWorkbook(is)) {
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String email = getCellString(row, 0);
                String rollNumber = getCellString(row, 1);
                if ((email == null || email.isBlank()) && (rollNumber == null || rollNumber.isBlank())) continue;
                rows.add(com.mentorlink.util.LeftoverStudentRow.builder()
                        .email(email != null ? email.trim() : null)
                        .rollNumber(rollNumber != null ? rollNumber.trim() : null)
                        .build());
            }
        }
        return rows;
    }

    /**
     * Parse faculty Excel: columns [Email, FullName, Department, Expertise, MaxGroups]
     */
    public List<FacultyUploadRow> parseFaculty(MultipartFile file) throws Exception {
        List<FacultyUploadRow> rows = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook wb = new XSSFWorkbook(is)) {
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String email = getCellString(row, 0);
                if (email == null || email.isBlank()) continue;
                rows.add(FacultyUploadRow.builder()
                        .email(email.trim())
                        .fullName(getCellString(row, 1))
                        .department(getCellString(row, 2))
                        .expertise(getCellString(row, 3))
                        .maxGroups(getCellInt(row, 4) > 0 ? getCellInt(row, 4) : 3)
                        .build());
            }
        }
        return rows;
    }

    private String getCellString(Row row, int col) {
        Cell c = row.getCell(col);
        if (c == null) return null;
        return switch (c.getCellType()) {
            case STRING -> c.getStringCellValue();
            case NUMERIC -> String.valueOf((long) c.getNumericCellValue());
            default -> null;
        };
    }

    private Integer getCellInt(Row row, int col) {
        Cell c = row.getCell(col);
        if (c == null) return null;
        return switch (c.getCellType()) {
            case NUMERIC -> (int) c.getNumericCellValue();
            case STRING -> {
                try {
                    yield Integer.parseInt(c.getStringCellValue());
                } catch (NumberFormatException e) {
                    yield null;
                }
            }
            default -> null;
        };
    }

    @lombok.Data
    @lombok.Builder
    public static class StudentUploadRow {
        private String email;
        private String fullName;
        private String rollNumber;
        private String department;
        private Integer yearOfStudy;
    }

    @lombok.Data
    @lombok.Builder
    public static class FacultyUploadRow {
        private String email;
        private String fullName;
        private String department;
        private String expertise;
        private int maxGroups;
    }
}
