package no.bekk.wearworkshop.todoapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

public class WearableListenerService extends com.google.android.gms.wearable.WearableListenerService {

    public static final String NOTIFY_PATH = "/notify";
    private GoogleApiClient googleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        if (messageEvent.getPath().equals(NOTIFY_PATH)) {
            String content = new String(messageEvent.getData());

            int notificationId = 1;
            Intent viewIntent = new Intent(this, WearMainActivity.class);
            viewIntent.putExtra(WearMainActivity.NOTIFICATION_ID_EXTRA, notificationId);
            PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, PendingIntent.FLAG_CANCEL_CURRENT);


            NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_action_done, getString(R.string.notification_open), viewPendingIntent);

            NotificationCompat.WearableExtender wearableExtender =
                    new NotificationCompat.WearableExtender()
                            .addAction(action)
                            .setHintHideIcon(true);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_action_done)
                            .setContentTitle(getString(R.string.notification_title))
                            .setContentText(content)
                            .setAutoCancel(true)
                            .extend(wearableExtender);

            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(this);

            notificationManager.notify(notificationId, notificationBuilder.build());
        }
    }

    @Override
    public void onDestroy() {
        googleApiClient.disconnect();
        super.onDestroy();
    }
}
