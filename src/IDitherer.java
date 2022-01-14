import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Set;

public interface IDitherer {
    public void addColorToPallet(Color color);
    public void setColorPallet(Set<Color> pallet);
    public void resetColorPallet();
    public BufferedImage dither(BufferedImage image);
}
