package com.cloudmediaplus.followme.Interfaces;

import android.location.Location;

public interface ILocationManagerClient {

    public void OnStatusMessage(String message);

    public void OnStatus(int status);

    public void OnLocationUpdate(Location loc);

    public void OnWaitingForLocation(boolean inProgress);

    public void OnComplete();

    public void OnFailure();
}
