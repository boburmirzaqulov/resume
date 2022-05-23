package com.hh.resume.mapping;

import com.hh.resume.dao.Employee;
import com.hh.resume.dto.EducationDTO;
import com.hh.resume.dto.EmployeeDTO;
import com.hh.resume.helper.DateHelper;

import java.util.List;
import java.util.stream.Collectors;

public class EmployeeMapping {
    public static Employee toEntity(EmployeeDTO employeeDTO){
        if (employeeDTO == null) return null;
        Long idEdu = null;
        if (employeeDTO.getEducations() != null) {
            for (EducationDTO education : employeeDTO.getEducations()) {
                if (education.getIsHead() != null && education.getIsHead()) {
                    idEdu = education.getId();
                    break;
                }
            }
        }
        return new Employee(
                employeeDTO.getId(),
                employeeDTO.getSurname(),
                employeeDTO.getName(),
                employeeDTO.getPatronymic(),
                DateHelper.toDate(employeeDTO.getBirthDate()),
                employeeDTO.getAddress(),
                employeeDTO.getPhotoUrl(),
                employeeDTO.getPhotoUri(),
                idEdu,
                employeeDTO.getSkills() == null ? null :
                        employeeDTO.getSkills()
                                .stream()
                                .map(SkillMapping::toEntity)
                                .collect(Collectors.toList()),
                employeeDTO.getEducations() == null ? null :
                        employeeDTO.getEducations()
                                .stream()
                                .map(e -> EducationMapping.toEntity(e, null))
                                .collect(Collectors.toList())
        );
    }

    public static EmployeeDTO toDto(Employee employee){
        if (employee == null) return null;
        List<EducationDTO> educationDTOList = employee.getEducations() == null ? null :
                employee.getEducations()
                        .stream()
                        .map(e-> {
                            EducationDTO educationDTO;
                            if (employee.getHeadEducation().equals(e.getId())) {
                               educationDTO = EducationMapping.toDto(e,true);
                            } else {
                                educationDTO = EducationMapping.toDto(e,false);
                            }
                            return educationDTO;
                        })
                        .collect(Collectors.toList());
        return new EmployeeDTO(
                employee.getId(),
                employee.getSurname(),
                employee.getName(),
                employee.getPatronymic(),
                DateHelper.toString(employee.getBirthDate()),
                employee.getAddress(),
                employee.getPhotoUrl(),
                employee.getPhotoUri(),
                employee.getSkills() == null ? null :
                        employee.getSkills()
                                .stream().map(SkillMapping::toDto)
                                .collect(Collectors.toList()),
                educationDTOList
        );
    }
}
