import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class OrderedDitherer implements IDitherer {

    private static final int MAX_CHANNEL_VALUE = 255;

    private Set<Color> pallet;
    private Matrix matrix;
    private float strength;

    public OrderedDitherer() {
        pallet = new HashSet<Color>();
        strength = 1f;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public void setStrength(float strength) {
        this.strength = strength;
    }

    @Override
    public void addColorToPallet(Color color) {
        pallet.add(color);
    }

    @Override
    public void setColorPallet(Set<Color> pallet) {
        this.pallet = pallet;
    }

    @Override
    public void resetColorPallet() {
        pallet = new HashSet<Color>();
    }

    @Override
    public BufferedImage dither(BufferedImage image) {
        BufferedImage output = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        BufferedImage copy = copyImage(image);

        int x = 0;
        int y = 0;
        while (y < copy.getHeight()) {
            while (x < copy.getWidth()) {
                applyMatrix(copy, output, x, y);
                x += matrix.getTilingOffset().getX();
            }
            x = 0;
            y += matrix.getTilingOffset().getY();
        }

        return output;
    }
    
    private void applyMatrix(BufferedImage copy, BufferedImage output, int x, int y) {
        int matrixMax = matrix.getMax();
        int[][] ditherMatrix = matrix.getMatrix();
        

        for (int h = 0; h < ditherMatrix.length; h++) {
            for (int w = 0; w < ditherMatrix[0].length; w++) {
                if (!Utils.inBounds(x + w, y + h, copy.getWidth(), copy.getHeight())) {
                    continue;
                }

                int cellValue = ditherMatrix[h][w];
                int adjustAmount = (int) ((MAX_CHANNEL_VALUE * strength) * ((cellValue / (float) matrixMax - 0.5f)));

                Color adjustedColor = adjustColor(copy.getRGB(x, y), adjustAmount);
                Color newColor = closestPalletColor(adjustedColor);
                output.setRGB(x + w, y + h, newColor.getRGB());
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

    private Color adjustColor(int rgb, int amount) {
        Color initialColor = new Color(rgb);
        int adjustedRed = initialColor.getRed() + amount;
        int adjustedGreen = initialColor.getGreen() + amount;
        int adjustedBlue = initialColor.getBlue() + amount;

        adjustedRed = Utils.clamp(adjustedRed, 0, 255);
        adjustedGreen = Utils.clamp(adjustedGreen, 0, 255);
        adjustedBlue = Utils.clamp(adjustedBlue, 0, 255);

        return new Color(adjustedRed, adjustedGreen, adjustedBlue);
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
