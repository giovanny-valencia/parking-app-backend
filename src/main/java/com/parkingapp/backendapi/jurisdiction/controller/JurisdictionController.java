package com.parkingapp.backendapi.jurisdiction.controller;

import com.parkingapp.backendapi.common.annotations.RateLimit;
import com.parkingapp.backendapi.jurisdiction.dto.JurisdictionDto;
import com.parkingapp.backendapi.jurisdiction.dto.UserLocationDto;
import com.parkingapp.backendapi.jurisdiction.entity.Jurisdiction;
import com.parkingapp.backendapi.jurisdiction.mapper.JurisdictionMapper;
import com.parkingapp.backendapi.jurisdiction.service.JurisdictionService;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/jurisdictions")
public class JurisdictionController {

  private final JurisdictionService jurisdictionService;
  private final JurisdictionMapper jurisdictionMapper;

  /**
   * Checks if the given user location falls within a supported jurisdiction.
   *
   * <p>Returns status with a jurisdictionDto in the response body:
   *
   * @param userLocationDto DTO containing the latitude and longitude to validate and check.
   * @return A ResponseEntity with 200 OK and the jurisdictionDto result in the body.
   */
  // TODO: if/when multi-gov-services are supported, this should return which services are supported
  @RateLimit(limit = 5, timeWindowSeconds = 30, keyPrefix = "userId")
  @GetMapping("/isUserLocationSupported")
  public ResponseEntity<JurisdictionDto> isUserLocationSupported(
      @Valid @ModelAttribute UserLocationDto userLocationDto) {

    Optional<Jurisdiction> jurisdiction = jurisdictionService.isLocationSupported(userLocationDto);

    JurisdictionDto dto = null;

    if (jurisdiction.isPresent()) {
      Jurisdiction entity = jurisdiction.get();
      dto = jurisdictionMapper.toDto(entity);
    }

    return ResponseEntity.ok().body(dto);
  }
}
