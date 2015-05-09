package no.bekk.wearexamples;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class StaticHelpers {
    public static Gson getGson() {
        GsonBuilder gsonb = new GsonBuilder();
        return gsonb.create();
    }

    public static void write(SharedPreferences prefs, List<Item> items) {
        Gson gson = getGson();
        String value = gson.toJson(items);
        SharedPreferences.Editor e = prefs.edit();
        e.putString("items", value);
        e.commit();
    }

    public static String read(SharedPreferences prefs) {
        return prefs.getString("items", null);
    }

    public static void delete(SharedPreferences prefs) {
        prefs.edit().clear().commit();
    }
}
