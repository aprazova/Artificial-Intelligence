import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("Enter number of queens: ");
        Scanner input = new Scanner(System.in);
        Integer n = input.nextInt();
        NQueens game = new NQueens(n);
        Long start = System.currentTimeMillis();
        game.findSolution();
        Long end = System.currentTimeMillis();
        System.out.println("Execution time: " + (end - start) + "ms.");
    }
}
