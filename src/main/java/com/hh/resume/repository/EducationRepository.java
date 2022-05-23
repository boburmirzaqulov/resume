package com.hh.resume.repository;

import com.hh.resume.dao.Education;
import com.hh.resume.dao.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findAllByEmployee(Employee employee);
}
