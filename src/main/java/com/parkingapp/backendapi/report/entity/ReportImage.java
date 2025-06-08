package com.parkingapp.backendapi.report.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "report_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReportImage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  Long id;

  @Column(name = "url", nullable = false, length = 2048)
  String url;

  // -- Relationship --
  @ManyToOne(fetch = FetchType.LAZY) // Many imageUrls belong to one report
  @JoinColumn(name = "report_id", nullable = false) // Foreign key column in report_images table
  private Report report;
}
