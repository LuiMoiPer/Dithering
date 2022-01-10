import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Ditherer {

    private Set<Color> pallet;
    private ErrorKernel errorKernel;
    private ColorErrorBuffer colorErrorBuffer;

    public Ditherer() {
        pallet = new HashSet<Color>();
    }

    public void addColorToPallet(Color color) {
        pallet.add(color);
    }

    public void setColorPallet(Set<Color> pallet) {
        this.pallet = pallet;
    }

    public void setErrorKernelSize(int size) {
        errorKernel = new ErrorKernel(size);
    }

    public BufferedImage dither(BufferedImage image) {
        if (pallet == null || errorKernel == null) {
            throw new IllegalArgumentException();
        }

        BufferedImage output = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        BufferedImage copy = copyImage(image);
        colorErrorBuffer = new ColorErrorBuffer(image.getWidth(), errorKernel.getSize() + 1);

        for (int y = 0; y < copy.getHeight(); y++) {
            for (int x = 0; x < copy.getWidth(); x++) {
                Color currentColor = applyError(x, y, copy.getRGB(x, y));
                Color newColor = closestPalletColor(currentColor);
                output.setRGB(x, y, newColor.getRGB());

                pushErrorToBuffer(image, x, y, currentColor, newColor);
            }
            colorErrorBuffer.advanceRow();
        }
        return output;
    }

    private Color applyError(int x, int y, int rgb) {
        Color initialColor = new Color(rgb);
        ColorError colorError = colorErrorBuffer.getError(x, y);
        int adjustedRed = initialColor.getRed() + colorError.red;
        int adjustedGreen = initialColor.getGreen() + colorError.green;
        int adjustedBlue = initialColor.getBlue() + colorError.blue;

        adjustedRed = Utils.clamp(adjustedRed, 0, 255);
        adjustedGreen = Utils.clamp(adjustedGreen, 0, 255);
        adjustedBlue = Utils.clamp(adjustedBlue, 0, 255);

        Color output = new Color(adjustedRed, adjustedGreen, adjustedBlue);
        return output;
    }

    private void pushErrorToBuffer(BufferedImage image, int x, int y, Color oldColor, Color newColor) {
        int redError = oldColor.getRed() - newColor.getRed();
        int greenError = oldColor.getGreen() - newColor.getGreen();
        int blueError = oldColor.getBlue() - newColor.getBlue();

        Point currentPixel = new Point(x, y);
        ErrorKernel.WeightedPoint[] neighbors = errorKernel.getWeightedPoints();

        for (int i = 0; i < neighbors.length; i++) {
            Point neighbor = Point.add(currentPixel, neighbors[i].getPoint());
            float weight = neighbors[i].getWeight();
            
            if (Utils.inBounds(neighbor, image.getWidth(), image.getHeight())) {
                // push the rgb error multiplied 
                ColorError colorError = new ColorError(
                    (int) (redError * weight), 
                    (int) (greenError * weight), 
                    (int) (blueError * weight)
                );
                colorErrorBuffer.pushError(neighbor, colorError);
            }
        }
    }

    private void pushErrorToImage(BufferedImage image, int x, int y, Color oldColor, Color newColor) {
        int redError = oldColor.getRed() - newColor.getRed();
        int greenError = oldColor.getGreen() - newColor.getGreen();
        int blueError = oldColor.getBlue() - newColor.getBlue();

        Point currentPixel = new Point(x, y);
        ErrorKernel.WeightedPoint[] neighbors = errorKernel.getWeightedPoints();

        for (int i = 0; i < neighbors.length; i++) {
            Point neighbor = Point.add(currentPixel, neighbors[i].getPoint());
            float weight = neighbors[i].getWeight();
            
            if (Utils.inBounds(neighbor, image.getWidth(), image.getHeight())) {
                Color neighborColor = new Color(image.getRGB(neighbor.getX(), neighbor.getY()));
                int r = (int) (neighborColor.getRed() + redError * weight);
                int g = (int) (neighborColor.getGreen() + greenError * weight);
                int b = (int) (neighborColor.getBlue() + blueError * weight);
                r = Utils.clamp(r, 0, 255);
                g = Utils.clamp(g, 0, 255);
                b = Utils.clamp(b, 0, 255);
                neighborColor = new Color(r, g, b);
                image.setRGB(neighbor.getX(), neighbor.getY(), neighborColor.getRGB());
            }
        }
    }

    private BufferedImage copyImage(BufferedImage image) {
        BufferedImage output = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                output.setRGB(x, y, image.getRGB(x, y));
            }
        }
        return output;
    }

    private Color closestPalletColor(int rgb) {
        return closestPalletColor(new Color(rgb));
    }

    private Color closestPalletColor(Color color) {
        Color closest = Color.BLACK;
        double closestDistance = Double.MAX_VALUE;

        Iterator<Color> palletIterator = pallet.iterator();
        while (palletIterator.hasNext()) {
            Color palletColor = palletIterator.next();
            double distance = Utils.distanceFromColor(palletColor, color);
            if (distance < closestDistance) {
                closest = palletColor;
                closestDistance = distance;
            }
        }
        
        return closest;
    }
}
