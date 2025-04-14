package com.kaydev.appstore.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;

import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

public class ExcelUtil<T> {

    public static boolean hasExcelFormat(MultipartFile file) {
        String fileType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        if (!fileType.equals(file.getContentType())) {
            return false;
        }
        return true;
    }

    public static List<Map<String, String>> readFile(MultipartFile file) {
        try (
                Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(getCellValueAsString(cell));
            }

            List<Map<String, String>> dataList = new ArrayList<>();

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    Map<String, String> rowData = new HashMap<>();
                    for (int cellIndex = 0; cellIndex < headers.size(); cellIndex++) {
                        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        String header = headers.get(cellIndex);
                        String cellValue = getCellValueAsString(cell);
                        rowData.put(header.toLowerCase().replace(" ", "_"), cellValue);
                    }
                    dataList.add(rowData);
                }
            }

            return dataList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return ""; // Handle null cells
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula(); // You might need to evaluate formula cells
            default:
                return "";
        }
    }

    public ByteArrayOutputStream generateExcel(List<T> data, Class<T> clazz) throws IOException {
        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet("Report");

        // Create header row
        Row header = sheet.createRow(0);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            header.createCell(i).setCellValue(fields[i].getName());
        }

        int rowNum = 1;
        for (T entity : data) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                try {
                    Object value = fields[i].get(entity);
                    row.createCell(i).setCellValue(value != null ? value.toString() : "");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream;
    }

}