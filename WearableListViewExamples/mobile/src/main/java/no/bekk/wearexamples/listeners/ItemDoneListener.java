package no.bekk.wearexamples.listeners;

import no.bekk.wearexamples.domain.Item;

public interface ItemDoneListener {
    void itemHasChanged(Item item, int index);
}
