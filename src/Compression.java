import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Compression {
    public static void main(String[] args) throws IOException{
        BufferedImage inputImage = ImageIO.read(new File("/home/luis/Documents/GitRepos/ImageCompression/src/input.png"));

        Ditherer ditherer = new Ditherer();
        ditherer.addColorToPallet(Color.WHITE);
        ditherer.addColorToPallet(Color.BLACK);
        ditherer.setErrorKernelSize(1);

        BufferedImage ditheredImage = ditherer.dither(inputImage);
        saveImage(ditheredImage, "/home/luis/Documents/GitRepos/ImageCompression/src/");
    }

    static BufferedImage quantize(BufferedImage image) {
        BufferedImage output = image.getSubimage(0, 0, image.getWidth(), image.getHeight());
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int currPixel = image.getRGB(x, y);
                if (isCloserToBlack(currPixel)) {
                    output.setRGB(x, y, Color.BLACK.getRGB());
                }
                else {
                    output.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }
        return output;
    }

    static BufferedImage dither(BufferedImage image) {
        BufferedImage output = image.getSubimage(0, 0, image.getWidth(), image.getHeight());
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color originalColor = new Color(image.getRGB(x, y));
                Color newColor;
                if (isCloserToBlack(originalColor.getRGB())) {
                    newColor = Color.BLACK;
                    output.setRGB(x, y, newColor.getRGB());
                }
                else {
                    newColor = Color.WHITE;
                    output.setRGB(x, y, newColor.getRGB());
                }
                pushQuantizeError(output, x, y, originalColor, newColor);
            }
            System.out.println();
        }
        return output;
    }

    static boolean isCloserToBlack(int rgb) {
        Color color = new Color(rgb);

        double distFromBlack = Utils.distanceFromColor(color, Color.black);
        double distFromWhite = Utils.distanceFromColor(color, Color.white);

        if (distFromBlack < distFromWhite) {
            return true;
        }
        else {
            return false;
        }
    }

    static void pushQuantizeError(BufferedImage image, int x, int y, Color oldColor, Color newColor) {
        int errorR = oldColor.getRed() - newColor.getRed();
        int errorG = oldColor.getGreen() - newColor.getGreen();
        int errorB = oldColor.getBlue() - newColor.getBlue();

        // Right neighbor
        if (Utils.inBounds(x + 1, y, image.getWidth(), image.getHeight())) {
            Color neighborColor = new Color(image.getRGB(x + 1, y));
            int r = neighborColor.getRed() + (errorR * 6 / 16);
            int g = neighborColor.getGreen() + (errorG * 6 / 16);
            int b = neighborColor.getBlue() + (errorB * 6 / 16);
            r = Utils.clamp(r, 0, 255);
            g = Utils.clamp(g, 0, 255);
            b = Utils.clamp(b, 0, 255);
            neighborColor = new Color(r, g, b);
            image.setRGB(x + 1, y, neighborColor.getRGB());
        }

        // Bottom left neighbor
        if (Utils.inBounds(x - 1, y + 1, image.getWidth(), image.getHeight())) {
            Color neighborColor = new Color(image.getRGB(x - 1, y + 1));
            int r = neighborColor.getRed() + (errorR * 2 / 16);
            int g = neighborColor.getGreen() + (errorG * 2 / 16);
            int b = neighborColor.getBlue() + (errorB * 2 / 16);
            r = Utils.clamp(r, 0, 255);
            g = Utils.clamp(g, 0, 255);
            b = Utils.clamp(b, 0, 255);
            neighborColor = new Color(r, g, b);
            image.setRGB(x - 1, y + 1, neighborColor.getRGB());
        }

        // Bottom neighbor
        if (Utils.inBounds(x, y + 1, image.getWidth(), image.getHeight())) {
            Color neighborColor = new Color(image.getRGB(x, y + 1));
            int r = neighborColor.getRed() + (errorR * 6 / 16);
            int g = neighborColor.getGreen() + (errorG * 6 / 16);
            int b = neighborColor.getBlue() + (errorB * 6 / 16);
            r = Utils.clamp(r, 0, 255);
            g = Utils.clamp(g, 0, 255);
            b = Utils.clamp(b, 0, 255);
            neighborColor = new Color(r, g, b);
            image.setRGB(x, y + 1, neighborColor.getRGB());
        }

        // Bottom right neighbor
        if (Utils.inBounds(x + 1, y + 1, image.getWidth(), image.getHeight())) {
            Color neighborColor = new Color(image.getRGB(x + 1, y + 1));
            int r = neighborColor.getRed() + (errorR * 2 / 16);
            int g = neighborColor.getGreen() + (errorG * 2 / 16);
            int b = neighborColor.getBlue() + (errorB * 2 / 16);
            r = Utils.clamp(r, 0, 255);
            g = Utils.clamp(g, 0, 255);
            b = Utils.clamp(b, 0, 255);
            neighborColor = new Color(r, g, b);
            image.setRGB(x + 1, y + 1, neighborColor.getRGB());
        }
    }

    public static boolean saveImage(BufferedImage image, String path) {
        boolean saved = false;
        try {
            ImageIO.write(image, "png", new File(path + Utils.timestamp() + ".png"));
            saved = true;
        } catch (IOException exception) {
            System.out.println("Could not save file: " + path);
        }

        return saved;
    }

    static byte[] imageToByteArray(BufferedImage image) {
        long numOfPixels = image.getWidth() * image.getHeight();
        int arraySize = (int) numOfPixels / 8;
        if (numOfPixels % 8 > 0) {
            arraySize += 1;
        }
        byte[] output = new byte[arraySize];
        
        int arrayIndex = 0;
        int bitIndex = 0;
        int currentByte = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                //set bit
                if (!isCloserToBlack(image.getRGB(x, y))) {
                    currentByte = currentByte | 1;
                }

                //shift over
                currentByte = currentByte << 1;
                bitIndex += 1;
                

                //write byte then shift byte
                if (bitIndex > 7) {
                    System.out.print(String.format("%-5d", (byte) currentByte) + "\t");
                    System.out.print(Integer.toBinaryString((currentByte & 0xFF) + 0x100).substring(1));
                    System.out.println();
                    output[arrayIndex] = (byte) currentByte;
                    arrayIndex += 1;
                    currentByte = 0;
                    bitIndex = 0;
                }
            }
        }
        return output;
    }
}