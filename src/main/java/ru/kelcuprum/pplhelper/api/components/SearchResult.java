package ru.kelcuprum.pplhelper.api.components;

import java.util.ArrayList;

public record  SearchResult(ArrayList<ru.kelcuprum.pplhelper.api.components.project.Project> arrayList, int pages) {
}
