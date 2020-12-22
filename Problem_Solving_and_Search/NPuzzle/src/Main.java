import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter number of tiles: ");
        Integer n = in.nextInt();
        System.out.println("Enter the position of 0 in the solution: ");
        Integer indexOfZero = in.nextInt();
        Board board = new Board((int)Math.sqrt(n+1), 0, indexOfZero);

        in.nextLine();
        System.out.println("Enter the initial state of the board: ");
        for(int i=0; i<Math.sqrt(n+1); i++){
            String line = in.nextLine();
            board.fillRow(i, line.trim());
        }

        SlidingPuzzle puzzle = new SlidingPuzzle(board, indexOfZero);
        Long start = System.currentTimeMillis();
        puzzle.playGame();
        Long elapsedTime = System.currentTimeMillis() - start;
        System.out.println("Execution time: " + elapsedTime + "ms.");
    }
}
