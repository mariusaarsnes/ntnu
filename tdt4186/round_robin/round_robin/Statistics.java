package round_robin;

/**
 * This class contains a lot of public variables that can be updated
 * by other classes during a simulation, to collect information about
 * the run.
 */
public class Statistics
{
	/** The number of processes that have exited the system */
	public long nofCompletedProcesses = 0;
	/** The number of processes that have entered the system */
	public long nofCreatedProcesses = 0;

	/** The total time that the CPU has been busy (processing) */
	public long totalBusyCpuTime = 0;
	/** The total time that all completed processes have spent waiting for memory */
	public long totalTimeSpentWaitingForMemory = 0;
	/** The total time that all completed processes have spent waiting in the CPU queue */
	public long totalTimeSpentInReadyQueue = 0;
	/** The total time that all completed processes have spent in the CPU, should equal totalBusyCpuTime */
	public long totalTimeSpentInCpu = 0;
	/** The total time that all completed processes have spent in the I/O queue */
	public long totalTimeSpentWaitingForIo = 0;
	/** The total time that all completed processes have spent performing I/O operations */
	public long totalTimeSpentInIo = 0;

	/** The total number of times that all completed processes have been placed in the CPU queue */
	public long totalNofTimesInReadyQueue = 0;
	/** The total number of times that all completed processes have been placed in the I/O queue */
	public long totalNofTimesInIoQueue = 0;

	/** The time-weighted length of the memory queue, divide this number by the total time to get average queue length */
	public long memoryQueueLengthTime = 0;
	/** The largest memory queue length that has occured */
	public long memoryQueueLargestLength = 0;
	/** The time-weighted length of the CPU queue, divide this number by the total time to get average queue length */
	public long cpuQueueLengthTime = 0;
	/** The largest CPU queue length that has occured */
	public long cpuQueueLargestLength = 0;
	/** The time-weighted length of the I/O queue, divide this number by the total time to get average queue length */
	public long ioQueueLengthTime = 0;
	/** The largest I/O queue length that has occured */
	public long ioQueueLargestLength = 0;

	/** The number of process switches due to a Round Robin time quant having been spent
	 (process switches due to a need for I/O or the completion of a process are not counted) */
	public long nofProcessSwitches = 0;
	/** The total number of I/O operations that have been performed */
	public long nofProcessedIoOperations = 0;
    
	/**
	 * Prints out a report summarizing all collected data about the simulation.
	 * @param simulationLength	The number of milliseconds that the simulation covered.
	 */
	public void printReport(long simulationLength) {
		System.out.println();
		System.out.println("Simulation statistics:");
		System.out.println();
		System.out.println("Number of completed processes:                                "+nofCompletedProcesses);
		System.out.println("Number of created processes:                                  "+nofCreatedProcesses);
		System.out.println("Number of (forced) process switches:                          "+nofProcessSwitches);
		System.out.println("Number of processed I/O operations:                           "+nofProcessedIoOperations);
		System.out.println("Average throughput (processes per second):                    "+(nofCompletedProcesses*1000.0f)/simulationLength);
		System.out.println();
		System.out.println("Total CPU time spent processing:                              "+totalBusyCpuTime+" ms");
		System.out.println("Fraction of CPU time spent processing:                        "+totalBusyCpuTime*100.0f/simulationLength+"%");
		System.out.println("Total CPU time spent waiting:                                 "+(simulationLength-totalBusyCpuTime)+" ms");
		System.out.println("Fraction of CPU time spent waiting:                           "+((simulationLength-totalBusyCpuTime)*100.0f/simulationLength)+"%");
		System.out.println();
		System.out.println("Largest occuring memory queue length:                         "+memoryQueueLargestLength);
		System.out.println("Average memory queue length:                                  "+(float)memoryQueueLengthTime/simulationLength);
		System.out.println("Largest occuring cpu queue length:                            "+cpuQueueLargestLength);
		System.out.println("Average cpu queue length:                                     "+(float)cpuQueueLengthTime/simulationLength);
		System.out.println("Largest occuring I/O queue length:                            "+ioQueueLargestLength);
		System.out.println("Average I/O queue length:                                     "+(float)ioQueueLengthTime/simulationLength);
		if(nofCompletedProcesses > 0) {
			System.out.println("Average # of times a process has been placed in memory queue: "+1);
			System.out.println("Average # of times a process has been placed in cpu queue:    "+(float)totalNofTimesInReadyQueue/nofCompletedProcesses);
			System.out.println("Average # of times a process has been placed in I/O queue:    "+(float)totalNofTimesInIoQueue/nofCompletedProcesses);
			System.out.println();
			System.out.println("Average time spent in system per process:                     "+
					(totalTimeSpentWaitingForMemory+totalTimeSpentInReadyQueue+totalTimeSpentInCpu+
							totalTimeSpentWaitingForIo+totalTimeSpentInIo)/nofCompletedProcesses+" ms");
			System.out.println("Average time spent waiting for memory per process:            "+
					totalTimeSpentWaitingForMemory/nofCompletedProcesses+" ms");
			System.out.println("Average time spent waiting for cpu per process:               "+
					totalTimeSpentInReadyQueue/nofCompletedProcesses+" ms");
			System.out.println("Average time spent processing per process:                    "+
					totalTimeSpentInCpu/nofCompletedProcesses+" ms");
			System.out.println("Average time spent waiting for I/O per process:               "+
					totalTimeSpentWaitingForIo/nofCompletedProcesses+" ms");
			System.out.println("Average time spent in I/O per process:                        "+
					totalTimeSpentInIo/nofCompletedProcesses+" ms");
		}
	}
}
