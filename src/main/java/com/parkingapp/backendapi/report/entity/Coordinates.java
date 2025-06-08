package com.parkingapp.backendapi.report.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Coordinates {
  @Min(value = -90, message = "Latitude must be between -90 and 90 degrees.")
  @Max(value = 90, message = "Latitude must be between -90 and 90 degrees.")
  @Column(name = "latitude", nullable = false)
  private double latitude;

  @Min(value = -180, message = "Longitude must be between -180 and 180 degrees.")
  @Max(value = 180, message = "Longitude must be between -180 and 180 degrees.")
  @Column(name = "longitude", nullable = false)
  private double longitude;
}
