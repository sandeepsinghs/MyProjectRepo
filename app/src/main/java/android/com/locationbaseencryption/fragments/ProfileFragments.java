package android.com.locationbaseencryption.fragments;

import android.com.locationbaseencryption.Constant;
import android.com.locationbaseencryption.R;
import android.com.locationbaseencryption.activity.LoginActivity;
import android.com.locationbaseencryption.activity.UpdateGEOFenceActivity;
import android.com.locationbaseencryption.preferance.Shareprefrance;
import android.com.locationbaseencryption.retro.GenralResponse;
import android.com.locationbaseencryption.retro.Retro;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProfileFragments extends Fragment {

    View view;
    TextView tv_user_name, tv_user_email, tv_user_mobile, tv_user_address;
    Shareprefrance shareprefrance;
    Button bttn_logout, bttn_delete_geofence,bttn_update_geofence;
    String TAG = ProfileFragments.class.getSimpleName();
    private double lat, log;

    public static ProfileFragments newInstance(double lat, double log) {
        ProfileFragments profileFragments = new ProfileFragments();
        Bundle bundle = new Bundle();
        bundle.putDouble(Constant.LATITUDE, lat);
        bundle.putDouble(Constant.LONGTITUDE, log);
        profileFragments.setArguments(bundle);
        return profileFragments;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        lat = getArguments().getDouble(Constant.LATITUDE);
        log = getArguments().getDouble(Constant.LONGTITUDE);
        Log.e(TAG, " Lat values in profile " + lat + " and Long " + log);

        return view = inflater.inflate(R.layout.fragments_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tv_user_address = (TextView) view.findViewById(R.id.tv_user_address);
        tv_user_mobile = (TextView) view.findViewById(R.id.tv_user_mobile);
        tv_user_email = (TextView) view.findViewById(R.id.tv_user_email);
        tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
        bttn_logout = (Button) view.findViewById(R.id.bttn_logout);
        bttn_delete_geofence = (Button) view.findViewById(R.id.bttn_delete_geofence);
        bttn_update_geofence = (Button) view.findViewById(R.id.bttn_update_geofence);

        shareprefrance = new Shareprefrance();

        bttn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareprefrance.clear(getContext());
                startActivity(new Intent(getContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                getActivity().finish();
            }
        });

        try {
            tv_user_email.setText(shareprefrance.getEmail(getContext()));
            tv_user_mobile.setText(shareprefrance.getPhone(getContext()));
            tv_user_name.setText(shareprefrance.getName(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        bttn_delete_geofence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteGeoFence();
            }
        });

        bttn_update_geofence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), UpdateGEOFenceActivity.class).putExtra(Constant.EMAIL,shareprefrance.getEmail(getActivity())).putExtra(Constant.ISUPDATE,true));
            }
        });

    }

    private void deleteGeoFence() {
        Retro.getInterface(getActivity()).DeleteGeofence(shareprefrance.getUserID(getActivity()) + "", new Callback<GenralResponse>() {
            @Override
            public void success(GenralResponse genralResponse, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

}
