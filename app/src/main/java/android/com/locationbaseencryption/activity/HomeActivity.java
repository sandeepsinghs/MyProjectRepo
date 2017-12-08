package android.com.locationbaseencryption.activity;

import android.com.locationbaseencryption.R;
import android.com.locationbaseencryption.fragments.InboxFragments;
import android.com.locationbaseencryption.fragments.ProfileFragments;
import android.com.locationbaseencryption.fragments.TransectionFragments;
import android.com.locationbaseencryption.preferance.Shareprefrance;
import android.com.locationbaseencryption.retro.RegisterResponse;
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
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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

public class HomeActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //Location
    private static final int PERMISSION_REQUEST_CODE = 101;
    private static int UPDATE_INTERVAL1 = 3000; // 30 sec
    private static int FATEST_INTERVAL = 1500; // 15 sec
    private static int DISPLACEMENT = 10; // 10 meters
    double latitude;
    double longitude;
    private LocationManager locationManager;
    private Shareprefrance shareprefrance;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private Context context = HomeActivity.this;
    private String TAG = HomeActivity.class.getSimpleName();
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.home);
        setSupportActionBar(toolbar);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        selectedFragment = InboxFragments.newInstance();
                        toolbar.setTitle(R.string.home);
                        break;

                    case R.id.action_bank:
                        selectedFragment = TransectionFragments.newInstance(latitude, longitude);
                        toolbar.setTitle(R.string.transection);
                        break;

                    case R.id.action_profile:
                        selectedFragment = ProfileFragments.newInstance(latitude, longitude);
                        toolbar.setTitle(R.string.profile);
                        break;
                }

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
                return true;
            }
        });

        getLocation();

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
        Log.e(TAG, "location changed " + location.getLatitude() + "," + location.getLongitude());
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address obj = addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateLocation();
    }

    private void updateLocation() {
        Retro.getInterface(context).UserLocation("", latitude + "", longitude + "", new Callback<RegisterResponse>() {
            @Override
            public void success(RegisterResponse registerResponse, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
