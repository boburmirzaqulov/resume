package com.hh.resume.service;

import com.hh.resume.dao.Education;
import com.hh.resume.dao.Employee;
import com.hh.resume.dto.EmployeeDTO;
import com.hh.resume.dto.ResponseDTO;
import com.hh.resume.dto.ValidatorDTO;
import com.hh.resume.helper.StringHelper;
import com.hh.resume.helper.Validator;
import com.hh.resume.helper.constants.AppResponseCode;
import com.hh.resume.helper.constants.AppResponseMessages;
import com.hh.resume.helper.property.FileStorageProperties;
import com.hh.resume.mapping.EmployeeMapping;
import com.hh.resume.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final SkillService skillService;
    private final EducationService educationService;
    private final Path fileStorageLocation;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, SkillService skillService, EducationService educationService, FileStorageProperties fileStorageProperties) {
        this.employeeRepository = employeeRepository;
        this.skillService = skillService;

        this.educationService = educationService;

        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ResponseDTO<?> getAll(MultiValueMap<String, String> params) {
        boolean isPageble = StringHelper.isNumber(params.getFirst("page"))
                && StringHelper.isNumber(params.getFirst("size"));
        if(isPageble){
            int page = StringHelper.getNumber(params.getFirst("page"));
            int size = StringHelper.getNumber(params.getFirst("size"));
            try {
                PageRequest pageRequest = PageRequest.of(page, size);
                Page<Employee> employeePage = employeeRepository.findAll(pageRequest);

                List<EmployeeDTO> employeeDTOList = employeeRepository.findAll()
                        .stream()
                        .map(EmployeeMapping::toDto)
                        .collect(Collectors.toList());
                Page<EmployeeDTO> result = new PageImpl<>(employeeDTOList, employeePage.getPageable(), employeePage.getTotalPages());
                return new ResponseDTO<>(true, AppResponseCode.OK, AppResponseMessages.OK, result);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseDTO<>(false, AppResponseCode.DATABASE_ERROR, AppResponseMessages.DATABASE_ERROR,null);
            }
        }
        return new ResponseDTO<>(false,AppResponseCode.VALIDATION_ERROR, AppResponseMessages.VALIDATION_ERROR, params);
    }

    //add new Employee
    public ResponseDTO<?> addEmployee(EmployeeDTO employeeDTO, MultipartFile photo) {
        List<ValidatorDTO> errors = Validator.validateEmployee(employeeDTO);

        if (employeeDTO.getEducations() != null) {
            errors.addAll(Validator.validateEducation(employeeDTO.getEducations()));
        }

        errors.addAll(setPhoto(photo, employeeDTO));

        if (errors.size() > 0){
            FileStorageProperties.deleteFile(photo.getOriginalFilename(), employeeDTO);
            return new ResponseDTO<>(false, AppResponseCode.VALIDATION_ERROR,
                    AppResponseMessages.VALIDATION_ERROR, employeeDTO, errors);
        }

        Employee employee = EmployeeMapping.toEntity(employeeDTO);

        if (employee.getSkills() != null) {
            employee.setSkills(skillService.getSkillListFromDB(employee.getSkills(), errors));
        }
        if (errors.size() > 0){
            FileStorageProperties.deleteFile(photo.getOriginalFilename(), employeeDTO);
            return new ResponseDTO<>(false, AppResponseCode.VALIDATION_ERROR,
                    AppResponseMessages.VALIDATION_ERROR, employeeDTO, errors);
        }

        try {
            employeeRepository.save(employee);
        } catch (Exception e){
            e.printStackTrace();
            FileStorageProperties.deleteFile(photo.getOriginalFilename(), employeeDTO);
            return new ResponseDTO<>(false, AppResponseCode.DATABASE_ERROR, AppResponseMessages.DATABASE_ERROR, employeeDTO);
        }

        List<Education> educations = educationService.saveByEmployee(employeeDTO.getEducations(), errors, employee);


        if (errors.size() > 0){
            FileStorageProperties.deleteFile(photo.getOriginalFilename(), employeeDTO);
            return new ResponseDTO<>(false, AppResponseCode.DATABASE_ERROR, AppResponseMessages.DATABASE_ERROR, employeeDTO, errors);
        }
        try {
            employeeRepository.save(employee);
        } catch (Exception e){
            e.printStackTrace();
            FileStorageProperties.deleteFile(photo.getOriginalFilename(), employeeDTO);
            return new ResponseDTO<>(false, AppResponseCode.DATABASE_ERROR, AppResponseMessages.DATABASE_ERROR, employeeDTO);
        }

        employee.setEducations(educations);

        return new ResponseDTO<>(true, AppResponseCode.OK, AppResponseMessages.OK, EmployeeMapping.toDto(employee));
    }

    //set photo to Employee
    private List<ValidatorDTO> setPhoto(MultipartFile file, EmployeeDTO employeeDTO){
        List<ValidatorDTO> errors = new ArrayList<>();
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                errors.add(new ValidatorDTO("photo", "Sorry! Filename contains invalid path sequence " + fileName));
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            FileStorageProperties.addFilePath(file.getOriginalFilename(), targetLocation);
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("employee/downloadFile/")
                    .path(fileName)
                    .toUriString();
            employeeDTO.setPhotoUrl(fileDownloadUri);
            employeeDTO.setPhotoUri(targetLocation.toString());
        } catch (IOException ex) {
            errors.add(new ValidatorDTO("photo", "Could not store file " + fileName + ". Please try again!"));
            FileStorageProperties.deleteFile(file.getOriginalFilename(), employeeDTO);
            ex.printStackTrace();
        }
        return errors;
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<Employee> listAll(){
        return employeeRepository.findAll();
    }
}
