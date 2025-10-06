package com.parkingapp.backendapi.jurisdiction.utils;

import com.parkingapp.backendapi.jurisdiction.entity.Jurisdiction;
import java.util.List;
import java.util.Optional;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

/**
 * A utility class for performing spatial calculations on jurisdiction data.
 *
 * <p>This class is stateless and contains the core business logic for finding a jurisdiction based
 * on geographic coordinates. It is designed to be easily unit-testable without any external
 * dependencies.
 */
public final class JurisdictionSpatialUtils {

  private static final GeometryFactory geometryFactory = new GeometryFactory();

  /**
   * Private constructor to prevent instantiation. This is a utility class containing only static
   * methods.
   */
  private JurisdictionSpatialUtils() {}

  /**
   * Finds the specific jurisdiction containing the given coordinates from a provided list.
   *
   * <p>This method employs a two-step process for efficiency:
   *
   * <ol>
   *   <li>Bounding Box Filter: It first performs a simple and fast rectangular check to filter out
   *       jurisdictions that are definitely not a match.
   *   <li>Point-in-Polygon Check: For the remaining few candidates, it performs a precise, but more
   *       computationally expensive, point-in-polygon check to find the exact match.
   * </ol>
   *
   * @param allJurisdictions The list of all jurisdictions to search through.
   * @param longitude The longitude of the point to check.
   * @param latitude The latitude of the point to check.
   * @return An {@link Optional} containing the found {@link Jurisdiction} if one exists, or an
   *     empty Optional if no jurisdiction is found.
   */
  public static Optional<Jurisdiction> findJurisdiction(
      List<Jurisdiction> allJurisdictions, double longitude, double latitude) {

    // had to inverse the coordinates because this library swaps the long,lat coordinates into
    // lat,long
    Coordinate coordinate = new Coordinate(latitude, longitude);
    Point userPoint = geometryFactory.createPoint(coordinate);

    for (Jurisdiction jurisdiction : allJurisdictions) {
      if (isWithinBoundingBox(jurisdiction, longitude, latitude)) {

        // fixed a failing testcase. Needs more testing
        if (jurisdiction.getBoundary().contains(userPoint)) {
          return Optional.of(jurisdiction);
        }
      }
    }
    return Optional.empty();
  }

  /**
   * Helper method to determine if a point falls within a jurisdiction's bounding box.
   *
   * <p>This is a quick and simple check used to reduce the number of more expensive
   * point-in-polygon calculations.
   *
   * @param jurisdiction The jurisdiction with bounding box coordinates.
   * @param longitude The longitude of the point to check.
   * @param latitude The latitude of the point to check.
   * @return {@code true} if the point is within the bounding box, {@code false} otherwise.
   */
  private static boolean isWithinBoundingBox(
      Jurisdiction jurisdiction, double longitude, double latitude) {

    return longitude >= jurisdiction.getMinLongitude()
        && longitude <= jurisdiction.getMaxLongitude()
        && latitude >= jurisdiction.getMinLatitude()
        && latitude <= jurisdiction.getMaxLatitude();
  }
}
