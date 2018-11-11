package ov2;

import java.util.LinkedList;

/**
 * This class implements a queue of customers as a circular buffer.
 */
public class CustomerQueue {
	/**
	 * Creates a new customer queue. Make sure to save these variables in the class.
	 * @param queueLength	The maximum length of the queue.
	 * @param gui			A reference to the GUI interface.
	 */
	
	private int queueLength;
	private LinkedList<Customer> customers;
	private Gui gui;
	private int firstPos;
	private int lastPos;
	
    public CustomerQueue(int queueLength, Gui gui) {
		// Incomplete
    	this.queueLength = queueLength;
    	this.gui = gui;
    	this.customers = new LinkedList<Customer>();
    	this.lastPos = -1;
    	this.firstPos = -1;
	}

	public synchronized void add(Customer customer) {
		while (customers.size() == queueLength) {
			try {
				gui.println("Doorman is waiting for empty seat");
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		customers.addLast(customer);
			
		lastPos = (lastPos == queueLength-1) ? 0 : (lastPos +1);
		gui.fillLoungeChair(lastPos, customer);
		
		if (customers.size() == 1) notifyAll();			
	}

	public synchronized Customer next() {
		while (!(customers.size()>0)) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		firstPos = (firstPos==queueLength-1) ? 0 : (firstPos+1);
		gui.emptyLoungeChair(firstPos);
		
		
		if (customers.size()==queueLength) notifyAll();
		
		return customers.poll();
	}

	// Add more methods as needed
}
