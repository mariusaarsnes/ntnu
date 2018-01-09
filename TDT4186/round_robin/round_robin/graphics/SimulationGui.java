package round_robin.graphics;

import round_robin.Process;
import round_robin.Simulator;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class provides a GUI displaying the queueing network used
 * to simulate our system.
 */
public class SimulationGui extends JFrame implements ActionListener, Runnable {
	private static Font font = new Font("Arial", Font.PLAIN, 10);

	private JPanel memoryQueue, cpuQueue, ioQueue;

	/** Resources components shown in the GUI */
	private Resource memory = new Resource("Memory");
	private Resource cpu = new Resource("CPU");
	private Resource io = new Resource("I/O");

	/** Button used to start and rush the simulation */
	private JButton startButton = new JButton("Start simulation");
	/** The background picture */
	private Image background;
	/** Slider controlling the speed of the simulation */
	private JSlider simulationSpeedSlider = new JSlider(0, 10000, 8000);
	/** Label showing how much simulated time has elapsed */
	private JLabel timeElapsedLabel = new JLabel("Simulated time elapsed: 0 ms.");
	/** The scheduling simulator */
	private Simulator simulator;
	/** How much simulated time has elapsed */
	private long timeElapsed = 0;
	/** Whether or not the simulation has been started */
	private boolean running = false;

