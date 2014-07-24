package com.cloudmediaplus.followme.services.location;

import android.location.*;
import android.os.Bundle;
import com.cloudmediaplus.followme.framework.Utilities;

import java.util.Iterator;

class LocationListener implements android.location.LocationListener, GpsStatus.Listener, GpsStatus.NmeaListener {

    private LocationManagerService locationManagerService;
    protected String latestHdop;
    protected String latestPdop;
    protected String latestVdop;
    protected String geoIdHeight;
    protected String ageOfDgpsData;
    protected String dgpsId;

    LocationListener(LocationManagerService locationManagerService) {
        this.locationManagerService = locationManagerService;
    }

    public void onLocationChanged(Location loc) {

        try {
            if (loc != null) {
                Bundle b = new Bundle();
                b.putString("HDOP", this.latestHdop);
                b.putString("PDOP", this.latestPdop);
                b.putString("VDOP", this.latestVdop);
                b.putString("GEOIDHEIGHT", this.geoIdHeight);
                b.putString("AGEOFDGPSDATA", this.ageOfDgpsData);
                b.putString("DGPSID", this.dgpsId);
                loc.setExtras(b);
                locationManagerService.OnLocationChanged(loc);

                this.latestHdop = "";
                this.latestPdop = "";
                this.latestVdop = "";
            }

        } catch (Exception ex) {
            locationManagerService.SetStatus(ex.getMessage());
        }

    }

    public void onProviderDisabled(String provider) {
        locationManagerService.RestartGpsManagers();
    }

    public void onProviderEnabled(String provider) {
        locationManagerService.RestartGpsManagers();
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (status == LocationProvider.OUT_OF_SERVICE) {
            locationManagerService.StopManagerAndResetAlarm();
        }

        if (status == LocationProvider.AVAILABLE) {
        }

        if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
            locationManagerService.StopManagerAndResetAlarm();
        }
    }

    public void onGpsStatusChanged(int event) {

        locationManagerService.SetStatus(event);

        switch (event) {
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                locationManagerService.SetStatus("fix_obtained");
                break;

            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

                GpsStatus status = locationManagerService.getGpsStatus(null);

                int maxSatellites = status.getMaxSatellites();

                Iterator<GpsSatellite> it = status.getSatellites().iterator();
                int count = 0;

                while (it.hasNext() && count <= maxSatellites) {
                    it.next();
                    count++;
                }

                locationManagerService.SetSatelliteInfo(count);
                break;

            case GpsStatus.GPS_EVENT_STARTED:
                locationManagerService.SetStatus("started_waiting");
                break;

            case GpsStatus.GPS_EVENT_STOPPED:
                locationManagerService.SetStatus("gps_stopped");
                break;

        }
    }

    @Override
    public void onNmeaReceived(long timestamp, String nmeaSentence) {
        locationManagerService.OnNmeaSentence(timestamp, nmeaSentence);

        if(Utilities.IsNullOrEmpty(nmeaSentence)){
            return;
        }

        String[] nmeaParts = nmeaSentence.split(",");

        if (nmeaParts[0].equalsIgnoreCase("$GPGSA")) {

            if (nmeaParts.length > 15 && !Utilities.IsNullOrEmpty(nmeaParts[15])) {
                this.latestPdop = nmeaParts[15];
            }

            if (nmeaParts.length > 16 &&!Utilities.IsNullOrEmpty(nmeaParts[16])) {
                this.latestHdop = nmeaParts[16];
            }

            if (nmeaParts.length > 17 &&!Utilities.IsNullOrEmpty(nmeaParts[17]) && !nmeaParts[17].startsWith("*")) {

                this.latestVdop = nmeaParts[17].split("\\*")[0];
            }
        }


        if (nmeaParts[0].equalsIgnoreCase("$GPGGA")) {
            if (nmeaParts.length > 8 &&!Utilities.IsNullOrEmpty(nmeaParts[8])) {
                this.latestHdop = nmeaParts[8];
            }

            if (nmeaParts.length > 11 &&!Utilities.IsNullOrEmpty(nmeaParts[11])) {
                this.geoIdHeight = nmeaParts[11];
            }

            if (nmeaParts.length > 13 &&!Utilities.IsNullOrEmpty(nmeaParts[13])) {
                this.ageOfDgpsData = nmeaParts[13];
            }

            if (nmeaParts.length > 14 &&!Utilities.IsNullOrEmpty(nmeaParts[14]) && !nmeaParts[14].startsWith("*")) {
                this.dgpsId = nmeaParts[14].split("\\*")[0];
            }
        }
    }
}
