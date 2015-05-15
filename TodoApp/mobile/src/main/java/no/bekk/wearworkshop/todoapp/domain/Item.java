package no.bekk.wearworkshop.todoapp.domain;

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
}
