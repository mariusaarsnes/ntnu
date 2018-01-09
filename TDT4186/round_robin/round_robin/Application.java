package round_robin;

import round_robin.graphics.SimulationGui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Application {
    /** Change this if you want to start with default values. */
    private static final boolean START_WITH_INPUT = false;

    public static void main(String args[]) {
        if (START_WITH_INPUT) {
            launchWithInput();
        } else {
            launch(2048, 5000, 225, 250000, 5000);
        }
    }

    static void launch(long memorySize, long maxCpuTime, long avgIoTime, long simulationLength, long avgArrivalInterval) {
        Simulator simulator = new Simulator(memorySize, maxCpuTime, avgIoTime, simulationLength, avgArrivalInterval);
        SimulationGui gui = new SimulationGui(simulator);
    }

    /**
     * Reads a number from the an input reader.
     * @param reader	The input reader from which to read a number.
     * @return			The number that was inputted.
     */
    private static long readLong(BufferedReader reader) {
        try {
            return Long.parseLong(reader.readLine());
        } catch (IOException ioe) {
            return 100;
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    /**
     * Reads relevant parameters from the standard input,
     * and starts up the GUI. The GUI will then start the simulation when
     * the user clicks the "Start simulation" button.
     */
    private static void launchWithInput(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please input system parameters: ");

        System.out.print("Memory size (KB): ");
        long memorySize = readLong(reader);
        while(memorySize < 400) {
            System.out.println("Memory size must be at least 400 KB. Specify memory size (KB): ");
            memorySize = readLong(reader);
        }

        System.out.print("Maximum uninterrupted cpu time for a process (ms): ");
        long maxCpuTime = readLong(reader);

        System.out.print("Average I/O operation time (ms): ");
        long avgIoTime = readLong(reader);

        System.out.print("Simulation length (ms): ");
        long simulationLength = readLong(reader);
        while(simulationLength < 1) {
            System.out.println("Simulation length must be at least 1 ms. Specify simulation length (ms): ");
            simulationLength = readLong(reader);
        }

        System.out.print("Average time between process arrivals (ms): ");
        long avgArrivalInterval = readLong(reader);

        launch(memorySize, maxCpuTime, avgIoTime, simulationLength, avgArrivalInterval);
    }
}