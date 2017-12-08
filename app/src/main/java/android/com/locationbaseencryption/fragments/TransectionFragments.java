package android.com.locationbaseencryption.fragments;

import android.com.locationbaseencryption.Constant;
import android.com.locationbaseencryption.R;
import android.com.locationbaseencryption.preferance.Shareprefrance;
import android.com.locationbaseencryption.retro.GenralResponse;
import android.com.locationbaseencryption.retro.OTPResponse;
import android.com.locationbaseencryption.retro.Retro;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TransectionFragments extends Fragment {

    String TAG = TransectionFragments.class.getSimpleName();
    int user_id = 0;
    String sadhar_number, sacc_number;
    private View view;
    private Button btn_send_money;
    private EditText et_cvv_number, et_expaire_date, et_card_number, et_amount, et_transection_type, et_acc_name, et_account_number;
    private String str_cvv, str_expire, str_card_number, str_amount, str_transection, str_acc_name, str_acc_number;
    private Shareprefrance shareprefrance;
    private double lat, log;
    private int str_otp = 0;
    AlertDialog alertDialog;

    public static TransectionFragments newInstance(double lat, double log) {
        TransectionFragments inboxFragments = new TransectionFragments();
        Bundle bundle = new Bundle();
        bundle.putDouble(Constant.LATITUDE, lat);
        bundle.putDouble(Constant.LONGTITUDE, log);
        inboxFragments.setArguments(bundle);
        return inboxFragments;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        lat = getArguments().getDouble(Constant.LATITUDE);
        log = getArguments().getDouble(Constant.LONGTITUDE);
        Log.e(TAG, " Latatitude " + lat + " and Longtitude " + log);

        return view = inflater.inflate(R.layout.fragments_transection, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        et_cvv_number = (EditText) view.findViewById(R.id.et_cvv_number);
        et_expaire_date = (EditText) view.findViewById(R.id.et_expaire_date);
        et_card_number = (EditText) view.findViewById(R.id.et_card_number);
        et_amount = (EditText) view.findViewById(R.id.et_amount);
        et_transection_type = (EditText) view.findViewById(R.id.et_transection_type);
        et_account_number = (EditText) view.findViewById(R.id.et_account_number);
        et_acc_name = (EditText) view.findViewById(R.id.et_acc_name);
        btn_send_money = (Button) view.findViewById(R.id.btn_send_money);


        shareprefrance = new Shareprefrance();
        user_id = shareprefrance.getUserID(getActivity());
        Log.e(TAG, " User id as follow " + user_id);

        sacc_number = shareprefrance.getAccountNumber(getActivity());
        sadhar_number = shareprefrance.getAdharNumber(getActivity());

        btn_send_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str_acc_name = et_acc_name.getText().toString();
                str_acc_number = et_account_number.getText().toString();
                str_amount = et_amount.getText().toString();
                str_card_number = et_card_number.getText().toString();
                str_cvv = et_cvv_number.getText().toString();
                str_transection = et_transection_type.getText().toString();
                str_expire = et_expaire_date.getText().toString();

                checkGeoFence();

                if (str_expire.trim().length() != 0 && str_transection.trim().length() != 0 && str_card_number.trim().length() != 0
                        && str_cvv.trim().length() != 0 && str_amount.trim().length() != 0 && str_acc_number.trim().length() != 0
                        && str_acc_name.trim().length() != 0) {

                    makeTransection();
                }
            }
        });

    }

    private void checkGeoFence() {

        Retro.getInterface(getActivity()).checkNear(user_id, new Callback<GenralResponse>() {
            @Override
            public void success(GenralResponse genralResponse, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }

    private void makeTransection() {

        Retro.getInterface(getActivity()).DoTransaction(sacc_number, str_acc_name, str_transection, str_amount, new Date().getTime() + "", str_acc_name, str_card_number, str_expire, str_cvv, new Callback<GenralResponse>() {
            @Override
            public void success(GenralResponse genralResponse, Response response) {
                getOTP();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }

    private void getOTP() {
        Retro.getInterface(getActivity()).GetOTP(sacc_number, str_acc_number, str_transection, str_amount, new Date().getTime() + "", new Callback<OTPResponse>() {
            @Override
            public void success(OTPResponse otpResponse, Response response) {
                str_otp = otpResponse.getOtp();
                Log.e(TAG, " OTP values as follow as " + str_otp);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void showOTPDialog() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_otp, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setTitle(R.string.otp_title);

        final EditText et_otp = (EditText) view.findViewById(R.id.et_otp);
        Button btn_otp_verify = (Button) view.findViewById(R.id.btn_otp_verify);

        alertDialog = builder.create();
        alertDialog.show();

        btn_otp_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int user_otp = Integer.parseInt(et_otp.getText().toString());
                if (str_otp == user_otp) {
                    alertDialog.dismiss();
                } else {
                    et_otp.setError("OTP is not valid");
                }
            }
        });
    }

}
