package no.bekk.wearexamples;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

public class WearableListenerService extends
        com.google.android.gms.wearable.WearableListenerService {
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
    public void onDestroy() {
        googleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        if (messageEvent.getPath().equals("/hello")) {
            Intent messageIntent = new Intent(Intent.ACTION_SEND);
            messageIntent.putExtra("message", new String(messageEvent.getData()));
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.i("service", "Event received in WearableListenerService");
        for (DataEvent dataEvent : dataEvents) {
            clearDataItemCache(dataEvent);
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = dataEvent.getDataItem();
                if (item.getUri().getPath().equals("/getItems")) {
                    SharedPreferences prefs = getSharedPreferences("todoItemList", Context.MODE_PRIVATE);
                    String itemsJson = StaticHelpers.read(prefs);
                    Item[] items = StaticHelpers.getGson().fromJson(itemsJson, Item[].class);
                    replyWithItemsToCaller(items);
                }
            }
        }
    }

    private void clearDataItemCache(DataEvent dataEvent) {
        Wearable.DataApi.deleteDataItems(googleApiClient, dataEvent.getDataItem().getUri());
    }

    private void replyWithItemsToCaller(Item[] items) {
        ArrayList<DataMap> dataMaps = new ArrayList<DataMap>();
        for (Item i : items) {
            dataMaps.add(i.toDataMap());
        }
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/getItemsResponse");
        putDataMapReq.getDataMap().putDataMapArrayList("items", dataMaps);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();

        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(googleApiClient, putDataReq);
        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult dataItemResult) {
                if (dataItemResult.getStatus().isSuccess()) {
                    Log.i("syncMessage", "SyncMessage successfully sent from service");
                }
                else {
                    Log.i("syncMessage", "Error sending syncMessage from service");
                }
            }
        });
    }
}