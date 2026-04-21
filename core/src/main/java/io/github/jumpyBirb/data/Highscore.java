package io.github.jumpyBirb.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Highscore {

    // Filen finns i assets
    private static final String FILE_NAME = "highscore.txt";

    public static class Entry {
        public String name;
        public int score;

        public Entry(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    public static void cleanHighScore() {
        try (FileWriter fw = new FileWriter(FILE_NAME, false)) {
            fw.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getFile() {
        FileHandle handle = Gdx.files.local(FILE_NAME);

        // Skapa filen om den inte finns
        if (!handle.exists()) {
            handle.writeString("", false);
        }

        return handle.file();
    }

    public static List<Entry> load() {
        List<Entry> list = new ArrayList<>();
        File file = getFile();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;

                String[] parts = line.split(";");
                if (parts.length == 2) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    list.add(new Entry(name, score));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void save(String name, int score) {
        List<Entry> list = load();
        list.add(new Entry(name, score));

        // Sortera listan fallande
        list.sort(Comparator.comparingInt(e -> -e.score));

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(getFile()))) {
            for (Entry e : list) {
                bw.write(e.name + ";" + e.score);
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Entry> top(int count) {
        List<Entry> list = load();
        list.sort(Comparator.comparingInt(e -> -e.score));

        if (list.size() > count)
            return list.subList(0, count);

        return list;
    }
}
