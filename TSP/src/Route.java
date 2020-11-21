import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Route {
    private Town[] route;
    private Integer length;

    Route(Integer numberOfTowns){
        this.route = new Town[numberOfTowns];
        this.length = numberOfTowns;
    }

    Route(Town[] towns){
        this.route = towns.clone();
        this.length = towns.length;
    }

    public void addTown(Town town){
        int size = this.getLength();
        this.route[size] = town;
    }

    public Integer getLength(){
        return this.route.length;
    }

    public Town[] getRoute(){
        return this.route;
    }

    public void setTown(Town town, Integer position){
        this.route[position] = town;
    }

    public Town getTown(Integer position){
        return this.route[position];
    }

//    @Override
//    public int compareTo(Route route) {
//        return this.route.compareTo (route.getRoute());
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        Route route1 = (Route) o;
        return Arrays.deepEquals(this.route, route1.route);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(length);
        result = 31 * result + Arrays.deepHashCode(this.route);
        return result;
    }
}
