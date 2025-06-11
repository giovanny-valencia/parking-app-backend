package com.parkingapp.backendapi.report.mapper;

import com.parkingapp.backendapi.report.entity.Report;
import com.parkingapp.backendapi.report.record.ReportSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {AddressMapper.class})
public interface ReportSummaryMapper {

  @Mapping(target = "id", source = "report.id")
  @Mapping(target = "location", source = "report.address")
  @Mapping(target = "createdOn", source = "report.createdOn")
  ReportSummary toDto(Report report);
}
