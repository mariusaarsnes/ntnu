import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Genotype implements Comparable<Genotype>, Cloneable {

    double fitness;

    int distance, capacityOverload, invalidFactor;
    double durationOverload;
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

    public void addNewCustomerToRandomRoute(Problem problem, int newCustomer) {
        int d = (int) (Math.random() * this.routes.length);
        int r = (int) (Math.random() * this.routes[d].length);

        int bestPos = this.routes[d][r].size();
        double minFitness = Double.MAX_VALUE;
        int prevNr = problem.depots[d].getNr();
        for (int c = 0; c < this.routes[d][r].size() + 1; c++) {
            int nextNr;

            if (c < this.routes[d][r].size()) nextNr = problem.customers[this.routes[d][r].get(c)].getNr();
            else nextNr = problem.closestDepotToCustomers[newCustomer];

            double distance = problem.neighbourMatrix[prevNr][newCustomer] + problem.neighbourMatrix[newCustomer][nextNr];

            prevNr = nextNr;

            ArrayList<Integer> tempRoute = new ArrayList<>(this.routes[d][r]);
            tempRoute.add(c, newCustomer);

            double tempFitness = getFitnessOfRoute(tempRoute, d, problem);

            if (minFitness > tempFitness) {
                bestPos = c;
                minFitness = tempFitness;
            }
        }
        this.routes[d][r].add(bestPos, newCustomer);
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

                    if (minRouteDistance > distance && isValidRoute(tempRoute, d, problem)) {
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
            if (emptyRoute != null) {
                emptyRoute.add(newCustomer);
            } else {
                addNewCustomerToRandomRoute(problem, newCustomer);
            }
        }

    }


    public void mutate(Problem problem) {
        int type = (int) Math.round(Math.random()), numMut = (int) (Math.random() * 3 + 1);
        for (int i = 0; i < numMut; i++) {
            switch (type) {
                case 0:
                    //intraRouteMutate(problem);
                    //break;
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
            return;
        }

        int type = (int) Math.floor(Math.random() * 6);
        switch (type) {
            case 0:
                moveCustomerToRandomRoute(problem);
                break;
            case 1:
                swapCustomersBetweenRoutes(problem);
                break;
            case 2:
                smartSwapCustomerBetweenRoutes(problem);
                break;
            case 3:
                redDistributeRoute(problem);
                break;
            case 4:
                moveCustomersToRandomRoutes(problem);
                break;
            case 5:
                splitWorstRoute(problem);
                break;
        }

    }

    private void redDistributeRoute(Problem problem) {
        ArrayList<Integer> route = selectWorstRoute(problem);

        ArrayList<Integer> tempRoute = new ArrayList<>(route);
        route = new ArrayList<>();
        Collections.shuffle(tempRoute);
        for (Integer customer : tempRoute) {
            addNewCustomerToRoutes(problem, customer);
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
        int numCustomers = (int) (Math.random() * 2);
        ArrayList<Integer> route = selectRandomNonEmptyRoute(problem);

        for (int c = 0; c < numCustomers; c++) {
            int customer;
            if (Math.random() * 100 < 70) {
                customer = selectWorstCustomerInRoute(route, problem);
                removeCustomerFromRoutes(customer);
            } else customer = route.remove((int) (Math.random() * route.size()));
            addNewCustomerToRoutes(problem, customer);
        }
    }

    private void moveCustomerFromWorstRoute(Problem problem) {
        int numMoves = (int) (Math.random() * 3);
        for (int n = 0; n < numMoves; n++) {

            ArrayList<Integer> worstRoute = selectWorstRoute(problem);
            if (worstRoute == null) {
                worstRoute = selectRandomNonEmptyRoute(problem);
            }

            int worstCustomer = selectWorstCustomerInRoute(worstRoute, problem);
            removeCustomerFromRoutes(worstCustomer);
            addNewCustomerToRoutes(problem, worstCustomer);
        }
    }

    private void splitWorstRoute(Problem problem) {
        ArrayList<Integer> route = selectWorstRoute(problem);
        if (route == null) {
            route = selectRandomNonEmptyRoute(problem);
        }

        int splitSize = (int) (Math.random() * route.size());
        ArrayList<Integer> splitCustomers = new ArrayList<>(route.subList(route.size() - splitSize, route.size()));
        route.subList(route.size() - splitSize, route.size()).clear();

        Collections.shuffle(splitCustomers);
        moveCustomersToRandomRoutes(problem, splitCustomers);

    }


    public ArrayList<Integer> selectWorstRoute(Problem problem) {
        ArrayList<Integer> worstRoute = null;
        double worstFitness = Double.MIN_VALUE;
        for (int d = 0; d < problem.numberOfDepots; d++) {
            for (int r = 0; r < problem.maxVehiclesPerDepot; r++) {
                if (this.routes[d][r].size() > 0) {
                    double routeFitness = getWeightedFitnessOfRoute(this.routes[d][r], d, problem);
                    if (worstFitness < routeFitness) {
                        worstRoute = this.routes[d][r];
                        worstFitness = routeFitness;
                    }
                }
            }

        }
        return worstRoute;
    }

    private int selectWorstCustomerInRoute(ArrayList<Integer> route, Problem problem) {
        int worstCustomer = 0;
        double worstDiff = Double.MIN_VALUE;
        int prevNr = problem.closestDepotToCustomers[route.get(0)];
        for (int c = 0; c < route.size(); c++) {
            int nextNr;
            if (c < route.size() - 1) nextNr = route.get(c + 1);
            else nextNr = problem.closestDepotToCustomers[route.get(c)];
            double keepDistance = problem.neighbourMatrix[prevNr][c]
                    + problem.neighbourMatrix[c][nextNr];
            double removeDistance = problem.neighbourMatrix[prevNr][nextNr];
            prevNr = nextNr;


            if (keepDistance - removeDistance > worstDiff) {
                worstCustomer = c;
                worstDiff = keepDistance - removeDistance;
            }
        }
        return worstCustomer;
    }

    public void splitInvalidRoute(Problem problem) {

        for (int n = 0; n < 3; n++) {

            ArrayList<Integer> route = selectRandomInvalidRoute(problem);
            if (route == null) {
                route = selectRandomNonEmptyRoute(problem);
            }

            int splitSize = (int) (Math.random() * route.size());
            ArrayList<Integer> splitCustomers = new ArrayList<>(route.subList(route.size() - splitSize, route.size()));
            route.subList(route.size() - splitSize, route.size()).clear();

            Collections.shuffle(splitCustomers);
            moveCustomersToRandomRoutes(problem, splitCustomers);
        }
    }

    private void moveCustomersToRandomRoutes(Problem problem, ArrayList<Integer> customers) {
        for (Integer customer : customers) {
            addNewCustomerToRoutes(problem, customer);
        }
    }

    private void moveCustomersToRandomRoutes(Problem problem) {
        ArrayList<Integer> route = selectRandomNonEmptyRoute(problem);
        int endPos = (int) (Math.random() * route.size());
        int startPos = (int) (Math.random() * endPos);
        ArrayList<Integer> splitCustomers = new ArrayList<>(route.subList(startPos, endPos));
        route.subList(startPos, endPos).clear();

        Collections.shuffle(splitCustomers);
        for (Integer customer : splitCustomers) {
            addNewCustomerToRoutes(problem, customer);
        }
    }

    private void swapCustomersBetweenRoutes(Problem problem) {
        ArrayList<Integer> route1 = selectRandomNonEmptyRoute(problem);
        ArrayList<Integer> route2 = selectRandomNonEmptyRoute(problem);

        while (route1 == route2) {
            route2 = selectRandomNonEmptyRoute(problem);
        }
        int segmentSize = (int) (Math.random() * Integer.max(route1.size(), route2.size()));
        if (Math.random() * 100 > 50) {
            ArrayList<Integer> tempSegment = new ArrayList<>(route2.subList(0, Integer.min(route2.size(), segmentSize)));
            route2.subList(0, Integer.min(route2.size(), segmentSize)).clear();

            route2.addAll(0, route1.subList(0, Integer.min(segmentSize, route1.size())));
            route1.subList(0, Integer.min(segmentSize, route1.size())).clear();
            route1.addAll(0, tempSegment);
        } else {
            ArrayList<Integer> tempSegment = new ArrayList<>(route2.subList(Integer.max(0, route2.size() - segmentSize), route2.size()));
            route2.subList(Integer.max(0, route2.size() - segmentSize), route2.size()).clear();

            route2.addAll(route1.subList(Integer.max(0, route1.size() - segmentSize), route1.size()));
            route1.subList(Integer.max(0, route1.size() - segmentSize), route1.size());
            route1.addAll(tempSegment);

        }
    }

    public void smartSwapCustomerBetweenRoutes(Problem problem) {
        int d1 = (int) (Math.random() * this.routes.length);
        int d2 = (int) (Math.random() * this.routes.length);

        int r1 = (int) (Math.random() * this.routes[d1].length);
        int r2 = (int) (Math.random() * this.routes[d2].length);
        while (r1 == r2) {
            if (d1 != d2) {
                break;
            }
            r2 = (int) (Math.random() * this.routes[d2].length);
        }
        int prevNr1 = problem.depots[d1].getNr();
        for (int c1 = 0; c1 < this.routes[d1][r1].size(); c1++) {
            int nextNr1;
            if (c1 < this.routes[d1][r1].size() - 1) nextNr1 = this.routes[d1][r1].get(c1 + 1);
            else nextNr1 = problem.depots[problem.closestDepotToCustomers[this.routes[d1][r1].get(c1)]].getNr();

            double bestSwapDistance = 0;
            int bestSwapCustomer = 0;
            double distance1 = problem.neighbourMatrix[prevNr1][this.routes[d1][r1].get(c1)] + problem.neighbourMatrix[this.routes[d1][r1].get(c1)][nextNr1];

            int prevNr2 = problem.depots[d2].getNr();
            for (int c2 = 0; c2 < this.routes[d2][r2].size(); c2++) {
                int nextNr2;
                if (c2 < this.routes[d2][r2].size() - 1) nextNr2 = this.routes[d2][r2].get(c2 + 1);
                else nextNr2 = problem.depots[problem.closestDepotToCustomers[this.routes[d2][r2].get(c2)]].getNr();

                double distance2 = problem.neighbourMatrix[prevNr2][this.routes[d2][r2].get(c2)] + problem.neighbourMatrix[this.routes[d2][r2].get(c2)][nextNr2];
                double swapDistance1 = problem.neighbourMatrix[prevNr2][this.routes[d1][r1].get(c1)] + problem.neighbourMatrix[this.routes[d1][r1].get(c1)][nextNr2];
                double swapDistance2 = problem.neighbourMatrix[prevNr1][this.routes[d2][r2].get(c2)] + problem.neighbourMatrix[this.routes[d2][r2].get(c2)][nextNr1];

                double tempSwapDistance = distance1 - swapDistance1 + distance2 - swapDistance2;
                if (tempSwapDistance > bestSwapDistance) {
                    bestSwapDistance = tempSwapDistance;
                    bestSwapCustomer = c2;
                    break;
                }
            }

            if (bestSwapDistance != 0) {
                ArrayList<Integer> tempRoute1 = new ArrayList<Integer>(this.routes[d1][r1]);
                ArrayList<Integer> tempRoute2 = new ArrayList<Integer>(this.routes[d2][r2]);

                int tempVal = tempRoute1.get(c1);
                tempRoute1.set(c1, tempRoute2.get(bestSwapCustomer));
                tempRoute2.set(bestSwapCustomer, tempVal);


                //if (isValidRoute(tempRoute1, d1, problem) && isValidRoute(tempRoute2, d2, problem)) {
                this.routes[d1][r1] = tempRoute1;
                this.routes[d2][r2] = tempRoute2;
                //  }

            }
        }


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
        return distance <= problem.depots[depot].maxDuration && capacity <= problem.depots[depot].maxLoad;
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

    public ArrayList<Integer> selectRandomInvalidRoute(Problem problem) {
        List<Integer> possibleDepots = IntStream.rangeClosed(0, problem.numberOfDepots - 1).boxed().collect(Collectors.toList());
        ArrayList<Integer> route = null;
        int d;
        do {
            int d2 = possibleDepots.get((int) (Math.random() * possibleDepots.size()));
            possibleDepots.removeIf(possibleDepot -> possibleDepot == d2);
            route = selectRandomInvalidRoute(problem, d2);
            d = d2;
        } while (isValidRoute(route, d, problem) && possibleDepots.size() > 0);

        if (isValidRoute(route, d, problem)) {
            return null;
        }
        return route;
    }

    private ArrayList<Integer> selectRandomInvalidRoute(Problem problem, int depot) {
        List<Integer> possibleRoutes = IntStream.rangeClosed(0, problem.maxVehiclesPerDepot - 1).boxed().collect(Collectors.toList());
        ArrayList<Integer> route = null;

        do {
            int r = possibleRoutes.get((int) (Math.random() * possibleRoutes.size()));
            possibleRoutes.removeIf(possibleRoute -> possibleRoute == r);
            route = this.routes[depot][r];
        }
        while (isValidRoute(route, depot, problem) && possibleRoutes.size() > 0);

        return route;
    }

    private ArrayList<Integer> selectRandomEmptyRoute(Problem problem) {
        List<Integer> possibleDepots = IntStream.rangeClosed(0, problem.numberOfDepots - 1).boxed().collect(Collectors.toList());
        ArrayList<Integer> route = new ArrayList<Integer>();
        route.add(1);

        while (route.size() > 0 && possibleDepots.size() > 0) {
            int d = possibleDepots.get((int) (Math.random() * possibleDepots.size()));
            possibleDepots.removeIf(possibleDepot -> possibleDepot == d);
            route = selectRandomEmptyRoute(problem, d);
        }
        if (route.size() > 0) {
            return null;
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

    public ArrayList<Integer> selectRandomNonEmptyRoute(Problem problem) {
        List<Integer> possibleDepots = IntStream.rangeClosed(0, problem.numberOfDepots - 1).boxed().collect(Collectors.toList());
        ArrayList<Integer> route = new ArrayList<>();

        while (route.size() == 0 && possibleDepots.size() > 0) {
            int d = possibleDepots.get((int) (Math.random() * possibleDepots.size()));
            possibleDepots.removeIf(possibleDepot -> possibleDepot == d);
            route = selectRandomNonEmptyRoute(problem, d);
        }
        if (route.size() == 0) {
            return null;
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

    private double getWeightedFitnessOfRoute(ArrayList<Integer> route, int depot, Problem problem) {
        return getFitnessOfRoute(route, depot, problem) / route.size();
    }

    private double getFitnessOfRoute(ArrayList<Integer> route, int depot, Problem problem) {
        double routeDistanceOverload = 0;
        int routeDemandOverload = 0;
        double routeDistance = getDistanceOfRoute(route, problem.depots[depot].getNr(), problem);
        int routeDemand = getDemandOfRoute(route, problem);

        if (routeDistance > problem.depots[depot].maxDuration) {
            routeDistanceOverload = routeDistance - problem.depots[depot].maxDuration;
        }
        if (routeDemand > problem.depots[depot].maxLoad) {
            routeDemandOverload = routeDemand - problem.depots[depot].maxLoad;
        }

        return (routeDistance + routeDistanceOverload + routeDemandOverload);
    }

    public void updateFitnessVariables(Problem problem) {
        this.durationOverload = 0;
        this.capacityOverload = 0;
        this.distance = 0;
        this.invalidFactor = 0;


        for (int depot = 0; depot < this.routes.length; depot++) {
            double maxDuration = problem.depots[depot].maxDuration;
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
