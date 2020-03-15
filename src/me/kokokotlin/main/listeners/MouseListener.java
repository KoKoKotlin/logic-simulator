package me.kokokotlin.main.listeners;

import me.kokokotlin.main.engine.UpdateHandler;

import java.awt.event.MouseEvent;

public class MouseListener implements java.awt.event.MouseListener {

    private final UpdateHandler updateHandler;

    public MouseListener(UpdateHandler updateHandler) {
        this.updateHandler = updateHandler;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        updateHandler.handleMouseClicked(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
