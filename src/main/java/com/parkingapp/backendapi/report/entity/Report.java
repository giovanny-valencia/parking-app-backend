package com.parkingapp.backendapi.report.entity;

import com.parkingapp.backendapi.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reports")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // Reference to vehicle table: Many reports assigned to 1 vehicle
    // A vehicle can have multiple reports
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    // Reference to report_address table: 1:1 report to address
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "address_id", nullable = false)
    private ReportAddress address;

    // Reference to user table: Many reports assigned to 1 officer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_officer_id")
    private User assignedOfficer; // initially empty (null) for fresh reports / unassigned officers

    // Reference to user table: Many reports assigned to 1 user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporting_user_id", nullable = false)
    private User reportingUser;

    // One report assigned to many images
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ReportImage> images = new ArrayList<>();

    @Column(name = "description", nullable = false, length = 256)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "notes", length = 256) // possibly rename this field, officer related notes
    private String notes;   // reason for removal or rejecting citation.

    @Column(name = "created_on", nullable = false, updatable = false)
    private Instant createdOn;

    @Column(name = "updated_on")
    private Instant  updatedOn;

    public void addImage(ReportImage image) {
        images.add(image);
        image.setReport(this);
    }
}
