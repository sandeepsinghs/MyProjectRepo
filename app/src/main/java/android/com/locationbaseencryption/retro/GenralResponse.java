package android.com.locationbaseencryption.retro;

import android.com.locationbaseencryption.Constant;

import com.google.gson.annotations.SerializedName;

public class GenralResponse {


    @SerializedName(Constant.RESULT)
    private String success;


    public String getSuccess() {
        return success;
    }


}
