package ru.kelcuprum.pplhelper.api.components.project;

import ru.kelcuprum.pplhelper.api.PepeLandHelperAPI;

public class Page {
    public final int project;
    public final String id;
    public final String name;

    public Page(int project, String id, String name) {
        this.project = project;
        this.id = id;
        this.name = name == null || name.isEmpty() ? ("page #"+id) : name;
    }

    public String getContent(){
        return PepeLandHelperAPI.getProjectPageContent(project, id);
    }
}
