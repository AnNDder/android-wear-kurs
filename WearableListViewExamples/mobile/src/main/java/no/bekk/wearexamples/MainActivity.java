package no.bekk.wearexamples;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
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
        populateTodoItems();
        todoItemList.setAdapter(new TodoListAdapter(this, todoItems));
        todoItemList.setLayoutManager(new LinearLayoutManager(this));

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Item item = new Item();
                String content = itemInput.getText().toString();
                item.setContent(content);
                item.setUpdatedDate(new DateTime());
                todoItems.add(0, item);
                todoItemList.getAdapter().notifyDataSetChanged();
                sendMessage("/new_item", content);
            }
        });

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
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

    private void populateTodoItems() {
        Item firstItem = new Item();
        firstItem.setContent("Kjøp Pepsi");
        firstItem.setUpdatedDate(new DateTime());
        Item secondItem = new Item();
        secondItem.setContent("Kjøp Cola");
        secondItem.setUpdatedDate(new DateTime());
        Item thirdItem = new Item();
        thirdItem.setContent("Kjøp Snus");
        thirdItem.setUpdatedDate(new DateTime());
        Item fourthItem = new Item();
        fourthItem.setContent("Kjøp Taco");
        fourthItem.setUpdatedDate(new DateTime());

        todoItems.add(firstItem);
        todoItems.add(secondItem);
        todoItems.add(thirdItem);
        todoItems.add(fourthItem);
    }

    private void sendMessage(final String path, final String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).    await();
                for (Node node : nodes.getNodes()) {
                    Log.i("NodeName", node.getDisplayName() + ", " + node.getId());
                    MessageApi.SendMessageResult result =
                            Wearable.MessageApi.sendMessage(googleApiClient, node.getId(),
                            path, content.getBytes()).await();
                    if (result.getStatus().isSuccess()) {
                        Log.i("status", "Successfully sent");
                    }
                    else {
                        Log.i("status", "Unsuccessfully sent");
                    }
                }
            }
        }).start();
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
}
