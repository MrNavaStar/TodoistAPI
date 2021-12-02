[![](https://jitpack.io/v/MrNavaStar/TodoistAPI.svg)](https://jitpack.io/#MrNavaStar/TodoistAPI)
[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://GitHub.com/Naereen/StrapDown.js/graphs/commit-activity)\
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)
[![MIT license](https://img.shields.io/badge/License-MIT-blue.svg)](https://lbesson.mit-license.org/)

# TodoistAPI
A well built Todoist v8 API wrapper for Java

# Usage
Include the project using jitpack:
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
  implementation 'com.github.MrNavaStar:TodoistAPI:v1.0'
}
```
You can set up the API like so:

```java
Todoist todoist = new todoist("token");

todoist.sync();

//Code here

todoist.commit();
```
\
Example usage:
```java
Project project = todoist.getProject("name");

for (Task task : project.getTasks()) {
    System.out.println(task.id());
}

Task task = new Task("title");
task.setDescription("description");
task.setPrioirity(3);

project.addTask(task);
```
