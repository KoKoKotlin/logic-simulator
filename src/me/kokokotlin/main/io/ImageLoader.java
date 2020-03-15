package me.kokokotlin.main.io;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageLoader {

    public static BufferedImage loadImage(String name) {
        Path path = Paths.get("res", name + ".png");

        BufferedImage bImage = null;
        try {
            bImage = ImageIO.read(Files.newInputStream(path));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bImage;
    }

}
