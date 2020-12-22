import java.util.*;

public class LeapFrogPuzzle {

    private static char LEFT_FROGS = '>';
    private static char RIGHT_FROGS = '<';
    private static char FREE_POSITION = '_';
    private static String DEAD_STATE = String.valueOf(RIGHT_FROGS).repeat(2) + FREE_POSITION + String.valueOf(LEFT_FROGS).repeat(2);

    private String start;
    private String goal;
    private List<String> path;

    public LeapFrogPuzzle(Integer n) {
        this.path = new ArrayList<>();
        this.start = String.valueOf(LEFT_FROGS).repeat(n) + FREE_POSITION + String.valueOf(RIGHT_FROGS).repeat(n);
        this.goal = String.valueOf(RIGHT_FROGS).repeat(n) + FREE_POSITION + String.valueOf(LEFT_FROGS).repeat(n);
    }


    private String jumpFrog(String currentState, int indexOfFreePosition, int indexOfFrog){
        StringBuilder newState = new StringBuilder(currentState);
        char frog = newState.charAt(indexOfFrog);
        char freePosition = newState.charAt(indexOfFreePosition);
        newState.setCharAt(indexOfFreePosition, frog);
        newState.setCharAt(indexOfFrog, freePosition);
        return newState.toString();
    }

    private List<String> getPossibleMoves(String currentState){
        List<String> possibleMoves = new ArrayList<>();

        for(int j=0; j<currentState.length(); j++){

            if(j > 0 && currentState.charAt(j) == FREE_POSITION &&
                    currentState.charAt(j - 1) == LEFT_FROGS){
                possibleMoves.add(jumpFrog(currentState, j, j - 1));
            }

            if(j > 1 && currentState.charAt(j) == FREE_POSITION &&
                    currentState.charAt(j - 2) == LEFT_FROGS){
                possibleMoves.add(jumpFrog(currentState, j, j - 2));
            }

            if(j + 1 < currentState.length() && currentState.charAt(j) == FREE_POSITION &&
                    currentState.charAt(j + 1) == RIGHT_FROGS){
                possibleMoves.add(jumpFrog(currentState, j, j + 1));
            }

            if(j + 2 < currentState.length() && currentState.charAt(j) == FREE_POSITION &&
                    currentState.charAt(j + 2) == RIGHT_FROGS){
                possibleMoves.add(jumpFrog(currentState, j, j + 2));
            }
        }

        return possibleMoves;
    }

    private boolean dfs(String currentState){
        if (currentState.equals(this.goal)) return true;

        if (currentState.contains(DEAD_STATE)) return false;

        List<String> possibleMoves = this.getPossibleMoves(currentState);

        for(int i =0; i<possibleMoves.size(); i++){
            if(this.dfs(possibleMoves.get(i))){
                this.path.add(possibleMoves.get(i));
                return true;
            }
        }
        return false;
    }

    public void findPath(){
        if(this.path.size() == 0){
            this.dfs(this.start);
            this.path.add(this.start);
            Collections.reverse(this.path);
        }
    }

    public void printPath(){
        this.path.forEach(System.out::println);
    }
}
