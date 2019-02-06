import java.io.BufferedReader;
import java.io.FileReader;

public class Problem {

    Depot[] depots;
    Customer[] customers;
    int maxVehiclesPerDepot, numberOfDepots, numberOfCustomers;
    double solutionThreshold;
    int[] closestDepotToCustomers;
    double[][] neighbourMatrix;
    double maxDistance;

    public Problem(String fileName) {
        /**
         * Parse data-file contain information about selected problem
         */

        // Read in the problem information
        try {
            BufferedReader br = new BufferedReader(new FileReader("data_files/" + fileName));

            String rawLine;
            int i = 0;
            while ((rawLine = br.readLine()) != null) {
                int[] data = parseStringToIntArray(rawLine);
                if (i == 0) {
                    this.maxVehiclesPerDepot = data[0];
                    this.numberOfCustomers = data[1];
                    this.numberOfDepots = data[2];


                    this.depots = new Depot[this.numberOfDepots];
                    this.customers = new Customer[this.numberOfCustomers];
                } else if (i <= this.numberOfDepots) {
                    this.depots[i - 1] = new Depot(data[0], data[1]);
                } else if (i <= this.numberOfCustomers + this.numberOfDepots) {
                    this.customers[i - this.numberOfDepots - 1] = new Customer(data[0]-1, data[1], data[2], data[3], data[4]);
                } else {
                    this.depots[i - (this.numberOfCustomers + this.numberOfDepots) - 1].setNr(data[0]-1);
                    this.depots[i - (this.numberOfCustomers + this.numberOfDepots) - 1].setPos(data[1], data[2]);
                }
                i++;
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Read solution threshold
        try {
            BufferedReader br = new BufferedReader(new FileReader("solution_files/" + fileName + ".res"));
            String rawLine = br.readLine();
            this.solutionThreshold = Double.parseDouble(rawLine.trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        generateNeighbourMatrix();
        generateClosestDepotArray();

    }

    private void generateNeighbourMatrix() {
        this.maxDistance = Double.MIN_VALUE;
        this.neighbourMatrix = new double[this.numberOfCustomers+this.numberOfDepots][this.numberOfCustomers+this.numberOfDepots];
        for (int i = 0; i < this.numberOfCustomers + this.numberOfDepots; i++) {
            Point startPoint;
            if (i < this.numberOfCustomers)startPoint = this.customers[i];
            else startPoint = this.depots[i-this.numberOfCustomers];
            for(int j = 0; j < this.numberOfCustomers + this.numberOfDepots; j++) {
                Point destPoint;
                if (j < this.numberOfCustomers)destPoint = this.customers[j];
                else destPoint = this.depots[j-this.numberOfCustomers];

                double distance = startPoint.getDistanceTo(destPoint);
                this.neighbourMatrix[i][j] =  distance;
                if(distance > this.maxDistance) {
                    this.maxDistance = distance;
                }
            }
        }
    }
    private double getDistance(Point startPoint, Point destPoint) {
        return this.neighbourMatrix[startPoint.getNr()][destPoint.getNr()];

    }

    private void generateClosestDepotArray() {
        this.closestDepotToCustomers = new int[this.customers.length];
        for (int c = 0; c < this.customers.length; c++) {
            double currentMinDistance = Double.MAX_VALUE;
            for (int d = this.numberOfCustomers; d < this.numberOfCustomers+ this.depots.length; d++) {
                if (this.neighbourMatrix[c][d] < currentMinDistance){
                    this.closestDepotToCustomers[c] = d-this.numberOfCustomers;
                    currentMinDistance = this.neighbourMatrix[c][d];
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
