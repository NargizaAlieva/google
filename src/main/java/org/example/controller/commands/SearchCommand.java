package org.example.controller.commands;

import org.example.model.Model;
import org.example.view.Viewer;

public class SearchCommand implements Command {
    private final Viewer viewer;
    private final Model model;
    public SearchCommand(Viewer viewer, Model model) {
        this.viewer = viewer;
        this.model = model;
    }

    @Override
    public void execute() {
        model.getHtml(viewer.getSiteUrl());
    }
}
