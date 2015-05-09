package no.bekk.wearexamples;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import no.bekk.wearexamples.adapter.TodoListAdapter;
import no.bekk.wearexamples.domain.Item;
import no.bekk.wearexamples.listeners.ItemDoneListener;

import static java.util.Arrays.asList;


public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, ItemDoneListener,
        GoogleApiClient.OnConnectionFailedListener {
    private List<Item> todoItems = new ArrayList<Item>();
    private RecyclerView todoItemList;
    private EditText itemInput;
    private Button addButton;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        itemInput = (EditText) findViewById(R.id.item_input);
        addButton = (Button) findViewById(R.id.add_btn);
        todoItemList = (RecyclerView) findViewById(R.id.items_view);
        todoItemList.setAdapter(new TodoListAdapter(this, todoItems, this));
        todoItemList.setLayoutManager(new LinearLayoutManager(this));
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Item item = new Item();
                String content = itemInput.getText().toString();
                item.setContent(content);
                DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MM/dd/yyyy");
                item.setUpdatedDate(dtfOut.print(new DateTime()));
                todoItems.add(0, item);
                todoItemList.getAdapter().notifyDataSetChanged();
                storeNewItem(item);
            }
        });
        // Receive messages broadcast from the WearableListenerService
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        LocalBroadcastManager.getInstance(this).registerReceiver(new MessageReceiver(), messageFilter);
        prePopulateList();
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("Message", "Received message " + intent.getExtras().getString("message"));
        }
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
        Log.i("Connection", "Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("Connection", "Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("Connection", "Failed");
    }

    private void populateTodoItems() {
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MM/dd/yyyy");
        Item firstItem = new Item();
        firstItem.setContent("Kjøp Pepsi");
        firstItem.setUpdatedDate(dtfOut.print(new DateTime()));
        Item secondItem = new Item();
        secondItem.setContent("Kjøp Cola");
        secondItem.setUpdatedDate(dtfOut.print(new DateTime()));
        Item thirdItem = new Item();
        thirdItem.setContent("Kjøp Snus");
        thirdItem.setUpdatedDate(dtfOut.print(new DateTime()));
        Item fourthItem = new Item();
        fourthItem.setContent("Kjøp Taco");
        fourthItem.setUpdatedDate(dtfOut.print(new DateTime()));

        todoItems.add(firstItem);
        todoItems.add(secondItem);
        todoItems.add(thirdItem);
        todoItems.add(fourthItem);
    }

    private void syncItems(final String path) {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(path);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(googleApiClient, putDataReq);
        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult dataItemResult) {
                if (dataItemResult.getStatus().isSuccess()) {
                    Log.i("itemChangedMessage", "itemChangedMessage successfully sent from handheld activity");
                }
            }
        });
    }

    @Override
    public void itemHasChanged(Item item, int index) {
        todoItems.get(index).setDone(item.isDone());
        SharedPreferences prefs = getSharedPreferences("todoItemList", Context.MODE_PRIVATE);
        StaticHelpers.write(prefs, todoItems);
        syncItems("/itemsHaveChanged");
    }

    private void storeNewItem(Item item) {
        SharedPreferences prefs = getSharedPreferences("todoItemList", Context.MODE_PRIVATE);
        Gson gson = StaticHelpers.getGson();
        todoItems.clear();
        todoItems.addAll(asList(gson.fromJson(StaticHelpers.read(prefs), Item[].class)));
        todoItems.add(0, item);
        StaticHelpers.write(prefs, todoItems);
    }

    private void prePopulateList() {
        SharedPreferences prefs = getSharedPreferences("todoItemList", Context.MODE_PRIVATE);
        StaticHelpers.delete(prefs);
        todoItems.clear();
        populateTodoItems();
        StaticHelpers.write(prefs, todoItems);
    }
}
