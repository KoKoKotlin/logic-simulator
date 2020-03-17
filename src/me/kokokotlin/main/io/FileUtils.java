package me.kokokotlin.main.io;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.nio.file.Paths;

public class FileUtils {

    public static JFileChooser getChooserWithTitle(String title) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(Paths.get(".").toFile());
        fileChooser.setDialogTitle(title);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".txt") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Text Files";
            }
        };

        fileChooser.setFileFilter(fileFilter);

        return fileChooser;
    }

}
