package rocks.wallenius.joop.createdobject;

import java.util.ArrayList;
import rocks.wallenius.joop.mvc.Observable;
import rocks.wallenius.joop.mvc.Observer;

public class ObjectArrayList<CreatedObject> extends ArrayList implements Observable {

	private ArrayList<Observer> observers;
	private boolean changed;
	
	public ObjectArrayList() {
		super();
		observers = new ArrayList<Observer>();
		changed = false;
	}

	public void addCreatedObject(CreatedObject object){
		add(object);
		setChanged(true);
		notifyObservers();
	}

	/*
	 * Observer methods to add, delete and notify observers
	 * 
	 */
	public void addObserver(Observer o) {
		observers.add(o);
	}
	
	public void deleteObserver(Observer o) {
		int i = observers.indexOf(o);
		if (i >= 0 ) {
			observers.remove(i);
		}
	}
	
	public void notifyObservers() {
		if(changed) {
			for(int i = 0; i < observers.size(); i++) {
				Observer observer = observers.get(i);
				observer.update();
			}
			setChanged(false);
		}
	}
	
	public void setChanged(boolean changed) {
		this.changed = changed;
	}
}