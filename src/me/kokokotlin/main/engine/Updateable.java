package me.kokokotlin.main.engine;

import java.awt.event.MouseEvent;

public interface Updateable {
    public void propagate();
    public void onClick(MouseEvent e);
}
