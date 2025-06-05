package com.parkingapp.backendapi.report.mapper;

import com.parkingapp.backendapi.jurisdiction.mapper.JurisdictionMapper;
import com.parkingapp.backendapi.report.entity.ReportAddress;
import com.parkingapp.backendapi.report.record.AddressData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {JurisdictionMapper.class})
public interface AddressMapper {

    @Mapping(target = "streetAddress", source = "dto.streetAddress")
    @Mapping(target = "zipCode", source = "dto.zipCode")
    @Mapping(target = "locationNotes", source = "dto.locationNotes")
    @Mapping(target = "location", source = "dto.location")
    @Mapping(target = "jurisdiction", source = "dto.jurisdiction")
    ReportAddress toEntity(AddressData dto);
}
