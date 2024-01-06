package data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class Note {
    String text;
    final ArrayList<String> tags;
    final Date dateCreated; // FIXME actually use this, currently just overwrites
    Date dateModified;

    public Note(
            String text,
            ArrayList<String> tags,
            Date dateCreated,
            Date dateModified
    ) {
        this.text = text;
        this.tags = tags;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
    }

    public void setText(String text) {
        this.text = text;
        this.dateModified = new Date();
    }

    public Map<String, Object> toMap() {
        return Map.of(
                "text", text,
                "tags", tags,
                "dateCreated", dateCreated.toString(),
                "dateModified", dateModified.toString()
        );
    }

    public static Note fromMap(Map<String, Object> map) {
        return new Note(
                (String) map.get("text"),
                (ArrayList<String>) map.get("tags"),
                new Date((String) map.get("dateCreated")),
                new Date((String) map.get("dateModified"))
        );
    }
}
