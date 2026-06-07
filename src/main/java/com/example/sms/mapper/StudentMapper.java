package com.example.sms.mapper;

import com.example.sms.dto.request.StudentRequestDTO;
import com.example.sms.dto.response.StudentResponseDTO;
import com.example.sms.entity.Student;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {AddressMapper.class, CourseMapper.class})
public interface StudentMapper {
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "courses", ignore = true)
    Student toEntity(StudentRequestDTO dto);

    StudentResponseDTO toResponseDTO(Student student);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "courses", ignore = true)
    void updateEntityFromDTO(StudentRequestDTO dto, @MappingTarget Student student);
}
