package com.parkingapp.backendapi.jurisdiction.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.parkingapp.backendapi.jurisdiction.entity.Jurisdiction;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.mockito.Mockito;

/**
 * Unit tests for the {@link JurisdictionSpatialUtils} class. These tests verify the spatial search
 * logic in isolation, without requiring a live database or Spring application context.
 */
class JurisdictionSpatialUtilsTest {

  private final GeometryFactory geometryFactory = new GeometryFactory();
  private List<Jurisdiction> testJurisdictions;

  @BeforeEach
  void setUp() {
    // Create a mock jurisdiction for Hoboken with a simple square boundary
    Jurisdiction mockHoboken = Mockito.mock(Jurisdiction.class);
    Mockito.when(mockHoboken.getId()).thenReturn(1L);
    Mockito.when(mockHoboken.getCity()).thenReturn("HOBOKEN");
    Mockito.when(mockHoboken.getMinLongitude()).thenReturn(-74.04);
    Mockito.when(mockHoboken.getMaxLongitude()).thenReturn(-74.02);
    Mockito.when(mockHoboken.getMinLatitude()).thenReturn(40.73);
    Mockito.when(mockHoboken.getMaxLatitude()).thenReturn(40.75);

    // Define the polygon for the mock jurisdiction
    Coordinate[] coordinates =
        new Coordinate[] {
          new Coordinate(-74.04, 40.73),
          new Coordinate(-74.02, 40.73),
          new Coordinate(-74.02, 40.75),
          new Coordinate(-74.04, 40.75),
          new Coordinate(-74.04, 40.73) // Close the polygon
        };
    Polygon polygon = geometryFactory.createPolygon(coordinates);
    Mockito.when(mockHoboken.getBoundary()).thenReturn(polygon);

    this.testJurisdictions = List.of(mockHoboken);
  }

  @Test
  void findJurisdiction_pointInside_shouldReturnCorrectJurisdiction() {
    // Given a point clearly inside the mock Hoboken polygon
    double testLongitude = -74.03;
    double testLatitude = 40.74;

    // When the findJurisdiction method is called
    Optional<Jurisdiction> result =
        JurisdictionSpatialUtils.findJurisdiction(testJurisdictions, testLongitude, testLatitude);

    // Then the result should be present and contain the correct jurisdiction
    assertTrue(result.isPresent(), "A jurisdiction should have been found.");
    assertEquals("HOBOKEN", result.get().getCity(), "The found jurisdiction should be Hoboken.");
  }

  /** Tests the case where the point is outside all jurisdictions. */
  @Test
  void findJurisdiction_pointOutside_shouldReturnEmpty() {
    // Given a point clearly outside the mock Hoboken polygon's bounding box
    double testLongitude = -75.0;
    double testLatitude = 41.0;

    // When the findJurisdiction method is called
    Optional<Jurisdiction> result =
        JurisdictionSpatialUtils.findJurisdiction(testJurisdictions, testLongitude, testLatitude);

    // Then the result should be empty
    assertFalse(result.isPresent(), "No jurisdiction should have been found.");
  }

  /**
   * Tests the case where the point is exactly on the boundary of a jurisdiction. The JTS
   * `contains()` method includes the boundary.
   */
  @Test
  void findJurisdiction_pointOnBoundary_shouldReturnJurisdiction() {
    // Given a point on the western edge of the mock Hoboken polygon
    double testLongitude = -74.04;
    double testLatitude = 40.74;

    // When the findJurisdiction method is called
    Optional<Jurisdiction> result =
        JurisdictionSpatialUtils.findJurisdiction(testJurisdictions, testLongitude, testLatitude);

    // Then the result should be present and contain the correct jurisdiction
    assertTrue(result.isPresent(), "The point on the boundary should be included.");
    assertEquals("HOBOKEN", result.get().getCity());
  }
}
