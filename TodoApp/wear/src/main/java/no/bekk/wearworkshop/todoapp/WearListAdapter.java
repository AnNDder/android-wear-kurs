package no.bekk.wearworkshop.todoapp;

import android.content.Context;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import no.bekk.wearworkshop.todoapp.domain.Item;

public class WearListAdapter extends WearableListView.Adapter {

    private final Context context;
    private final List<Item> items;

    public WearListAdapter(final Context context, final List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        Item item = items.get(position);

        itemViewHolder.contentView.setText(item.getContent());
        itemViewHolder.imageView.setImageResource(
                item.isDone() ? R.drawable.ic_action_done : R.drawable.ic_action_attach);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ItemViewHolder extends WearableListView.ViewHolder {
        private final CircledImageView imageView;
        private final TextView contentView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            contentView = (TextView) itemView.findViewById(R.id.text);
            imageView = (CircledImageView) itemView.findViewById(R.id.image);
        }
    }
}
