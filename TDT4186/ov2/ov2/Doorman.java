package ov2;

/**
 * This class implements the doorman's part of the
 * Barbershop thread synchronization example.
 * One doorman instance corresponds to one producer in
 * the producer/consumer problem.
 */
public class Doorman implements Runnable {
	
	
	private CustomerQueue queue;
	private Gui gui;
	private boolean running;
	Thread doorman;
	/**
	 * Creates a new doorman. Make sure to save these variables in the class.
	 * @param queue		The customer queue.
	 * @param gui		A reference to the GUI interface.
	 */
	public Doorman(CustomerQueue queue, Gui gui) { 
		// Incomplete
		this.queue = queue;
		this.gui = gui;
		doorman = new Thread(this,"doorman");
	}

	/**
	 * This is the code that will run when a new thread is
	 * created for this instance.
	 */
	@Override
	public void run(){
		while (running) {
			try {
				gui.println("Doorman is sleeping");
				Thread.sleep((long)(Globals.doormanSleep*Math.random()));
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			queue.add(new Customer());
		}
		
	}


	/**
	 * Starts the doorman running as a separate thread. Make
	 * sure to create the thread and start it.
	 */
	public void startThread() {
		// Incomplete
		running = true;
		doorman.start();
	}

	/**
	 * Stops the doorman thread. Use Thread.join() for stopping
	 * a thread.
	 */
	public void stopThread() {
		running = false;
		try {
			doorman.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Add more methods as needed
}
