package com.parkingapp.backendapi.report.service;

import com.parkingapp.backendapi.common.enums.State;
import com.parkingapp.backendapi.jurisdiction.entity.Jurisdiction;
import com.parkingapp.backendapi.jurisdiction.service.JurisdictionCacheService;
import com.parkingapp.backendapi.jurisdiction.service.JurisdictionService;
import com.parkingapp.backendapi.report.entity.Report;
import com.parkingapp.backendapi.report.entity.Status;
import com.parkingapp.backendapi.report.record.ReportSummaryDto;
import com.parkingapp.backendapi.report.repository.ReportRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ActiveParkingReportService {

  private final ReportRepository reportRepository;
  private final JurisdictionService jurisdictionService;

  // TODO: move this to jurisdiction service
  private final JurisdictionCacheService jurisdictionCacheService;

  /**
   * validates jurisdiction then fetches all reports given that jurisdiction
   *
   * @param jurisdictionState Officer jwt supported jurisdiction state
   * @param jurisdictionCity Officer jwt supported jurisdiction city
   * @return list containing {@link ReportSummaryDto} summary of active reports in officers
   *     jurisdiction within a createdOn cutoff period
   */
  @Transactional
  public List<Report> retrieveReportsByJurisdiction(
      String jurisdictionState, String jurisdictionCity) {

    // before retrieval, ensure that the jurisdiction is supported
    jurisdictionCacheService
        .findJurisdictionByStateAndCity(jurisdictionState, jurisdictionCity)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Jurisdiction "
                        + jurisdictionState
                        + ", "
                        + jurisdictionCity
                        + " is not supported."));

    Jurisdiction jurisdiction =
        jurisdictionService.getManagedJurisdiction(
            State.valueOf(jurisdictionState), jurisdictionCity);

    Collection<Status> activeRetrievalStatuses = List.of(Status.OPEN);

    /*  This might vary by jurisdiction. For now, it's a hardcoded arbitrary 2-hour timeframe.
    In the future, it might be stored under jurisdiction.
    Possibly under specific departments, and maybe even under the specific 'supported violation' */
    Duration activeTimeFrame = Duration.ofHours(2);
    Instant createdOnCutOff = Instant.now().minus(activeTimeFrame);

    return reportRepository.findByAddress_JurisdictionAndStatusInAndCreatedOnAfter(
        jurisdiction, activeRetrievalStatuses, createdOnCutOff);
  }

  /**
   * TODO: possible validation here? Is the report still active in the db?
   *
   * @param id selected report id
   * @return full Report data
   */
  @Transactional
  public Report retrieveReportDetails(Long id) {
    return reportRepository
        .findById(id)
        .orElseThrow(() -> new NoSuchElementException("Report with ID " + id + " not found."));
  }
}
