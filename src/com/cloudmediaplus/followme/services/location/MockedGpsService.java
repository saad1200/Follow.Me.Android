package com.cloudmediaplus.followme.services.location;

import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import com.cloudmediaplus.followme.Interfaces.ILocationManagerClient;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MockedGpsService {
    private final ILocationManagerClient mainServiceClient;
    private Timer timer;

    public MockedGpsService(Context context, ILocationManagerClient mainServiceClient){
        this.mainServiceClient = mainServiceClient;
    }

    public void StartGpsManager() {
        mainServiceClient.OnStatus(GpsStatus.GPS_EVENT_STARTED);
        timer = new Timer("task", true);
        timer.scheduleAtFixedRate(new UpdateLocationTask(), 10*1000, 10*1000);
    }

    public void StopGpsManager() {
        timer.cancel();
    }

    class UpdateLocationTask extends TimerTask{

        @Override
        public void run() {
            Location location = new Location("reverseGeocoded");
            location.setLatitude(77.00);
            location.setLongitude(0);
            location.setAccuracy(3333);
            location.setBearing(333);
            location.setTime(new Date().getTime());
            mainServiceClient.OnLocationUpdate(location);
            mainServiceClient.OnStatus(GpsStatus.GPS_EVENT_FIRST_FIX);
        }
    }
}