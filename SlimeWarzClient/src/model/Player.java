package model;

import helper.Pair;

import java.util.ArrayList;
import java.util.Objects;

public class Player {
	private ArrayList<Pair> cellCoords;
	private int playerIndex;

	public Player(int playerIndex) {
		this.cellCoords = new ArrayList<>();
		this.playerIndex = playerIndex;
	}

	public void init(int playerIndex) {
		this.playerIndex = playerIndex;
	}

	public int getPlayerIndex() {
		return playerIndex;
	}

	public ArrayList<Pair> getCellCoords() {
		return cellCoords;
	}

	public int getSlimes() {
		return cellCoords.size();
	}

	public void add(Pair cell) {
		if (this.cellCoords.contains(cell)) {
			System.out.println("Occupied in " + cell);
			return;
		}
		this.cellCoords.add(cell);
	}

	public void add(int x, int y) {
		Pair temp = new Pair(x, y);
		if (this.cellCoords.contains(temp)) {
			System.out.println("Occupied in " + temp);
			return;
		}
		this.cellCoords.add(temp);
	}

	public void remove(Pair cell) {
		this.cellCoords.remove(cell);
	}
}
