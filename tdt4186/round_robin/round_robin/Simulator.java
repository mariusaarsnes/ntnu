package round_robin;

import java.lang.*;
import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * The main class of the P3 exercise. This class is only partially complete.
 */
public class Simulator
{
	/** Process queues */
	private LinkedList<Process> memoryQueue = new LinkedList<>();
	private LinkedList<Process> cpuQueue = new LinkedList<>();
	private LinkedList<Process> ioQueue = new LinkedList<>();

	/** The queue of events to come */
    private EventQueue eventQueue = new EventQueue();

	/** Reference to the statistics collector */
	private Statistics statistics = new Statistics();

	private volatile Consumer<Long> onTimeStep = null;
	private volatile Consumer<Long> onEventHandled = null;

	/** Reference to the memory unit */
    private Memory memory;
	/** Reference to the CPU */
	private Cpu cpu;
	/** Reference to the I/O device */
	private Io io;

	/** The global clock */
    private long clock;
	/** The length of the simulation */
	private long simulationLength;
	/** The average length between process arrivals */
	private long avgArrivalInterval;
	// Add member variables as needed

	/**
	 * Constructs a scheduling simulator with the given parameters.
	 * @param memorySize			The size of the memory.
	 * @param maxCpuTime			The maximum time quant used by the RR algorithm.
	 * @param avgIoTime				The average length of an I/O operation.
	 * @param simulationLength		The length of the simulation.
	 * @param avgArrivalInterval	The average time between process arrivals.
	 */
	public Simulator(long memorySize, long maxCpuTime, long avgIoTime, long simulationLength, long avgArrivalInterval) {
		this.simulationLength = simulationLength;
		this.avgArrivalInterval = avgArrivalInterval;
		this.statistics = new Statistics();
		memory = new Memory(memoryQueue, memorySize, statistics);
		cpu = new Cpu(cpuQueue, maxCpuTime, statistics);
		io = new Io(ioQueue, avgIoTime, statistics);

		memoryQueue.add(new Process(memorySize, avgIoTime));
		clock = 0;

		// Add code as needed
    }

	/**
	 * Starts the simulation. Contains the main loop, processing events.
	 * This method is called when the "Start simulation" button in the
	 * GUI is clicked.
	 */
	public void simulate() {

		System.out.print("Simulating...");
		// Genererate the first process arrival event
		eventQueue.insertEvent(new Event(Event.NEW_PROCESS, 0));
		statistics.nofCreatedProcesses++;
		// Process events until the simulation length is exceeded:
		while (clock < simulationLength && !eventQueue.isEmpty()) {
			// Find the next event
			Event event = eventQueue.getNextEvent();
			// Find out how much time that passed...
			long timeDifference = event.getTime()-clock;
			// ...and update the clock.
			clock = event.getTime();

			/* Let the GUI know that time passed. */
			Consumer<Long> cb = onTimeStep;
			if (cb != null) { cb.accept(timeDifference); }

			// Let the cpu, memory unit, io and the GUI know that time has passed
			cpu.timePassed(timeDifference);
			memory.timePassed(timeDifference);
			io.timePassed(timeDifference);

			// Deal with the event
			if (clock < simulationLength) {
				processEvent(event);
			}

			// Let the GUI know we handled an event.
			cb = onEventHandled;
			if (cb != null) { cb.accept(timeDifference); }

			// Note that the processing of most events should lead to new
			// events being added to the event queue!

		}
		System.out.println("..done.");
		// End the simulation by printing out the required statistics
		statistics.printReport(simulationLength);
	}

	/**
	 * Processes an event by inspecting its type and delegating
	 * the work to the appropriate method.
	 * @param event	The event to be processed.
	 */
	private void processEvent(Event event) {
		switch (event.getType()) {
			case Event.NEW_PROCESS:
				createProcess();
				break;
			case Event.SWITCH_PROCESS:
				switchProcess();
				break;
			case Event.END_PROCESS:
				endProcess();
				break;
			case Event.IO_REQUEST:
				processIoRequest();
				break;
			case Event.END_IO:
				endIoOperation();
				break;
		}
	}

