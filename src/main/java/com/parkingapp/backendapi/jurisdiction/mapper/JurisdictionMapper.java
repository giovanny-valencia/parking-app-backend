package com.parkingapp.backendapi.jurisdiction.mapper;

import com.parkingapp.backendapi.jurisdiction.entity.Jurisdiction;
import com.parkingapp.backendapi.jurisdiction.dto.JurisdictionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JurisdictionMapper {

  // mapping seems to be required due to state being labeled as an enum type. City wasn't returning
  // without it either
  @Mapping(target = "state", expression = "java(entity.getState().name())")
  @Mapping(target = "city", source = "city")
  JurisdictionDto toDto(Jurisdiction entity);

  // need to double-check this
  @Mapping(
      target = "state",
      expression = "java(com.parkingapp.backendapi.common.enums.State.valueOf(dto.state()))")
  @Mapping(target = "city", source = "city")
  Jurisdiction toEntity(JurisdictionDto dto);
}
