package com.cloudmediaplus.followme.services.messaging;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import com.cloudmediaplus.followme.framework.PhoneService;
import com.cloudmediaplus.followme.framework.DeviceNotifier;
import com.cloudmediaplus.followme.framework.PostMan;
import com.cloudmediaplus.followme.services.location.LocationManagerClient;
import org.apache.http.NameValuePair;

import java.util.ArrayList;

public class MessageReceiverService extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if ("com.google.android.c2dm.intent.RECEIVE".equals(action)) {
            DeviceNotifier notifier = new DeviceNotifier(context);
            notifier.notify("Location Requested", "");
            String phoneNumber = new PhoneService(context).getMyNumber();
            LocationManagerClient.getInstance(context).requestLocationUpdate(phoneNumber);
            sendRequestConfirmation(phoneNumber);
        }

    }

    private void sendRequestConfirmation(String phoneNumber) {
        PostMan postman = new PostMan();
        String url = "http://www.almogtarbeen.com/Follow/LocationRequestReceived/" + phoneNumber;
        postman.post(url, new ArrayList<NameValuePair>());
    }
}
