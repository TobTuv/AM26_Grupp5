package io.github.jumpyBirb.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Highscore {

    private static final String PREF_NAME = "neon-runner-highscore";
    private static final String KEY_SCORES = "scores";

    public static class Entry {
        public String name;
        public long score;

        public Entry(String name, long score) {
            this.name = name;
            this.score = score;
        }
    }

    private static Preferences prefs() {
        return Gdx.app.getPreferences(PREF_NAME);
    }

    public static void cleanHighScore() {
        prefs().putString(KEY_SCORES, "");
        prefs().flush();
    }

    public static List<Entry> load() {
        List<Entry> list = new ArrayList<Entry>();

        String data = prefs().getString(KEY_SCORES, "");

        if (data.trim().isEmpty()) {
            return list;
        }

        String[] lines = data.split("\\n");

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.split(";");

            if (parts.length == 2) {
                try {
                    String name = parts[0];
                    long score = Long.parseLong(parts[1].trim());
                    list.add(new Entry(name, score));
                } catch (NumberFormatException e) {
                    // Ignore broken score rows
                }
            }
        }

        list.sort(new Comparator<Entry>() {
            @Override
            public int compare(Entry a, Entry b) {
                return Long.compare(b.score, a.score);
            }
        });
        return list;
    }

    public static void save(String name, long score) {
        List<Entry> list = load();
        list.add(new Entry(name, score));

        list.sort(new Comparator<Entry>() {
            @Override
            public int compare(Entry a, Entry b) {
                return Long.compare(b.score, a.score);
            }
        });

        StringBuilder builder = new StringBuilder();

        for (Entry e : list) {
            builder.append(e.name)
                .append(";")
                .append(e.score)
                .append("\n");
        }

        prefs().putString(KEY_SCORES, builder.toString());
        prefs().flush();
    }

    public static List<Entry> top(int count) {
        List<Entry> list = load();

        if (list.size() > count) {
            return list.subList(0, count);
        }

        return list;
    }
}
