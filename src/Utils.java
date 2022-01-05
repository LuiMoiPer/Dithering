import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

public class Utils {
    static double distanceFromColor(Color color, Color target) {
        double dist = Math.sqrt(
            Math.pow(target.getRed() - color.getRed(), 2)
            + Math.pow(target.getGreen() - color.getGreen(), 2)
            + Math.pow(target.getBlue() - color.getBlue(), 2)
        );
        return dist;
    }

    public static void saveImage(BufferedImage image, String path, int millsToSleep) {
        try {
            ImageIO.write(image, "png", new File(path + Utils.timestamp() + ".png"));
        } catch (IOException exception) {
            
        }
    }

    public static String timestamp() {
        return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SS").format(new Date());
    }

    public static boolean inBounds(int x, int y, int width, int height) {
        if ((x >= 0 && x < width)
            && (y >= 0 && y < height)
        ) {
            return true;
        }
        return false;
    }

    public static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}
