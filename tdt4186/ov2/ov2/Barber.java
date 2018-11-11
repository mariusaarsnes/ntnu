package ov2;
/**
 * This class implements the barber's part of the
 * Barbershop thread synchronization example.
 * One barber instance corresponds to one consumer in
 * the producer/consumer problem.
 */
public class Barber implements Runnable {
	
	private CustomerQueue queue;
	private Gui gui;
	private int pos;
	private boolean running = true;
	Customer customer;
	Thread barber;
	/**
	 * Creates a new barber. Make sure to save these variables in the class.
	 * @param queue		The customer queue.
	 * @param gui		The GUI.
	 * @param pos		The position of this barber's chair
	 */
	public Barber(CustomerQueue queue, Gui gui, int pos) { 
		this.queue = queue;
		this.gui = gui;
		this.pos = pos;
		this.barber = new Thread(this,"barber");
	}

	/**
	 * This is the code that will run when a new thread is
	 * created for this instance.
	 */
	@Override
	public void run(){
		while (running) {
			try {
				gui.barberIsSleeping(pos);
				gui.println("Barber" +pos +"is sleeping");
				Thread.sleep((long)(Globals.barberSleep*Math.random()));
				gui.barberIsAwake(pos);
				gui.println("Barber" + pos + " is awake");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			customer = queue.next();
			gui.fillBarberChair(pos, customer);
			gui.println("Barber" + pos + " is working");
			try {
				Thread.sleep((long)(Globals.barberWork*Math.random()));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gui.emptyBarberChair(pos);
			
			
		}
		// Incomplete
	}

	/**
	 * Starts the barber running as a separate thread.
	 */
	public void startThread() {
		barber.start();
		// Incomplete
	}

	/**
	 * Stops the barber thread.
	 */
	public void stopThread() {
		running = false;
		try {
			barber.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Add more methods as needed
}

