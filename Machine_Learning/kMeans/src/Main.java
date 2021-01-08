import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Enter a filename: ");
        Scanner in = new Scanner(System.in);
        String filename = in.nextLine();

        System.out.println("Enter number of clusters: ");
        Integer numberOfClusters = in.nextInt();

        KMeans algorithm = new KMeans(filename, numberOfClusters);
        algorithm.clusterization();
        algorithm.generateImage("./image.png");
	}
}
