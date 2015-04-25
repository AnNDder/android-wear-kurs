package no.bekk.wearexamples;

import org.joda.time.DateTime;

public class Item {
    private String content;
    private DateTime updatedDate;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public DateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(DateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
}
