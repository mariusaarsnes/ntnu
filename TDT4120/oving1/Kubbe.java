package oving1;

public class Kubbe {
	int value;
	Kubbe next;
	
	public Kubbe (int value) {
		this.value = value;
	}
	
	public void addNext (Kubbe next) {
		this.next = next;
		
	}
}
