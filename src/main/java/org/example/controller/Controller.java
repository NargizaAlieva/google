package org.example.controller;

import org.example.controller.commands.Command;
import org.example.controller.commands.SearchCommand;
import org.example.model.Model;
import org.example.view.Viewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class Controller implements ActionListener {
    private final Model model;
    private final Viewer viewer;
    private HashMap<String, Command> commandMap;

    public Controller(Viewer viewer) {
        this.viewer = viewer;
        model = new Model(viewer);
        setupCommands();
    }

    public Model getModel() {
        return model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        commandMap.get(actionCommand).execute();
    }

    private void setupCommands() {
        SearchCommand searchCommand = new SearchCommand(viewer, model);

        commandMap = new HashMap<>();
        commandMap.put("Search", searchCommand);
    }
}
