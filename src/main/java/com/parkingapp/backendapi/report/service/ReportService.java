package com.parkingapp.backendapi.report.service;

import com.parkingapp.backendapi.report.entity.Jurisdiction;
import com.parkingapp.backendapi.report.entity.Report;
import com.parkingapp.backendapi.report.mapper.ReportSubmissionRequestMapper;
import com.parkingapp.backendapi.report.record.ReportSubmissionRequest;
import com.parkingapp.backendapi.report.repository.ReportRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportSubmissionRequestMapper reportSubmissionRequestMapper;

    public void processReportSubmissionRequest(ReportSubmissionRequest request){
        // convert to entity
        Report report = reportSubmissionRequestMapper.toEntity(request);

        System.out.println("dto -> entity:");
        System.out.println(report);
    }

}
