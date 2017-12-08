package android.com.locationbaseencryption.retro;


import android.com.locationbaseencryption.Constant;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {

    @SerializedName(Constant.MOBILE)
    private String mobile;

    @SerializedName(Constant.USERID)
    private int userid;

    @SerializedName(Constant.EMAIL)
    private String email;

    @SerializedName(Constant.CITY)
    private String city;

    @SerializedName(Constant.ADHAAR_NUMBER)
    private String aadhar_number;

    @SerializedName(Constant.USERNAME)
    private String name;

    @SerializedName(Constant.RESULT)
    private String success;

    @SerializedName(Constant.ACCOUNT_NUMBER)
    private String account_number;


    public String getSuccess() {
        return success;
    }

    public String getMobile() {
        return mobile;
    }

    public int getUserid() {
        return userid;
    }

    public String getEmail() {
        return email;
    }

    public String getCity() {
        return city;
    }

    public String getName() {
        return name;
    }

    public String getAadhar_number() {
        return aadhar_number;
    }

    public String getAccount_number() {
        return account_number;
    }
}
