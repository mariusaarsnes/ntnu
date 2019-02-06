import java.util.ArrayList;

public class Genotype implements Comparable<Genotype> {

    double fitness;

    int distance, durationOverload, capacityOverload,invalidFactor;
    ArrayList<Integer>[][] routes;

    public Genotype(Problem problem) {
        this.durationOverload = 0;
        this.capacityOverload = 0;
        this.invalidFactor = 0;
        ArrayList<Integer>[][] routing = new ArrayList[problem.numberOfDepots][problem.maxVehiclesPerDepot];

        for (int customer = 0; customer < problem.customers.length; customer++) {
            int depot = problem.closestDepotToCustomers[customer];

            int vehicle = (int) (Math.random() * problem.maxVehiclesPerDepot);

            if (routing[depot][vehicle] == null) {
                routing[depot][vehicle] = new ArrayList<Integer>();
            }

            routing[depot][vehicle].add(customer);

        }

        //TODO: Ask TA if it is necessary to have final depot in genotype or if this can be derived in the phenotype.
        // If not, then remove this part, else update distance and validity tests
        /*
        for (int i = 0; i < routing.length; i++) {
            System.out.println("Depot: " + i);
            for (int j = 0; j < routing[i].length; j++) {
                // In case the route for a given vehicle is empty we skip it, since it will never drive anywhere
                System.out.println("Route: " + routing[i][j]);
                if (routing[i][j].size() == 0) {
                    continue;
                }
                int lastCustomer = routing[i][j].get(routing[i][j].size() - 1);
                routing[i][j].add(problem.closestDepotToCustomers[lastCustomer]);
            }
        }
        */
        this.routes = routing;
    }


    public boolean isValid(Problem problem) {
        for (int d = 0; d < this.routes.length; d++) {
            for (int r = 0; r < this.routes[d].length; r++) {
                if (!isValidRoute(this.routes[d][r], problem)) {
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
    private double getDistanceOfRoute(ArrayList<Integer> route,int depotNr, Problem problem) {
        int i = 0;
        double distance = problem.neighbourMatrix[depotNr][route.get(0)];
        for (i = 1; i< route.size(); i++){
            distance += problem.neighbourMatrix[route.get(i-1)][route.get(i)];
        }
        distance += problem.neighbourMatrix[route.get(i)][problem.depots[problem.closestDepotToCustomers[i]].getNr()];
        return distance;
    }

    public void updateFitnessVariables(Problem problem) {
        this.durationOverload = 0;
        this.capacityOverload = 0;
        this.distance = 0;
        this.invalidFactor = 0;


        for(int depot = 0; depot < this.routes.length; depot++) {
            int maxDuration = problem.depots[depot].maxDuration;
            int maxLoad = problem.depots[depot].maxLoad;

            for (ArrayList<Integer> route: this.routes[depot]){
                this.distance += getDistanceOfRoute(route, problem.depots[depot].getNr(),problem);
                int routeDuration = getDurationOfRoute(route, problem);
                int routeDemand = getDemandOfRoute(route, problem);

                if(routeDuration> maxDuration) {
                    this.durationOverload += routeDuration - maxDuration;
                    this.invalidFactor += routeDuration / maxDuration;
                }
                if (routeDemand > maxLoad) {
                    this.capacityOverload += routeDemand - maxLoad;
                    this.invalidFactor += routeDemand/maxLoad;
                }
            }
        }
    }
    public double getFitness() {
        return this.fitness;
    }

    public void updateFitness(Problem problem) {
        //Calculate the fitness. here we penalise an individual depending on how many invalid routes it has.
        this.fitness = this.distance + this.durationOverload + this.capacityOverload + 2*this.invalidFactor*(problem.maxDistance);
    }
    @Override
    public int compareTo(Genotype genotype) {
        return (int) Math.floor(this.fitness - genotype.fitness);
    }
}
