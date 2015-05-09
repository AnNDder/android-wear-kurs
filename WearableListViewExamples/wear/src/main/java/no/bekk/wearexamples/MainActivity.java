package no.bekk.wearexamples;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements WearableListView.ClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener {
    private TextView headerView;
    private WearableListView listView;
    private List<Item> items = new ArrayList<Item>();
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
                headerView = (TextView) stub.findViewById(R.id.header);
                listView = (WearableListView) stub.findViewById(R.id.wearable_list);
                listView.setAdapter(new MyListAdapter(items, MainActivity.this));
                listView.setClickListener(MainActivity.this);
                sendMessage("/hello", "Hva skjer?");
                syncItems("/getItems");
            }
        });
    }

    private void syncItems(final String path) {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(path);
        putDataMapReq.getDataMap().putString("testKey", DateTime.now().toString());
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(googleApiClient, putDataReq);
        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult dataItemResult) {
                if (dataItemResult.getStatus().isSuccess()) {
                    Log.i("syncMessage", "SyncMessage successfully sent from caller");
                }
            }
        });
    }

    private void sendMessage(final String path, final String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
                Log.i("status", "Trying to send message");
                for (Node node : nodes.getNodes()) {
                    Log.i("NodeName", node.getDisplayName() + ", " + node.getId());
                    MessageApi.SendMessageResult result =
                            Wearable.MessageApi.sendMessage(googleApiClient, node.getId(),
                                    path, content.getBytes()).await();
                    if (result.getStatus().isSuccess()) {
                        Log.i("messageStatus", "Message successfully sent");
                    }
                    else {
                        Log.i("messageStatus", "Message nsuccessfully sent");
                    }
                }
            }
        }).start();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.i("wearActivity", "syncMessage received in WearActivity");
        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = dataEvent.getDataItem();
                if (item.getUri().getPath().equals("/getItemsResponse")) {
                    updateList(item);
                }
            }
        }
    }

    private void updateList(DataItem item) {
        DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
        ArrayList<DataMap> dataMapItems = dataMap.getDataMapArrayList("items");
        List<Item> items = new ArrayList<Item>();
        for (DataMap map : dataMapItems) {
            items.add(Item.fromDataMap(map));
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
    public void onClick(WearableListView.ViewHolder viewHolder) {
        Toast.makeText(this, "clicked row", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTopEmptyRegionClick() {
        Toast.makeText(this, "clicked top region", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("Connection", "Connected");
        Wearable.DataApi.addListener(googleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("Connection", "Suspended");
        Wearable.DataApi.removeListener(googleApiClient, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("Connection", "Failed");
        Wearable.DataApi.removeListener(googleApiClient, this);
    }
}
