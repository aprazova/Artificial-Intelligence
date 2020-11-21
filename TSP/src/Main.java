import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Integer n = input.nextInt();

        TSP_Solver tsp = new TSP_Solver(n);
    }
}
