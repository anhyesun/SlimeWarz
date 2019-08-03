package viewModel;

import helper.CellHelper;
import helper.Observable;
import helper.Observer;
import helper.Pair;
import model.Player;

import javax.swing.*;
import java.util.*;

import static java.lang.Integer.MAX_VALUE;

public class GameManager implements Observable {
	private final int LINE_COUNT;
	private List<Player> players;
	private Map<Pair, Integer> board;
	private int currentPlayerIndex = 0;
	private List<Observer> observers;

	private enum Status {notSelected, clicked}
	private Status status;
	private Pair selectedCell;
	private int turnCount = 0;

	public GameManager(final int LINE_COUNT) {
		this.LINE_COUNT = LINE_COUNT;
		this.players = new ArrayList<>();
		this.board = new HashMap<>();
		this.observers = new ArrayList<>();

		this.players.add(new Player(0));
		this.players.add(new Player(1));
	}

	public void startProcedure() {
		initPlayers();
		initCells();
	}

	/**
	 * When click event happens, check the current status
	 * @param clickedCell represents currently clicked cell
	 */
	public void clickEvent(Pair clickedCell) {
		Player currPlayer = this.players.get(this.currentPlayerIndex);
		switch (status) {
			case notSelected:
				if (currentPlayerIndex != board.get(clickedCell)) break;
				if (isPlayerCell(clickedCell, currPlayer)) {
					this.selectedCell = clickedCell;
					this.status = Status.clicked;
					updateAvailableCells(selectedCell);
					notifyObserver();
				}
				System.out.println("not selected");
				break;

			case clicked:
				if (isPlayerCell(clickedCell, currPlayer)) {
					this.selectedCell = clickedCell;
					clearAvailableCells();
					updateAvailableCells(selectedCell);
					notifyObserver();
					break;
				}

				if (currentPlayerIndex == 0) {
					if (players.get(1).getCellCoords().contains(clickedCell)) break;

				} else if (currentPlayerIndex == 1) {
					if (players.get(0).getCellCoords().contains(clickedCell)) break;
				}

				this.status = Status.notSelected;
				attack(clickedCell);
				notifyObserver();
				System.out.println("clicked");
				break;
		}
	}

	private void attack(Pair clickedCell) {
		int distance = CellHelper.getDistance(selectedCell, clickedCell);
		if (distance == 2) {
			removeCurrentCell();
		}
		moveCell(clickedCell);
		consumeCell(clickedCell);
		endTurn();
	}

	private void endTurn() {
		currentPlayerIndex = (currentPlayerIndex == 0) ? 1 : 0;

		if (!canContinue()) {
			gameOver();
			startProcedure();
			return;
		}
		clearAvailableCells();
		notifyObserver();
		turnCount++;
	}

	private void gameOver() {
		JOptionPane.showMessageDialog(null, "Game over!", "GAME OVER", JOptionPane.INFORMATION_MESSAGE);
	}

	private boolean canContinue() {
		ArrayList<Integer> temp1 = new ArrayList<>();

		for (Map.Entry<Pair, Integer> entry : board.entrySet()) {
			if (entry.getValue() == 0 && !temp1.contains(0)) {
				temp1.add(0);
			} else if (entry.getValue() == 1 && !temp1.contains(1)) {
				temp1.add(1);
			}
		}
		return temp1.size() == 2;
	}

	private void consumeCell(Pair clickedCell) {
		Player currPlayer = players.get(currentPlayerIndex);
		Player otherPlayer = (currentPlayerIndex == 0) ? players.get(1) : players.get(0);
		clearAvailableCells();

		List<Pair> neighbors = findNeighborCells(clickedCell, 1);

		for (Pair pair : neighbors) {
			if (board.get(pair).equals(0)) {
				board.put(pair, currentPlayerIndex);
				currPlayer.add(pair);
				otherPlayer.remove(pair);
			}
			if (board.get(pair).equals(1)) {
				board.put(pair, currentPlayerIndex);
				currPlayer.add(pair);
				otherPlayer.remove(pair);
			}
		}
	}

