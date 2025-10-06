//package com.parkingapp.backendapi.report.service;
//
//import com.parkingapp.backendapi.common.enums.State;
//import com.parkingapp.backendapi.jurisdiction.entity.Jurisdiction;
//import com.parkingapp.backendapi.jurisdiction.service.JurisdictionCacheService;
//import com.parkingapp.backendapi.jurisdiction.service.JurisdictionService;
//import com.parkingapp.backendapi.report.dto.ReportImageDto;
//import com.parkingapp.backendapi.report.dto.ReportOfficerViewDto;
//import com.parkingapp.backendapi.report.dto.ReportSummaryDto;
//import com.parkingapp.backendapi.report.entity.Report;
//import com.parkingapp.backendapi.report.entity.Status;
//import com.parkingapp.backendapi.report.mapper.ReportViewMapper;
//import com.parkingapp.backendapi.report.repository.ReportRepository;
//import com.parkingapp.backendapi.infrastructure.service.s3.S3Service;
//import java.time.Duration;
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.NoSuchElementException;
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@AllArgsConstructor
//public class ActiveParkingReportService {
//
//  private final ReportRepository reportRepository;
//
//  private final S3Service s3Service;
//
//  private final ReportViewMapper reportViewMapper;
//
//  private final JurisdictionService jurisdictionService;
//  private final JurisdictionCacheService jurisdictionCacheService;
//
//  //TODO: rewrite
//
//  /**
//   * validates jurisdiction then fetches all reports given that jurisdiction
//   *
//   * @param jurisdictionState Officer jwt supported jurisdiction state
//   * @param jurisdictionCity Officer jwt supported jurisdiction city
//   * @return list containing {@link ReportSummaryDto} summary of active reports in officers
//   *     jurisdiction within a createdOn cutoff period
//   */
//  @Transactional
//  public List<Report> retrieveReportsByJurisdiction(
//      String jurisdictionState, String jurisdictionCity) {
//
//    Report report = new Report();
//    List<Report> list = new ArrayList<>();
//    list.add(report);
//    return list;
//
//    // before retrieval, ensure that the jurisdiction is supported
//    jurisdictionCacheService
//        .findJurisdictionByStateAndCity(jurisdictionState, jurisdictionCity)
//        .orElseThrow(
//            () ->
//                new IllegalArgumentException(
//                    "Jurisdiction "
//                        + jurisdictionState
//                        + ", "
//                        + jurisdictionCity
//                        + " is not supported."));
//
//    Jurisdiction jurisdiction =
//        jurisdictionService.getManagedJurisdiction(
//            State.valueOf(jurisdictionState), jurisdictionCity);
//
//    Collection<Status> activeRetrievalStatuses = List.of(Status.OPEN);
//
//    /*  This might vary by jurisdiction. For now, it's a hardcoded arbitrary 2-hour timeframe.
//    In the future, it might be stored under jurisdiction.
//    Possibly under specific departments, and maybe even under the specific 'supported violation' */
//    Duration activeTimeFrame = Duration.ofHours(2);
//    Instant createdOnCutOff = Instant.now().minus(activeTimeFrame);
//
//    return reportRepository.findByAddress_JurisdictionAndStatusInAndCreatedOnAfter(
//        jurisdiction, activeRetrievalStatuses, createdOnCutOff);
//  }
//
//  /**
//   * Retrieves full Report data and transforms it into a `ReportOfficerViewDto` with S3 pre-signed
//   * URLs for images.
//   *
//   * @param id The ID of the report to retrieve.
//   * @return A `ReportOfficerViewDto` containing the report data with pre-signed image URLs.
//   * @throws NoSuchElementException if the report with the given ID is not found.
//   */
//  @Transactional(readOnly = true)
//  public ReportOfficerViewDto retrieveReportDetails(Long id) {
//    // fetch the report from the database
//    Report report =
//        reportRepository
//            .findById(id)
//            .orElseThrow(() -> new NoSuchElementException("Report with ID " + id + " not found."));
//
//    // convert it to a mutable dto
//    ReportOfficerViewDto dto = reportViewMapper.toDto(report);
//
//    // debug to confirm dto
//    System.out.println("dto data");
//    System.out.println(dto.getAddressDto());
//    System.out.println(dto.getVehicleDto());
//    System.out.println(dto.getReportImageDto());
//    System.out.println(dto.getDescription());
//    System.out.println(dto.getCreatedOn());
//
//    List<ReportImageDto> signedImageDtos =
//        report.getImages().stream()
//            .map(
//                imageEntity -> {
//                  String s3Key = imageEntity.getUrl();
//
//                  String s3SignedUrl = s3Service.generatePresignedUrl(s3Key);
//
//                  return new ReportImageDto(s3SignedUrl);
//                })
//            .toList();
//
//    dto.setReportImageDto(signedImageDtos);
//
//    dto.getReportImageDto()
//        .forEach(
//                System.out::println);
//
//    return dto;
//  }
//}
