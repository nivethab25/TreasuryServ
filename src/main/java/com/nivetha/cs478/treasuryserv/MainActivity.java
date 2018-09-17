package com.nivetha.cs478.treasuryserv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Widgets to display status of service
    Button getStatus;
    TextView statusView ;
    private String mStatus = new String();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TextView to display Service status
        statusView = (TextView) findViewById(R.id.tvStatus);

        // Button which on clicking updates statusView
        getStatus = (Button) findViewById(R.id.btnStatus);
        getStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TreasuryServiceImpl.isBound) {
                    if(TreasuryServiceImpl.isIdle)
                        mStatus = "Bound to one or more clients but idle";
                    else
                        mStatus = "Bound to one or more clients and running an API method";
                }
                else
                    mStatus = "Not yet bound";

                if(TreasuryServiceImpl.isDestroyed)
                    mStatus = "Destroyed";

                statusView.setText(mStatus);
            }
        });



    }
}
