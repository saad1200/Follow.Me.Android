package com.cloudmediaplus.followme.views.map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapView{
    private GoogleMap map;
    private Marker currentLocationMarker;

    public GoogleMapView(GoogleMap map){

        this.map = map;
        map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        map.setMyLocationEnabled(false);
    }

    public void updateLocation(double latitude, double longitude) {
        LatLng coordinates = new LatLng(latitude, longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));

        MarkerOptions markerOptions = new MarkerOptions()
                .position(coordinates)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .title("Your Location");

        if(currentLocationMarker!=null)
            currentLocationMarker.remove();
        currentLocationMarker = map.addMarker(markerOptions);
    }
}