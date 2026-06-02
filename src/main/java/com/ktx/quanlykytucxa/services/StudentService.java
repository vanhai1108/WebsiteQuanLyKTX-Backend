package com.ktx.quanlykytucxa.services;

import java.util.List;
import java.util.Optional;

import com.ktx.quanlykytucxa.entities.Student;

public interface StudentService {
    List<Student> getAllStudents();
    Optional<Student> getStudentById(Long id);
    Student createStudent(Student student);
    Student updateStudent(Long id, Student studentDetails);
    void deleteStudent(Long id);
}
