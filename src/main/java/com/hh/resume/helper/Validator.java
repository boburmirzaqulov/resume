package com.hh.resume.helper;


import com.hh.resume.dto.EducationDTO;
import com.hh.resume.dto.EmployeeDTO;
import com.hh.resume.dto.ValidatorDTO;
import com.hh.resume.helper.constants.AppResponseMessages;

import java.util.ArrayList;
import java.util.List;

public class Validator {


    public static List<ValidatorDTO> validateEmployee(EmployeeDTO employeeDTO){
       List<ValidatorDTO> errors = new ArrayList<>();
        if (!StringHelper.isValid(employeeDTO.getName())){
            errors.add(new ValidatorDTO("employee.name", AppResponseMessages.EMPTY_FIELD));
        }
        if (!StringHelper.isValid(employeeDTO.getJob())){
            errors.add(new ValidatorDTO("employee.job", AppResponseMessages.EMPTY_FIELD));
        }
        if (!DateHelper.isValidDate(employeeDTO.getBirthDate())){
            errors.add(new ValidatorDTO("employee.birthDate", AppResponseMessages.DATE_FORMAT_YYYY_MM_DD));
        }
        if (!StringHelper.isValid(employeeDTO.getAddress())){
            errors.add(new ValidatorDTO("employee.address", AppResponseMessages.EMPTY_FIELD));
        }
        if (employeeDTO.getProfile() != null && employeeDTO.getProfile().length() > 255){
            errors.add(new ValidatorDTO("employee.profile", AppResponseMessages.VALUE_LENGTH_255));
        }
        return errors;
    }

    public static List<ValidatorDTO> validateEducation(EducationDTO education){
        List<ValidatorDTO> errors = new ArrayList<>();
       boolean isDate = false;
       if (!DateHelper.isValidDate(education.getBeginDate())){
            errors.add(new ValidatorDTO("education.beginDate", AppResponseMessages.DATE_FORMAT_YYYY_MM_DD));
        } else isDate = true;
        if (!DateHelper.isValidDate(education.getEndDate())){
            errors.add(new ValidatorDTO("education.endDate", AppResponseMessages.DATE_FORMAT_YYYY_MM_DD));
        } else if (isDate && education.getBeginDate().compareTo(education.getEndDate()) >= 0){
            errors.add(new ValidatorDTO("education.beginDate || education.endDate", "beginDate cannot equals or large than endDate"));
        }
        if (!StringHelper.isValid(education.getName())){
            errors.add(new ValidatorDTO("education.name", AppResponseMessages.EMPTY_FIELD));
        }
        return errors;
    }

    public static List<ValidatorDTO> validateEducation(List<EducationDTO> educationList){
        int i = 0;
        List<ValidatorDTO> errors = new ArrayList<>();
        for (EducationDTO education : educationList) {
            if (education.getIsHead() != null && education.getIsHead()) i++;
            validateEducation(education);
        }
        if (i!=1) {
            errors.add(new ValidatorDTO("isHead", "<isHead = true> should be and count cannot be more than 1"));
        }
        return errors;
    }



}
