import java.util.Objects;

public class Town{
    private Integer x;
    private  Integer y;

    Town(Integer x, Integer y){
        this.x = x;
        this.y = y;
    }

    public Integer getX(){
        return this.x;
    }

    public Integer getY(){
        return this.y;
    }

    public double getDistanceTo(Town town){
        return Math.sqrt(Math.pow(town.getX() - this.getX(), 2) + Math.pow(town.getY() - this.getY(),2));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Town)) return false;
        Town town = (Town) o;
        return x.equals(town.x) &&
                y.equals(town.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
