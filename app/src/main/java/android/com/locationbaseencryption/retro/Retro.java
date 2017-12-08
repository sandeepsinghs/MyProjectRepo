package android.com.locationbaseencryption.retro;

import android.com.locationbaseencryption.Constant;
import android.com.locationbaseencryption.preferance.Shareprefrance;
import android.content.Context;
import android.util.Log;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public class Retro {

    private static final String Loginuser = "userLogin";
    private static final String UserRegistration = "UserRegistration";
    private static final String AddGeofence = "AddGeofence";
    private static final String UserLocation = "UserLocation";
    private static final String checkNear = "checkNear";
    private static final String GetOTP = "GetOTP";

    private static final String DoTransaction = "DoTransaction";
    private static final String VerifyOTP = "VerifyOTP";
    private static final String UpdateGeofence = "UpdateGeofence";
    private static final String DeleteGeofence = "DeleteGeofence";
    private static final String UpdatePassword = "UpdatePassword";


    public static String BASE_URL = "http://localhost:8080/VehicleMaintenance/rest/AppService/";

    public static RestAdapter getClient(Context context) {

        Shareprefrance shareprefrance = new Shareprefrance();
        BASE_URL = "http://" + shareprefrance.getServerURL(context) + "/VehicleMaintenance/rest/AppService/";

        Log.e("Retro", " BASE URL AS " + BASE_URL);

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        return adapter;
    }

    public static Retrointerface getInterface(Context context) {
        return getClient(context).create(Retrointerface.class);
    }

    public interface Retrointerface {

        @FormUrlEncoded
        @POST("/" + Loginuser)
        public void loginUser(
                @Field(Constant.EMAILID) String email,
                @Field(Constant.PASSWORD) String password,
                @Field(Constant.FCMID) String fcmdeviceid,

                Callback<RegisterResponse> response);

        @FormUrlEncoded
        @POST("/" + UserLocation)
        public void UserLocation(
                @Field(Constant.USERID) String userId,
                @Field(Constant.LATITUDE) String latitude,
                @Field(Constant.LONGTITUDE) String longitude,

                Callback<RegisterResponse> response);

        @FormUrlEncoded
        @POST("/" + UserRegistration)
        public void UserRegistration(
                @Field(Constant.USERNAME) String name,
                @Field(Constant.EMAILID) String email,
                @Field(Constant.MOBILENUMBER) String mobile,
                @Field(Constant.PASSWORD) String password,
                @Field(Constant.CITY) String city,
                @Field(Constant.GENDER) String gender,
                @Field(Constant.ACCNUMBER) String accNumber,
                @Field(Constant.ADHARNUMBER) String adharNumber,
                @Field(Constant.BALANCE) String balance,
                @Field(Constant.DOB) String dob,

                Callback<GenralResponse> response);

        @FormUrlEncoded
        @POST("/" + AddGeofence)
        public void AddGeofence(
                @Field(Constant.EMAILID) String email,
                @Field(Constant.LONGTITUDE) String longitude,
                @Field(Constant.LATITUDE) String latitude,
                @Field(Constant.RADIUS) String radius,

                Callback<GenralResponse> response);

        @FormUrlEncoded
        @POST("/" + checkNear)
        public void checkNear(
                @Field(Constant.USERID) int userId,
                Callback<GenralResponse> response);

        @FormUrlEncoded
        @POST("/" + GetOTP)
        public void GetOTP(
                @Field(Constant.SENDER_ACC_NO) String senderAccNo,
                @Field(Constant.RECIVER_ACC_NO) String receiverAccNo,
                @Field(Constant.TRANSECION_TYPE) String transactionType,
                @Field(Constant.AMOUNT) String amount,
                @Field(Constant.DATE) String date,

                Callback<OTPResponse> response);

        @FormUrlEncoded
        @POST("/" + DoTransaction)
        public void DoTransaction(
                @Field(Constant.SENDER_ACC_NO) String senderAccNo,
                @Field(Constant.RECIVER_ACC_NO) String receiverAccNo,
                @Field(Constant.TRANSECION_TYPE) String transactionType,
                @Field(Constant.AMOUNT) String amount,
                @Field(Constant.DATE) String date,
                @Field(Constant.ACC_HOLDER_NAME) String accHolderNm,
                @Field(Constant.CARD_NO) String cardNo,
                @Field(Constant.EXPIRDATE) String expireDt,
                @Field(Constant.CVV_NO) String cvvNo,

                Callback<GenralResponse> response);


        @FormUrlEncoded
        @POST("/" + VerifyOTP)
        public void VerifyOTP(
                @Field(Constant.OTP) String otp,
                @Field(Constant.SENDER_ACC_NO) String senderAccNo,
                @Field(Constant.RECIVER_ACC_NO) String receiverAccNo,
                @Field(Constant.TRANSECION_TYPE) String transactionType,
                @Field(Constant.AMOUNT) String amount,
                @Field(Constant.DATE) String date,

                Callback<GenralResponse> response);

        @FormUrlEncoded
        @POST("/" + UpdateGeofence)
        public void UpdateGeofence(
                @Field(Constant.EMAILID) String email,
                @Field(Constant.LATITUDE) String latitude,
                @Field(Constant.LONGTITUDE) String longitude,
                @Field(Constant.RADIUS) String radius,

                Callback<GenralResponse> response);

        @FormUrlEncoded
        @POST("/" + DeleteGeofence)
        public void DeleteGeofence(
                @Field(Constant.USERID) String userId,

                Callback<GenralResponse> response);

        @FormUrlEncoded
        @POST("/" + UpdatePassword)
        public void UpdatePassword(
                @Field(Constant.EMAILID) String email,
                @Field(Constant.PASSWORD) String password,

                Callback<GenralResponse> response);


    }
}
