package no.bekk.wearworkshop.todoapp;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

import no.bekk.wearworkshop.todoapp.domain.Item;

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

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem dataItem = dataEvent.getDataItem();
                if (dataItem.getUri().getPath().equals("/updateItems")) {
                    DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                    ArrayList<DataMap> dataMapItems = dataMap.getDataMapArrayList("items");
                    ArrayList<Item> items = new ArrayList<>();
                    for (DataMap dataMapItem : dataMapItems) {
                        items.add(Item.fromDataMap(dataMapItem));
                    }
                    sharedPrefs.write(items);
                }
            }
        }

    }
}
