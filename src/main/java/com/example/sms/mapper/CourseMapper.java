package com.example.sms.mapper;

import com.example.sms.dto.request.CourseRequestDTO;
import com.example.sms.dto.response.CourseResponseDTO;
import com.example.sms.entity.Course;
import org.mapstruct.*;

import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseMapper {
    Course toEntity(CourseRequestDTO dto);

    CourseResponseDTO toResponseDTO(Course course);

    Set<CourseResponseDTO> toResponseDTOSet(Set<Course> courses);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(CourseRequestDTO dto, @MappingTarget Course course);
}
