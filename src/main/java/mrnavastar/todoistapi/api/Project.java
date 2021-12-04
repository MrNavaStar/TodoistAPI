package mrnavastar.todoistapi.api;

import java.util.ArrayList;
import java.util.HashMap;

public class Project {

    private final Todoist todoist;
    private final HashMap<String, Object> properties = new HashMap<>();

    public Project(Todoist todoist, String name, long id) {
        this.todoist = todoist;
        this.properties.put("name", name);
        this.properties.put("id", id);
    }

    public void addTask(Task task) {
        this.todoist.addTaskToProject(this.id(), task);
    }

    public Task createTask(String title) {
        Task task = new Task(this.todoist, title);
        addTask(task);
        return task;
    }

    public ArrayList<Task> getTasks() {
        long id = (Long) this.properties.get("id");
        ArrayList<Task> tasks = new ArrayList<>();
        this.todoist.getTasks().forEach(task -> {
            if (task.projectId() == id) tasks.add(task);
        });
        return tasks;
    }

    public HashMap<String, Object> getProperties() {
        return this.properties;
    }

    public String name() {
        return (String) this.properties.get("name");
    }

    public long id() {
        return (Long) this.properties.get("id");
    }
}
