package no.bekk.wearworkshop.todoapp.domain;

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
}
