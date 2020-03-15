package me.kokokotlin.main.listeners;

import me.kokokotlin.main.engine.UpdateHandler;

import java.awt.event.MouseEvent;

public class MouseMotionListener implements java.awt.event.MouseMotionListener {

    private final UpdateHandler updateHandler;

    public MouseMotionListener(UpdateHandler updateHandler) {
        this.updateHandler = updateHandler;
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        updateHandler.handleMouseMoved(e.getPoint());
    }
}
