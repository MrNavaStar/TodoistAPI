package mrnavastar.todoistapi.api;

import java.util.ArrayList;
import java.util.HashMap;

public class Task {

    private final Todoist todoist;
    private boolean isDirty = false;
    private final ArrayList<Label> labels = new ArrayList<>();
    private final HashMap<String, Object> properties;

    public Task(Todoist todoist, String title) {
        this.todoist = todoist;
        this.properties = new HashMap<>();
        this.properties.put("content", title);
        this.properties.put("id", 0L);
    }

    public Task(Todoist todoist, HashMap<String, Object> properties) {
        this.todoist = todoist;
        this.properties = properties;
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    public Task addLabel(Label label) {
        this.isDirty = true;
        this.labels.add(label);
        return this;
    }

    public Task addLabel(String label) {
        addLabel(this.todoist.getLabel(label));
        return this;
    }

    public Task addLabels(ArrayList<Label> labels) {
        this.isDirty = true;
        this.labels.addAll(labels);
        return this;
    }

    public Task setProjectId(long id) {
        this.isDirty = true;
        this.properties.put("project_id", id);
        return this;
    }

    public Task setSection(Section section) {
        this.isDirty = true;
        this.properties.put("section_id", section.id());
        return this;
    }

    public Task setSection(String section) {
        setSection(this.todoist.getSection(this.projectId(), section));
        return this;
    }

    public Task setDescription(String description) {
        this.isDirty = true;
        this.properties.put("description", description);
        return this;
    }

    public Task setPriority(int priority) {
        this.isDirty = true;
        this.properties.put("priority", priority);
        return this;
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