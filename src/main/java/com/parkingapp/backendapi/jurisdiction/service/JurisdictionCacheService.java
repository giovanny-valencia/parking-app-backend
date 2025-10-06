package com.parkingapp.backendapi.jurisdiction.service;

import com.parkingapp.backendapi.jurisdiction.entity.Jurisdiction;
import com.parkingapp.backendapi.jurisdiction.repository.JurisdictionRepository;
import com.parkingapp.backendapi.jurisdiction.utils.JurisdictionSpatialUtils;
import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * The primary cache service for jurisdiction data.
 *
 * <p>This class is responsible for loading all jurisdiction data from the {@link
 * JurisdictionRepository} into an in-memory cache at application startup. It provides
 * high-performance access to jurisdiction data by avoiding repeated database lookups for frequent
 * requests, implementing a cache-aside pattern. It delegates the core spatial search logic to
 * {@link JurisdictionSpatialUtils}.
 */
@Service
@Slf4j
@AllArgsConstructor
// TODO: Convert this into a Redis service.
public class JurisdictionCacheService {

  private final JurisdictionRepository jurisdictionRepository;

  /**
   * An unmodifiable list containing all preloaded jurisdiction data. This field is volatile to
   * ensure thread-safe reads after the initial population during startup.
   */
  private volatile List<Jurisdiction> allJurisdictions;

  /**
   * Initializes the in-memory cache by loading all jurisdiction data from the database. This method
   * is called automatically by Spring after the bean has been constructed.
   */
  @PostConstruct
  public void preloadJurisdictions() {
    List<Jurisdiction> jurisdictions = jurisdictionRepository.findAll();
    this.allJurisdictions = Collections.unmodifiableList(jurisdictions);
    log.info("preloaded {} jurisdictions", this.allJurisdictions.size());
  }

  /**
   * Finds the specific jurisdiction that contains the given coordinates.
   *
   * <p>This method delegates the spatial search logic to a stateless utility, providing it with the
   * cached jurisdiction data. This two-step process (bounding box filtering followed by a precise
   * point-in-polygon check) ensures optimal performance for frequently accessed location data.
   *
   * @param longitude The longitude of the point to check.
   * @param latitude The latitude of the point to check.
   * @return An {@link Optional} containing the found {@link Jurisdiction} if one exists, or an
   *     empty Optional if no jurisdiction is found at the given coordinates.
   */
  public Optional<Jurisdiction> findJurisdictionByCoordinates(double longitude, double latitude) {
    return JurisdictionSpatialUtils.findJurisdiction(this.allJurisdictions, longitude, latitude);
  }
}
