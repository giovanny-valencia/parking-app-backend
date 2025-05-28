package com.parkingapp.backendapi.report.mapper;

import com.parkingapp.backendapi.report.entity.Vehicle;
import com.parkingapp.backendapi.report.record.VehicleData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    @Mapping(target = "state", source = "plateState")
    @Mapping(target = "plateNumber", source = "plateNumber")
    VehicleData toDto(Vehicle entity);

    @Mapping(target = "plateState", source = "state")
    @Mapping(target = "plateNumber", source = "plateNumber")
    Vehicle toEntity(VehicleData record);
}
