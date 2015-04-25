package no.bekk.wearexamples;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
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
                item.setContent(itemInput.getText().toString());
                item.setUpdatedDate(new DateTime());
                todoItems.add(0, item);
                todoItemList.getAdapter().notifyDataSetChanged();
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
}
