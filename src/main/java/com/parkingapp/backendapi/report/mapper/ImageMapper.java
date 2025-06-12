package com.parkingapp.backendapi.report.mapper;

import com.parkingapp.backendapi.report.entity.ReportImage;
import com.parkingapp.backendapi.report.record.ReportImageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ImageMapper {

  @Mapping(target = "url", source = "url")
  ReportImageDto toDto(ReportImage reportImage);
}
