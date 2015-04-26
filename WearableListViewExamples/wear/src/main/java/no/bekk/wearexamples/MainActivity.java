package no.bekk.wearexamples;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements WearableListView.ClickListener {
    private TextView headerView;
    private WearableListView listView;
    private List<Item> items = new ArrayList<Item>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        Item item1 = new Item();
        item1.setContent("Kjøp Pepsi Max");
        Item item2 = new Item();
        item2.setContent("Kjøp snus");
        Item item3 = new Item();
        item3.setContent("Kjøp Cola!");
        items.add(item1);
        items.add(item2);
        items.add(item3);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                headerView = (TextView) stub.findViewById(R.id.header);
                listView = (WearableListView) stub.findViewById(R.id.wearable_list);
                listView.setAdapter(new MyListAdapter(items, MainActivity.this));
                listView.setClickListener(MainActivity.this);
            }
        });

        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        LocalBroadcastManager.getInstance(this).registerReceiver(new MessageReceiver(), messageFilter);
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        Toast.makeText(this, "clicked row", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTopEmptyRegionClick() {
        Toast.makeText(this, "clicked top region", Toast.LENGTH_SHORT).show();
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, intent.getExtras().getString("message"), Toast.LENGTH_SHORT).show();
        }
    }
}
