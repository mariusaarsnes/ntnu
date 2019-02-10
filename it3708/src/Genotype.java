import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Genotype implements Comparable<Genotype>, Cloneable {

    double fitness;

    int distance, durationOverload, capacityOverload, invalidFactor;
    ArrayList<Integer>[][] routes;

    public Genotype(Problem problem, ArrayList<Integer>[][] routes) {
        this.durationOverload = 0;
        this.capacityOverload = 0;
        this.invalidFactor = 0;
        this.routes = new ArrayList[problem.numberOfDepots][problem.maxVehiclesPerDepot];
        for (int i = 0; i < routes.length; i++) {
            for (int j = 0; j < routes[i].length; j++) {
                this.routes[i][j] = new ArrayList<>(routes[i][j]);
            }
        }
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
    }

    public void removeDuplicateCustomer(Problem problem, int customerNr) {
        ArrayList<int[]> positions = new ArrayList<>();
        for (int d = 0; d < this.routes.length; d++) {
            for (int r = 0; r < this.routes[d].length; r++) {
                for (int c = 0; c < this.routes[d][r].size(); c++) {
                    if (this.routes[d][r].get(c) == customerNr) {
                        positions.add(new int[]{d, r, c});
                    }
                }
            }
        }
        int[] position = positions.get((int) (Math.random() * positions.size()));
        this.routes[position[0]][position[1]].remove(position[2]);
    }


    public void removeCustomerFromRoutes(int customer) {
        for (ArrayList<Integer>[] depot : this.routes) {
            for (ArrayList<Integer> route : depot) {
                route.removeIf(customerInRoute -> customerInRoute == customer);
            }
        }
    }

    public void addNewCustomerToRoutes(int newCustomer) {
        int d = (int) (Math.random() * this.routes.length);
        int r = (int) (Math.random() * this.routes[d].length);
        this.routes[d][r].add(newCustomer);
    }

    public void addNewCustomerToRoutes(Problem problem, int newCustomer) {
        int[] location = new int[3];
        double minDistance = Double.MAX_VALUE;

        for (int d = 0; d < this.routes.length; d++) {
            for (int r = 0; r < this.routes[d].length; r++) {
                int[] routeLocation = new int[3];
                double minRouteDistance = Double.MAX_VALUE;
                int prevNr = problem.depots[d].getNr();
                for (int c = 0; c < this.routes[d][r].size() + 1; c++) {
                    int nextNr;
                    if (c < routes[d][r].size()) nextNr = problem.customers[this.routes[d][r].get(c)].getNr();
                    else nextNr = problem.closestDepotToCustomers[newCustomer];
                    double distance = problem.neighbourMatrix[prevNr][newCustomer]
                            + problem.neighbourMatrix[newCustomer][nextNr];
                    prevNr = nextNr;
                    ArrayList<Integer> tempRoute = new ArrayList<>(this.routes[d][r]);
                    tempRoute.add(c, newCustomer);

                    double temprouteDistance = getDistanceOfRoute(tempRoute, problem.depots[d].getNr(), problem);

                    if (minRouteDistance > distance && temprouteDistance < problem.depots[d].maxDuration) {
                        routeLocation = new int[]{d, r, c};
                        minRouteDistance = distance;
                    }
                }
                if (minDistance > minRouteDistance) {
                    location = new int[]{routeLocation[0], routeLocation[1], routeLocation[2]};
                    minDistance = minRouteDistance;
                }
            }
        }
        if (minDistance != Double.MAX_VALUE) {
            this.routes[location[0]][location[1]].add(location[2], newCustomer);
        } else {
            ArrayList<Integer> emptyRoute = selectRandomEmptyRoute(problem);
            emptyRoute.add(newCustomer);
        }

    }


    public void mutate(Problem problem) {
        int type = (int) Math.round(Math.random()), numMut = (int) (Math.random() * 2 + 1);
        for (int i = 0; i < numMut; i++) {
            switch (type) {
                case 0:
                    intraRouteMutate(problem);
                    break;
                case 1:
                    interRouteMutate(problem);
                    break;
            }
        }

    }

    private void intraRouteMutate(Problem problem) {
        int type = (int) Math.round(Math.random());

        switch (type) {
            case 0:
                invertRandomRoute(problem);
                break;
            case 1:
                swapCustomersInRoute(problem);
                break;
        }

    }

    private void interRouteMutate(Problem problem) {
        if (!isValidRoutes(this.routes, problem)) {
            splitInvalidRoute(problem);
        } else {


            int type = (int) Math.round(Math.random() * 2);
            switch (type) {
                case 0:
                    moveCustomerToRandomRoute(problem);
                    break;
                case 1:
                    swapCustomersBetweenRoutes(problem);
                    break;
                case 2:
                    splitInvalidRoute(problem);
                    break;
            }
        }
    }

    private void invertRandomRoute(Problem problem) {

        ArrayList<Integer> route = selectRandomNonEmptyRoute(problem);

        int endPos = (int) (Math.random() * route.size());
        int startPos = (int) (Math.random() * endPos);

        Collections.reverse(route.subList(startPos, endPos));
    }

    private void swapCustomersInRoute(Problem problem) {

        ArrayList<Integer> route = selectRandomNonEmptyRoute(problem);

        int customer1 = (int) (Math.random() * route.size());
        int customer2 = (int) (Math.random() * route.size());

        int temp = route.get(customer1);
        route.set(customer1, route.get(customer2));
        route.set(customer2, temp);
    }

    private void moveCustomerToRandomRoute(Problem problem) {
        ArrayList<Integer> route = selectRandomNonEmptyRoute(problem);

        int customer = route.get((int) (Math.random() * route.size()));
        removeCustomerFromRoutes(customer);
        addNewCustomerToRoutes(problem, customer);
    }

    private void splitInvalidRoute(Problem problem) {
        ArrayList<Integer> invalidRoute = selectRandomInvalidRoute(problem);
        int splitSize = (int) (Math.random() * invalidRoute.size());
        ArrayList<Integer> splitCustomers = new ArrayList<>(invalidRoute.subList(invalidRoute.size() - splitSize, invalidRoute.size()));
        invalidRoute.subList(invalidRoute.size() - splitSize, invalidRoute.size()).clear();

        ArrayList<Integer> destRoute = selectRandomEmptyRoute(problem);
        if (destRoute == null) {
            moveCustomersToRandomRoutes(problem);
        } else {
            destRoute.addAll(splitCustomers);
        }
    }

    private void moveCustomersToRandomRoutes(Problem problem) {
        ArrayList<Integer> route = selectRandomInvalidRoute(problem);

        ArrayList<Integer> splitCustomers = new ArrayList<>(route.subList(route.size() / 2, route.size()));
        route.subList(route.size() / 2, route.size()).clear();

        for (Integer customer : splitCustomers) {
            addNewCustomerToRoutes(problem, customer);
        }
    }

    private void swapCustomersBetweenRoutes(Problem problem) {
        ArrayList<Integer> route1 = selectRandomNonEmptyRoute(problem);
        ArrayList<Integer> route2 = selectRandomNonEmptyRoute(problem);

        int customer1 = route1.get((int) (Math.random() * route1.size()));
        int customer2 = route2.get((int) (Math.random() * route2.size()));

        route1.set(route1.indexOf(customer1), customer2);
        route2.set(route2.indexOf(customer2), customer1);
    }

    public boolean isValidRoutes(ArrayList<Integer>[][] routes, Problem problem) {
        for (int d = 0; d < routes.length; d++) {
            for (ArrayList<Integer> route : routes[d]) {
                if (!isValidRoute(route, d, problem)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidRoute(ArrayList<Integer> route, int depot, Problem problem) {

        double distance = getDistanceOfRoute(route, problem.depots[depot].getNr(), problem);

        int capacity = getDemandOfRoute(route, problem);
        return distance < problem.depots[depot].maxDuration && capacity < problem.depots[depot].maxLoad;
    }

    public int getDemandOfRoute(ArrayList<Integer> route, Problem problem) {
        int demand = 0;
        for (int customer : route) {
            demand += problem.customers[customer].demand;
        }
        return demand;
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

    private ArrayList<Integer> selectRandomInvalidRoute(Problem problem) {
        List<Integer> possibleDepots = IntStream.rangeClosed(0, problem.numberOfDepots - 1).boxed().collect(Collectors.toList());
        ArrayList<Integer> route = new ArrayList<>();
        int d = possibleDepots.get((int) (Math.random() * possibleDepots.size()));
        while (isValidRoute(route, d, problem) && possibleDepots.size() > 0) {
            int d2 = possibleDepots.get((int) (Math.random() * possibleDepots.size()));
            possibleDepots.removeIf(possibleDepot -> possibleDepot == d2);
            route = selectRandomInvalidRoute(problem, d);
            d = d2;
        }

        return route;
    }

    private ArrayList<Integer> selectRandomInvalidRoute(Problem problem, int depot) {
        List<Integer> possibleRoutes = IntStream.rangeClosed(0, problem.maxVehiclesPerDepot - 1).boxed().collect(Collectors.toList());
        ArrayList<Integer> route = new ArrayList<>();

        while (isValidRoute(route, depot, problem) && possibleRoutes.size() > 0) {
            int r = possibleRoutes.get((int) (Math.random() * possibleRoutes.size()));
            possibleRoutes.removeIf(possibleRoute -> possibleRoute == r);
            route = this.routes[depot][r];
        }
        return route;
    }

    private ArrayList<Integer> selectRandomEmptyRoute(Problem problem) {
        List<Integer> possibleDepots = IntStream.rangeClosed(0, problem.numberOfDepots - 1).boxed().collect(Collectors.toList());
        ArrayList<Integer> route = null;

        while (route == null && possibleDepots.size() > 0) {
            int d = possibleDepots.get((int) (Math.random() * possibleDepots.size()));
            possibleDepots.removeIf(possibleDepot -> possibleDepot == d);
            route = selectRandomEmptyRoute(problem, d);
        }

        return route;
    }

    private ArrayList<Integer> selectRandomEmptyRoute(Problem problem, int depot) {
        List<Integer> possibleRoutes = IntStream.rangeClosed(0, problem.maxVehiclesPerDepot - 1).boxed().collect(Collectors.toList());
        ArrayList<Integer> route = new ArrayList<Integer>();
        route.add(1);

        while (route.size() > 0 && possibleRoutes.size() > 0) {
            int r = possibleRoutes.get((int) (Math.random() * possibleRoutes.size()));
            possibleRoutes.removeIf(possibleRoute -> possibleRoute == r);
            route = this.routes[depot][r];
        }
        return route;
    }

    private ArrayList<Integer> selectRandomNonEmptyRoute(Problem problem) {
        List<Integer> possibleDepots = IntStream.rangeClosed(0, problem.numberOfDepots - 1).boxed().collect(Collectors.toList());
        ArrayList<Integer> route = new ArrayList<>();

        while (route.size() == 0 && possibleDepots.size() > 0) {
            int d = possibleDepots.get((int) (Math.random() * possibleDepots.size()));
            possibleDepots.removeIf(possibleDepot -> possibleDepot == d);
            route = selectRandomNonEmptyRoute(problem, d);
        }
        return route;
    }

    private ArrayList<Integer> selectRandomNonEmptyRoute(Problem problem, int depot) {
        List<Integer> possibleRoutes = IntStream.rangeClosed(0, problem.maxVehiclesPerDepot - 1).boxed().collect(Collectors.toList());
        ArrayList<Integer> route = new ArrayList<Integer>();

        while (route.size() == 0 && possibleRoutes.size() > 0) {
            int r = possibleRoutes.get((int) (Math.random() * possibleRoutes.size()));
            possibleRoutes.removeIf(possibleRoute -> possibleRoute == r);
            route = this.routes[depot][r];
        }
        return route;
    }

    public double getDistanceOfRoute(ArrayList<Integer> route, int depotNr, Problem problem) {
        if (route.size() == 0) {
            return 0;
        }
        int i = 0;
        double distance = problem.neighbourMatrix[depotNr][route.get(0)];
        for (i = 1; i < route.size(); i++) {
            distance += problem.neighbourMatrix[route.get(i - 1)][route.get(i)];
        }

        distance += problem.neighbourMatrix[route.get(route.size() - 1)][problem.depots[problem.closestDepotToCustomers[route.get(route.size() - 1)]].getNr()];
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

                    double routeDuration = getDistanceOfRoute(route, problem.depots[depot].getNr(), problem);
                    this.distance += routeDuration;

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
