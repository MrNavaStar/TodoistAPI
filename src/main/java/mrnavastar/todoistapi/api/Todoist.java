package mrnavastar.todoistapi.api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Todoist {

    private final HttpClient client;
    private final String token;
    private final HashMap<String, Project> projects = new HashMap<>();
    private final HashMap<Long, Section> sections = new HashMap<>();
    private final HashMap<String, Label> labels = new HashMap<>();
    private final HashMap<Long, Task> tasks = new HashMap<>();

    public Todoist(String token) {
        this.client = HttpClient.newHttpClient();
        this.token = token;
    }

    private JsonElement get(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .setHeader("Authorization", "Bearer " + token)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return JsonParser.parseString(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void post(String url, HashMap<String, Object> properties) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(properties)))
                    .uri(URI.create(url))
                    .setHeader("Content-Type", "application/json")
                    .setHeader("X-Request-Id", UUID.randomUUID().toString())
                    .header("Authorization", "Bearer " + token)
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sync() {
        projects.clear();
        labels.clear();
        tasks.clear();

        JsonElement projectData = get("https://api.todoist.com/rest/v1/projects");
        JsonElement sectionData = get("https://api.todoist.com/rest/v1/sections");
        JsonElement labelData = get("https://api.todoist.com/rest/v1/labels");
        JsonElement taskData = get("https://api.todoist.com/rest/v1/tasks");

        if (projectData != null) {
            for (JsonElement p : projectData.getAsJsonArray()) {
                JsonObject data = p.getAsJsonObject();
                String name = data.get("name").getAsString();
                this.projects.put(name, new Project(this, name, data.get("id").getAsLong()));
            }
        }

        if (sectionData != null) {
            for (JsonElement s : sectionData.getAsJsonArray()) {
                JsonObject data = s.getAsJsonObject();
                long id = data.get("id").getAsLong();
                this.sections.put(id, new Section(data.get("name").getAsString(), id, data.get("project_id").getAsLong()));
            }
        }

        if (labelData != null) {
            for (JsonElement l : labelData.getAsJsonArray()) {
                JsonObject data = l.getAsJsonObject();
                String name = data.get("name").getAsString();
                this.labels.put(name, new Label(name, data.get("id").getAsLong()));
            }
        }

        if (taskData != null) {
            for (JsonElement t : taskData.getAsJsonArray()) {
                JsonObject data = t.getAsJsonObject();

                HashMap<String, Object> properties = new HashMap<>();
                properties.put("id", data.get("id").getAsLong());
                properties.put("project_id", data.get("project_id").getAsLong());
                properties.put("content", data.get("content").getAsString());
                properties.put("description", data.get("description").getAsString());
                properties.put("priority", data.get("priority").getAsInt());

                this.tasks.put((Long) properties.get("id"), new Task(this, properties));
            }
        }
    }

    public void commit() {
        tasks.forEach((id, task) -> {
            if (task.isDirty()) {
                if (task.id() == 0) post("https://api.todoist.com/rest/v1/tasks", task.getProperties());
                else post("https://api.todoist.com/rest/v1/tasks/" + task.id(), task.getProperties());
            }
        });
    }

    public ArrayList<Project> getProjects() {
        return new ArrayList<>(this.projects.values());
    }

    public Project getProject(String name) {
        return this.projects.get(name);
    }

    public ArrayList<Section> getSections() {
        return new ArrayList<>(this.sections.values());
    }

    public Section getSection(long projectId, String name) {
        for (Section section : sections.values()) {
            if (section.name().equals(name) && section.projectId() == projectId) return section;
        }
        return null;
    }

    public ArrayList<Label> getLabels() {
        return new ArrayList<>(this.labels.values());
    }

    public Label getLabel(String name) {
        return this.labels.get(name);
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(this.tasks.values());
    }

    public void addTaskToProject(long projectId, Task task) {
        task.setProjectId(projectId);
        tasks.put(task.id(), task);
    }

    public Task createTask(String title) {
        Task task = new Task(this, title);
        tasks.put(task.id(), task);
        return task;
    }
}