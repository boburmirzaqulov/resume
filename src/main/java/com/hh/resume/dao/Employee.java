package com.hh.resume.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "employee")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(generator = "employee_id_seq")
    @SequenceGenerator(name = "employee_id_seq", sequenceName = "employee_id_seq", allocationSize = 1)
    private Long id;

    private String surname;
    private String name;
    private String patronymic;

    @Column(name = "birthdate", columnDefinition = "date default '2020-02-02'")
    private Date birthDate;

    private String job;
    private String profile;
    private String address;

    @Column(name = "photourl")
    private String photoUrl;

    @Column(name = "photouri")
    private String photoUri;

    @Column(name = "headeducation")
    private Long headEducation;

    @ManyToMany
    @JoinTable(
            name = "employee_skill",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private List<Skill> skills;

    @OneToMany( mappedBy = "employee")
    private List<Education> educations;

}
