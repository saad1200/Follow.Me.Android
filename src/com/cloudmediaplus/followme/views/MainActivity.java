package com.cloudmediaplus.followme.views;

import android.app.ActionBar;
import android.app.Activity;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.cloudmediaplus.followme.Resources;
import com.cloudmediaplus.followme.Interfaces.ILocationBroadcastSubscriber;
import com.cloudmediaplus.followme.services.location.LocationBroadcastReceiver;
import com.cloudmediaplus.followme.services.location.LocationManagerClient;
import com.cloudmediaplus.followme.services.messaging.MessagingService;
import com.cloudmediaplus.followme.views.map.GoogleMapView;
import com.cloudmediaplus.followme.views.actionbar.SpinnerNavItem;
import com.cloudmediaplus.followme.views.actionbar.TitleNavigationAdapter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import java.util.ArrayList;

public class MainActivity extends Activity implements ILocationBroadcastSubscriber, ActionBar.OnNavigationListener {

    private ActionBar actionBar;
    private ArrayList<SpinnerNavItem> navSpinner;
    private TitleNavigationAdapter adapter;
    private GoogleMapView googleMapView;
    private Menu menu;
    private boolean inProgress;
    private LocationBroadcastReceiver locationBroadcastReceiver;
    public LocationManagerClient service;
    private MessagingService messagingService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(Resources.mainLayout);
        messagingService = new MessagingService(this);
        messagingService.register();

        locationBroadcastReceiver = new LocationBroadcastReceiver();
        locationBroadcastReceiver.subscribe(this);

        actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        // Enabling Spinner dropdown navigation
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Spinner title navigation data
        navSpinner = new ArrayList<SpinnerNavItem>();
        navSpinner.add(new SpinnerNavItem("Local", Resources.newsIcon));
        navSpinner.add(new SpinnerNavItem("My Places", Resources.artIcon));
        navSpinner.add(new SpinnerNavItem("Checkins", Resources.chartIcon));
        navSpinner.add(new SpinnerNavItem("Latitude", Resources.femaleIcon));

        // title drop down adapter
        adapter = new TitleNavigationAdapter(getApplicationContext(), navSpinner);

        // assigning the spinner navigation
        actionBar.setListNavigationCallbacks(adapter, this);

        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(Resources.mapId)).getMap();
        googleMapView = new GoogleMapView(map);
        googleMapView.updateLocation(51.5072, 0.1275);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(Resources.main_actionsMenu, menu);
        this.menu = menu;
        updateProgressBar();
        return super.onCreateOptionsMenu(menu);
    }

    private void updateProgressBar() {

        if (menu != null) {
            MenuItem menuItem = menu.findItem(Resources.action_gpslocatorId);

            if (inProgress) {
                menuItem.setActionView(Resources.action_progressbarLayout);
                menuItem.expandActionView();
            } else {
                menuItem.collapseActionView();
                menuItem.setActionView(null);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(locationBroadcastReceiver, new IntentFilter(LocationBroadcastReceiver.NOTIFICATION));

        if(service != null)
            service.publishMostRecentLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(locationBroadcastReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == Resources.action_gpslocatorId)
        {
            LocationManagerClient.getInstance(this).requestLocationUpdate("");
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return false;
    }

    @Override
    public void updateLocationProgress(boolean inProgress) {
        this.inProgress = inProgress;
        updateProgressBar();
    }

    @Override
    public void LocationChanged(Location location) {
        googleMapView.updateLocation(location.getLatitude(), location.getLongitude());
    }

}