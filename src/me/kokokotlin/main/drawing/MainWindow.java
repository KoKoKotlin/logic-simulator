package me.kokokotlin.main.drawing;

import me.kokokotlin.main.engine.SchematicElement;
import me.kokokotlin.main.engine.State;
import me.kokokotlin.main.engine.UpdateHandler;
import me.kokokotlin.main.io.CircuitLoader;
import me.kokokotlin.main.io.CircuitSaver;
import me.kokokotlin.main.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static me.kokokotlin.main.drawing.DrawingConstants.*;

public class MainWindow extends Canvas implements Runnable {

    private final JFrame window;
    private final Thread drawingThread;
    private List<Drawable> drawables = new CopyOnWriteArrayList<>();

    private boolean drawablesDirty = false;
    private final String TITLE = "Logic Simulator";

    private final ToolBar toolBar;
    private final UpdateHandler updateHandler;

    public MainWindow(UpdateHandler u) {
        this.updateHandler = u;

        toolBar = new ToolBar(u);

        window = new JFrame(TITLE);
        buildWindow(u);

        drawingThread = new Thread(this);
        buildThread();
    }

    private void buildWindow(UpdateHandler u) {
        window.setResizable(true);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel();
        contentPane.add(this);

        // TODO: add proper size of menu bar
        Dimension dim = new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT);
        setSize(dim);
        setBackground(new Color(40,100,150));

        buildMenu(u);

        window.setContentPane(contentPane);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.add(toolBar);

        createBufferStrategy(2);
    }

    private void buildThread() {
        drawingThread.setName("drawing-01");
        drawingThread.setDaemon(true);
    }

    private void buildMenu(UpdateHandler u) {
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("File");

        JCheckBoxMenuItem editingMode = new JCheckBoxMenuItem("Editing");
        editingMode.addActionListener(e -> {
            if(editingMode.getState()) enterEditing(u);
            else exitEditing(u);
        });
        file.add(editingMode);

        // TODO: implement save schematic
        // TODO: implement save as
        //        --> implement save path after load and reset all before load

        JMenuItem save = new JMenuItem("Save");
        file.add(save);

        JMenuItem saveAs = new JMenuItem("Save As");
        saveAs.addActionListener(e -> saveCircuitAs());
        file.add(saveAs);

        JMenuItem loadFromFile = new JMenuItem("Load From File");
        loadFromFile.addActionListener(e -> loadSchematicFromFile());
        file.add(loadFromFile);

        JMenuItem quit = new JMenuItem("Quit");
        quit.addActionListener(e -> System.exit(0));
        file.add(quit);

        menuBar.add(file);

        window.setJMenuBar(menuBar);
    }

    public void start() {
        drawingThread.start();
    }

    public void addDrawable(Drawable d) {
        drawables.add(d);
        drawablesDirty = true;
    }

    private void onDrawablesDirty() {
        // after a new drawable has been added sort the list new
        drawables = drawables.stream()
                .sorted((o1, o2) ->
                    o1.getDrawPriority().compare(o2.getDrawPriority()))
                .collect(Collectors.toList());


        drawablesDirty = false;
    }

    public void draw() {
        Graphics g = getBufferStrategy().getDrawGraphics();
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.clearRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        if(drawablesDirty) onDrawablesDirty();

        for(Drawable d: drawables) {
            d.draw(g);
        }

        getBufferStrategy().show();
    }

    private void enterEditing(UpdateHandler updateHandler) {
        toolBar.setVisible(true);
        window.setSize(window.getWidth(), window.getHeight() + toolBar.getSize().height);
        updateHandler.currentState = State.EDITING;
    }

    private void exitEditing(UpdateHandler updateHandler) {
        toolBar.setVisible(false);
        window.setSize(window.getWidth(), window.getHeight() - toolBar.getSize().height);
        updateHandler.currentState = State.VIEWING;
    }

    private void loadSchematicFromFile() {
        JFileChooser fc = FileUtils.getChooserWithTitle("Choose File to load from");
        int result = fc.showOpenDialog(window);

        if(result == JFileChooser.APPROVE_OPTION) {
            Path p = fc.getSelectedFile().toPath();

            CircuitLoader.loadCircuit(p, this, updateHandler);
        }
    }

    private void saveCircuitAs() {
        JFileChooser fc = FileUtils.getChooserWithTitle("Choose File to save in");
        int result = fc.showOpenDialog(window);

        if(result == JFileChooser.APPROVE_OPTION) {
            List<SchematicElement> elements = updateHandler.getUpdateables()
                    .stream()
                    .filter(u -> u instanceof SchematicElement)
                    .map(s -> (SchematicElement)s)
                    .collect(Collectors.toList());

            CircuitSaver.saveCircuit(fc.getSelectedFile().toPath(), elements, updateHandler);
        }
    }

    @Override
    public void run() {
        while(!drawingThread.isInterrupted()) {
            draw();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }
}
