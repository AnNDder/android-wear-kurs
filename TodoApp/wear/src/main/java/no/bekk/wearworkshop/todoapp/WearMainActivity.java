package no.bekk.wearworkshop.todoapp;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

import no.bekk.wearworkshop.todoapp.domain.Item;

public class WearMainActivity extends Activity implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, WearableListView.ClickListener {

    public static final String NOTIFICATION_ID_EXTRA = "NOTIFICATION_ID";

    private WearableListView listView;
    private List<Item> items = new ArrayList<>();
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                listView = (WearableListView) stub.findViewById(R.id.wearable_list);
                listView.setAdapter(new WearListAdapter(WearMainActivity.this, items));
                listView.setClickListener(WearMainActivity.this);
                sendPullRequest();
            }
        });

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(getIntent().getIntExtra(NOTIFICATION_ID_EXTRA, -1));
    }

    private void sendPullRequest() {
        PendingResult<NodeApi.GetConnectedNodesResult> nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient);
        nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                for (Node node : getConnectedNodesResult.getNodes()) {
                    Wearable.MessageApi.sendMessage(googleApiClient, node.getId(), "/getItems", new byte[0]);
                }
            }
        });

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for (DataEvent dataEvent : dataEventBuffer) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = dataEvent.getDataItem();
                if (item.getUri().getPath().equals("/updateItems")) {
                    updateList(item);
                }
            }
        }
    }

    private void updateList(DataItem item) {
        DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
        ArrayList<DataMap> dataMapItems = dataMap.getDataMapArrayList("items");
        List<Item> items = new ArrayList<>();
        for (DataMap dataMapItem : dataMapItems) {
            items.add(Item.fromDataMap(dataMapItem));
        }
        this.items.clear();
        this.items.addAll(items);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(googleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Wearable.DataApi.removeListener(googleApiClient, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Wearable.DataApi.removeListener(googleApiClient, this);
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/updateItems");

        Item clickedItem = items.get(viewHolder.getPosition());
        clickedItem.flipState();
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

    @Override
    public void onTopEmptyRegionClick() {

    }
}
