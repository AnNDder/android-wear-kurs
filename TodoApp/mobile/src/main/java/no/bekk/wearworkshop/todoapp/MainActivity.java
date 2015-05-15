package no.bekk.wearworkshop.todoapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import no.bekk.wearworkshop.todoapp.domain.Item;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;


public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener, ItemChangedListener {

    public static final String SHARED_PREFS_NAME = "todoList";
    private static final int NOTIFICATION_ID = 1;

    private final List<Item> items = new ArrayList<>();
    private RecyclerView list;
    private SharedPrefsHelper sharedPrefs;

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
    }

    @Override
    public void itemChanged(int position) {
        items.get(position).flipState();
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

    private void sendNotification(String content) {
        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                        .setHintHideIcon(true);

        Intent viewIntent = new Intent(this, MainActivity.class);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_action_done)
                        .setContentTitle("TODO-Liste")
                        .setContentText(content)
                        .setAutoCancel(true)
                        .extend(wearableExtender)
                        .setContentIntent(viewPendingIntent);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
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
