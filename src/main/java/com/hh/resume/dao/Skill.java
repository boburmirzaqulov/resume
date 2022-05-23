package com.hh.resume.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "skill")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Skill  {
    @Id
    @GeneratedValue(generator = "skill_id_seq")
    @SequenceGenerator(name = "skill_id_seq", sequenceName = "skill_id_seq", allocationSize = 1)
    private Long id;

    private String name;

    public Skill(String name) {
        this.name = name;
    }
}
