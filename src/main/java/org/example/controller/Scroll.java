package org.example.controller;

import org.example.view.Canvas;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class Scroll implements MouseWheelListener {
    private final Canvas canvas;

    public Scroll(Canvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int scrollAmount = e.getWheelRotation();
        int step = 30;
        canvas.setScrollOffsetY(canvas.getScrollOffsetY() + (scrollAmount * step));

        canvas.setScrollOffsetY(Math.max(0, Math.min(canvas.getScrollOffsetY(), canvas.getMaxScrollOffsetY())));

        canvas.repaint();
    }
}