package com.cloudmediaplus.followme.services.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import com.cloudmediaplus.followme.Interfaces.ILocationBroadcastSubscriber;

public class LocationBroadcastReceiver extends BroadcastReceiver{

    private ILocationBroadcastSubscriber locationBroadcastSubscriber;
    public static final String LOCATION_PROGRESS = "IS_LOCATION_PROGRESS";
    public static final String LOCATION_UPDATED = "LOCATION_UPDATED";
    public static final String NOTIFICATION = "com.cloudmediaplus.followme.service";


    public void subscribe(ILocationBroadcastSubscriber locationBroadcastSubscriber){

        this.locationBroadcastSubscriber = locationBroadcastSubscriber;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {

            if (bundle.containsKey(LOCATION_PROGRESS)) {
                boolean isInProgress = bundle.getBoolean(LOCATION_PROGRESS);
                locationBroadcastSubscriber.updateLocationProgress(isInProgress);
            }

            if (bundle.containsKey(LOCATION_UPDATED)) {
                Location location = (Location) bundle.get(LOCATION_UPDATED);
                locationBroadcastSubscriber.LocationChanged(location);
            }
        }
    }
}
