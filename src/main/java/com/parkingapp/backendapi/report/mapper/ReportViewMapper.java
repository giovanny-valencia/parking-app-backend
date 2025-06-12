package com.parkingapp.backendapi.report.mapper;

import com.parkingapp.backendapi.report.entity.Report;
import com.parkingapp.backendapi.report.record.ReportOfficerViewDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {AddressMapper.class, VehicleMapper.class, ImageMapper.class})
public interface ReportViewMapper {

  @Mapping(target = "addressDto", source = "report.address")
  @Mapping(target = "vehicleDto", source = "report.vehicle")
  @Mapping(target = "reportImageDto", source = "report.images")
  @Mapping(target = "description", source = "description")
  @Mapping(target = "createdOn", source = "createdOn")
  ReportOfficerViewDto toDto(Report report);
}
