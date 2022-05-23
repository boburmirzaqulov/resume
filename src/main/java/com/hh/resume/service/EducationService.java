package com.hh.resume.service;

import com.hh.resume.dao.Education;
import com.hh.resume.dao.Employee;
import com.hh.resume.dto.ValidatorDTO;
import com.hh.resume.helper.constants.AppResponseMessages;
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

    public void saveByEmployee(Employee employee, List<ValidatorDTO> errors) {
        List<Education> listFromDB = new ArrayList<>();
        try {
             listFromDB = educationRepository.findAllByEmployee(employee);
        } catch (Exception e){
            errors.add(new ValidatorDTO("database education - find method", AppResponseMessages.DATABASE_ERROR));
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

        if (employee.getId() != null) {
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
                errors.add(new ValidatorDTO("database education - save method", AppResponseMessages.DATABASE_ERROR));
            }
        }
    }
}
