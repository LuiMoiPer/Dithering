import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class RandomNoiseDitherer implements IDitherer {

    private Set<Color> pallet;
    private Random random;

    public RandomNoiseDitherer() {
        pallet = new HashSet<Color>();
        random = new Random();
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

        for (int y = 0; y < copy.getHeight(); y++) {
            for (int x = 0; x < copy.getWidth(); x++) {
                int currentColor = applyNoise(copy.getRGB(x, y));
                Color newColor = closestPalletColor(currentColor);
                output.setRGB(x, y, newColor.getRGB());
            }
        }
        return output;
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

    private int applyNoise(int rgb) {
        Color initialColor = new Color(rgb);
        int adjustedRed = initialColor.getRed() + (random.nextInt(256) - 128);
        int adjustedGreen = initialColor.getGreen() + (random.nextInt(256) - 128);
        int adjustedBlue = initialColor.getBlue() + (random.nextInt(256) - 128);

        adjustedRed = Utils.clamp(adjustedRed, 0, 255);
        adjustedGreen = Utils.clamp(adjustedGreen, 0, 255);
        adjustedBlue = Utils.clamp(adjustedBlue, 0, 255);

        return new Color(adjustedRed, adjustedGreen, adjustedBlue).getRGB();
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
