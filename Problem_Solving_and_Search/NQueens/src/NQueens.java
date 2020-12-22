import java.util.*;

public class NQueens {

    private final Integer MAX_ITERATIONS;
    private Random random;

    private int numberOfQueens;
    private int[] queens;
    private int[] rowQueens;
    private int[] mainDiagonalQueens;
    private int[] oppositeDiagonalQueens;

    NQueens(Integer numberOfQueens){
        this.numberOfQueens = numberOfQueens;
        this.MAX_ITERATIONS = 2*numberOfQueens;
        this.queens = new int[numberOfQueens];
        this.rowQueens = new int[numberOfQueens];
        this.mainDiagonalQueens = new int[2*numberOfQueens - 1];
        this.oppositeDiagonalQueens = new int[2*numberOfQueens - 1];
        this.random = new Random();
    }

    public void findSolution(){
        if (this.numberOfQueens == 2 || this.numberOfQueens == 3){
            System.out.println("There is no solution.");
            return;
        }
        this.solve();
        if(this.numberOfQueens < 50){
            this.printQueensMatrix();
        }
    }

    private void solve(){
        this.randomize();
        int iterator = 0;
        while(iterator < this.MAX_ITERATIONS){
            iterator++;
            int col = this.getColWithQueenWithMaxConflict();

            if (col == -1){
                return;
            }

            int row = this.getRowWithMinConflict(col);
            if (this.queens[col] == row){
                iterator--;
                continue;
            }
            this.updatePositionOfQueen(col, row);
        }

        if (this.hasConflicts()){
            this.solve();
        }
    }

    private void randomize(){

        Arrays.fill(this.rowQueens, 0);
        Arrays.fill(this.mainDiagonalQueens, 0);
        Arrays.fill(this.oppositeDiagonalQueens, 0);
        for (int i = 0; i < numberOfQueens; i++){

            int value = random.nextInt(this.numberOfQueens);
            this.queens[i] = value;
            this.rowQueens[value]++;
            int mainDiagonalCoordinate = i - value;
            this.mainDiagonalQueens[this.numberOfQueens + mainDiagonalCoordinate - 1]++;
            int oppositeDiagonalCoordinate =  value + i ;
            this.oppositeDiagonalQueens[oppositeDiagonalCoordinate]++;
        }
    }

    private int getColWithQueenWithMaxConflict(){
        List<Integer> maxConflictsRow = new ArrayList<>();
        int maxConflicts = 0;
        for (int i = 0; i < this.numberOfQueens; i++){
            int queenRow = this.queens[i];
            int conflicts = this.rowQueens[queenRow] +
                    this.mainDiagonalQueens[this.numberOfQueens + (i - queenRow) - 1] +
                    this.oppositeDiagonalQueens[queenRow + i] - 3;

            if (maxConflicts == conflicts){
                maxConflictsRow.add(i);
            } else if (maxConflicts < conflicts){
                maxConflicts = conflicts;
                maxConflictsRow.clear();
                maxConflictsRow.add(i);
            }
        }

        return maxConflicts == 0 ? -1 : maxConflictsRow.get(random.nextInt(maxConflictsRow.size()));
    }

    private int getRowWithMinConflict(Integer col){
        List<Integer> rowsWithMinConflicts = new ArrayList<>();

        int currRow = this.queens[col];
        int currStateConflicts = this.rowQueens[currRow] +
                this.mainDiagonalQueens[this.numberOfQueens + (col - currRow) - 1] +
                this.oppositeDiagonalQueens[col + currRow];

        int minConflicts = currStateConflicts;

        for (int i=0; i<this.numberOfQueens; i++){
            int conflicts = this.rowQueens[i] +
                    this.mainDiagonalQueens[this.numberOfQueens + (col - i) - 1] +
                    this.oppositeDiagonalQueens[col + i];

            if(conflicts == minConflicts){
                rowsWithMinConflicts.add(i);
            } else if (conflicts < minConflicts){
                minConflicts = conflicts;
                rowsWithMinConflicts.clear();
                rowsWithMinConflicts.add(i);
            }
        }

        if (minConflicts == currStateConflicts && rowsWithMinConflicts.size() > 1){
            rowsWithMinConflicts.remove((Object)currRow);
        }

        return rowsWithMinConflicts.get(random.nextInt(rowsWithMinConflicts.size()));
    }

    private void updatePositionOfQueen(Integer col, Integer newRow){
        int currRow = this.queens[col];
        this.rowQueens[currRow]--;
        this.mainDiagonalQueens[this.numberOfQueens + (col - currRow) - 1]--;
        this.oppositeDiagonalQueens[col + currRow]--;

        this.queens[col] = newRow;
        this.rowQueens[newRow]++;
        this.mainDiagonalQueens[this.numberOfQueens + (col - newRow) - 1]++;
        this.oppositeDiagonalQueens[col + newRow]++;
    }

    private boolean hasConflicts(){
        for (int i=0; i<this.numberOfQueens; i++){
            if (this.rowQueens[i] != 1){
                return true;
            }
        }
        for (int i=0; i<mainDiagonalQueens.length; i++){
            if (this.mainDiagonalQueens[i] > 1 || this.oppositeDiagonalQueens[i] > 1){
                return true;
            }
        }
        return false;
    }
    private void printQueensMatrix(){
        for (int i = 0; i < this.numberOfQueens; i++){
            for ( int j = 0; j < this.numberOfQueens; j++){
                if (this.queens[j]==i){
                    System.out.print("* ");
                } else {
                    System.out.print("_ ");
                }
            }
            System.out.println();
        }
    }
}
