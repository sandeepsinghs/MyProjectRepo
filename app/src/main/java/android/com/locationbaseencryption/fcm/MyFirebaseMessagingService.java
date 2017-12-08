package android.com.locationbaseencryption.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.com.locationbaseencryption.Constant;
import android.com.locationbaseencryption.R;
import android.com.locationbaseencryption.activity.HomeActivity;
import android.com.locationbaseencryption.preferance.Shareprefrance;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    Context context = MyFirebaseMessagingService.this;
    Shareprefrance shareprefrance;

    Random random;
    int notify_id = 0;
    String notification_type;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(TAG, " FCM Message " + remoteMessage.getData() + " Other " + remoteMessage.getNotification().getBody() + " title " + remoteMessage.getNotification().getTitle());
        if (remoteMessage.getNotification().getBody().length() > 0) {

            sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
        }

        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "FCM Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private void sendNotification(String messageBody, String title) {

        Intent intent = new Intent(this, HomeActivity.class);
        Log.e("######", " Message for notification is as " + messageBody + " notifcation type values " + notification_type);
        intent.putExtra(Constant.BODY, messageBody);
        intent.putExtra(Constant.TITLE, title);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_encryption)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //ToDO For multiple notification setting random id
        random = new Random();
        notify_id = random.nextInt(1) + 1000;

        notificationManager.notify(notify_id /* ID of notification */, notificationBuilder.build());
    }
}