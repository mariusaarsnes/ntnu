import java.io.BufferedReader;
import java.io.FileReader;

public class Problem {

    Depot[] depots;
    Customer[] customers;
    int maxVehiclesPerDepot, numberOfDepots, numberOfCustomers;
    double solutionThreshold;
    int[] closestDepotToCustomers;
    double[][] customerToCustomerDistances;

    public Problem(String fileName) {
        /**
         * Parse data-file contain information about selected problem
         */

        // Read in the problem information
        try {
            BufferedReader br = new BufferedReader(new FileReader("data_files/" + fileName));


            int[] metaData = parseStringToIntArray(br.readLine());
            this.maxVehiclesPerDepot = metaData[0];
            this.numberOfDepots = metaData[1];
            this.numberOfCustomers = metaData[2];

            this.depots = new Depot[this.numberOfDepots];
            this.customers = new Customer[this.numberOfCustomers];

            String rawLine;
            int i = 0;
            while ((rawLine = br.readLine()) != null) {
                int[] data = parseStringToIntArray(rawLine);
                if (i <= this.numberOfDepots) {
                    this.depots[i] = new Depot(data[0], data[1]);
                } else if (i <= this.numberOfCustomers + this.numberOfDepots) {
                    this.customers[i - this.numberOfDepots] = new Customer(data[0], data[1], data[2], data[3], data[4]);

                } else {
                    this.depots[i - (this.numberOfCustomers + this.numberOfDepots)].setDepotNr(data[0]);
                    this.depots[i - (this.numberOfCustomers + this.numberOfDepots)].setPos(data[1], data[2]);
                }
                i++;
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Read solution threshold
        try {
            BufferedReader br = new BufferedReader(new FileReader("solution_files/" + fileName));
            String rawLine = br.readLine();
            this.solutionThreshold = Integer.parseInt(rawLine.trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        generateClosestDepotArray();
        generateCustomerToCustomerDistances();
    }

    private void generateCustomerToCustomerDistances() {
        this.customerToCustomerDistances = new double[this.numberOfCustomers][this.numberOfCustomers];

        for (int i = 0; i < this.numberOfCustomers; i++) {
            for (int j = 0; j < this.numberOfCustomers; j++) {
                int[] startPos = {this.customers[i].xPos, this.customers[i].yPos};
                int[] endPos = {this.customers[j].xPos, this.customers[j].yPos};
                this.customerToCustomerDistances[i][j] = Math.hypot(startPos[0] - endPos[0], startPos[1] + endPos[1]);
            }
        }
    }

    private void generateClosestDepotArray() {
        /**
         * Loops through the array of customers and finds the nearest depot.
         * The results is an array which is stored in the class for later use
         */
        this.closestDepotToCustomers = new int[this.customers.length];
        for (int c = 0; c < this.customers.length; c++) {
            int[] customerPos = {this.customers[c].xPos, this.customers[c].yPos};
            double currentShortestDistance = Double.MAX_VALUE;
            for (int d = 0; d < this.depots.length; d++) {
                int[] depotPos = {this.depots[d].xPos, this.depots[d].yPos};
                double customerDepotDistance = Math.hypot(customerPos[0] - depotPos[0], customerPos[1] - depotPos[1]);
                if (currentShortestDistance < customerDepotDistance) {
                    this.closestDepotToCustomers[c] = d;
                    currentShortestDistance = customerDepotDistance;
                }
            }
        }
    }

    private int[] parseStringToIntArray(String line) {
        /**
         * Take in a string of number separated by space and split into array of integers
         */
        String[] temp = line.trim().split("\\s+");
        int[] intArray = new int[temp.length];
        for (int i = 0; i < temp.length; i++) {
            intArray[i] = Integer.parseInt(temp[i]);
        }
        return intArray;
    }

}
