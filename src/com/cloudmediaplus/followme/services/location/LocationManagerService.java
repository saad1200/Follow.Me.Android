package com.cloudmediaplus.followme.services.location;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import com.cloudmediaplus.followme.Interfaces.IActionListener;
import com.cloudmediaplus.followme.Interfaces.ILocationManagerClient;

public class LocationManagerService extends Service implements IActionListener {

    private Context context;
    private LocationListener gpsLocationListener;
    private LocationListener towerLocationListener;
    private LocationManager gpsLocationManager;
    private LocationManager towerLocationManager;
    private static ILocationManagerClient mainServiceClient;
    private final IBinder binder = new GpsLoggingBinder();

    public LocationManagerService(Context context, ILocationManagerClient mainServiceClient){
        this.context = context;
        this.mainServiceClient = mainServiceClient;
        towerLocationListener = new LocationListener(this);
        gpsLocationListener = new LocationListener(this);
    }

    private boolean startGps(){
        gpsLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = gpsLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(isGpsEnabled)  {
            mainServiceClient.OnStatus(GpsStatus.GPS_EVENT_STARTED);
            mainServiceClient.OnStatusMessage("GPS_PROVIDER");
            gpsLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, gpsLocationListener);

            gpsLocationManager.addGpsStatusListener(gpsLocationListener);
            gpsLocationManager.addNmeaListener(gpsLocationListener);

        }

        return isGpsEnabled;
    }

    private boolean startTower() {

        towerLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isTowerEnabled = towerLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(isTowerEnabled){
            mainServiceClient.OnStatus(GpsStatus.GPS_EVENT_STARTED);
            mainServiceClient.OnStatusMessage("NETWORK_PROVIDER");
            towerLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0, 0, towerLocationListener);

        }

        return isTowerEnabled;
    }

    public void start() {

        boolean isGpsEnabled = startGps();
        boolean isTowerEnabled = startTower();
        if(!isTowerEnabled && !isGpsEnabled) {
            mainServiceClient.OnStatusMessage("no provider");
            return;
        }

        if (mainServiceClient != null) {
            mainServiceClient.OnWaitingForLocation(true);
        }

    }

    public void stop() {

        stopNetwork();
        stopGps();

        if (mainServiceClient != null) {
            mainServiceClient.OnWaitingForLocation(false);
        }
    }

    private void stopGps() {
        if (gpsLocationListener != null) {
            gpsLocationManager.removeUpdates(gpsLocationListener);
            gpsLocationManager.removeGpsStatusListener(gpsLocationListener);
        }
        if (mainServiceClient != null) {
            mainServiceClient.OnWaitingForLocation(false);
        }
    }

    void stopNetwork() {
        if (towerLocationListener != null) {
            towerLocationManager.removeUpdates(towerLocationListener);
        }
    }

    public void RestartGpsManagers() {
        stop();
        start();
    }

    public void OnLocationChanged(Location loc) {
        mainServiceClient.OnLocationUpdate(loc);
    }

    void SetSatelliteInfo(int count) {
    }

    private boolean IsMainFormVisible() {
        return mainServiceClient != null;
    }

    public void OnNmeaSentence(long timestamp, String nmeaSentence) {
    }

    @Override
    public void OnComplete() {
        mainServiceClient.OnComplete();
    }

    @Override
    public void OnFailure() {
        mainServiceClient.OnFailure();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void SetStatus(String status) {
        mainServiceClient.OnStatusMessage(status);
    }

    public void SetStatus(int status) {
        mainServiceClient.OnStatus(status);
    }

    public void StopManagerAndResetAlarm() {
        Boolean shouldkeepFix = false;
        if (!shouldkeepFix) {
            stop();
        }
    }

    public GpsStatus getGpsStatus(GpsStatus status) {
        return gpsLocationManager.getGpsStatus(status);
    }

    public class GpsLoggingBinder extends Binder {
        public LocationManagerService getService() {
            return LocationManagerService.this;
        }
    }
}
