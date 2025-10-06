package com.parkingapp.backendapi.jurisdiction.dto;

// TODO: will implement this later so that the client can do point-in-polygon checks via cache
public record CurrentSupportedCityDto(String state, String city, String[] boundaries) {}