	/**
	 * Simulates a process arrival/creation.
	 */
	private void createProcess() {
		// Create a new process
		Process newProcess = new Process(memory.getMemorySize(), clock);
		memory.insertProcess(newProcess);
		transferProcessFromMemToReady();
		// Add an event for the next process arrival
		long nextArrivalTime = clock + 1 + (long)(2*Math.random()*avgArrivalInterval);
		eventQueue.insertEvent(new Event(Event.NEW_PROCESS, nextArrivalTime));
		// Update statistics
		statistics.nofCreatedProcesses++;
    }

	/**
	 * Transfers processes from the memory queue to the ready queue as long as there is enough
	 * memory for the processes.
	 */
	private void transferProcessFromMemToReady() {
		Process p = memory.checkMemory(clock);
		// As long as there is enough memory, processes are moved from the memory queue to the cpu queue
		while(p != null) {
			
			// TODO: Add this process to the CPU queue!
			// Also add new events to the event queue if needed
			Event e = cpu.insertProcess(p, clock);
			eventQueue.insertEvent(e);
			

			// Since we haven't implemented the CPU and I/O device yet,
			// we let the process leave the system immediately, for now.
			// memory.processCompleted(p);
			// Try to use the freed memory:
			//transferProcessFromMemToReady();
			// Update statistics
			//p.updateStatistics(statistics);

			// Check for more free memory
			p =	 memory.checkMemory(clock);
		}
	}

	/**
	 * Simulates a process switch.
	 */
	private void switchProcess() {
		Event e = cpu.switchProcess(clock);
		eventQueue.insertEvent(e);
		statistics.nofProcessSwitches++;
		
		// Incomplete
	}

	/**
	 * Ends the active process, and deallocates any resources allocated to it.
	 */
	private void endProcess() {
		Process endedProcess = cpu.endActiveProcess(clock);
		memory.processCompleted(endedProcess);
		Event e = cpu.switchProcess(clock);
		eventQueue.insertEvent(e);
		endedProcess.updateStatistics(statistics);
		// Incomplete
	}

	/**
	 * Processes an event signifying that the active process needs to
	 * perform an I/O operation.
	 */
	private void processIoRequest() {
		// Incomplete
		Process temp = cpu.getActiveProcess();
		Event e = cpu.activeProcessLeft(clock);
		eventQueue.insertEvent(e);
		e = io.addIoRequest(temp, clock);
		eventQueue.insertEvent(e);
	}

	/**
	 * Processes an event signifying that the process currently doing I/O
	 * is done with its I/O operation.
	 */
	private void endIoOperation() {
		if (io.getActiveProcess() != null) {
		Process temp = io.removeActiveProcess(clock);
		Event e = io.startIoOperation(clock);
		statistics.nofProcessedIoOperations++;
		eventQueue.insertEvent(e);
		 e = cpu.insertProcess(temp, clock);
		 eventQueue.insertEvent(e);
		// Incomplete
		}
	}


	/* The following methods are used by the GUI and should not be removed or modified. */

	public LinkedList<Process> getMemoryQueue() {
		return memoryQueue;
	}

	public LinkedList<Process> getCpuQueue() {
		return cpuQueue;
	}

	public LinkedList<Process> getIoQueue() {
		return ioQueue;
	}

	public Memory getMemory() {
		return memory;
	}

	public Cpu getCpu() {
		return cpu;
	}

	public Io getIo() {
		return io;
	}

	public void setOnTimeStep(Consumer<Long> onTimeStep) {
		this.onTimeStep = onTimeStep;
	}

	public void setOnEventHandled(Consumer<Long> onEventHandled) {
		this.onEventHandled = onEventHandled;
	}
}
