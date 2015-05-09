package no.bekk.wearexamples.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import no.bekk.wearexamples.R;
import no.bekk.wearexamples.domain.Item;
import no.bekk.wearexamples.listeners.ItemDoneListener;

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.TodoListViewHolder> {
    private final Context context;
    private final List<Item> items;
    private final ItemDoneListener itemDoneListener;

    public TodoListAdapter(Context context, List<Item> items, ItemDoneListener itemDoneListener) {
        this.context = context;
        this.items = items;
        this.itemDoneListener = itemDoneListener;
    }

    @Override
    public TodoListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.todo_list_item, viewGroup, false);
        final TodoListViewHolder todoListViewHolder = new TodoListViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = todoListViewHolder.getPosition();
                Item item = items.get(position);
                if (todoListViewHolder.doneImageView.getVisibility() == View.VISIBLE) {
                    todoListViewHolder.doneImageView.setVisibility(View.GONE);
                    item.setDone(false);
                }
                else {
                    todoListViewHolder.doneImageView.setVisibility(View.VISIBLE);
                    item.setDone(true);
                }
                itemDoneListener.itemHasChanged(item, position);
            }
        });
        return todoListViewHolder;
    }

    @Override
    public void onBindViewHolder(TodoListViewHolder todoListViewHolder, int i) {
        Item item = items.get(todoListViewHolder.getPosition());
        todoListViewHolder.contentView.setText(item.getContent());
        todoListViewHolder.dateView.setText(item.getUpdatedDate());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class TodoListViewHolder extends RecyclerView.ViewHolder {
        private final TextView contentView;
        private final TextView dateView;
        private final ImageView doneImageView;

        public TodoListViewHolder(View itemView) {
            super(itemView);

            contentView = (TextView) itemView.findViewById(R.id.content_view);
            dateView = (TextView) itemView.findViewById(R.id.date_view);
            doneImageView = (ImageView) itemView.findViewById(R.id.done_image);
        }
    }
}
