package com.hh.resume.service;

import com.hh.resume.dao.Skill;
import com.hh.resume.dto.ValidatorDTO;
import com.hh.resume.helper.constants.AppResponseMessages;
import com.hh.resume.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;


    //method for formulating skill list
    public List<Skill> getSkillListFromDB(List<Skill> skills, List<ValidatorDTO> errors){
        if (skills == null) return null;
        //get skill names
        List<String> skillNames = skills
                .stream()
                .map(Skill::getName)
                .collect(Collectors.toList());

        //find available skills
        List<Skill> skillsDB = new ArrayList<>();
        try {
            skillsDB = skillRepository.findAllByNameIn(skillNames);
        } catch (Exception e){
            errors.add(new ValidatorDTO("database skill - find method", AppResponseMessages.DATABASE_ERROR));
        }

        skills.removeAll(skillsDB);

        //save new Skills
        if (!skills.isEmpty()) {
            try {
                skillRepository.saveAll(skills);
                skillsDB.addAll(skills);
            } catch (Exception e){
                errors.add(new ValidatorDTO("database skill - saveAll method", AppResponseMessages.DATABASE_ERROR));
                return null;
            }
        }
        return skillsDB;
    }

}
