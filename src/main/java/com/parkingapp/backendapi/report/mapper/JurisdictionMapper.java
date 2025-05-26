package com.parkingapp.backendapi.report.mapper;

import com.parkingapp.backendapi.report.dto.JurisdictionDto;
import com.parkingapp.backendapi.report.entity.Jurisdiction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JurisdictionMapper {

    @Mapping(target = "state", expression = "java(jurisdiction.getState().name())")
    JurisdictionDto toDto(Jurisdiction jurisdiction);
}
