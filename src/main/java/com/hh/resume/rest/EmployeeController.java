package com.hh.resume.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hh.resume.dao.Employee;
import com.hh.resume.dto.EmployeeDTO;
import com.hh.resume.dto.ResponseDTO;
import com.hh.resume.helper.constants.AppResponseCode;
import com.hh.resume.helper.constants.AppResponseMessages;
import com.hh.resume.service.EmployeeExcelExport;
import com.hh.resume.service.EmployeeExportPdf;
import com.hh.resume.service.EmployeeService;
import com.lowagie.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employee")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/get-all")
    public ResponseDTO<?> getAll(@RequestParam MultiValueMap<String, String> params){
        return employeeService.getAll(params);
    }

    @PostMapping("/add")
    public ResponseDTO<?> addEmployee(@RequestParam("employee") String employeeDTO, @RequestParam("photo") MultipartFile photo){
        if (employeeDTO == null) return new ResponseDTO<>(false,AppResponseCode.NOT_FOUND, AppResponseMessages.EMPTY_FIELD, null);
        EmployeeDTO dto = new EmployeeDTO();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            dto = objectMapper.readValue(employeeDTO, EmployeeDTO.class);
        } catch (IOException e){
            e.printStackTrace();
        }
        dto.setId(null);
        return employeeService.addEmployee(dto, photo);
    }

    @GetMapping("/image-manual-response/{fileName:.+}")
    public void getImageAsByteArray(HttpServletResponse response, @PathVariable String fileName, HttpServletRequest request) throws IOException {
        // Load file as Resource
        Resource resource = employeeService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "image/jpeg";
        }

        FileInputStream fileInputStream = new FileInputStream(resource.getURI().getPath());
        response.setContentType(contentType);
        IOUtils.copy(fileInputStream, response.getOutputStream());
    }


    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<Employee> listEmployee = employeeService.listAll();

        EmployeeExcelExport excelExporter = new EmployeeExcelExport(listEmployee);

        excelExporter.export(response);
    }

    @GetMapping("/get-by-id/{id}")
    public void exportToPDF(@PathVariable Long id, HttpServletResponse response) throws DocumentException {
        Employee employee = employeeService.findById(id);
        EmployeeExportPdf pdfExport = new EmployeeExportPdf();
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=employee_%d_%s_%s_%s.pdf", employee.getId(), employee.getSurname(), employee.getName(), employee.getPatronymic());
        response.setHeader(headerKey, headerValue);
        pdfExport.pdfExport(response, employee);
    }
}
