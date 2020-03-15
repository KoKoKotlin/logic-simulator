package me.kokokotlin.main.engine;

import me.kokokotlin.main.drawing.Drawable;
import me.kokokotlin.main.drawing.DrawingConstants;
import me.kokokotlin.main.drawing.DrawingPriorities;
import me.kokokotlin.main.drawing.MainWindow;
import me.kokokotlin.main.engine.connectios.ConnectionHandler;
import me.kokokotlin.main.engine.editor.SchematicManipulator;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class UpdateHandler implements Runnable, Drawable {

    private final List<Updateable> updateables = new CopyOnWriteArrayList<>();
    private final Thread updateThread = new Thread(this);
    private final ConnectionHandler connHandler = new ConnectionHandler();
    private final SchematicManipulator smc = new SchematicManipulator(this);

    private final double tickTime = 10D;
    private double accTickTime = 0D;

    public State currentState = State.VIEWING;
    private SchematicElement selected = null;

    public UpdateHandler() {
        updateThread.setName("update-01");
        updateThread.setDaemon(true);
        updateThread.start();
    }

    public void initSmc(MainWindow window) {
        window.addDrawable(smc);
        smc.disable();
        smc.setWindow(window);
    }

    public void addUpdateable(Updateable u) {
        updateables.add(u);
    }

    @Override
    public void draw(Graphics g) {
        final int fontSize = 20;

        Font f = new Font("Courier New", Font.BOLD, fontSize);
        g.setFont(f);

        String time = (int)(accTickTime) + "/" + (int)(tickTime);

        g.drawString(time,
                DrawingConstants.WINDOW_WIDTH - (g.getFontMetrics().stringWidth(time) + 5),
                DrawingConstants.WINDOW_HEIGHT - (fontSize + 5)
                );
    }

    @Override
    public DrawingPriorities getDrawPriority() {
        return DrawingPriorities.HIGH;
    }

    @Override
    public void run() {

        long lastLoopTime = System.nanoTime();
        final int TARGET_FPS = 60;
        final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
        long lastFpsTime = 0;

        while (true) {
            long now = System.nanoTime();
            long updateLength = now - lastLoopTime;
            lastLoopTime = now;
            double delta = updateLength / ((double) OPTIMAL_TIME);

            lastFpsTime += updateLength;
            if (lastFpsTime >= 1000000000) {
                lastFpsTime = 0;
            }

            tick(delta);

            long sleepTime = (lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000;

            try {
                Thread.sleep((sleepTime > 0) ? sleepTime : 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void tick(double delta) {
        accTickTime += delta;

        if(accTickTime >= tickTime) {
            accTickTime -= tickTime;

            updateElements();
        }

        handleState();
    }

    private void updateElements() {
        updateables.forEach(Updateable::propagate);
        connHandler.updateStates();
    }

    private void handleState() {
        switch(currentState) {
            case VIEWING: {
                smc.disable();
                smc.reset();
                break;
            }
            case EDITING: {
                smc.enable();
                break;
            }
        }
    }

    public List<Updateable> getUpdateables() {
        return updateables;
    }

    public ConnectionHandler getConnHandler() {
        return connHandler;
    }

    public SchematicManipulator getSmc() {
        return smc;
    }

    public void handleMouseClicked(MouseEvent e) {

        switch (currentState) {
            case VIEWING: {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    updateables.forEach(updateable -> updateable.onClick(e));
                } else {
                    Point p = e.getPoint();

                    if (e.getButton() == MouseEvent.BUTTON3) {

                        boolean foundSelected = false;
                        for (Updateable u : updateables) {
                            if (u instanceof SchematicElement) {
                                SchematicElement s = (SchematicElement) u;

                                if (s.getBounds().contains(p)) {
                                    s.setHighlight(true);
                                    selected = s;
                                    foundSelected = true;
                                } else {
                                    s.setHighlight(false);
                                }
                            }
                        }

                        if (!foundSelected) selected = null;
                        e.consume();
                    }
                }
                break;
            }
            case EDITING: {
                if(e.getButton() == MouseEvent.BUTTON1)
                    smc.finishCurrent();
                else if(e.getButton() == MouseEvent.BUTTON3) {
                    smc.reset();
                }
            }
        }
    }

    public void handleMouseMoved(Point p) {
        if(currentState == State.EDITING)
            smc.setPosition(p);
    }
}
