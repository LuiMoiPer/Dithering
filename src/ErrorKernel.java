public class ErrorKernel {
    private int size;
    private Point[] points;
    private float[] weights;

    public ErrorKernel(int size) {
        if (size < 1) {
            throw new IllegalArgumentException();
        }

        this.size = size;
        int arraySize = size * (2 * size + 1) + size;
        points = new Point[arraySize];
        weights = new float[arraySize]; 
        makeKernel();
    }

    public int getSize() {
        return size;
    }

    private void makeKernel() {
        makePoints();
        makeWeights();
    }

    private void makePoints() {
        int arrayIndex = 0;
        // make points the right of the pixel on the same row
        for (int x = 1; x <= size; x++) {
            points[arrayIndex] = new Point(x, 0);
            arrayIndex++;
        }

        // make all the points with a larger y than the pixel
        for (int y = 1; y <= size; y++) {
            for (int x = -size; x <= size; x++) {
                points[arrayIndex] = new Point(x, y);
                arrayIndex++;
            }
        }
    }

    private void makeWeights() {
        int[] parts = new int[weights.length];
        int minPart = Integer.MAX_VALUE;

        // set negative parts
        for (int i = 0; i < weights.length; i++) {
            Point point = points[i];
            int part = -(Point.manhattanMagnitude(point));
            parts[i] = part;
            if (part < minPart) {
                minPart = part;
            }
        }

        // shift parts to be positive and determine the weights
        int partShift = Math.abs(minPart) + 1;
        float totalParts = 0;
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i] + partShift;
            totalParts += parts[i];
        }

        for (int i = 0; i < weights.length; i++) {
            weights[i] = parts[i] / totalParts;
        }
    }

    public class WeightedPoint {
        private Point point;
        private float weight;

        protected WeightedPoint(Point point, float weight) {
            this.point = point;
            this.weight = weight;
        }

        public Point getPoint() {
            return point;
        }

        public float getWeight() {
            return weight;
        }
    }

    public WeightedPoint[] getWeightedPoints() {
        WeightedPoint[] weightedPoints = new WeightedPoint[points.length];
        
        for (int i = 0; i < weightedPoints.length; i++) {
            weightedPoints[i] = new WeightedPoint(points[i], weights[i]);
        }
        
        return weightedPoints;
    }
}
