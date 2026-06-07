package com.example.sms.mapper;

import com.example.sms.dto.request.AddressRequestDTO;
import com.example.sms.dto.response.AddressResponseDTO;
import com.example.sms.entity.Address;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressMapper {
    Address toEntity(AddressRequestDTO dto);

    AddressResponseDTO toResponseDTO(Address address);

    List<AddressResponseDTO> toResponseDTOList(List<Address> addresses);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(AddressRequestDTO dto, @MappingTarget Address address);
}
