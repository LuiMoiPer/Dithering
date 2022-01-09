public class ColorErrorBuffer {
    private ColorError[] buffer;
    private int rowOffset;
    private int width;
    private int height;

    public ColorErrorBuffer(int width, int height) {
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException();
        }
        this.width = width;
        this.height = height;
        rowOffset = 0;
        buffer = new ColorError[width * height];
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = new ColorError();
        }
    }

    public void finishedRow() {
        for (int i = 0; i + width < buffer.length; i++) {
            buffer[i] = buffer[i + width];
        }
        rowOffset++;
    }

    public void pushError(Point point, ColorError error) {
        pushError(point.getX(), point.getY(), error);
    }

    public void pushError(int x, int y, ColorError error) {
        if (y < rowOffset || y > rowOffset + height) {
            throw new IllegalArgumentException();
        }

        if (x < 0 || x > width) {
            throw new IllegalArgumentException();
        }

        y -= rowOffset;
        int index = y * width + x;
        buffer[index].add(error);
    }

    public ColorError getError(Point point) {
        return getError(point.getX(), point.getY());
    }

    public ColorError getError(int x, int y) {
        if (y < rowOffset || y > rowOffset + height) {
            throw new IllegalArgumentException();
        }

        if (x < 0 || x > width) {
            throw new IllegalArgumentException();
        }

        y -= rowOffset;
        int index = y * width + x;
        return new ColorError(buffer[index]);
    }
}
