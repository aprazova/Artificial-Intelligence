import java.util.Random;

public class TicTacToe {

    private final Integer SIZE = 3;
    private final String OPPONENT_SYMBOL = "O";
    private final String BOT_SYMBOL = "X";
    private final String EMPTY_CELL = "_";
    private final Integer NO_WINNER_SCORE = 0;

    private String[][] matrix;
    private boolean isOver;

    TicTacToe(Integer firstPlayer){
        this.matrix = new String[this.SIZE][this.SIZE];
        this.isOver = false;

        for(int i = 0; i < this.SIZE; i++){
            for(int j = 0; j < this.SIZE; j++){
                this.matrix[i][j] = this.EMPTY_CELL;
            }
        }

        Integer OPPONENT_FIRST = 0;
        Integer BOT_FIRST = 1;

        if (firstPlayer.equals(OPPONENT_FIRST)){
            System.out.println("You are first!");
        } else if (firstPlayer.equals(BOT_FIRST)){
            System.out.println("Bot is first.");
            Random random = new Random();
            int row = random.nextInt(this.SIZE);
            int col = random.nextInt(this.SIZE);
            this.matrix[row][col] = this.BOT_SYMBOL;
            printMatrix();
        }
    }

    public void addMove(Integer row, Integer col){

        if (!this.matrix[row - 1][col - 1].equals(this.EMPTY_CELL)){
            System.out.println("Please, select valid cell.");
            return;
        }

        this.opponentMove(row, col);
        String winner = this.checkForWinner();
        if (!winner.isEmpty()){
            this.isOver = true;
            if (winner.equals(this.OPPONENT_SYMBOL)){
                System.out.println("\nCongratulations! You win.");
            } else {
                System.out.println("\nSorry! You lost.");
            }
        } else if (checkForGameIsOver()){
            this.isOver = true;
            System.out.println("\nGame over!");
        } else {
            System.out.println("\nBot move.");
            int[] move = this.botMove();
            this.matrix[move[0]][move[1]] = this.BOT_SYMBOL;
            printMatrix();
            winner = checkForWinner();
            if (winner.equals(this.BOT_SYMBOL)){
                this.isOver = true;
                System.out.println("\nSorry! You lost.");
            } else {
                this.isOver = this.checkForGameIsOver();
            }
        }
    }

    public boolean gameIsOver(){
        return this.isOver;
    }

    public void opponentMove(Integer row, Integer col){
        this.matrix[row - 1][col - 1] = this.OPPONENT_SYMBOL;
        this.printMatrix();
    }

    private int[] botMove(){
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int[] move = new int[2];

        for (int i = 0; i < this.SIZE; i++){
            for (int j = 0; j < this.SIZE; j++){
                if (this.matrix[i][j].equals(this.EMPTY_CELL)){
                    this.matrix[i][j] = this.BOT_SYMBOL;
                    int score = this.minimax(this.matrix, 0, false, alpha, beta);
                    this.matrix[i][j] = this.EMPTY_CELL;
                    if (score > alpha) {
                        alpha = score;
                        move[0] = i;
                        move[1] = j;
                    }
                }
            }
        }
        return move;
    }
    private void printMatrix(){
        for(int i = 0; i < this.SIZE; i++){
            for(int j = 0; j < this.SIZE; j++){
                System.out.print(this.matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    private Integer minimax(String[][] board, Integer depth, boolean minimazing, Integer alpha, Integer beta){
        String winner = this.checkForWinner();
        if (!winner.isEmpty()){
            if (winner.equals(this.OPPONENT_SYMBOL)){
                return depth - 10;
            } else if (winner.equals(this.BOT_SYMBOL)){
                return 10 - depth;
            }
        }

        if (checkForGameIsOver()){
            return this.NO_WINNER_SCORE;
        }

        if (minimazing){
            for (int i = 0; i < this.SIZE; i++){
                for (int j = 0; j < this.SIZE; j++){
                    if (board[i][j].equals(this.EMPTY_CELL)){
                        board[i][j] = this.BOT_SYMBOL;
                        int score = this.minimax(board, depth + 1, false, alpha, beta);
                        board[i][j] = this.EMPTY_CELL;
                        if (score >= beta){
                            return score;
                        }
                        alpha = Math.max(score, alpha);
                    }
                }
            }
            return alpha;
        } else {
            for (int i = 0; i < this.SIZE; i++){
                for (int j = 0; j < this.SIZE; j++){
                    if (board[i][j].equals(this.EMPTY_CELL)){
                        board[i][j] = this.OPPONENT_SYMBOL;
                        int score = this.minimax(board, depth + 1, true, alpha, beta);
                        board[i][j] = this.EMPTY_CELL;
                        if (score <= alpha){
                            return score;
                        }
                        beta = Math.min(score, beta);
                    }
                }
            }
            return beta;
        }
    }

    private String checkForWinner(){
        String move = "";
        for (int i = 0; i < this.SIZE; i++){
            if (!this.matrix[i][0].equals(this.EMPTY_CELL) &&
                    this.matrix[i][0].equals(this.matrix[i][1]) &&
                    this.matrix[i][1].equals(this.matrix[i][2])){
                move = this.matrix[i][0];
            }
        }

        for (int i = 0; i < this.SIZE; i++){
            if (!this.matrix[0][i].equals(this.EMPTY_CELL) &&
                    this.matrix[0][i].equals(this.matrix[1][i]) &&
                    this.matrix[1][i].equals(this.matrix[2][i])){
                move = this.matrix[0][i];
            }
        }

        if  (!this.matrix[0][0].equals(this.EMPTY_CELL) &&
                this.matrix[0][0].equals(this.matrix[1][1]) &&
                this.matrix[1][1].equals(this.matrix[2][2])){
            move = this.matrix[0][0];
        }

        if  (!this.matrix[0][2].equals(this.EMPTY_CELL) &&
                this.matrix[0][2].equals(this.matrix[1][1]) &&
                this.matrix[1][1].equals(this.matrix[2][0])){
            move = this.matrix[0][2];
        }

        return move;
    }

    private boolean checkForGameIsOver(){
        for (int i = 0; i < this.SIZE; i++){
            for (int j = 0; j < this.SIZE; j++){
                if (this.matrix[i][j].equals(this.EMPTY_CELL)){
                    return false;
                }
            }
        }
        return true;
    }
}
