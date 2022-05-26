package com.hh.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {
    private Long id;
    private String surname;
    private String name;
    private String patronymic;
    private String birthDate;
    private String job;
    private String profile;
    private String address;
    private String photoUrl;
    private String photoUri;
    private Set<SkillDTO> skills;
    private List<EducationDTO> educations;

    public EmployeeDTO(Long id) {
        this.id = id;
    }
}
