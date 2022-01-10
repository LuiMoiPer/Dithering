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
        colorErrorBuffer = new ColorErrorBuffer(image.getWidth(), errorKernel.getSize() + 1);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color originalColor = new Color(image.getRGB(x, y));
                Color newColor = closestPalletColor(originalColor);
                output.setRGB(x, y, newColor.getRGB());

                pushError(image, x, y, newColor);
            }
            colorErrorBuffer.advanceRow();
        }
        return output;
    }

    private void pushError(BufferedImage image, int x, int y, Color newColor) {
        Color originalColor = new Color(image.getRGB(x, y));
        int redError = newColor.getRed() - originalColor.getRed();
        int greenError = newColor.getGreen() - originalColor.getGreen();
        int blueError = newColor.getBlue() - originalColor.getBlue();

        Point currentPixel = new Point(x, y);
        ErrorKernel.WeightedPoint[] neighbors = errorKernel.getWeightedPoints();

        for (int i = 0; i < neighbors.length; i++) {
            Point neighbor = Point.add(currentPixel, neighbors[i].getPoint());
            float weight = neighbors[i].getWeight();
            
            if (Utils.inBounds(neighbor, image.getWidth(), image.getHeight())) {
                // push the rgb error multiplied 
                ColorError colorError = new ColorError(
                    (int) (redError / weight), 
                    (int) (greenError / weight), 
                    (int) (blueError / weight)
                );
                colorErrorBuffer.pushError(neighbor, colorError);
            }
        }
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
