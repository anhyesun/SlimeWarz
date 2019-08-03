package viewModel;

import helper.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.lang.Integer.MAX_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestGameManager {
	private GameManager gameManager;
	private Pair pair;
	private Map<Pair, Integer> board;
	private enum Status {notSelected, clicked, afterClicked}
	private Status status;
	private int LINE_COUNT;


	@BeforeEach
	void setUp(){
		LINE_COUNT = 4;
		gameManager = new GameManager(LINE_COUNT);
		board = gameManager.getBoard();
		status = Status.notSelected;
		pair = new Pair(0, 0);
		gameManager.startProcedure();
	}

	//initial board triggers initCells and initPlayers, then check whether it success or not
	@Test
	void testBoard() {
		// check left top cells
		assertEquals(0, (int) board.get(pair));
		assertEquals(0, (int) board.get(new Pair(1, 0)));
		//check right bottom cells
		assertEquals(0, (int) board.get(new Pair(LINE_COUNT - 1, LINE_COUNT - 1)));
		assertEquals(0, (int) board.get(new Pair(LINE_COUNT - 2, LINE_COUNT - 1)));

		// check left bottom cells
		assertEquals(1, (int) board.get(new Pair(LINE_COUNT - 1, 0)));
		assertEquals(1, (int) board.get(new Pair(LINE_COUNT - 2, 0)));
		//check right top cells
		assertEquals(1, (int) board.get(new Pair(0, LINE_COUNT - 1)));
		assertEquals(1, (int) board.get(new Pair(1, LINE_COUNT - 1)));

		//check unoccupied cells
		assertEquals((int) board.get(new Pair(0, LINE_COUNT - 2)), MAX_VALUE);
	}

	// check the 3*3 board's initial status
	@Test
	void testSmallBoard() {
		LINE_COUNT = 3;
		GameManager gameManagerSmall = new GameManager(LINE_COUNT);
		gameManagerSmall.startProcedure();
		board = gameManagerSmall.getBoard();
		// check left top cells
		assertEquals(0, (int) board.get(pair));
		assertEquals((int) board.get(new Pair(1, 0)), MAX_VALUE);

		//check right bottom cells
		assertEquals(1, (int) board.get(new Pair(LINE_COUNT - 1, LINE_COUNT - 1)));
		assertEquals((int) board.get(new Pair(LINE_COUNT - 2, 0)), MAX_VALUE);
	}

	@Test
	void testClickEventNotSelected() {
		assertEquals(gameManager.getCurrentPlayerIndex(), (int) board.get(pair));
	}





}
