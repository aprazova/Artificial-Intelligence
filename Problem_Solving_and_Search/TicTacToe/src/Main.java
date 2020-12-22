import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("Who will be first? You (0) or the bot (1):");
        Integer firstPlayer  = input.nextInt();
        System.out.println("Your symbol is O.");
        TicTacToe game = new TicTacToe(firstPlayer);

        do {
            System.out.println("\nChoose cell: ");
            int opponentRow = input.nextInt();
            int opponentCol = input.nextInt();
            game.addMove(opponentRow, opponentCol);
        } while (!game.gameIsOver());
    }
}
