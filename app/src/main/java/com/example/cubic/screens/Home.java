package com.example.cubic.screens;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cubic.R;
import com.example.cubic.controllers.AuthController;
import com.example.cubic.controllers.BeaconController;
import com.example.cubic.controllers.UserController;
import com.google.android.gms.common.util.ArrayUtils;
import com.segment.analytics.Analytics;
import com.segment.analytics.Traits;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

import static com.example.cubic.constants.Beacon.BEACON_REGION_ENTER_EVENT;
import static com.example.cubic.constants.Beacon.BEACON_REGION_EXIT_EVENT;


public class Home extends AppCompatActivity implements BeaconConsumer {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private TextView mNameText, beaconStatus;
    private Button saveBtn;
    private EditText mName;

    private BeaconManager beaconManager;

    private AuthController authController;
    private UserController userController;
    private BeaconController beaconController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Casting
        mName = findViewById(R.id.home_name);
        mNameText = findViewById(R.id.home_name_text);
        saveBtn = findViewById(R.id.home_submitBtn);
        beaconStatus = findViewById(R.id.beacon_status);

        //Initialize
        authController = AuthController.getInstance();
        userController = UserController.getInstance();
        beaconController = BeaconController.getInstance();
    }

    private void checkAuthentication() {
        if(!authController.isLoggedIn()){
            startActivity(new Intent(this, Login.class));
            finish();
        } else {
            checkPermission();
            populateUser();
        }
    }

    public void populateUser(){
        userController.getUserName(new UserController.UserDataCallBack() {
            @Override
            public void onSuccess(String name) {
                if(name.equals(null) || name.equals("")){
                   mNameText.setVisibility(View.GONE);
                   mName.setVisibility(View.VISIBLE);
                   saveBtn.setVisibility(View.VISIBLE);
                } else {
                    mName.setVisibility(View.GONE);
                    mNameText.setVisibility(View.VISIBLE);
                    mNameText.setText("Hey, "+ name);
                    saveBtn.setVisibility(View.GONE);
                }
                //Send identity to Segment
                Analytics.with(getApplicationContext()).identify(new Traits().putName(name));
            }

            @Override
            public void onFailure(String err) {

            }
        });
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("Need Location Permission To Monitor Beacons Nearby");
                builder.setPositiveButton("OK",null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        requestPermissions(new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION }, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            } else {
                beaconManager = BeaconManager.getInstanceForApplication(this);
                beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
                beaconManager.bind(this);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (beaconManager != null) {
            beaconManager.unbind(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (beaconManager != null) {
            beaconManager.unbind(this);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthentication();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {

                // If Permission is Granted than its ok
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    beaconManager = BeaconManager.getInstanceForApplication(this);
                    beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
                    beaconManager.bind(this);
                }

                // If not Granted then alert the user by the message
                else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            checkPermission();
                        }
                    });
                    builder.show();
                }
                return;
            }
        }
    }


    @Override
    public void onBeaconServiceConnect() {
        final Region beaconRegion = new Region("my-beacon-region", null, null, null);

        beaconManager.removeAllMonitorNotifiers();
        beaconManager.removeAllRangeNotifiers();
        beaconManager.setForegroundScanPeriod(com.example.cubic.constants.Beacon.BEACON_SCANNER_INTERVAL);

        //Attach Custom Beacon Listener
        beaconController.setBeaconInterface(new BeaconController.CubicBeaconInterface() {
            @Override
            public void onExit(String beaconId) {
                beaconStatus.setText("Exit Beacon Region "+ beaconId);

                //Write Event To Database
                userController.writeUserEvent(BEACON_REGION_EXIT_EVENT, beaconId);

                //Send event to Segment
                Analytics.with(getApplicationContext()).track("Exit Beacon Region "+ beaconId);
            }

            @Override
            public void onEnter(String beaconId) {
                beaconStatus.setText("Entered Beacon Region "+ beaconId);

                //Write Event To Database
                userController.writeUserEvent(BEACON_REGION_ENTER_EVENT, beaconId);

                //Send event to Segment
                Analytics.with(getApplicationContext()).track("Entered Beacon Region "+ beaconId);
            }
        });

        //If user already within region start monitor beacons
        try {
            beaconManager.startRangingBeaconsInRegion(beaconRegion);
        } catch (RemoteException e) {
        }

        //Listen To Beacons Change Every 1 Seconds
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {

                //Clear Expired Beacon Updates
                beaconController.clearOutOfRegionBeacons();

                //Iterate All Captured Beacons
                for (Beacon b:collection){
                    String uuid = String.valueOf(b.getId1());

                    Log.e("Cubic", uuid);

                    //Filter Accepted Beacons
                    if(ArrayUtils.contains(com.example.cubic.constants.Beacon.BEACONS_IDS, uuid)) {
                        beaconController.addBeaconChange(b);
                    }
                }
            }
        });

    }

    public void onSavePressed(View view) {
        String name = mName.getText().toString();
        userController.writeNewUsername(name);
    }

    public void onLogoutPressed(View view) {
        authController.logOut();
        startActivity(new Intent(Home.this, Login.class));
        finish();
    }
}
