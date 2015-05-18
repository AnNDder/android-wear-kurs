package no.bekk.wearworkshop.todoapp.domain;

import com.google.android.gms.wearable.DataMap;

public class Item {
    private final String content;
    private boolean done = false;

    public Item(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public boolean isDone() {
        return done;
    }

    public void flipState() {
        done = !done;
    }

    public DataMap toDataMap() {
        DataMap map = new DataMap();
        map.putString("content", content);
        map.putBoolean("done", done);
        return map;
    }
}
