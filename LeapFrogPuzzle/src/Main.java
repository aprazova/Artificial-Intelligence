import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("Enter number of frogs:");
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();

        LeapFrogPuzzle puzzle = new LeapFrogPuzzle(n);
        puzzle.findPath();
        puzzle.printPath();
    }
}
