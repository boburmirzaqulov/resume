package com.hh.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EducationDTO {
    private Long id;
    private String beginDate;
    private String endDate;
    private String name;
    private Boolean isHead;
}
