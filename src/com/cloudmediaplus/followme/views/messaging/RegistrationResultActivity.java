package com.cloudmediaplus.followme.views.messaging;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.cloudmediaplus.followme.Resources;

public class RegistrationResultActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(Resources.activity_resultLayout);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String registrationId = extras.getString("registration_id");
            if (registrationId != null && registrationId.length() > 0) {
                TextView view = (TextView) findViewById(Resources.resultId);
                view.setText(registrationId);
            }
        }

        super.onCreate(savedInstanceState);
    }
}