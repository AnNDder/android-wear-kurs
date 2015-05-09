package no.bekk.wearexamples;

import com.google.android.gms.wearable.DataMap;

public class Item {
    private String content;
    private String updatedDate;

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

    public DataMap toDataMap() {
        DataMap map = new DataMap();
        map.putString("content", content);
        map.putString("updatedDate", updatedDate);
        return map;
    }

    public static Item fromDataMap(DataMap map) {
        Item item = new Item();
        item.setContent(map.getString("content"));
        item.setUpdatedDate(map.getString("updatedDate"));
        return item;
    }
}
