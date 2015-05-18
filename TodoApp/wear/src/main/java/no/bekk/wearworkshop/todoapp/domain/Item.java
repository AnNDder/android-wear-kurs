package no.bekk.wearworkshop.todoapp.domain;

import com.google.android.gms.wearable.DataMap;

public class Item {
    private String content;
    private boolean done;

    public Item(final String content, final boolean done) {
        this.content = content;
        this.done = done;
    }

    public String getContent() {
        return content;
    }

    public boolean isDone() {
        return done;
    }

    public static Item fromDataMap(DataMap dataMapItem) {
        return new Item(dataMapItem.getString("content"), dataMapItem.getBoolean("done"));
    }
}
