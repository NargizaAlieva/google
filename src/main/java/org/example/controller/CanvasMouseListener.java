package org.example.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CanvasMouseListener extends MouseAdapter {
    private final Controller controller;

    public CanvasMouseListener(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        controller.handleMouseClick(e);
    }
}
