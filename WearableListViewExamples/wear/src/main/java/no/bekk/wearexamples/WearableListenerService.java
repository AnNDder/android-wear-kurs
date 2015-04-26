package no.bekk.wearexamples;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.wearable.MessageEvent;

public class WearableListenerService extends com.google.android.gms.wearable.WearableListenerService {
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        if (messageEvent.getPath().equals("/new_item")) {
            Intent messageIntent = new Intent(Intent.ACTION_SEND);
            messageIntent.putExtra("message", new String(messageEvent.getData()));
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        }
    }
}
