package com.hh.resume.mapping;

import com.hh.resume.dao.Skill;
import com.hh.resume.dto.SkillDTO;

public class SkillMapping {
    public static Skill toEntity(SkillDTO skillDTO){
        return skillDTO == null ? null : new Skill(
                skillDTO.getId(),
                skillDTO.getName()
        );
    }
    public static SkillDTO toDto(Skill skill){
        return skill == null ? null : new SkillDTO(
                skill.getId(),
                skill.getName()
        );
    }
}
