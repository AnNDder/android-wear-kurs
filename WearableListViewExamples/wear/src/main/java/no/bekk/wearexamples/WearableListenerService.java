package no.bekk.wearexamples;

import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;

public class WearableListenerService extends com.google.android.gms.wearable.WearableListenerService {
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        if (messageEvent.getPath().equals("/new_item")) {
            Toast.makeText(this, new String(messageEvent.getData()), Toast.LENGTH_SHORT).show();
        }
    }
}
