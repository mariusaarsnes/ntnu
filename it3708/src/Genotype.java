import java.util.ArrayList;
import java.util.Collections;

public class Genotype implements Comparable<Genotype>, Cloneable {

    double fitness;

    int distance, durationOverload, capacityOverload, invalidFactor;
    ArrayList<Integer>[][] routes;

    public Genotype(Problem problem, ArrayList<Integer>[][] routes) {
        this.durationOverload = 0;
        this.capacityOverload = 0;
        this.invalidFactor = 0;

        this.routes = routes;
    }

    public Genotype(Problem problem) {
        this.durationOverload = 0;
        this.capacityOverload = 0;
        this.invalidFactor = 0;
        ArrayList<Integer>[][] routing = new ArrayList[problem.numberOfDepots][problem.maxVehiclesPerDepot];

        for (int i = 0; i < routing.length; i++) {
            for (int j = 0; j < routing[i].length; j++) {
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

    public void removeCustomerFromRoutes(int customer) {
        for (ArrayList<Integer>[] depot : this.routes) {
            for (ArrayList<Integer> route : depot) {
                route.removeIf(customerInRoute -> customerInRoute == customer);
            }
        }
    }

    public void addNewCustomerToRoutes(Problem problem, int newCustomer) {
        int[] location = new int[3];
        double minDistance = Double.MAX_VALUE;
        for (int d = 0; d < this.routes.length; d++) {
            for (int r = 0; r < this.routes[d].length; r++) {
                int prevNr = problem.depots[d].getNr();
                for (int c = 0; c < this.routes[d][r].size()+1; c++) {
                    int nextNr;
                    if (c < routes[d][r].size()) nextNr = problem.customers[this.routes[d][r].get(c)].getNr();
                    else nextNr = problem.closestDepotToCustomers[newCustomer];
                    double distance = problem.neighbourMatrix[prevNr][newCustomer]
                            + problem.neighbourMatrix[newCustomer][nextNr];
                    prevNr = nextNr;

                    if (minDistance > distance) {
                        location = new int[]{d, r, c};
                        minDistance = distance;
                    }
                }
            }
        }
        routes[location[0]][location[1]].add(location[2], newCustomer);
    }

    private ArrayList<Integer> scheduleRoute(Problem problem, ArrayList<Integer> route, int depot) {
        ArrayList<Integer> schedule = new ArrayList<>();

        int closestCustomer = route.get(0);
        double closestDistance = problem.neighbourMatrix[depot][closestCustomer];
        for (Integer customer : route) {
            if (problem.neighbourMatrix[depot][customer] < closestDistance) {
                closestDistance = problem.neighbourMatrix[depot][customer];
                closestCustomer = customer;
            }
        }
        schedule.add(route.remove(route.indexOf(closestCustomer)));

        while (route.size() > 0) {
            closestCustomer = route.get(0);
            closestDistance = problem.neighbourMatrix[schedule.size() - 1][closestCustomer];
            for (Integer customer : route) {
                if (problem.neighbourMatrix[schedule.size() - 1][customer] < closestDistance) {
                    closestDistance = problem.neighbourMatrix[schedule.size() - 1][customer];
                    closestCustomer = customer;
                }
            }
            schedule.add(route.remove(route.indexOf(closestCustomer)));
        }
        return schedule;
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
        int len = 0, depot = 0, vehicle = 0;

        while (len == 0) {
            depot = (int) (Math.random() * this.routes.length);
            vehicle = (int) (Math.random() * this.routes[depot].length);
            len = this.routes[depot][vehicle].size();
        }

        int customer1 = (int) (Math.random() * this.routes[depot][vehicle].size());
        int customer2 = (int) (Math.random() * this.routes[depot][vehicle].size());

        int temp = this.routes[depot][vehicle].get(customer1);
        this.routes[depot][vehicle].set(customer1, this.routes[depot][vehicle].get(customer2));
        this.routes[depot][vehicle].set(customer2, temp);
    }

    private void moveCustomerToRandomRoute() {
        int len = 0, depot = 0, vehicle1 = 0, vehicle2 = 0;

        while (len == 0) {
            depot = (int) (Math.random() * this.routes.length);

            vehicle1 = (int) (Math.random() * this.routes[depot].length);
            vehicle2 = (int) (Math.random() * this.routes[depot].length);

            len = this.routes[depot][vehicle1].size() + this.routes[depot][vehicle2].size();

        }

        if (this.routes[depot][vehicle1].size() > 0) {
            int customer1 = (int) (Math.random() * this.routes[depot][vehicle1].size());
            this.routes[depot][vehicle2].add(this.routes[depot][vehicle1].remove(customer1));

        } else {
            int customer2 = (int) (Math.random() * this.routes[depot][vehicle2].size());
            this.routes[depot][vehicle1].add(this.routes[depot][vehicle2].remove(customer2));
        }


    }

    private void swapCustomersBetweenRoutes() {
        int len1 = 0, len2 = 0, depot1 = 0, depot2 = 0, vehicle1 = 0, vehicle2 = 0;
        while (len1 == 0 || len2 == 0) {
            depot1 = (int) (Math.random() * this.routes.length);
            depot2 = (int) (Math.random() * this.routes.length);

            vehicle1 = (int) (Math.random() * this.routes[depot1].length);
            vehicle2 = (int) (Math.random() * this.routes[depot2].length);

            len1 = this.routes[depot1][vehicle1].size();
            len2 = this.routes[depot2][vehicle2].size();
        }

        int customer1 = (int) (Math.random() * this.routes[depot1][vehicle1].size());
        int customer2 = (int) (Math.random() * this.routes[depot2][vehicle2].size());

        int temp = this.routes[depot1][vehicle1].get(customer1);
        this.routes[depot1][vehicle1].set(customer1, this.routes[depot2][vehicle2].get(customer2));
        this.routes[depot2][vehicle2].set(customer2, temp);

    }

    public boolean isValidRoutes(ArrayList<Integer>[][] routes, Problem problem) {
        for (ArrayList<Integer>[] depot : routes) {
            for (ArrayList<Integer> route : depot) {
                if (!isValidRoute(route, problem)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidRoute(ArrayList<Integer> route, Problem problem) {
        //int duration = getDurationOfRoute(route, problem);
        int capacity = getDemandOfRoute(route, problem);
        return /*duration > problem.depots[0].maxDuration || */capacity > problem.depots[0].maxLoad;
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

    public double getDistanceOfRoutes(Problem problem) {
        double distance = 0;
        for (int d = 0; d < this.routes.length; d++) {
            for (int r = 0; r < this.routes[d].length; r++) {
                if (this.routes[d][r].size() > 0) {
                    distance += getDistanceOfRoute(this.routes[d][r], d, problem);
                }
            }
        }
        return distance;
    }

    private double getDistanceOfRoute(ArrayList<Integer> route, int depotNr, Problem problem) {
        int i = 0;
        double distance = problem.neighbourMatrix[depotNr][route.get(0)];
        for (i = 1; i < route.size(); i++) {
            distance += problem.neighbourMatrix[route.get(i - 1)][route.get(i)];
        }

        distance += problem.neighbourMatrix[route.get(route.size()-1)][problem.depots[problem.closestDepotToCustomers[route.get(route.size()-1)]].getNr()];
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
        return (int) (this.fitness - genotype.fitness);
    }
}
