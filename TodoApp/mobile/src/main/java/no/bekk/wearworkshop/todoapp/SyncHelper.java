package no.bekk.wearworkshop.todoapp;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

import no.bekk.wearworkshop.todoapp.domain.Item;

public class SyncHelper {

    public static void syncItems(List<Item> items, GoogleApiClient googleApiClient) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/updateItems");

        ArrayList<DataMap> datamaps = new ArrayList<>();
        for (Item item : items) {
            datamaps.add(item.toDataMap());
        }
        putDataMapRequest.getDataMap().putDataMapArrayList("items", datamaps);

        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(googleApiClient, putDataRequest);
        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult dataItemResult) {
                if (!dataItemResult.getStatus().isSuccess()) {
                    Log.w("syncItems", "Unable to sync items to wearable");
                }
            }
        });
    }
}
