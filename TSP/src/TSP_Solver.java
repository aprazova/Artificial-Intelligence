import java.util.*;

public class TSP_Solver {
    private final Integer MAX_COORDINATE = 10000;
    private final Integer MAX_GENERATIONS = 500;
    private final Integer MAX_INDIVIDUAL;
    private final Integer SAVED_INDIVIDUAL;

    private final List<Integer> GENERATION_FOR_PRINT = new ArrayList<Integer>(){
        {
            add(MAX_GENERATIONS/5);
            add(MAX_GENERATIONS/4);
            add(MAX_GENERATIONS/3);
            add(MAX_GENERATIONS/2);
            add(MAX_GENERATIONS);
        }
    };

    private List<Town> towns;
    private Integer numberOfTowns;
    private List<Town[]> population;
    private List<Town[]> generatedIndividuals;
    private Random random;


    TSP_Solver(Integer numberOfTowns) {
        this.towns = new ArrayList<>();
        this.population = new ArrayList<>();
        this.numberOfTowns = numberOfTowns;
        this.random = new Random();
        this.MAX_INDIVIDUAL = this.numberOfTowns/3;
        this.SAVED_INDIVIDUAL = MAX_INDIVIDUAL/3;
        this.generatedIndividuals = new ArrayList<>();

        for (int i = 0; i < numberOfTowns; i++){
            int coordinateX = this.random.nextInt(this.MAX_COORDINATE);
            int coordinateY = this.random.nextInt(this.MAX_COORDINATE);
            this.addTown(coordinateX, coordinateY);
        }
    }

    public void solveProblem(){

        this.population.add(this.towns.toArray(new Town[this.numberOfTowns]));
        this.makePopulation(this.MAX_INDIVIDUAL - 1);

        int generation = 0;
        while (generation < this.MAX_GENERATIONS){
            generation++;

            if (this.GENERATION_FOR_PRINT.contains(generation)){
                System.out.println("Population: " + generation);
                this.printTowns(this.population.get(0));
            }

            this.sortPopulation(this.population);

            List<Town[]> nextPopulation = new ArrayList<>();
            for ( int i = 0; i < this.SAVED_INDIVIDUAL; i++){
                nextPopulation.add(this.population.get(i));
            }
            
            for (int i = this.SAVED_INDIVIDUAL; i <= this.population.size()/2; i++){
                nextPopulation.addAll(this.crossover(this.population.get(i-1), this.population.get(i)));
            }

            for (int i = 1; i < nextPopulation.size(); i++){
                do {
                    this.swapMutation(nextPopulation.get(i));
                } while (isCheckedRoute(nextPopulation.get(i)));
                this.generatedIndividuals.add(nextPopulation.get(i).clone());
            }

            this.population = nextPopulation;
        }
    }

    private void addTown(Integer x, Integer y){
        this.towns.add(new Town(x,y));
    }

    private void printTowns(Town[] individual){
        System.out.println("Best route:");
        for (Town town : individual) {
            System.out.print("(" + town.getX() + ", " + town.getY() + ") ");
        }
        System.out.println("Cost: " + this.getRouteCost(individual));
        System.out.println();
    }

    private void makePopulation(Integer numberOfIndividuals){
        for (int i=0; i<numberOfIndividuals; i++){
            List<Town> route = new ArrayList<>(this.towns);
            Town[] routeToArray;
            do {
                Collections.shuffle(route);
                routeToArray = route.toArray(new Town[this.numberOfTowns]);
            } while(isCheckedRoute(routeToArray));
            this.population.add(routeToArray);
            this.generatedIndividuals.add(routeToArray.clone());
        }
    }

    private Long getRouteCost(Town[] individual){
        long routeCost = 0;
        for (int i = 1; i < individual.length; i++){
            routeCost += individual[i].getDistanceTo(individual[i-1]);
        }
        return routeCost;
    }

    private List<Town[]> crossover(Town[] firstParent, Town[] secondParent){
        int firstIndex = this.random.nextInt(this.numberOfTowns);
        int secondIndex = this.random.nextInt(this.numberOfTowns);

        int leftIndex = Math.min(firstIndex, secondIndex);
        int rightIndex = Math.max(firstIndex, secondIndex);

        Town[] firstChild = new Town[this.numberOfTowns];
        Town[] secondChild = new Town[this.numberOfTowns];

        for (int i = leftIndex; i<=rightIndex; i++){
            firstChild[i] = secondParent[i];
            secondChild[i] = firstParent[i];
        }

        this.fillChild(firstParent, firstChild, rightIndex);
        this.fillChild(secondParent, secondChild, rightIndex);

        List<Town[]> childes = new ArrayList<>();
        childes.add(firstChild);
        childes.add(secondChild);
        return childes;
    }

    private void fillChild(Town[] parent, Town[] child, Integer rightIndex){
        int indexInChild = rightIndex + 1;
        int indexInParent = rightIndex + 1;
        do {
            if (indexInParent == this.numberOfTowns){
                indexInParent = 0;
            }
            if (!isVisitedTown(child, parent[indexInParent])){
                if (indexInChild == this.numberOfTowns){
                    indexInChild = 0;
                }

                child[indexInChild] = parent[indexInParent];
                indexInChild++;

            }

            indexInParent++;
        } while (indexInParent != rightIndex + 1);

    }

    private void sortPopulation(List<Town[]> population){
        population.sort(new Comparator<Town[]>() {
            @Override
            public int compare(Town[] t1, Town[] t2) {
                Long routeCostT1 = getRouteCost(t1);
                Long routeCostT2 = getRouteCost(t2);
                return Long.compare(routeCostT1, routeCostT2);
            }
        });
    }

    private boolean isVisitedTown(Town[] route, Town town){
        for(Town visitedTown: route){
            if (town.equals(visitedTown)){
                return true;
            }
        }
        return false;
    }

    private void swapMutation(Town[] individual){
        int firstGene = random.nextInt(this.numberOfTowns);
        int secondGene = random.nextInt(this.numberOfTowns);
        Town temp = individual[firstGene];
        individual[firstGene] = individual[secondGene];
        individual[secondGene] = temp;
    }

    private boolean isCheckedRoute(Town[] route){
        for (Town[] checkedRoute:
             this.generatedIndividuals) {
            if(Arrays.deepEquals(checkedRoute, route)){
                return true;
            }
        }
        return false;
    }

}
