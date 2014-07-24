package com.cloudmediaplus.followme.services.location;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import com.cloudmediaplus.followme.Interfaces.ILocationManagerClient;
import com.cloudmediaplus.followme.framework.PostMan;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.*;

public class LocationManagerClient implements ILocationManagerClient {

    private LocationManagerService locationManagerService;
    private Location currentBestLocation;
    private boolean lastKnowIsInProgress;
    private Context context;
    private PostMan postMan = new PostMan();
    private static LocationManagerClient instance = null;
    private ArrayList<String> codes = new ArrayList<String>();
    private boolean inProgress = false;


    protected LocationManagerClient(Context context) {
        this.context = context;
    }

    public static LocationManagerClient getInstance(Context context) {
        if(instance == null) {
            instance = new LocationManagerClient(context);
        }
        return instance;
    }

    public void requestLocationUpdate(String code) {
        if(!code.isEmpty() && !codes.contains(code)) {
            codes.add(code);
        }

        if(!inProgress){
            locationManagerService = new LocationManagerService(context, this);
            locationManagerService.start();
            publishIsInProgress(true);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    inProgress = false;
                    publishIsInProgress(inProgress);
                    locationManagerService.stop();
                }
            }, 2000*60);
        }
    }

    private void publishIsInProgress(boolean inProgress){
        this.inProgress = inProgress;
        Intent intent = new Intent(LocationBroadcastReceiver.NOTIFICATION);
        intent.putExtra(LocationBroadcastReceiver.LOCATION_PROGRESS, inProgress);
        context.sendBroadcast(intent);
        lastKnowIsInProgress = inProgress;
    }

    public void publishLocation(Location location){
        for (String code : codes){
            sendLocationToServer(code, location);

        }

        Intent intent = new Intent(LocationBroadcastReceiver.NOTIFICATION);
        intent.putExtra(LocationBroadcastReceiver.LOCATION_UPDATED, location);
        context.sendBroadcast(intent);
    }

    private void sendLocationToServer(String code, Location location) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("latitude", Double.toString(location.getLatitude())));
        nameValuePairs.add(new BasicNameValuePair("longitude", Double.toString(location.getLongitude())));
        String url = "http://www.almogtarbeen.com/Follow/Update/" + code;
        postMan.post(url, nameValuePairs);
    }

    public void publishMostRecentLocation(){
        publishLocation(currentBestLocation);
        publishIsInProgress(lastKnowIsInProgress);
    }

    @Override
    public void OnStatusMessage(String message) {
    }

    @Override
    public void OnStatus(int status) {
    }

    @Override
    public void OnLocationUpdate(Location location) {

        if(location.getProvider().compareTo("network") == 0){
            locationManagerService.stopNetwork();
        }

        Log.e("out_OnLocationUpdate", location.getProvider() + " - " + Float.toString(location.getAccuracy()));
        if(isBetterLocation(location, currentBestLocation)) {
            Log.e("OnLocationUpdate", location.getProvider() + " - " + Float.toString(location.getAccuracy()));
            publishLocation(location);
            currentBestLocation = location;
        }
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        int TIME_PERIOD_REJECTION_LIMIT = 1000 * 60 * 2;
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TIME_PERIOD_REJECTION_LIMIT;
        boolean isSignificantlyOlder = timeDelta < -TIME_PERIOD_REJECTION_LIMIT;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return false;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return false;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Override
    public void OnWaitingForLocation(boolean inProgress) {
        publishIsInProgress(inProgress);

    }

    @Override
    public void OnComplete() {
        publishIsInProgress(false);
    }

    @Override
    public void OnFailure() {
        publishIsInProgress(false);
    }
}
