package no.bekk.wearworkshop.todoapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

import no.bekk.wearworkshop.todoapp.domain.Item;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;


public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener, ItemChangedListener {

    public static final String SHARED_PREFS_NAME = "todoList";

    private final List<Item> items = new ArrayList<>();
    private RecyclerView list;
    private SharedPrefsHelper sharedPrefs;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (RecyclerView) findViewById(R.id.list);
        list.setAdapter(new TodoListAdapter(this, items, this));
        list.setLayoutManager(new LinearLayoutManager(this));

        EditText input = (EditText) findViewById(R.id.input);
        input.setOnEditorActionListener(this);

        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        sharedPrefs = new SharedPrefsHelper(prefs);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
    }

    @Override
    public void itemChanged(int position) {
        items.get(position).flipState();
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
    protected void onResume() {
        super.onResume();
        loadItems();
    }

    @Override
    protected void onPause() {
        super.onPause();
        persistUnfinishedItems();
    }

    private void sendNotification(final String content) {
        PendingResult<NodeApi.GetConnectedNodesResult> nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient);
        nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                for (Node node : getConnectedNodesResult.getNodes()) {
                    Wearable.MessageApi.sendMessage(googleApiClient, node.getId(), "/notify", content.getBytes());
                }
            }
        });
    }

    private void persistUnfinishedItems() {
        List<Item> unfinishedItems = new ArrayList<>();
        for (Item item : items) {
            if (!item.isDone()) {
                unfinishedItems.add(item);
            }
        }
        sharedPrefs.write(unfinishedItems);
    }

    private void loadItems() {
        items.clear();
        items.addAll(sharedPrefs.read());
        list.getAdapter().notifyDataSetChanged();
    }

    @Override
    public boolean onEditorAction(TextView view, int action, KeyEvent event) {
        if (action == IME_ACTION_DONE) {
            String content = view.getText().toString();
            view.getEditableText().clear();

            Item item = new Item(content);
            items.add(0, item);
            list.getAdapter().notifyDataSetChanged();
            sendNotification(content);
            return true;
        }
        return false;
    }
}
