package rocks.wallenius.joop.mvc;

/* 
* Observable.java
* An interface for observable classes.  
* 
* v1.0 
* 10/03/2012 
* 
* Author: Philip Wallenius, UNN-id: 09031635
*/ 

public interface Observable {
	void addObserver(Observer observer);
	void deleteObserver(Observer observer);
	void notifyObservers();
	void setChanged(boolean changed);
}
