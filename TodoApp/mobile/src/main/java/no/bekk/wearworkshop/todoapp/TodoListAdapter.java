package no.bekk.wearworkshop.todoapp;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import no.bekk.wearworkshop.todoapp.domain.Item;

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.TodoListViewHolder> {

    private final Context context;
    private final List<Item> items;

    public TodoListAdapter(final Context context, final List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public TodoListViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
        final TodoListViewHolder viewHolder = new TodoListViewHolder(view);
        viewHolder.stateView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int oldPaintFlags = viewHolder.contentView.getPaintFlags();
                int newPaintFlags = isChecked ? oldPaintFlags | Paint.STRIKE_THRU_TEXT_FLAG : oldPaintFlags & (~Paint.STRIKE_THRU_TEXT_FLAG);
                viewHolder.contentView.setPaintFlags(newPaintFlags);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TodoListViewHolder viewHolder, final int position) {
        Item item = items.get(position);
        viewHolder.contentView.setText(item.getContent());
        viewHolder.stateView.setChecked(item.isDone());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class TodoListViewHolder extends RecyclerView.ViewHolder {
        private final TextView contentView;
        private final CheckBox stateView;

        public TodoListViewHolder(View itemView) {
            super(itemView);

            contentView = (TextView) itemView.findViewById(R.id.content_view);
            stateView = (CheckBox) itemView.findViewById(R.id.done_checkbox);
        }
    }
}
