package me.kokokotlin.main.drawing;

import me.kokokotlin.main.engine.UpdateHandler;
import me.kokokotlin.main.engine.logicgates.NotGate;

import javax.swing.*;
import java.awt.*;

public class ToolBar extends JPanel {

    private final int HEIGHT = 40;

    // TODO: add picture to button
    // TODO: add connection button

    public ToolBar(UpdateHandler u) {
        Button addGate = new Button("Add Gate");
        addGate.setSize(new Dimension(HEIGHT, HEIGHT));

        addGate.addActionListener(e -> {
            NotGate n = new NotGate(0, 0);
            n.finish();
            u.getSmc().setElement(n);
        });

        add(addGate);
        setSize(DrawingConstants.WINDOW_WIDTH, HEIGHT);
    }
}
