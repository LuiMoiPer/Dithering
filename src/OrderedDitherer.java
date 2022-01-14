import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class OrderedDitherer implements IDitherer {

    private static final int MAX_CHANNEL_VALUE = 255;

    private Set<Color> pallet;
    private int[][] matrix = {
        {24, 10, 12, 26, 35, 47, 49, 37},
		{8, 0, 2, 14, 45, 59, 61, 51},
		{22, 6, 4, 16, 43, 57, 63, 53},
		{30, 20, 18, 28, 33, 41, 55, 39},
		{34, 46, 48, 36, 25, 11, 13, 27},
		{44, 58, 60, 50, 9, 1, 3, 15},
		{42, 56, 62, 52, 23, 7, 5, 17},
		{32, 40, 54, 38, 31, 21, 19, 29},
    };

    public OrderedDitherer() {
        pallet = new HashSet<Color>();
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
                x += matrix[0].length;
            }
            x = 0;
            y += matrix.length;
        }

        return output;
    }
    
    private void applyMatrix(BufferedImage copy, BufferedImage output, int x, int y) {
        int matrixMax = matrix.length * matrix[0].length;

        for (int h = 0; h < matrix.length; h++) {
            for (int w = 0; w < matrix[0].length; w++) {
                if (!Utils.inBounds(x + w, y + h, copy.getWidth(), copy.getHeight())) {
                    continue;
                }

                int cellValue = matrix[h][w];
                int adjustAmount = (int) (MAX_CHANNEL_VALUE * ((cellValue / (float) matrixMax - 0.5f)));

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
