package mrnavastar.todoistapi.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.restlet.Client;
import org.restlet.data.*;
import org.restlet.resource.ClientResource;

import java.util.ArrayList;
import java.util.HashMap;

public class Todoist {

    private final Client client;
    private final ChallengeResponse authHeader;
    private final HashMap<String, Project> projects = new HashMap<>();
    private final HashMap<Long, Section> sections = new HashMap<>();
    private final HashMap<String, Label> labels = new HashMap<>();
    private final HashMap<Long, Task> tasks = new HashMap<>();

    public Todoist(String token) {
        this.client = new Client(Protocol.HTTP);
        this.authHeader = new ChallengeResponse(ChallengeScheme.HTTP_OAUTH_BEARER);
        authHeader.setRawValue(token);
        authHeader.setIdentifier("Bearer");
    }

    private JsonElement get(String url) {
        ClientResource clientResource = new ClientResource(url);
        clientResource.setChallengeResponse(this.authHeader);
        clientResource.setMethod(Method.GET);

        return JsonParser.parseString(client.handle(clientResource.createRequest()).getEntityAsText());
    }

    private void post(String url, HashMap<String, Object> properties) {
        ClientResource clientResource = new ClientResource(url);
        clientResource.setChallengeResponse(this.authHeader);
        clientResource.post(properties);
    }

    public void sync() {
        projects.clear();
        labels.clear();
        tasks.clear();

        JsonElement projectData = get("https://api.todoist.com/rest/v1/projects");
        JsonElement sectionData = get("https://api.todoist.com/rest/v1/sections");
        JsonElement labelData = get("https://api.todoist.com/rest/v1/labels");
        JsonElement taskData = get("https://api.todoist.com/rest/v1/tasks");


        for (JsonElement p : projectData.getAsJsonArray()) {
            JsonObject data = p.getAsJsonObject();
            String name = data.get("name").getAsString();
            this.projects.put(name, new Project(this, name, data.get("id").getAsLong()));
        }

        for (JsonElement s : sectionData.getAsJsonArray()) {
            JsonObject data = s.getAsJsonObject();
            long id = data.get("id").getAsLong();
            this.sections.put(id, new Section(data.get("name").getAsString(), id, data.get("project_id").getAsLong()));
        }

        for (JsonElement l : labelData.getAsJsonArray()) {
            JsonObject data = l.getAsJsonObject();
            String name = data.get("name").getAsString();
            this.labels.put(name, new Label(name, data.get("id").getAsLong()));
        }

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