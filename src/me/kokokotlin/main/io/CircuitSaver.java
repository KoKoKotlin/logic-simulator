package me.kokokotlin.main.io;

import me.kokokotlin.main.engine.SchematicElement;
import me.kokokotlin.main.engine.UpdateHandler;
import me.kokokotlin.main.engine.connectios.Connection;
import me.kokokotlin.main.engine.logicgates.LogicGate;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CircuitSaver {

    public static void saveCircuit(Path path, List<SchematicElement> elements, UpdateHandler updateHandler) {
        try(BufferedWriter bWriter = Files.newBufferedWriter(path)) {
            bWriter.write(getStringRepr(elements, updateHandler));
            bWriter.flush();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Can't save circuit at " + path.toAbsolutePath() + "\n" +
                    "Error: " + e.getMessage(),
                    "Save error!",
                    JOptionPane.ERROR_MESSAGE);

            return;
        }

        JOptionPane.showMessageDialog(null,
                "Successfully saved circuit!",
                "Save complete!",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static String getStringRepr(List<SchematicElement> elements, UpdateHandler updateHandler) {
        StringBuilder sBuilder = new StringBuilder();

        // save elements
        for(SchematicElement se : elements) {
            sBuilder.append(se.REPR); // command for creating the element
            sBuilder.append(se.getArguments()); // arguments
            sBuilder.append('\n');
        }

        sBuilder.append('\n');

        // save connections
        for(Connection c : updateHandler.getConnHandler().getConns()) {
            sBuilder.append(c.getStringRepr(elements));
            sBuilder.append('\n');
        }

        return sBuilder.toString();
    }

}
