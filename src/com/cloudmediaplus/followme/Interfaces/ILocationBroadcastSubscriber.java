package com.cloudmediaplus.followme.Interfaces;

import android.location.Location;

public interface ILocationBroadcastSubscriber {
    void updateLocationProgress(boolean inProgress);
    void LocationChanged(Location location);
}