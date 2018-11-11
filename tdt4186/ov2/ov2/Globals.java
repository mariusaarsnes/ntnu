package ov2;

/**
 * Class containing three globally available variables modified by the GUI
 * and used by the Barber and Doorman threads.
 *
 * It should not be necessary to edit this class (but feel free to do so if you wish).
 */
public class Globals
{
	/** The number of milliseconds a barber sleeps between each work period */
	public static int barberSleep = (Constants.MAX_BARBER_SLEEP+Constants.MIN_BARBER_SLEEP)/2;
	/** The number of milliseconds it takes a barber to cut a customer's hair */
	public static int barberWork = (Constants.MAX_BARBER_WORK+Constants.MIN_BARBER_WORK)/2;
	/** The number of milliseconds between each time a new customer arrives */
	public static int doormanSleep = (Constants.MAX_DOORMAN_SLEEP+Constants.MIN_DOORMAN_SLEEP)/2;
}