	private void moveCell(Pair clickedCell) {
		clearAvailableCells();
		board.put(clickedCell, currentPlayerIndex);
		players.get(currentPlayerIndex).add(clickedCell.getX(), clickedCell.getY());
	}

	private void removeCurrentCell() {
		board.put(selectedCell, MAX_VALUE);
		players.get(currentPlayerIndex).getCellCoords().remove(selectedCell);
	}

	private void updateAvailableCells(Pair selectedCell) {
		List<Pair> list = findAvailableCells(selectedCell);
		for (Pair pair : list) {
			if (board.containsKey(pair)) board.put(pair, 2);
		}
		System.out.println("update available cells!");
	}

	private void clearAvailableCells() {
		for (Map.Entry<Pair, Integer> entry : board.entrySet()) {
			if (entry.getValue() == 2) entry.setValue(MAX_VALUE);
		}
	}

	private List<Pair> findAvailableCells(Pair base) {
		List<Pair> retList = findNeighborCells(base, 2);
		for (Map.Entry<Pair, Integer> entry : board.entrySet()) {
			if (entry.getValue() == 0 || entry.getValue() == 1) {
				retList.remove(entry.getKey());
			}
		}
		System.out.println("find available cells");
		return retList;
	}

	private List<Pair> findNeighborCells(Pair base, int gap) {
		List<Pair> retList = new ArrayList<>();
		for (int y = 0; y < LINE_COUNT; y++) {
			for (int x = 0; x < LINE_COUNT; x++) {
				Pair temp = new Pair(x, y);
				if (CellHelper.getDistance(base, temp) <= gap) {
					retList.add(temp);
				}
			}
		}
		return retList;
	}

	private boolean isPlayerCell(Pair cell, Player currPlayer) {
		return currPlayer.getCellCoords().contains(cell);
	}

	private void initCells() {
		this.board.clear();
		for (int y = 0; y < LINE_COUNT; y++) { // Initialize the board with max_int values
			for (int x = 0; x < LINE_COUNT; x++) {
				this.board.put(new Pair(x, y), MAX_VALUE);
			}
		}

		for (Player player : players) { // put each players' cells into the board
			for (Pair pair : player.getCellCoords()) {
				this.board.put(pair, player.getPlayerIndex());
			}
		}

		this.currentPlayerIndex = 0;
		this.status = Status.notSelected;
		selectedCell = new Pair(MAX_VALUE, MAX_VALUE);
	}

	private void initPlayers() {
		if (LINE_COUNT < 4) {
			this.players.get(0).add(0, 0);
			this.players.get(1).add(LINE_COUNT - 1, LINE_COUNT - 1);
		} else {
			this.players.get(0).add(0, 0);
			this.players.get(0).add(1, 0);
			this.players.get(0).add(LINE_COUNT - 1, LINE_COUNT - 1);
			this.players.get(0).add(LINE_COUNT - 2, LINE_COUNT - 1);

			this.players.get(1).add(LINE_COUNT - 1, 0);
			this.players.get(1).add(LINE_COUNT - 2, 0);
			this.players.get(1).add(0, LINE_COUNT - 1);
			this.players.get(1).add(1, LINE_COUNT - 1);
		}
	}

	int getCurrentPlayerIndex() {
		return currentPlayerIndex;
	}

	public Map<Pair, Integer> getBoard() {
		return board;
	}

	public int getTurnCount() {
		return turnCount;
	}

	public int getRedSlimesCount() {
		return players.get(0).getSlimes();
	}

	public int getBlueSlimesCount() {
		return players.get(1).getSlimes();
	}

	public Pair getSelectedCell() {
		return selectedCell;
	}

	@Override
	public void addObserver(Observer o) {
		if (!observers.contains(o)) observers.add(o);
	}

	@Override
	public void removeObserver(Observer o) {
		observers.remove(o);
	}

	@Override
	public void notifyObserver() {
		for (Observer o : observers) {
			o.update();
		}
	}
}
