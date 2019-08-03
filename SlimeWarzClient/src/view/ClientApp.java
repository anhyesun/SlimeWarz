package view;

import helper.Observer;
import helper.Pair;
import viewModel.GameManager;
import helper.ComponentAttacher;

import javax.swing.*;
import java.awt.*;

public class ClientApp extends JFrame implements Observer {
	private static final int LINE_COUNT = 7;
	private final Dimension CLIENT_FRAME_DIM = new Dimension(768, 550);
	private final Rectangle CLIENT_FRAME_RECT = new Rectangle(CLIENT_FRAME_DIM);

	private final String TURN = "Current turn is : ";
	private final String RED_CELL = "Red slimes are : ";
	private final String BLUE_CELL = "Blue slimes are : ";
	private final String SELECTED_CELL = "You selected : ";
	private final Icon oIcon = new ImageIcon(this.getClass().getResource("/res/red.png"));
	private final Icon xIcon = new ImageIcon(this.getClass().getResource("/res/blue.png"));
	private final Icon border = new ImageIcon(this.getClass().getResource("/res/border.png"));

	private JButton[][] squares;

	private JLabel currentTurnInfo;
	private JLabel redCellInfo;
	private JLabel blueCellInfo;
	private JLabel selectedCellInfo;

	private GameManager gameManager;

	public static void main(String[] args) {
		ClientApp clientApp = new ClientApp();
		clientApp.start();
	}


	/**
	 * Construct a game board and set up the view, keep draw board
	 */
	private void start() {
		gameManager = new GameManager(LINE_COUNT);
		gameManager.addObserver(this);
		initView();
		gameManager.startProcedure();
		drawBoard();
	}

	/**
	 * Draw appropriate icon depends on current status of block
	 * Status 0: occupied by o player
	 *        1: occupied by x player
	 *        2: border line to represent movable blocks
	 *        default: not occupied and not movable blocks
	 */

	private void drawBoard() {
		for (int y = 0; y < LINE_COUNT; y++) {
			for (int x = 0; x < LINE_COUNT; x++) {
				Pair temp = new Pair(x, y);
				switch (gameManager.getBoard().get(temp)) {
					case 0:
						squares[y][x].setIcon(oIcon);
						break;
					case 1:
						squares[y][x].setIcon(xIcon);
						break;
					case 2:
						squares[y][x].setIcon(border);
						break;
					default:
						squares[y][x].setIcon(null);
						break;
				}
			}
		}
		currentTurnInfo.setText(TURN + gameManager.getTurnCount());
		redCellInfo.setText(RED_CELL + gameManager.getRedSlimesCount());
		blueCellInfo.setText(BLUE_CELL + gameManager.getBlueSlimesCount());
	}

	/**
	 * draw initial view with a basic setting
	 */

	private void initView() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("SlimeWars Client");
		setPreferredSize(CLIENT_FRAME_DIM);
		setResizable(false);
		setLayout(null);
		ComponentAttacher.attach(this, gamePanel(), CLIENT_FRAME_RECT);
		pack();
		setVisible(true);
	}

	/**
	 * set gamepanel size
	 * @return new 512 * 512 game panel with 256*512 info panel
	 */

	private JPanel gamePanel() {
		JPanel returnPanel = new JPanel(null);
		ComponentAttacher.attach(returnPanel, playPanel(), 0, 0, 512, 512);
		ComponentAttacher.attach(returnPanel, infoPanel(), 512, 0, 256, 512);

		return returnPanel;
	}

	/**
	 * when click event happens, update play panel
	 * @return new current panel
	 */

	private JPanel playPanel() {
		JPanel returnPanel = new JPanel();
		squares = new JButton[LINE_COUNT][LINE_COUNT];
		returnPanel.setLayout(new GridLayout(LINE_COUNT, LINE_COUNT));
		for (int y = 0; y < LINE_COUNT; y++) {
			for (int x = 0; x < LINE_COUNT; x++) {
				squares[y][x] = new JButton();
				returnPanel.add(squares[y][x]);

				Pair currentCoord = new Pair(x, y);
				squares[y][x].addActionListener(e -> { // set clickEvent to each of buttons
					gameManager.clickEvent(currentCoord);
					if (currentCoord == gameManager.getSelectedCell()) {
						selectedCellInfo.setText(SELECTED_CELL + currentCoord.toString());
					} else {
						selectedCellInfo.setText("");
					}
				});
			}
		}
		return returnPanel;
	}

	private JPanel infoPanel() {
		JPanel returnPanel = new JPanel();
		returnPanel.setLayout(null);
		currentTurnInfo = new JLabel(TURN + gameManager.getTurnCount());
		redCellInfo = new JLabel();
		blueCellInfo = new JLabel();
		selectedCellInfo = new JLabel();

		ComponentAttacher.attach(returnPanel, currentTurnInfo, 10, 100, 200, 25);
		ComponentAttacher.attach(returnPanel, redCellInfo, 10, 150, 200, 25);
		ComponentAttacher.attach(returnPanel, blueCellInfo, 10, 180, 200, 25);
		ComponentAttacher.attach(returnPanel, selectedCellInfo, 10, 210, 200, 25);

		return returnPanel;
	}


	@Override
	public void update() {
		drawBoard();
	}
}
