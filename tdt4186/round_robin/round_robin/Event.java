package round_robin;

/**
 * This class contains information about an event.
 *
 * @see EventQueue
 */
public class Event implements Comparable
{
    /** Event type describing the arrival of a new process */
	public static final int NEW_PROCESS = 1;
    /** Event type describing the completion of the active process */
	public static final int END_PROCESS = 2;
    /** Event type describing a process switch due to the completion of a RR time quant */
	public static final int SWITCH_PROCESS = 3;
    /** Event type describing the need for the active process to perform I/O */
	public static final int IO_REQUEST = 4;
    /** Event type describing the end of the current I/O operation */
	public static final int END_IO = 5;

    /** The sort of event, see above. */
	private int type;
	/** The time at which the event will occur */
	private long time;

	/**
	 * Creates a new event with the given parameters.
	 * @param type	The type of event.
	 * @param time	The time at which the event will occur.
	 */
	public Event(int type, long time) {
		this.type = type;
		this.time = time;
	}

	/**
	 * Gets the type of this event.
	 * @return	The type of this event.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Gets the time at which this event will occur.
	 * @return	The time at which this event will occur.
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Compares the time of two events. This method is used when sorting
	 * the list of events maintained by the EventQueue class.
	 * @param o	The event to compare this event with.
	 * @return	A negative number if this event occurs before the other event,
	 *			0 if they occur at the same time, and a positive number if the other
	 *			event occurs before this event.
	 * @see	java.lang.Comparable
	 */
	public int compareTo(Object o) {
		Event e = (Event)o;
		return (int)(time-e.time);
	}
}
