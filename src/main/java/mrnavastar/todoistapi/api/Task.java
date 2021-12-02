package mrnavastar.todoistapi.api;

import java.util.ArrayList;
import java.util.HashMap;

public class Task {

    private boolean isDirty = false;
    private final ArrayList<Label> labels = new ArrayList<>();
    private final HashMap<String, Object> properties;

    public Task(String title) {
        this.properties = new HashMap<>();
        this.properties.put("content", title);
        this.properties.put("id", 0L);
    }

    public Task(HashMap<String, Object> properties) {
        this.properties = properties;
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    public void addLabel(Label label) {
        this.isDirty = true;
        this.labels.add(label);
    }

    public void addLabels(ArrayList<Label> labels) {
        this.isDirty = true;
        this.labels.addAll(labels);
    }

    public void setProjectId(long id) {
        this.isDirty = true;
        this.properties.put("project_id", id);
    }

    public void setSection(Section section) {
        this.isDirty = true;
        this.properties.put("section_id", section.id());
    }

    public void setDescription(String description) {
        this.isDirty = true;
        this.properties.put("description", description);
    }

    public void setPriority(int priority) {
        this.isDirty = true;
        this.properties.put("priority", priority);
    }

    public HashMap<String, Object> getProperties() {
        ArrayList<Long> labelIds = new ArrayList<>();
        labels.forEach(label -> labelIds.add(label.id()));
        this.properties.put("label_ids", labelIds);
        return this.properties;
    }

    public long id() {
        return (long) this.properties.get("id");
    }

    public long projectId() {
        return (long) this.properties.get("project_id");
    }

    public long sectionId() {
        return (long) this.properties.get("section_id");
    }

    public String title() {
        return (String) this.properties.get("content");
    }

    public String description() {
        return (String) this.properties.get("description");
    }

    public int priority() {
        return (int) this.properties.get("priority");
    }
}