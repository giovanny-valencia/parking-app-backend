package com.parkingapp.backendapi.jurisdiction.utils;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * File to parse through city limit files which contain the coordinates for the city's boundary.
 * Intended use is to print it and manually paste it into a script file. Don't forget convention is
 * the list coordinates are in [longitude, latitude] order.
 */
public class CityLimitDataExtractor {
  public static void main(String[] args) {
    try {
      InputStream inputStream =
          CityLimitDataExtractor.class
              .getClassLoader()
              .getResourceAsStream("CityLimits/Hoboken_City_Limits.geojson");

      if (inputStream == null) {
        System.out.println(
            "File not found, make sure file is in the 'src/main/resources/CityLimits' folder.");
        return;
      }

      // Read the InputStream into a single String
      String geoJsonContent =
          new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();

      // Parse the GeoJSON string
      JSONObject geoJson = new JSONObject(geoJsonContent);
      JSONArray features = geoJson.getJSONArray("features");

      // arbitrary values outside of range
      BigDecimal maxLongitude = new BigDecimal(-200);
      BigDecimal minLongitude = new BigDecimal(200);
      BigDecimal maxLatitude = new BigDecimal(-100);
      BigDecimal minLatitude = new BigDecimal(100);

      if (!features.isEmpty()) {
        JSONObject feature = features.getJSONObject(0);
        JSONObject geometry = feature.getJSONObject("geometry");
        JSONArray coordinates = geometry.getJSONArray("coordinates");

        for (int i = 0; i < coordinates.length(); i++) {
          JSONArray subCoordinates = coordinates.getJSONArray(i);
          for (int j = 0; j < subCoordinates.length(); j++) {
            JSONArray coordinatePair = subCoordinates.getJSONArray(j);
            BigDecimal longitude = coordinatePair.getBigDecimal(0);
            BigDecimal latitude = coordinatePair.getBigDecimal(1);

            // Update max/min values
            if (longitude.compareTo(maxLongitude) > 0) {
              maxLongitude = longitude;
            }
            if (longitude.compareTo(minLongitude) < 0) {
              minLongitude = longitude;
            }
            if (latitude.compareTo(maxLatitude) > 0) {
              maxLatitude = latitude;
            }
            if (latitude.compareTo(minLatitude) < 0) {
              minLatitude = latitude;
            }

            System.out.println(longitude + " " + latitude + ",");
          }
        }
        System.out.println("**************");
        System.out.println("Max Longitude: " + maxLongitude);
        System.out.println("Min Longitude: " + minLongitude);
        System.out.println("Max Latitude: " + maxLatitude);
        System.out.println("Min Latitude: " + minLatitude);
      }

    } catch (Exception e) {
      System.err.println("Error processing GeoJSON file: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
