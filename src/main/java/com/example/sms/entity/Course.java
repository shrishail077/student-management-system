package com.example.sms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "course_name", nullable = false)
    private String courseName;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(name = "course_type", nullable = false)
    private String courseType;
    @Column(nullable = false)
    private String duration;
    @Column(columnDefinition = "TEXT")
    private String topics;
    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Student> students = new HashSet<>();
}
