package data;

import helpers.JSON.JSONParser;
import helpers.JSON.JSONSerializer;

import java.lang.reflect.Array;
import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Directory {
    ArrayList<Note> notes = new ArrayList<>();
    public ArrayList<Directory> directories = new ArrayList<>();
    public Path path;

    public Directory(String path) { // FIXME very inefficient, recursively walks entire tree on each instantiation
        this.path = Paths.get(path);
        try {
            Files.walk(this.path, 1)
                    .filter(Files::isDirectory)
                    .map(Path::toString)
                    .filter(dir -> !(dir.equals(path) || dir.equals(".")))
                    .map(Directory::new)
                    .forEach(directories::add);

        } catch (IOException e) {
            System.out.println("Error: " + e);
            throw new RuntimeException("Error getting directory tree: " + e);
        }
    }

    @Override
    public String toString() {
        return path.getFileName().toString();
    }

    private String serializeNotes() {
        return new JSONSerializer(notes.stream().map(Note::toMap).toList()).serialize();
    }

    private void loadNotes(String json) {
        ArrayList<Map<String, Object>> notesMap = (ArrayList<Map<String, Object>>) new JSONParser(json).parse();
        notes.clear();
        notesMap.stream().map(Note::fromMap).forEach(notes::add);
    }

    public void save() {
        if (notes.stream().allMatch(note -> note.text.isEmpty())) {
            try {
                Files.delete(path.resolve(".dirnotes"));
            } catch (IOException e) {
                System.out.println("Error: " + e);
                throw new RuntimeException("Error deleting .dirnotes: " + e);
            }
            return;
        }

        try {
            Files.writeString(path.resolve(".dirnotes"), serializeNotes());
        } catch (IOException e) {
            System.out.println("Error: " + e);
            throw new RuntimeException("Error saving notes: " + e);
        }
    }

    public void load() {
        try {
            loadNotes(Files.readString(path.resolve(".dirnotes")));
        } catch (IOException e) {
            if (e instanceof java.nio.file.NoSuchFileException) {
                notes.clear();
                return;
            }
            System.out.println("Error: " + e);
            throw new RuntimeException("Error loading notes: " + e);
        }
    }

    public String getFirstNote() {
        load();
        System.out.println(2);
        System.out.println(notes);
        if (!notes.isEmpty()) {
            return notes.getFirst().text;
        } else {
            return "";
        }
    }

    public void saveFirstNote(String text) {
        load();
        if (!notes.isEmpty()) {
            notes.getFirst().setText(text);
        } else {
            notes.add(new Note(
                    text,
                    new ArrayList<String>(),
                    new Date(),
                    new Date()
            ));
        }
        save();
    }
}