	/**
	 * Constructs and shows a new GUI.
	 */
	public SimulationGui(Simulator simulator) {
		super("Process scheduling simulator");
		this.simulator = simulator;

		this.memoryQueue = new QueuePanel(simulator.getMemoryQueue(),
				"memory queue", 10, QueuePanel.Direction.RIGHT);
		this.cpuQueue = new QueuePanel(simulator.getCpuQueue(),
				"CPU queue", 10, QueuePanel.Direction.LEFT);
		this.ioQueue = new QueuePanel(simulator.getIoQueue(),
				"I/O queue", 10, QueuePanel.Direction.RIGHT);

		simulator.setOnTimeStep(this::onTimePassed);
		simulator.setOnEventHandled(this::onEventHandled);

		placeComponents();
		setSize(800,800);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	/**
	 * Creates an places the components used by the GUI.
	 */
	private void placeComponents() {
		loadImages();
		
		/* Panel containing the background picture */
		PicturePanel backgroundPanel = new PicturePanel(background);
		getContentPane().setLayout(null);
		getContentPane().add(backgroundPanel);
		backgroundPanel.setBounds(0,0,494,374);
		backgroundPanel.setLayout(null);

		backgroundPanel.add(memoryQueue);
		backgroundPanel.add(cpuQueue);
		backgroundPanel.add(ioQueue);
		backgroundPanel.add(memory);
		backgroundPanel.add(cpu);
		backgroundPanel.add(io);

		memoryQueue.setBounds(110,20,200,50);
		memory.setBounds(310,10,90,90);
		cpuQueue.setBounds(200,120,200,50);
		cpu.setBounds(110,110,90,90);
		ioQueue.setBounds(110,220,200,50);
		io.setBounds(310,210,90,90);

		JPanel lowerPanel = new JPanel();
		lowerPanel.setLayout(null);
		getContentPane().add(lowerPanel);
		lowerPanel.setBounds(0,374,494,100);
		lowerPanel.add(simulationSpeedSlider);
		addSliderLabels(lowerPanel,10,10,474,20,"Slow","Fast","Simulation speed");
		simulationSpeedSlider.setBounds(10,30,474,20);

		lowerPanel.add(timeElapsedLabel);
		timeElapsedLabel.setBounds(10,60,300,20);

		lowerPanel.add(startButton);
		startButton.setBounds(320,60,154,20);
		startButton.addActionListener(this);
	}

	/**
	 * Method running as a separate thread when the "Start simulation"
	 * button is pressed. Starts the main simulation loop.
	 */
	public void run() {
		running = true;
		simulator.simulate();
	}

	/**
	 * Listener method called when a button has been clicked.
	 * @ae	The action event.
	 */
	public void actionPerformed(ActionEvent ae) {
		if(!running) {
			startButton.setText("Complete simulation");
			 // The simulation must be run in a separate thread,
			// we can't "hijack" the GUI's event handling thread.
			Thread t = new Thread(this);
			t.start();
		}
		else {
			simulator.setOnTimeStep(null);
		}
	}

	/**
	 * Creates, adds and positions labels above a slider.
	 * @param p			The panel to add the labels to.
	 * @param x			The x position of the leftmost label.
	 * @param y			The y position of the topmost label.
	 * @param w			The width from leftmost label to rightmost pixel of the rightmost label
	 * @param h			The height of the labels.
	 * @param leftText	The text to be displayed in the left label.
	 * @param rightText	The text to be displayed in the right label.
	 * @param text		The text to be displayed in the central label.
	 */
	private void addSliderLabels(JPanel p, int x, int y, int w, int h, String leftText, String rightText, String text) {
		JLabel left, middle, right;
		left = new JLabel(leftText);
		left.setHorizontalAlignment(JLabel.LEFT);
		left.setOpaque(false);
		p.add(left);
		left.setBounds(x,y,w,h);
		middle = new JLabel(text);
		middle.setHorizontalAlignment(JLabel.CENTER);
		middle.setOpaque(false);
		p.add(middle);
		middle.setBounds(x,y,w,h);
		right = new JLabel(rightText);
		right.setHorizontalAlignment(JLabel.RIGHT);
		right.setOpaque(false);
		p.add(right);
		right.setBounds(x,y,w,h);
	}

	/**
	 * Lets the GUI know that a discrete amount of time has passed.
	 * @param time	The (simulated) time that has passed since the last call to this method.
	 */
	public void onTimePassed(long time) {
		try	{
			if (isShowing())
				Thread.sleep((10000-simulationSpeedSlider.getValue())*time/3000);
		} catch (InterruptedException ie) {}
		timeElapsed += time;
		timeElapsedLabel.setText("Simulated time elapsed: "+timeElapsed+" ms.");
		timeElapsedLabel.repaint();
	}

	public void onEventHandled(long time) {
		cpu.setActiveProcess(simulator.getCpu().getActiveProcess());
		io.setActiveProcess(simulator.getIo().getActiveProcess());
		memoryQueue.repaint();
		cpuQueue.repaint();
		ioQueue.repaint();
	}

	/**
	 * Loads an image from a file.
	 * @param tk		The toolkit to be used to load the image.
	 * @param file		The name of the file containing the image.
	 * @param tracker	The media tracker tracking the progress of the load.
	 * @return			The image that was loaded, as an Image object.
	 */
	private Image loadImage(Toolkit tk, String file, MediaTracker tracker) {
		Image result = tk.createImage(file);
		tracker.addImage(result, 0);
		return result;
	}

	/**
	 * Loads all images to be used by the GUI, and waits for them to
	 * be fully loaded before returning.
	 */
	private void loadImages() {
		MediaTracker tracker = new MediaTracker(this);
		Toolkit tk = Toolkit.getDefaultToolkit();
		background = loadImage(tk, "images/background.gif", tracker);
		try {
			tracker.waitForID(0);
	    } catch (InterruptedException ie) {}
	}


	/**
	 * Draws this process as a colored box with a process ID inside.
	 * @param g	The graphics context.
	 * @param x	The leftmost x-coordinate of the box.
	 * @param y	The topmost y-coordinate of the box.
	 * @param w	The width of the box.
	 * @param h	The height of the box.
	 */
	 static void drawProcess(Process p, Graphics g, int x, int y, int w, int h) {
		long id = p.getProcessId();
		// Assign a pseudo-random color used by the GUI
		int red = 64+(int)((id*101)%128);
		int green = 64+(int)((id*47)%128);
		int blue = 64+(int)((id*53)%128);
		Color color = new Color(red, green, blue);

		g.setColor(color);
		g.fillRect(x, y, w, h);
		g.setColor(Color.black);
		g.drawRect(x, y, w, h);
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics(font);
		g.drawString("" + id,
				x + w / 2 - fm.stringWidth("" + id) / 2, y + h / 2 + fm.getHeight() / 2);
	}
}