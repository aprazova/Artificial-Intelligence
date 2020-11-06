import javafx.util.Pair;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SlidingPuzzle {

    private static final String UP_DIRECTION = "up";
    private static final String DOWN_DIRECTION = "down";
    private static final String LEFT_DIRECTION = "left";
    private static final String RIGHT_DIRECTION = "right";
    private static final Integer COST_OF_STEP = 1;

    private Board initialBoard;
    private Board goalBoard;
    private Stack<String> solutionDirections;

    SlidingPuzzle(Board startBoard, Integer indexOfZero){
        this.solutionDirections = new Stack<>();
        this.initialBoard = startBoard;
        Integer sizeOfGoalBoard = startBoard.getSize();
        this.goalBoard = new Board(sizeOfGoalBoard, 0, indexOfZero);

        Integer value = 1;
        for (int i=0; i<sizeOfGoalBoard; i++){
            for (int j=0; j<sizeOfGoalBoard; j++){
                if ((indexOfZero)/sizeOfGoalBoard == i && (indexOfZero)%sizeOfGoalBoard == j){
                    this.goalBoard.setTileInBoard(i, j, 0);
                } else {
                    this.goalBoard.setTileInBoard(i, j, value);
                    value++;
                }
            }
        }
        if (indexOfZero == -1){
            this.goalBoard.setTileInBoard(sizeOfGoalBoard -1, sizeOfGoalBoard - 1, 0);
        }
    }

    public void playGame(){
        if (this.isSolvable(this.initialBoard)){
            this.idaStar(this.initialBoard);
            this.printSolution();
        } else {
            System.out.println("The puzzle is unsolvable.");
        }
    }

    private boolean isSolvable(Board startBoard){

        int[] tiles = Stream.of(startBoard.getBoard())
                .flatMapToInt(IntStream::of)
                .toArray();

        int inversions = 0;
        int indexOfZero = 0;
        for (int i = 0; i < tiles.length - 1; i++){
            for (int j = i + 1; j < tiles.length; j++){
                if (tiles[j] > 0 && tiles[i] > tiles[j]) inversions++;
                if (tiles[j] == 0) indexOfZero = j;
            }
        }

        if ( startBoard.getSize() % 2 == 1 && inversions % 2 == 0) return true;
        if ( startBoard.getSize() % 2 == 0){
            inversions += indexOfZero/startBoard.getSize();
            return inversions % 2 == 1;
        }
        return false;
    }

    private void printSolution(){
        System.out.println(this.solutionDirections.size());
        for (String solution: this.solutionDirections) {
            System.out.println(solution);
        }
    }

    private void idaStar(Board initialBoard){
        Integer threshold = initialBoard.evaluationFunction();

        while(true){
            Integer nextThreshold = this.search(initialBoard, threshold);

            if (nextThreshold == 0) {
                return;
            }

            if (nextThreshold == Integer.MAX_VALUE) {
                System.out.println("There is no solution.");
                return;
            }
            threshold = nextThreshold;
        }
    }

    private Integer search(Board currentBoard, Integer threshold){
        Integer evaluationFunction = currentBoard.evaluationFunction();

        if ( evaluationFunction > threshold) return evaluationFunction;

        if (this.goalBoard.equals(currentBoard)) {
            return 0;
        }

        int minThreshold = Integer.MAX_VALUE;
        PriorityQueue<Pair<String, Integer>> possibleMoves = this.addPossibleMoves(currentBoard);

        for(Pair<String, Integer> move: possibleMoves){
            if (this.solutionDirections.isEmpty() || !this.areOpposite(this.solutionDirections.peek(), move.getKey())){
                this.solutionDirections.push(move.getKey());
                this.moveBoard(currentBoard, move.getKey());

                Integer nextThreshold = this.search(currentBoard, threshold);

                if (nextThreshold == 0) return 0;
                if (nextThreshold < minThreshold) minThreshold = nextThreshold;

                this.solutionDirections.pop();
                this.reverseMove(currentBoard, move.getKey());
            }
        }
        return minThreshold;
    }

    private PriorityQueue<Pair<String, Integer>> addPossibleMoves(Board currentBoard){
        Integer size= currentBoard.getSize();
        Integer rowIndexOfZero = currentBoard.getRowIndexOfZero();
        Integer collIndexOfZero = currentBoard.getCollIndexOfZero();

        PriorityQueue<Pair<String, Integer>> possibleMoves = new PriorityQueue<>(new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair<String, Integer> stringIntegerPair, Pair<String, Integer> t1) {
                return stringIntegerPair.getValue().compareTo(t1.getValue());
            }
        });

        if (isPossibleMove(rowIndexOfZero - 1, collIndexOfZero, size)){
            currentBoard.swapTiles(rowIndexOfZero, collIndexOfZero, rowIndexOfZero - 1, collIndexOfZero);
            possibleMoves.add(new Pair<>(DOWN_DIRECTION, currentBoard.evaluationFunction() + COST_OF_STEP));
            currentBoard.swapTiles(rowIndexOfZero - 1,collIndexOfZero, rowIndexOfZero, collIndexOfZero);
        }

        if (isPossibleMove(rowIndexOfZero, collIndexOfZero - 1, size)){
            currentBoard.swapTiles(rowIndexOfZero, collIndexOfZero, rowIndexOfZero, collIndexOfZero - 1);
            possibleMoves.add(new Pair<>(RIGHT_DIRECTION, currentBoard.evaluationFunction() + COST_OF_STEP));
            currentBoard.swapTiles(rowIndexOfZero,collIndexOfZero - 1, rowIndexOfZero, collIndexOfZero);
        }

        if (isPossibleMove(rowIndexOfZero + 1, collIndexOfZero, size)){
            currentBoard.swapTiles(rowIndexOfZero, collIndexOfZero, rowIndexOfZero + 1, collIndexOfZero);
            possibleMoves.add(new Pair<>(UP_DIRECTION, currentBoard.evaluationFunction() + COST_OF_STEP));
            currentBoard.swapTiles(rowIndexOfZero + 1,collIndexOfZero, rowIndexOfZero, collIndexOfZero);
        }

        if (isPossibleMove(rowIndexOfZero, collIndexOfZero + 1, size)){
            currentBoard.swapTiles(rowIndexOfZero, collIndexOfZero, rowIndexOfZero, collIndexOfZero + 1);
            possibleMoves.add(new Pair<>(LEFT_DIRECTION, currentBoard.evaluationFunction() + COST_OF_STEP));
            currentBoard.swapTiles(rowIndexOfZero,collIndexOfZero + 1, rowIndexOfZero, collIndexOfZero);
        }
        return possibleMoves;
    }

    private void moveBoard(Board board, String direction){
        Integer rowIndexOfZero = board.getRowIndexOfZero();
        Integer collIndexOfZero = board.getCollIndexOfZero();

        if (direction.equals(DOWN_DIRECTION)){
            board.swapTiles(rowIndexOfZero, collIndexOfZero, rowIndexOfZero - 1, collIndexOfZero);
        } else if (direction.equals(UP_DIRECTION)){
            board.swapTiles(rowIndexOfZero, collIndexOfZero, rowIndexOfZero + 1, collIndexOfZero);
        } else if (direction.equals(RIGHT_DIRECTION)){
            board.swapTiles(rowIndexOfZero, collIndexOfZero, rowIndexOfZero, collIndexOfZero - 1);
        } else {
            board.swapTiles(rowIndexOfZero, collIndexOfZero, rowIndexOfZero, collIndexOfZero + 1);
        }
        Integer cost = board.getCost();
        board.setCost(cost + COST_OF_STEP);
    }

    private void reverseMove(Board board, String direction){

        Integer rowIndexOfZero = board.getRowIndexOfZero();
        Integer collIndexOfZero = board.getCollIndexOfZero();
        if (direction.equals(DOWN_DIRECTION)){
            board.swapTiles(rowIndexOfZero, collIndexOfZero, rowIndexOfZero + 1, collIndexOfZero);
        } else if (direction.equals(UP_DIRECTION)){
            board.swapTiles(rowIndexOfZero, collIndexOfZero, rowIndexOfZero - 1, collIndexOfZero);
        } else if (direction.equals(RIGHT_DIRECTION)){
            board.swapTiles(rowIndexOfZero, collIndexOfZero, rowIndexOfZero, collIndexOfZero + 1);
        } else {
            board.swapTiles(rowIndexOfZero, collIndexOfZero, rowIndexOfZero, collIndexOfZero - 1);
        }
        Integer cost = board.getCost();
        board.setCost(cost - COST_OF_STEP);
    }

    private boolean areOpposite(String directionOne, String directionTwo){
        return (directionOne.equals(LEFT_DIRECTION) && directionTwo.equals(RIGHT_DIRECTION)) ||
                (directionOne.equals(RIGHT_DIRECTION) && directionTwo.equals(LEFT_DIRECTION)) ||
                (directionOne.equals(DOWN_DIRECTION) && directionTwo.equals(UP_DIRECTION)) ||
                (directionOne.equals(UP_DIRECTION) && directionTwo.equals(DOWN_DIRECTION));
    }

    private boolean isPossibleMove(Integer row, Integer coll, Integer size){
        return row >=0 && coll >= 0 && row < size && coll < size;
    }

}
