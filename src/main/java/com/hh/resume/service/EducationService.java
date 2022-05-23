package com.hh.resume.service;

import com.hh.resume.dao.Education;
import com.hh.resume.dao.Employee;
import com.hh.resume.dto.EducationDTO;
import com.hh.resume.dto.EmployeeDTO;
import com.hh.resume.dto.ValidatorDTO;
import com.hh.resume.helper.constants.AppResponseMessages;
import com.hh.resume.mapping.EducationMapping;
import com.hh.resume.repository.EducationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationService {
    private final EducationRepository educationRepository;

    public List<Education> saveByEmployee(List<EducationDTO> educationDTOList, List<ValidatorDTO> errors, Employee employee) {
        EducationDTO headEducation = null;
        List<Education> entityList = new ArrayList<>();
        for (EducationDTO educationDTO : educationDTOList) {
            if (educationDTO.getIsHead()) {
                headEducation = educationDTO;
            } else {
                entityList.add(EducationMapping.toEntity(educationDTO, new EmployeeDTO(employee.getId())));
            }
        }
        try {
            educationRepository.saveAll(entityList);
        } catch (Exception e){
            e.printStackTrace();
            errors.add(new ValidatorDTO("database education - save method", AppResponseMessages.DATABASE_ERROR));
        }
        if (headEducation != null){
            try {
                Education education = EducationMapping.toEntity(headEducation, new EmployeeDTO(employee.getId()));
                educationRepository.save(education);
                employee.setHeadEducation(education.getId());
                entityList.add(education);
            } catch (Exception e){
                e.printStackTrace();
                errors.add(new ValidatorDTO("database education - save method", AppResponseMessages.DATABASE_ERROR));
            }
        }
        return entityList;
    }

    public void updateByEmployee(Employee employee, List<ValidatorDTO> errors) {
        List<Education> listFromDB = new ArrayList<>();
        try {
             listFromDB = educationRepository.findAllByEmployee(employee);
        } catch (Exception e){
            errors.add(new ValidatorDTO("database education - update method", AppResponseMessages.DATABASE_ERROR));
        }

        if (!listFromDB.isEmpty()) {
            List<Long> actualIds = employee.getEducations()
                    .stream()
                    .map(Education::getId)
                    .collect(Collectors.toList());

            List<Education> deleteList = new ArrayList<>();
            for (Education education : listFromDB) {
                if (actualIds.contains(education.getId())) {
                    deleteList.add(education);
                }
            }

            listFromDB.removeAll(deleteList);
        }

        if (employee.getId() != null && employee.getEducations() != null) {
            try {

                for (Education education : employee.getEducations()) {
                    education.setEmployee(employee);
                }
                educationRepository.saveAll(employee.getEducations());
                if (!listFromDB.isEmpty()) {
                    listFromDB.addAll(employee.getEducations());
                    employee.setEducations(listFromDB);
                }
            } catch (Exception e) {
                e.printStackTrace();
                errors.add(new ValidatorDTO("database education - update method", AppResponseMessages.DATABASE_ERROR));
            }
        }
    }
}
