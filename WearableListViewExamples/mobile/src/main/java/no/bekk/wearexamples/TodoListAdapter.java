package no.bekk.wearexamples;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.TodoListViewHolder> {
    private final Context context;
    private final List<Item> items;

    public TodoListAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public TodoListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.todo_list_item, viewGroup, false);
        return new TodoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TodoListViewHolder todoListViewHolder, int i) {
        Item item = items.get(todoListViewHolder.getPosition());
        todoListViewHolder.contentView.setText(item.getContent());
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MM/dd/yyyy");
        todoListViewHolder.dateView.setText(dtfOut.print(item.getUpdatedDate()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class TodoListViewHolder extends RecyclerView.ViewHolder {
        private final TextView contentView;
        private final TextView dateView;

        public TodoListViewHolder(View itemView) {
            super(itemView);

            contentView = (TextView) itemView.findViewById(R.id.content_view);
            dateView = (TextView) itemView.findViewById(R.id.date_view);
        }
    }
}
