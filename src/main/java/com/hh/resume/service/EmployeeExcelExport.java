package com.hh.resume.service;

import com.hh.resume.dao.Education;
import com.hh.resume.dao.Employee;
import com.hh.resume.helper.DateHelper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeExcelExport {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Employee> listEmployee;

    public EmployeeExcelExport(List<Employee> listEmployee) {
        this.listEmployee = listEmployee;
        workbook = new XSSFWorkbook();
    }


    private void writeHeaderLine() {
        sheet = workbook.createSheet("Employees");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "Employee ID", style);
        createCell(row, 1, "Surname", style);
        createCell(row, 2, "Name", style);
        createCell(row, 3, "Patronymic", style);
        createCell(row, 4, "Birth date", style);
        createCell(row, 5, "Address", style);
        createCell(row, 6, "Education", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (Employee employee : listEmployee) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            List<Education> educations = new ArrayList<>();
            if (employee.getEducations() != null) {
                educations = employee.getEducations()
                        .stream()
                        .filter(e -> employee.getHeadEducation() != null && employee.getHeadEducation().equals(e.getId()))
                        .collect(Collectors.toList());
            }
            createCell(row, columnCount++, employee.getId(), style);
            createCell(row, columnCount++, employee.getSurname(), style);
            createCell(row, columnCount++, employee.getName(), style);
            createCell(row, columnCount++, employee.getPatronymic(), style);
            createCell(row, columnCount++, DateHelper.toString(employee.getBirthDate()), style);
            createCell(row, columnCount++, employee.getAddress(), style);
            createCell(row, columnCount++, educations.isEmpty() ? "" : educations.get(0).getName(), style);
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }
}
