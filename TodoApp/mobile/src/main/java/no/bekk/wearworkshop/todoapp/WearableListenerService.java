package no.bekk.wearworkshop.todoapp;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

public class WearableListenerService extends com.google.android.gms.wearable.WearableListenerService {

    private GoogleApiClient googleApiClient;
    private SharedPrefsHelper sharedPrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        googleApiClient.connect();

        sharedPrefs = new SharedPrefsHelper(getSharedPreferences(MainActivity.SHARED_PREFS_NAME, Context.MODE_PRIVATE));
    }

    @Override
    public void onDestroy() {
        googleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        if (messageEvent.getPath().equals("/getItems")) {
            SyncHelper.syncItems(sharedPrefs.read(), googleApiClient);
        }
    }

}
