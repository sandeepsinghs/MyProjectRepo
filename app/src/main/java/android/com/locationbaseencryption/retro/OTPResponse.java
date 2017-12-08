package android.com.locationbaseencryption.retro;

import android.com.locationbaseencryption.Constant;

import com.google.gson.annotations.SerializedName;

public class OTPResponse {


    @SerializedName(Constant.RESULT)
    private String success;

    @SerializedName(Constant.OTP)
    private int otp;

    public String getSuccess() {
        return success;
    }

    public int getOtp() {
        return otp;
    }

}
