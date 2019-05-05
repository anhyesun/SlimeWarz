package model;

public interface Observable {
	void addObserver(Observer o);
	void removeObserver(Observer o);
	void notifyObserver(String message);
}
