public class ColorError {
    public int red;
    public int green;
    public int blue;

    public ColorError(){
        this.red = 0;
        this.green = 0;
        this.blue = 0;
    }

    public ColorError(ColorError colorError) {
        this.red = colorError.red;
        this.green = colorError.green;
        this.blue = colorError.blue;
    }

    public ColorError(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public void add(ColorError other) {
        this.red += other.red;
        this.green += other.green;
        this.blue += other.blue;
    }
}
