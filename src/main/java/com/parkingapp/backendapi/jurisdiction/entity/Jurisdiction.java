package com.parkingapp.backendapi.jurisdiction.entity;

import com.parkingapp.backendapi.common.enums.State;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.locationtech.jts.geom.Polygon;

@Entity
@Table(
    name = "jurisdictions",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"state", "city"})})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Jurisdiction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  // TODO: future expansions plan. Multi-services or lost client. "isSupported".

  @Enumerated(EnumType.STRING)
  @Column(name = "state", nullable = false, length = 2)
  private State state;

  @Column(name = "city", nullable = false, length = 32)
  private String city;

  @Column(name = "boundary", columnDefinition = "POLYGON", nullable = false)
  private Polygon boundary;

  @Column(name = "max_longitude", nullable = false)
  private Double maxLongitude;

  @Column(name = "min_longitude", nullable = false)
  private Double minLongitude;

  @Column(name = "max_latitude", nullable = false)
  private Double maxLatitude;

  @Column(name = "min_latitude", nullable = false)
  private Double minLatitude;
}
