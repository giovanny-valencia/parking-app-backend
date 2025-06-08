package com.parkingapp.backendapi.report.entity;

public enum Status {
  // don't currently see a need for a "NEW" status type but might change
  // client will visually track report status to the individual officer

  OPEN, // shown to all officers
  EXPIRED, // removed from list and marked as unattended
  ASSIGNED, // officer assigned, prevents another from also being assigned and removes it from their
            // view on fetch
  REMOVED, // for data tracking, lists the full report including the officer that removed it
}
