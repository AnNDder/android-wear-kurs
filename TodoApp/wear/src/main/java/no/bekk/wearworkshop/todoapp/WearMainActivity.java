package no.bekk.wearworkshop.todoapp;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import no.bekk.wearworkshop.todoapp.domain.Item;

public class WearMainActivity extends Activity {

    public static final String NOTIFICATION_ID_EXTRA = "NOTIFICATION_ID";

    private WearableListView listView;
    private List<Item> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                listView = (WearableListView) stub.findViewById(R.id.wearable_list);

                items.add(new Item("Test", false));
                items.add(new Item("Test2", true));


                listView.setAdapter(new WearListAdapter(WearMainActivity.this, items));
            }
        });

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(getIntent().getIntExtra(NOTIFICATION_ID_EXTRA, -1));
    }
}
