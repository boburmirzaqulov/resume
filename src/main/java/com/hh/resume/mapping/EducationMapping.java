package com.hh.resume.mapping;

import com.hh.resume.dao.Education;
import com.hh.resume.dao.Employee;
import com.hh.resume.dto.EducationDTO;
import com.hh.resume.dto.EmployeeDTO;
import com.hh.resume.helper.DateHelper;

public class EducationMapping {
    public static Education toEntity(EducationDTO educationDTO, EmployeeDTO employeeDTO){
        if (educationDTO == null) return null;
        Employee employee = null;
        if (employeeDTO != null) {
            employee = EmployeeMapping.toEntity(employeeDTO);
        }
        return new Education(
                educationDTO.getId(),
                DateHelper.toDate(educationDTO.getBeginDate()),
                DateHelper.toDate(educationDTO.getEndDate()),
                educationDTO.getName(),
                employee
        );
    }
    public static EducationDTO toDto(Education education, Boolean isHead){
        return education == null ? null : new EducationDTO(
                education.getId(),
                DateHelper.toString(education.getBeginDate()),
                DateHelper.toString(education.getEndDate()),
                education.getName(),
                isHead
        );
    }
}
