package com.parkingapp.backendapi.report.service;

import com.parkingapp.backendapi.jurisdiction.service.JurisdictionCacheService;
import com.parkingapp.backendapi.report.record.ReportSummary;
import com.parkingapp.backendapi.report.repository.ReportRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ActiveParkingReportService {

  private final ReportRepository reportRepository;

  private final JurisdictionCacheService jurisdictionCacheService;

  @Transactional
  public List<ReportSummary> getActiveReportSummaries() {
    // todo: replace this with the user (officer) jwt jurisdiction
    String JURISDICTION_TEST_STATE = "NJ";
    String JURISDICTION_TEST_CITY = "Union City";

    // before retrieval, ensure that the jurisdiction is supported
    jurisdictionCacheService
        .findJurisdictionByStateAndCity(JURISDICTION_TEST_STATE, JURISDICTION_TEST_CITY)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Jurisdiction "
                        + JURISDICTION_TEST_STATE
                        + ", "
                        + JURISDICTION_TEST_CITY
                        + " is not supported."));

    return null;
  }
}
