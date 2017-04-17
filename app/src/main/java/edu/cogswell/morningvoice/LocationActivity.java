package edu.cogswell.morningvoice;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Christian on 4/15/2017.
 */

public class LocationActivity extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;


    @Override
    public void onConnected(Bundle connectionHint) {
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
                mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));

            }
        }
    }

}
