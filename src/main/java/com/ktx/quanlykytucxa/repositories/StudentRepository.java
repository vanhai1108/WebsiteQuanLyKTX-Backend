package com.ktx.quanlykytucxa.repositories;

import com.ktx.quanlykytucxa.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentCode(String studentCode);
    boolean existsByStudentCode(String studentCode);
    List<Student> findByRoomId(Long roomId);
    Optional<Student> findByUser_Id(Long userId);
}
