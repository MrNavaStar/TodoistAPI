package mrnavastar.todoistapi;

import mrnavastar.todoistapi.api.Project;
import mrnavastar.todoistapi.api.Todoist;

public class TodoistAPI {

    public static void main(String[] args) {
        Todoist todoist = new Todoist("869ccfdcb50f0c582237a6f32760936466574db0");
        todoist.sync();

        Project project = todoist.getProject("Admin");
        project.createTask("New Task").setSection("Issues").setPriority(4);

        todoist.commit();
    }
}
