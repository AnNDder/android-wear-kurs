package no.bekk.wearworkshop.todoapp;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import no.bekk.wearworkshop.todoapp.domain.Item;

import static java.util.Arrays.asList;

public class SharedPrefsHelper {

    private final Gson gson;
    private final SharedPreferences prefs;

    public SharedPrefsHelper(final SharedPreferences prefs) {
        this.prefs = prefs;
        this.gson = new GsonBuilder().create();
    }

    public List<Item> read() {
        return asList(gson.fromJson(prefs.getString("items", "[]"), Item[].class));
    }

    public void write(final List<Item> items) {
        prefs.edit()
                .putString("items", gson.toJson(items))
                .apply();
    }
}
