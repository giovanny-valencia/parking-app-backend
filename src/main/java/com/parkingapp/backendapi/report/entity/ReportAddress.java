package com.parkingapp.backendapi.report.entity;

import com.parkingapp.backendapi.jurisdiction.entity.Jurisdiction;
import jakarta.persistence.CascadeType;
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
import lombok.ToString;

@Entity
@Table(name = "report_addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReportAddress {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  Long id;

  @Valid
  @Embedded // Tells JPA to embed the fields of CoordinatesData directly into the report_addresses
            // table
  Coordinates location;

  @ManyToOne(
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL) // Many report addresses to one jurisdiction
  @JoinColumn(name = "jurisdiction_id", nullable = false)
  Jurisdiction jurisdiction;

  @Column(
      name = "street_address",
      length = 256) // optional field (street address is not guaranteed)
  private String streetAddress;

  @Column(name = "zip_code", length = 10) // length (5 or 9 digits + optional hyphen)
  private String zipCode;

  @Column(name = "location_notes", length = 128)
  private String locationNotes;
}
