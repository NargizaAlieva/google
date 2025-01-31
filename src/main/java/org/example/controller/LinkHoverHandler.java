package org.example.controller;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class LinkHoverHandler extends MouseMotionAdapter {
    private final Controller controller;

    public LinkHoverHandler(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (controller.isCursorOverLink(e.getX(), e.getY())) {
            controller.getViewer().setCursor(Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        } else {
            controller.getViewer().setCursor(Cursor.getDefaultCursor());
        }
    }
}
