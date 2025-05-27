package com.parkingapp.backendapi.report.mapper;

import com.parkingapp.backendapi.report.record.JurisdictionResponse;
import com.parkingapp.backendapi.report.entity.Jurisdiction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JurisdictionMapper {

    // mapping seems to be required due to state being labeled as an enum type. City wasn't returning without it either
    @Mapping(target = "state", expression = "java(jurisdiction.getState().name())")
    @Mapping(target = "city", source = "city")
    JurisdictionResponse toDto(Jurisdiction jurisdiction);
}
