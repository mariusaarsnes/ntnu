import java.util.ArrayList;
import java.util.Collections;

public class Genotype implements Comparable<Genotype> {

    double fitness;

    int distance, durationOverload, capacityOverload, invalidFactor;
    ArrayList<Integer>[][] routes;

    public Genotype(Problem problem) {
        this.durationOverload = 0;
        this.capacityOverload = 0;
        this.invalidFactor = 0;
        ArrayList<Integer>[][] routing = new ArrayList[problem.numberOfDepots][problem.maxVehiclesPerDepot];

        for(int i = 0; i < routing.length; i++) {
            for (int j = 0;j < routing[i].length; j++) {
                routing[i][j] = new ArrayList<Integer>();
            }
        }

        for (int customer = 0; customer < problem.customers.length; customer++) {
            int depot = problem.closestDepotToCustomers[customer];

            int vehicle = (int) (Math.random() * problem.maxVehiclesPerDepot);

            routing[depot][vehicle].add(customer);

        }
        this.routes = routing;
        this.updateFitnessVariables(problem);
        this.updateFitness(problem);
    }

    public void mutate() {
        int type = (int) Math.round(Math.random());

        switch (type) {
            case 0:
                intraRouteMutate();
                break;
            case 1:
                extraRouteMutate();
        }

    }

    private void intraRouteMutate() {
        int type = (int) Math.round(Math.random());

        switch (type) {
            case 0:
                invertRandomRoute();
                break;
            case 1:
                swapCustomersInRoute();
                break;
        }

    }

    private void extraRouteMutate() {
        int type = (int) Math.round(Math.random());
        switch (type) {
            case 0:
                moveCustomerToRandomRoute();
                break;
            case 1:
                swapCustomersBetweenRoutes();
                break;
        }
    }

    private void invertRandomRoute() {
        int depot = (int) (Math.random() * this.routes.length);
        int vehicle = (int) (Math.random() * this.routes[depot].length);
        int startPos = (int) (Math.random() * this.routes[depot][vehicle].size());
        int endPos = (int) (Math.random() * this.routes[depot][vehicle].size());
        Collections.reverse(this.routes[depot][vehicle]);


    }

    private void swapCustomersInRoute() {
        int depot = (int) (Math.random() * this.routes.length);
        int vehicle = (int) (Math.random() * this.routes[depot].length);

        int customer1 = (int) (Math.random() * this.routes[depot][vehicle].size());
        int customer2 = (int) (Math.random() * this.routes[depot][vehicle].size());

        int temp = this.routes[depot][vehicle].get(customer1);
        this.routes[depot][vehicle].set(customer1, this.routes[depot][vehicle].get(customer2));
        this.routes[depot][vehicle].set(customer2, temp);
    }

    private void moveCustomerToRandomRoute() {
        int depot = (int) (Math.random() * this.routes.length);

        int vehicle1 = (int) (Math.random() * this.routes[depot].length);
        int vehicle2 = (int) (Math.random() * this.routes[depot].length);

        int customer = (int) (Math.random() * this.routes[depot][vehicle1].size());

        this.routes[depot][vehicle2].add(customer, this.routes[depot][vehicle1].remove(customer));

    }

    private void swapCustomersBetweenRoutes() {
        int depot1 = (int) (Math.random() * this.routes.length);
        int depot2 = (int) (Math.random() * this.routes.length);

        int vehicle1 = (int) (Math.random() * this.routes[depot1].length);
        int vehicle2 = (int) (Math.random() * this.routes[depot2].length);

        int customer1 = (int) (Math.random() * this.routes[depot1][vehicle1].size());
        int customer2 = (int) (Math.random() * this.routes[depot2][vehicle2].size());

        int temp = this.routes[depot1][vehicle1].get(customer1);
        this.routes[depot1][vehicle1].set(customer1, this.routes[depot2][vehicle2].get(customer2));
        this.routes[depot2][vehicle2].set(customer2, temp);

    }

    public boolean isValid(Problem problem) {
        for (ArrayList<Integer>[] depot : this.routes) {
            for (ArrayList<Integer> route : depot) {
                if (!isValidRoute(route, problem)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidRoute(ArrayList<Integer> route, Problem problem) {
        int duration = getDurationOfRoute(route, problem);
        int capacity = getDemandOfRoute(route, problem);
        return duration > problem.depots[0].maxDuration || capacity > problem.depots[0].maxLoad;
    }

    private int getDemandOfRoute(ArrayList<Integer> route, Problem problem) {
        int demand = 0;
        for (int customer : route) {
            demand += problem.customers[customer].demand;
        }
        return demand;
    }

    private int getDurationOfRoute(ArrayList<Integer> route, Problem problem) {
        int duration = 0;
        for (int customer : route
        ) {
            duration += problem.customers[customer].serviceDuration;
        }
        return duration;
    }

    private double getDistanceOfRoute(ArrayList<Integer> route, int depotNr, Problem problem) {
        int i = 0;
        double distance = problem.neighbourMatrix[depotNr][route.get(0)];
        for (i = 1; i < route.size(); i++) {
            distance += problem.neighbourMatrix[route.get(i - 1)][route.get(i)];
        }
        distance += problem.neighbourMatrix[route.get(i - 1)][problem.depots[problem.closestDepotToCustomers[i - 1]].getNr()];
        return distance;
    }

    public void updateFitnessVariables(Problem problem) {
        this.durationOverload = 0;
        this.capacityOverload = 0;
        this.distance = 0;
        this.invalidFactor = 0;


        for (int depot = 0; depot < this.routes.length; depot++) {
            int maxDuration = problem.depots[depot].maxDuration;
            int maxLoad = problem.depots[depot].maxLoad;

            for (ArrayList<Integer> route : this.routes[depot]) {
                if (route.size() > 0) {
                    this.distance += getDistanceOfRoute(route, problem.depots[depot].getNr(), problem);
                    int routeDuration = getDurationOfRoute(route, problem);
                    int routeDemand = getDemandOfRoute(route, problem);

                    if (routeDuration > maxDuration) {
                        this.durationOverload += routeDuration - maxDuration;
                        this.invalidFactor += routeDuration / maxDuration;
                    }
                    if (routeDemand > maxLoad) {
                        this.capacityOverload += routeDemand - maxLoad;
                        this.invalidFactor += routeDemand / maxLoad;
                    }
                }
            }
        }
    }

    public double getFitness() {
        return this.fitness;
    }

    public void updateFitness(Problem problem) {
        //Calculate the fitness. here we penalise an individual depending on how many invalid routes it has.
        this.fitness = this.distance + this.durationOverload + this.capacityOverload + 2 * this.invalidFactor * (problem.maxDistance);
    }

    @Override
    public int compareTo(Genotype genotype) {
        return (int) (genotype.fitness - this.fitness);
    }
}
