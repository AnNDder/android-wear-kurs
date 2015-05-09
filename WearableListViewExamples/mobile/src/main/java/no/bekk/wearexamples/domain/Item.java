package no.bekk.wearexamples.domain;

import com.google.android.gms.wearable.DataMap;

public class Item {
    private String content;
    private String updatedDate;
    private boolean done = false;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public DataMap toDataMap() {
        DataMap map = new DataMap();
        map.putString("content", content);
        map.putString("updatedDate", updatedDate);
        map.putBoolean("done", done);
        return map;
    }
}
