package com.cloudmediaplus.followme.framework;

import android.content.Context;
import android.telephony.TelephonyManager;

public class PhoneService {
    private Context activity;

    public PhoneService(Context activity){
        this.activity = activity;
    }

    public String getMyNumber(){
        TelephonyManager tMgr = (TelephonyManager)activity.getSystemService(Context.TELEPHONY_SERVICE);
        String myPhoneNumber = tMgr.getLine1Number();
        return myPhoneNumber;
    }
}
