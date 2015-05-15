package no.bekk.wearworkshop.todoapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import no.bekk.wearworkshop.todoapp.domain.Item;


public class MainActivity extends AppCompatActivity {

    private RecyclerView list;

    private final List<Item> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText input = (EditText) findViewById(R.id.input);
        list = (RecyclerView) findViewById(R.id.list);
        list.setAdapter(new TodoListAdapter(this, items));
        list.setLayoutManager(new LinearLayoutManager(this));

        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String content = v.getText().toString();
                    v.getEditableText().clear();

                    Item item = new Item(content);
                    items.add(0, item);
                    list.getAdapter().notifyDataSetChanged();

                    return true;
                }
                return false;
            }
        });

    }

}
