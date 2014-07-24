package com.cloudmediaplus.followme.views.messaging;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.cloudmediaplus.followme.Resources;

public class MessageReceivedActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(Resources.activity_resultLayout);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String message = extras.getString("payload");
            if (message != null && message.length() > 0) {
                TextView view = (TextView) findViewById(Resources.resultId);
                view.setText(message);
            }
        }

        super.onCreate(savedInstanceState);
    }

}