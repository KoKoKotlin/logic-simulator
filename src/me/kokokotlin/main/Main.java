package me.kokokotlin.main;

import me.kokokotlin.main.engine.editor.SchematicManipulator;
import me.kokokotlin.main.listeners.MouseListener;
import me.kokokotlin.main.drawing.MainWindow;
import me.kokokotlin.main.engine.UpdateHandler;
import me.kokokotlin.main.listeners.MouseMotionListener;

/*
    TODO: make editor
    TODO: fix padding
 */

public class Main {
    public static void main(String args[]) {
        UpdateHandler updateHandler = new UpdateHandler();
        MainWindow mainWindow = new MainWindow(updateHandler);

        updateHandler.initSmc(mainWindow);

        mainWindow.addMouseListener(new MouseListener(updateHandler));
        mainWindow.addMouseMotionListener(new MouseMotionListener(updateHandler));
        mainWindow.addDrawable(updateHandler);

        mainWindow.start();
    }
}
