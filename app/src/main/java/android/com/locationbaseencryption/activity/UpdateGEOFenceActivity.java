package android.com.locationbaseencryption.activity;

import android.app.ProgressDialog;
import android.com.locationbaseencryption.Constant;
import android.com.locationbaseencryption.R;
import android.com.locationbaseencryption.preferance.Shareprefrance;
import android.com.locationbaseencryption.retro.GenralResponse;
import android.com.locationbaseencryption.retro.Retro;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UpdateGEOFenceActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //Location
    private static final int PERMISSION_REQUEST_CODE = 101;
    private static int UPDATE_INTERVAL1 = 3000; // 30 sec
    private static int FATEST_INTERVAL = 1500; // 15 sec
    private static int DISPLACEMENT = 10; // 10 meters
    double latitude;
    double longitude;
    ProgressDialog progressDialog;
    private Context context = UpdateGEOFenceActivity.this;
    private String TAG = UpdateGEOFenceActivity.class.getSimpleName();
    private Toolbar toolbar;
    private Button btn_update;
    private EditText et_latt, et_longt, et_geo_fence;
    private LocationManager locationManager;
    private Shareprefrance shareprefrance;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private String str_email = "";
    private boolean isUpdate = false;
    private String str_latt, str_longg, str_geo_fence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_geofence);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.location);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        str_email = getIntent().getStringExtra(Constant.EMAIL);
        isUpdate = getIntent().getBooleanExtra(Constant.ISUPDATE, false);

        et_longt = (EditText) findViewById(R.id.et_longt);
        et_latt = (EditText) findViewById(R.id.et_latt);
        et_geo_fence = (EditText) findViewById(R.id.et_geo_fence);
        btn_update = (Button) findViewById(R.id.btn_update);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_latt = et_latt.getText().toString();
                str_longg = et_longt.getText().toString();
                str_geo_fence = et_geo_fence.getText().toString();

                if (str_geo_fence.trim().length() != 0 && str_longg.trim().length() != 0 && str_latt.trim().length() != 0) {
                    if (isUpdate) {
                        updateGeoFence();
                    } else {
                        addGeoFence();
                    }
                }
            }
        });

        getLocation();

    }

    private void addGeoFence() {
        progressDialog.show();
        Retro.getInterface(context).AddGeofence(str_email, str_longg, str_latt, str_geo_fence, new Callback<GenralResponse>() {
            @Override
            public void success(GenralResponse genralResponse, Response response) {
                progressDialog.dismiss();
                if (genralResponse.getSuccess().equalsIgnoreCase("success")) {
                    finish();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
            }
        });

    }

    private void updateGeoFence() {
        progressDialog.show();
        Retro.getInterface(context).UpdateGeofence(str_email, str_latt, str_longg, str_geo_fence, new Callback<GenralResponse>() {
            @Override
            public void success(GenralResponse genralResponse, Response response) {
                progressDialog.dismiss();
                if (genralResponse.getSuccess().equalsIgnoreCase("success")) {
                    finish();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
            }
        });
    }

    private void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
            }

            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();

            }
        } else {
            showSettingsAlert();
        }

    }

    public void showSettingsAlert() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                .enableAutoManage(this, this)
                .addApi(LocationServices.API).build();

        Log.e(TAG, "building client for location updates");
    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {

        GoogleApiAvailability api = GoogleApiAvailability.getInstance();

        int resultCode = api.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (api.isUserResolvableError(resultCode)) {

            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

    /**
     * Creating location request object
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL1);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 10 meters

        Log.e(TAG, " Creating location requestCreating location request");

    }

    /*@Override
    protected void onResume() {
        super.onResume();
        //registerReceiver(statusReceiver,mIntent);
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, new IntentFilter("serviceData"));
    }*/


    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {

        Log.e(TAG, " Starting location updates");

        if (Build.VERSION.SDK_INT >= 23) {

            if (checkPermission()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
            } else {
                requestPermission();
            }
        } else {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (checkPermission()) {
                    startLocationUpdates();
                } else {
                    requestPermission();
                }

            } else {

            }

        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            Toast.makeText(this, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        et_longt.setText(longitude + "");
        et_latt.setText(latitude + "");

        Log.e(TAG, "location changed " + location.getLatitude() + "," + location.getLongitude());
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address obj = addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        updateLocation();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
