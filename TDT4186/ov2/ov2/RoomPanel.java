package ov2;
import java.awt.*;
import javax.swing.*;

/**
 * Component used by the GUI to display the Barbershop salon.
 *
 * It should not be necessary to edit this class (but feel free to do so if you wish).
 */
public class RoomPanel extends JPanel {
	/** The waiting lounge chairs */
	private PicturePanel chairs[];
	/** The barbers' chairs */
	private PicturePanel barberChairs[];
	/** The barbers */
	private PicturePanel barbers[];

	/**
	 * Constructs a barbershop salon.
	 */
	public RoomPanel() {
		super();
		setLayout(null);
		placeFurniture();
	}

	/**
	 * Returns the preferred size of this component.
	 * @returns	The size of the barbershop salon.
	 */
	public Dimension getPreferredSize() {
		return new Dimension(Constants.ROOM_WIDTH, Constants.ROOM_HEIGHT);
	}

	/**
	 * Paints this component.
	 * @param g	The Graphics2D graphics content to paint this component in.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(BarbershopGui.floorImage,0,0,Constants.ROOM_WIDTH, Constants.ROOM_HEIGHT,this);
		g.drawImage(BarbershopGui.wallsImage,0,0,Constants.ROOM_WIDTH, Constants.ROOM_HEIGHT,this);
	}

	/**
	 * Sets a barber to be shown as awake or asleep.
	 * @param pos	The position of the barber.
	 * @param value	Whether the barber is asleep or not.
	 */
	public void setBarberSleep(int pos, boolean value) {
		if(value)
			barbers[pos].setTopImage(BarbershopGui.sleepImage);
		else
			barbers[pos].setTopImage(null);
	}

	/**
	 * Shows a customer sitting in a waiting lounge chair.
	 * @param pos		The position of the chair.
	 * @param customer	The customer that is sitting in that chair.
	 */
	public void fillLoungeChair(int pos, Customer customer) {
		chairs[pos].setTopImage(customer.getImage());
	}

	/**
	 * Shows a waiting lounge chair as being unoccupied.
	 * @param pos	The position of the chair.
	 */
	public void emptyLoungeChair(int pos) {
		chairs[pos].setTopImage(null);
	}

	/**
	 * Shows a customer sitting in a barber's chair.
	 * @param pos	The position of the barber chair.
	 */
	public void fillBarberChair(int pos, Customer customer) {
		barberChairs[pos].setTopImage(customer.getImage());
	}

	/**
	 * Shows a barber chair as being unoccupied.
	 * @param pos		The position of the barber chair.
	 */
	public void emptyBarberChair(int pos) {
		barberChairs[pos].setTopImage(null);
	}

	/**
	 * Creates a table with the given position and size in the room.
	 * @param x	The x position of the top left corner of the table.
	 * @param y	The y position of the top left corner of the table.
	 * @param w	The width of the table.
	 * @param h	The height of the table.
	 */
	private void createTable(int x, int y, int w, int h) {
		PicturePanel table = new PicturePanel(BarbershopGui.tableImage);
		placePicture(table,x,y,w,h,Constants.SOUTH);
	}

	/**
	 * Creates a desk with the given position and size in the room.
	 * @param x	The x position of the top left corner of the desk.
	 * @param y	The y position of the top left corner of the desk.
	 * @param w	The width of the desk.
	 * @param h	The height of the desk.
	 */
	private void createDesk(int x, int y, int w, int h) {
		PicturePanel desk = new PicturePanel(BarbershopGui.deskImage);
		placePicture(desk,x,y,w,h,Constants.SOUTH);
	}

	/**
	 * Places a picture panel with the given size and orientation in the given position in the room.
	 * @param pp	The picture panel to be placed.
	 * @param x		The x position of picture panel.
	 * @param y		The y position of the top left corner of the picture panel.
	 * @param w		The width of the picture panel.
	 * @param h		The height of the picture panel.
	 * @param or	The orientation of the picture panel.
	 */
	private void placePicture(PicturePanel pp, int x, int y, int w, int h, int or) {
		add(pp);
		pp.setBounds(x,y,w,h);
		pp.setOrientation(or);
	}

	/**
	 * Creates and places all the furniture of the room.
	 */
	private void placeFurniture() {
		// Lounge chairs
		chairs = new PicturePanel[Constants.NOF_CHAIRS];
		for(int i = 0; i < Constants.NOF_CHAIRS; i++) {
			chairs[i] = new PicturePanel(BarbershopGui.loungeChairImage);
			placePicture(chairs[i], Constants.CHAIR_POSITIONS[i][0], Constants.CHAIR_POSITIONS[i][1], Constants.CHAIR_WIDTH, Constants.CHAIR_HEIGHT, Constants.CHAIR_POSITIONS[i][2]);
		}
		// Tables
		createTable(15,15,30,30);
		createTable(354,15,30,30);
		createTable(354,177,30,30);
		createTable(15,177,30,30);
		// Barbers' desk:
		createDesk(4,219,40,177);
		// Barbers' chairs and barbers:
		barberChairs = new PicturePanel[Constants.NOF_BARBERS];
		barbers = new PicturePanel[Constants.NOF_BARBERS];
		for(int i = 0; i < Constants.NOF_BARBERS; i++) {
			barberChairs[i] = new PicturePanel(BarbershopGui.barberChairImage);
			placePicture(barberChairs[i], 60, 233+i*2*Constants.CHAIR_HEIGHT, Constants.CHAIR_WIDTH, Constants.CHAIR_HEIGHT, Constants.WEST);
			barbers[i] = new PicturePanel(BarbershopGui.barberImage);
			placePicture(barbers[i], 75+Constants.CHAIR_WIDTH, 233+i*2*Constants.CHAIR_HEIGHT, Constants.BARBER_WIDTH, Constants.BARBER_HEIGHT, Constants.WEST);
		}
	}
}
