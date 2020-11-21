import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter number of towns: ");
        Integer n = input.nextInt();

        TSP_Solver tsp = new TSP_Solver(n);
        try {
            tsp.solveProblem();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
