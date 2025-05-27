package com.parkingapp.backendapi.report.entity;

import com.parkingapp.backendapi.report.entity.Jurisdiction;
import com.parkingapp.backendapi.report.entity.Coordinates;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "report_addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "street_address", nullable = false, length = 256) // Explicit column name and length
    private String streetAddress; // Renamed from 'street' for clarity and common convention

    @Column(name = "zip_code", length = 10) // Explicit column name and length (5 or 9 digits + optional hyphen)
    private String zipCode; // Renamed from 'zipcode' to 'zipCode' for Java camelCase convention

    @Column(name = "location_notes", length = 128) // Explicit column name, use TEXT for notes
    private String locationNotes;

    @Valid
    @Embedded // Tells JPA to embed the fields of Coordinates directly into the report_addresses table
    Coordinates location;

    @ManyToOne(fetch = FetchType.LAZY) // Many report addresses to one jurisdiction
    @JoinColumn(name = "jurisdiction_id", nullable = false)
    Jurisdiction jurisdiction;
}
