import java.util.Arrays;

public class Board implements Comparable<Board> {

    private int[][] board;
    private Integer size;
    private Integer rowIndexOfZero;
    private Integer collIndexOfZero;
    private Integer cost;
    private Integer indexOfZeroInGoalBoard;

    Board(Integer size, Integer cost, Integer indexOfZeroInGoalBoard){
        this.size = size;
        this.cost = cost;
        this.indexOfZeroInGoalBoard = indexOfZeroInGoalBoard == -1 ? (int)Math.pow(this.size, 2) - 1 : indexOfZeroInGoalBoard;

        this.board = new int[this.size][];
        for(int i=0; i < this.size; i++){
            this.board[i] = new int[this.size];
        }
    }

    public void setCost(Integer cost){
        this.cost = cost;
    }

    public Integer getCost() { return this.cost; }

    public Integer getSize(){
        return this.size;
    }

    public int[][] getBoard() {
        return this.board;
    }

    public Integer getRowIndexOfZero() { return this.rowIndexOfZero; }

    public Integer getCollIndexOfZero() { return this.collIndexOfZero; }

    public void fillRow(Integer row, String lineOfNumbers){
        String[] numbers = lineOfNumbers.split(" ");
        for(int i=0; i<this.size; i++){
            this.setTileInBoard(row, i, Integer.parseInt(numbers[i]));
        }
    }

    public void setTileInBoard( Integer rowIndex, Integer collIndex, Integer value){
        if (value == 0){
            this.rowIndexOfZero = rowIndex;
            this.collIndexOfZero = collIndex;
        }
        this.board[rowIndex][collIndex] = value;
    }

    public Integer evaluationFunction(){
        return this.heuristicFunction() + this.cost;
    }

    private Integer heuristicFunction(){
        int manhattanDistance = 0;

        for (int i=0; i<this.size; i++){
            for (int j=0; j<this.size; j++){
                int value = this.board[i][j];
                if (value == 0) continue;
                if (value <= this.indexOfZeroInGoalBoard){
                    value--;
                }

                int indexOfRowInGoalBoard = value / this.size;
                int indexOfCollInGoalBoard = value % this.size;
                manhattanDistance += Math.abs(i - indexOfRowInGoalBoard ) + Math.abs(j - indexOfCollInGoalBoard);
            }
        }
        return manhattanDistance;
    }

    public void swapTiles(Integer row, Integer coll, Integer newRow, Integer newColl){
        int temp = this.board[row][coll];
        this.setTileInBoard(row, coll, this.board[newRow][newColl]);
        this.setTileInBoard(newRow, newColl, temp);
    }

    @Override
    public int compareTo(Board board) {
        Integer leftEvaluationFunctionValue = this.evaluationFunction();
        Integer rightEvaluationFunctionValue = board.evaluationFunction();
        return leftEvaluationFunctionValue.compareTo(rightEvaluationFunctionValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Board)) return false;
        Board board1 = (Board) o;
        return Arrays.deepEquals(board, board1.board);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Arrays.hashCode(board);
        return result;
    }
}
