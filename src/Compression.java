import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Compression {
    public static void main(String[] args) throws IOException{
        BufferedImage inputImage = ImageIO.read(new File("./input.png"));
        int out = 0;
        byte other = 0;
        System.out.println(out);
        System.out.println(Integer.toBinaryString(out));
        
        for (int x = 0; x < inputImage.getWidth(); x++) {
            int shiftCount = 0;
            for (int y = 0; y < inputImage.getHeight(); y++) {
                int currPixel = inputImage.getRGB(x, y);
                if (isCloserToBlack(currPixel)) {
                    System.out.print("#");
                    out = out | 1;
                }
                else {
                    System.out.print("-");
                }

                if (shiftCount < 7) {
                    out = out << 1;
                    shiftCount += 1;
                }
                else {
                    shiftCount = 0;
                }
            }
            System.out.print("\t" + Integer.toBinaryString(out) + "\n");
            out = 0;
        }
    }

    static boolean isCloserToBlack(int rgb) {
        Color color = new Color(rgb);

        double distFromBlack = distanceFromColor(color, Color.black);
        double distFromWhite = distanceFromColor(color, Color.white);

        if (distFromBlack < distFromWhite) {
            return true;
        }
        else {
            return false;
        }
    }

    static double distanceFromColor(Color color, Color target) {
        double dist = Math.sqrt(
            Math.pow(target.getRed() - color.getRed(), 2)
            + Math.pow(target.getGreen() - color.getGreen(), 2)
            + Math.pow(target.getBlue() - color.getBlue(), 2)
        );
        return dist;
    }
}