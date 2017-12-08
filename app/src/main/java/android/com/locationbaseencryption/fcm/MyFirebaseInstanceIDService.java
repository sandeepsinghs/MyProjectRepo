package android.com.locationbaseencryption.fcm;

import android.com.locationbaseencryption.preferance.Shareprefrance;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private Shareprefrance shareprefrance;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "FCM Refreshed token: " + refreshedToken);
        shareprefrance = new Shareprefrance();
        shareprefrance.setFCMKey(MyFirebaseInstanceIDService.this, refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }
}