import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Quantizer {

    private Set<Color> pallet;

    public Quantizer() {
        pallet = new HashSet<Color>();
    }

    public void addColorToPallet(Color color) {
        pallet.add(color);
    }

    public void resetColorPallet() {
        pallet = new HashSet<Color>();
    }

    public void setColorPallet(Set<Color> pallet) {
        this.pallet = pallet;
    }

    public BufferedImage quantize(BufferedImage image) {
        if (pallet == null && !pallet.isEmpty()) {
            throw new IllegalArgumentException();
        }

        BufferedImage output = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        BufferedImage copy = copyImage(image);

        for (int y = 0; y < copy.getHeight(); y++) {
            for (int x = 0; x < copy.getWidth(); x++) {
                Color newColor = closestPalletColor(image.getRGB(x, y));
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
