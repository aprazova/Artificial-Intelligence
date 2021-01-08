import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class KMeans {
    private String filename;
    private List<Point> points;
    private List<Point> centroids;
    private Map<Point, List<Point>> clusters;
    private Integer numberOfClusters;

    KMeans(String filename, Integer numberOfClusters) {
        this.points = new ArrayList<>();
        this.centroids = new ArrayList<>();
        this.clusters = new HashMap<>();
        this.filename = filename;
        this.numberOfClusters = numberOfClusters;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {

                String[] coordinates = line.split("\\s");
                float x = Float.parseFloat(coordinates[0]);
                float y = Float.parseFloat(coordinates[1]);
                this.points.add(new Point(x,y));
            }
            this.initialCentroids();
    } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void clusterization() throws IOException {
        boolean hasChange;
        float cost = Float.MAX_VALUE;
        float centroidsDistance = Float.MIN_VALUE;
        int counter = 0;
        do {
            hasChange = false;
            counter++;

            for (Point centroid:
                    this.clusters.keySet()) {
                this.clusters.get(centroid).clear();
            }

            for (Point point :
                    this.points) {
                Point centroid = this.getNearestCentroid(point);
                this.clusters.get(centroid).add(point);
            }

            for (Point centroid:
                    this.clusters.keySet()) {
                Point newCoordinate = this.updateCentroid(this.clusters.get(centroid));

                if (newCoordinate.getX() != centroid.getX() || newCoordinate.getY() != centroid.getY()) {
                    centroid.setX(newCoordinate.getX());
                    centroid.setY(newCoordinate.getY());
                    hasChange = true;
                }
            }

            String filename = "./image" + counter + ".png";
            this.generateImage(filename);

            float distance = this.calculateCentroidDistance();
            float currentCost = this.calculateWithinPointScatter(this.clusters);

            //Centroid distance and within point scatter optimizations
            if (distance < centroidsDistance || cost < currentCost || Float.isNaN(distance)) {
                centroidsDistance = Float.MIN_VALUE;
                cost = Float.MAX_VALUE;
                this.initialCentroids();
            } else {
                centroidsDistance = distance;
                cost = currentCost;
            }

        } while (hasChange);
        System.out.println("Number of iterations: " + counter);
    }

    public void generateImage(String filename) throws IOException {
        Color colors[] = new Color[]{Color.RED, Color.BLUE, Color.GRAY, Color.YELLOW, Color.PINK, Color.ORANGE,  Color.DARK_GRAY, Color.BLACK, Color.MAGENTA};
        Color centroidColor = Color.BLACK;
        int pixel = 1080;
        int divider = 1;
        int radius = 5;
        int multiple = 100;

        if (this.filename.contains("unbalance")) {
            radius = 5;
            multiple = 1;
            divider = 1080;
        }

        BufferedImage bufferedImage = new BufferedImage(pixel, pixel, BufferedImage.TYPE_INT_RGB);
        Color transparent = new Color(0x00FFFFFF, true);

        Graphics2D g = bufferedImage.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.setColor(transparent);
        g.setBackground(transparent);
        g.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int clusterColor = 0;
        for (Point centroid :
                this.clusters.keySet()) {

            g.setColor(colors[clusterColor]);
            clusterColor++;
            for (Point point :
                    this.clusters.get(centroid)) {
                int x = (int)(point.getX() * multiple)/divider;
                int y = (int)(point.getY() * multiple)/divider;
                g.fillOval(x - radius, y - radius, radius, radius);
            }

            g.setColor(centroidColor);
            int centroidX= (int)(centroid.getX() * multiple)/divider;
            int centroidY = (int)(centroid.getY() * multiple)/divider;
            g.drawString("X", centroidX, centroidY);

        }
        ImageIO.write(bufferedImage, "png", new File(filename));
    }

    private Point getNearestCentroid(Point point) {
        Point nearestCentroid = null;
        double nearestCentroidDistance = Double.MAX_VALUE;
        for (Point centroid:
                this.clusters.keySet()) {
            double distance = point.calculateDistance(centroid);
            if (distance < nearestCentroidDistance) {
                nearestCentroid = centroid;
                nearestCentroidDistance = distance;
            }
        }
        return nearestCentroid;
    }

    private Point updateCentroid(List<Point> points) {
        float x = 0;
        float y = 0;
        int numberOfPoints = points.size();
        for (Point point :
                points) {
            x += point.getX();
            y += point.getY();
        }
        return new Point(x/numberOfPoints, y/numberOfPoints);
    }

    private void initialCentroids() {
        Random random = new Random();
        List<Integer> indexes = new ArrayList<>();
        this.centroids.clear();
        this.clusters.clear();

        while (indexes.size() < this.numberOfClusters) {
            int pointIndex = random.nextInt(this.points.size());
            if (indexes.indexOf(pointIndex) == -1) {
                Point centroid = new Point(this.points.get(pointIndex).getX(),this.points.get(pointIndex).getY());
                this.centroids.add(centroid);
                this.clusters.put(centroid, new ArrayList<>());
                indexes.add(pointIndex);
            }
        }
    }

    private void initialCentroidsPlusPlus(){
        Random random = new Random();
        this.centroids.clear();
        this.clusters.clear();

        int pointsSize = this.points.size();
        Float[] distToClosestCentroid = new Float[pointsSize];
        Float[] weightedDistribution = new Float[pointsSize];

        int pointIndex = 0;
        for (int c = 0; c < this.numberOfClusters; c++) {
            if (c == 0) {
                pointIndex = random.nextInt(pointsSize);
            } else {
                for (int p = 0; p < pointsSize; p++) {
                    float tempDistance = this.points.get(p).calculateDistance(this.centroids.get(c - 1));

                    if (c == 1) {
                        distToClosestCentroid[p] = tempDistance;
                    } else {
                        if (tempDistance < distToClosestCentroid[p]) {
                            distToClosestCentroid[p] = tempDistance;
                        }
                    }

                    if (p == 0) {
                        weightedDistribution[p] = distToClosestCentroid[p];
                    } else {
                        weightedDistribution[p] = weightedDistribution[p - 1] + distToClosestCentroid[p];
                    }
                }

                float rand = random.nextFloat();
                for (int j = pointsSize - 1; j > 0; j--) {
                    if (rand > weightedDistribution[j - 1]/weightedDistribution[pointsSize - 1]) {
                        pointIndex = j;
                        break;
                    }
                    else {
                        pointIndex = 0;
                    }
                }
            }
            Point centroid = new Point(this.points.get(pointIndex).getX(), this.points.get(pointIndex).getY());
            this.centroids.add(centroid);
            this.clusters.put(centroid, new ArrayList<>());
        }
    }

    private float calculateWithinPointScatter(Map<Point, List<Point>> clusters) {
        float cost = 0;
        for (Point centroid :
                clusters.keySet()) {
            float clusterCost = 0;
            for (Point point:
                    clusters.get(centroid)) {
                clusterCost += Math.pow(point.calculateDistance(centroid),2);
            }
            cost += clusterCost * clusters.get(centroid).size();
        }

        return cost;
    }

    private float calculateCentroidDistance() {
        float centroidsDistance = 0;
        for (int i = 0; i < centroids.size(); i++) {
            for (int j = i + 1; j < centroids.size(); j++) {
                float distance = centroids.get(i).calculateDistance(centroids.get(j));
                centroidsDistance += distance;
            }
        }

        return centroidsDistance;
    }
}


