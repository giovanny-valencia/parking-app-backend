package com.parkingapp.backendapi.report.mapper;

import com.parkingapp.backendapi.report.entity.Report;
import com.parkingapp.backendapi.report.entity.ReportImage;
import com.parkingapp.backendapi.report.record.ReportSubmissionRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {VehicleMapper.class, AddressMapper.class})
public interface ReportSubmissionRequestMapper {
    @Mapping(target = "vehicle", source = "request.vehicle")
    @Mapping(target = "address", source = "request.address")

    @Mapping(target = "assignedOfficer", ignore = true) // This is initially null
    @Mapping(target = "reportingUser", ignore = true) // This will be set in the service or passed as param

    @Mapping(target = "images", source = "request", qualifiedByName = "mapImages")

    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "status", expression = "java(com.parkingapp.backendapi.report.entity.Status.OPEN)")
    @Mapping(target = "createdOn", expression = "java(java.time.Instant.now())") // Add this for createdOn
    @Mapping(target = "updatedOn", ignore = true) // Not updated on creation
    Report toEntity(ReportSubmissionRequest request);

    @Named("mapImages")
    default List<ReportImage> mapImages(ReportSubmissionRequest request){
        List<ReportImage> images = new ArrayList<>();

        // handle license plate image
        if(request.licensePlateImage() != null &&
                !request.licensePlateImage().isBlank()){
            ReportImage licensePlateImage = new ReportImage();
            licensePlateImage.setUrl(request.licensePlateImage());
            images.add(licensePlateImage);
        }

        // handle supporting violation images
        if(request.violationImages() != null){
            request.violationImages().forEach(url ->{
                if(url != null && !url.isBlank()){
                    ReportImage violationImage = new ReportImage();
                    violationImage.setUrl(url);
                    images.add(violationImage);
                }
            });
        }

        return images;
    }
}
