package no.bekk.wearexamples;

import android.content.Context;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MyListAdapter extends WearableListView.Adapter {

    private final List<Item> items;
    private final Context context;

    public MyListAdapter(List<Item> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WearableListView.ViewHolder(new ItemView(context));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
        Item item = items.get(position);
        ItemView view = (ItemView) holder.itemView;
        view.itemContentView.setText(item.getContent());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private final class ItemView extends FrameLayout implements WearableListView.OnCenterProximityListener {
        private Context context;
        public TextView itemContentView;
        public CircledImageView imageView;

        public ItemView(Context context) {
            super(context);
            this.context = context;
            LayoutInflater.from(context).inflate(R.layout.todo_item_layout, this, true);
            this.itemContentView = (TextView) findViewById(R.id.text);
            this.imageView = (CircledImageView) findViewById(R.id.image);
        }

        @Override
        public void onCenterPosition(boolean b) {
            itemContentView.animate().scaleX(1f).scaleY(1f).alpha(1);
            imageView.animate().scaleX(1f).scaleY(1f).alpha(1);
        }

        @Override
        public void onNonCenterPosition(boolean b) {
            itemContentView.animate().scaleX(0.8f).scaleY(0.8f).alpha(0.6f);
            imageView.animate().scaleX(0.8f).scaleY(0.8f).alpha(0.6f);
        }
    }
}
