package com.parkingapp.backendapi.report.entity;

public enum Status {
  OPEN, // Report is active, awaiting assignment to an officer.

  ASSIGNED, // An officer is currently attending to this report.

  ACCEPTED, // Report was acted upon; violator was cited.

  REJECTED, // Report was investigated but no action (e.g., citation) was taken.

  EXPIRED, // Report passed its defined active time limit without being resolved by an officer.

  INVALID, // Report deemed invalid (e.g., fake report, mismatching plate number)
}
