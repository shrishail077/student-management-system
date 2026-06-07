package com.example.sms.repository;

import com.example.sms.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentCode(String studentCode);

    boolean existsByStudentCode(String studentCode);

    boolean existsByEmail(String email);

    Page<Student> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Optional<Student> findByStudentCodeAndDob(String studentCode, LocalDate dob);

    @Query("SELECT s FROM Student s JOIN s.courses c WHERE c.id = :courseId")
    Page<Student> findByCourseId(@Param("courseId") Long courseId, Pageable pageable);
}
