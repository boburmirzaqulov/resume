package com.hh.resume.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hh.resume.dao.Employee;
import com.hh.resume.dto.EmployeeDTO;
import com.hh.resume.dto.ResponseDTO;
import com.hh.resume.helper.constants.AppResponseCode;
import com.hh.resume.helper.constants.AppResponseMessages;
import com.hh.resume.service.EmployeeExcelExport;
import com.hh.resume.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        return employeeService.addEmployee(dto, photo);
    }

    @PostMapping("/test")
    public ResponseDTO<?> testConnect(@RequestParam MultiValueMap<String, String> employeeDTO, @RequestParam("file") MultipartFile file){
        EmployeeDTO dto = new EmployeeDTO();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            dto = objectMapper.readValue(employeeDTO.getFirst("employee"), EmployeeDTO.class);
        } catch (IOException e){
            e.printStackTrace();
        }
        return new ResponseDTO<>(false, 0 , "OK", dto);
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
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
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
