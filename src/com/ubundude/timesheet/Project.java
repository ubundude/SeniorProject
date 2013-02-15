package com.ubundude.timesheet;

public class Project {
private long id;
private String project;

public long getId() {
return id;
}

public void setId(long id) {
this.id = id;
}

public String getProject() {
return project;
}

public void setProject(String project) {
this.project = project;
}

@Override
public String toString() {
return project;
}
}