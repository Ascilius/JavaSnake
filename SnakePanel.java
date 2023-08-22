import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

public class SnakePanel extends JPanel {

	// debugging
	private final boolean debug = true;
	int frame = 1;

	// screen dimensions
	private final int screenWidth, screenHeight;
	// user inputs
	private final KeyHandler keyHandler;

	// game/board
	private final int FPS = 144;
	int gameSpeed;
	boolean pause = false;
	int n; // board size
	int score = 0;
	int winState = -1;

	// snake
	ArrayList<int[]> snake = new ArrayList<int[]>();
	int direction = 0;
	Color snakeColor = Color.BLACK;
	Color snakeHeadColor = Color.RED;

	// food
	int[] food = new int[2]; // food location
	Color foodColor = Color.BLACK;

	public SnakePanel(double screenWidth, double screenHeight) {
		// screen dimensions setup
		this.screenWidth = (int) screenWidth;
		this.screenHeight = (int) screenHeight;

		// user input setup
		this.keyHandler = new KeyHandler();
		addKeyListener(this.keyHandler);
		setFocusable(true);

		// board setup
		if (debug == false) {
			Scanner input = new Scanner(System.in);
			System.out.print("Snake\nPlease input the board size: ");
			n = input.nextInt();
		} else
			n = 10;
		System.out.println(n + " x " + n + " board selected.\nGenerating...\nGame Loading...");
		gameSpeed = n / 10;

		// snake setup
		int[] start = { (int) (Math.random() * n), (int) (Math.random() * n) };
		snake.add(start);

		foodGen(); // generating first food
	}

	public void paintComponent(Graphics graphics) {
		long start = System.nanoTime();

		// setup
		Graphics2D g = (Graphics2D) graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // anti-aliasing
		g.setColor(Color.WHITE); // background
		g.fillRect(0, 0, screenWidth, screenHeight);
		//debugging
		int debugyY = 15;
		if (debug == true) {
			g.setColor(Color.BLACK);
			g.drawString("Debug: Frame: " + frame, 5, debugyY);
			debugyY += 20;
			g.drawString("Debug: Score: " + score, 5, debugyY);
			debugyY += 20;
			g.drawString("Debug: Game Speed: " + gameSpeed, 5, debugyY);
			debugyY += 20;
			g.drawString("Debug: Snake Size: " + snake.size(), 5, debugyY);
			debugyY += 20;
			g.drawString("Debug: Snake: ", 5, debugyY);
			for (int[] bodyPart : snake) {
				debugyY += 20;
				g.drawString(bodyPart[0] + ", " + bodyPart[1], 5, debugyY);
			}
			debugyY += 20;
			g.drawString("Debug: Direction: " + direction, 5, debugyY);
			debugyY += 20;
			g.drawString("Debug: Food Location: " + food[0] + ", " + food[1], 5, debugyY);
		}
		g.translate(screenWidth / 2, screenHeight / 2); // setting reference point to the center of the screen (+y is still down)

		// move snake
		if (frame % (FPS / gameSpeed) == 0)
			snakeMove();

		// board border
		int buffer = screenHeight / 16;
		int borderLength = screenHeight - 2 * buffer;
		// upper-left corner
		int x = borderLength / -2;
		int y = borderLength / -2;
		int spaceLength = borderLength / n;
		// outer border
		g.setColor(Color.BLACK);
		g.fillRect(x - spaceLength / 8, y - spaceLength / 8, borderLength + spaceLength / 4, borderLength + spaceLength / 4);
		// inner border
		g.setColor(Color.WHITE);
		g.fillRect(x, y, borderLength, borderLength);

		// ui
		g.setColor(Color.BLACK);
		g.setFont(new Font("Dialog", Font.PLAIN, 100));
		g.drawString("Snake", x + borderLength, y);
		g.setFont(new Font("Dialog", Font.PLAIN, 12));
		g.drawString("Score: " + score, x + borderLength, y + 20);

		// board state
		g.setColor(Color.BLACK);
		// food
		int foodBuffer = spaceLength / 4;
		int foodSpaceLength = spaceLength / 2;
		g.setColor(foodColor);
		g.fillRect(x + food[0] * spaceLength + foodBuffer, y + food[1] * spaceLength + foodBuffer, foodSpaceLength, foodSpaceLength);
		// snake
		int snakeBuffer = spaceLength / 8;
		int snakeSpaceLength = spaceLength * 3 / 4;
		g.setColor(snakeColor);
		for (int i = snake.size() - 1; i >= 0; i--) {
			if (i == 0)
				g.setColor(snakeHeadColor);
			g.fillRect(x + snake.get(i)[0] * spaceLength + snakeBuffer, y + snake.get(i)[1] * spaceLength + snakeBuffer, snakeSpaceLength, snakeSpaceLength);
		}

		// finish frame
		long finish = System.nanoTime();
		try {
			TimeUnit.NANOSECONDS.sleep((long) (1e9 / FPS - (finish - start)));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		frame++;

		// win check
		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", Font.PLAIN, 100));
		if (winState == 0 && snake.size() == 0) {
			g.drawString("you lose...", 0, 0);
			pause = true;
		} else if (winState == 1)
			g.drawString("YOU WIN!!!", 0, 0);

		// next frame
		if (pause == false)
			repaint();
	}

	// snake moving
	public void snakeMove() {
		if (winState == -1) {
			// generating new head
			int[] newHead = snake.get(0).clone();
			if (direction == 1)
				newHead[1] -= 1;
			else if (direction == 2)
				newHead[0] -= 1;
			else if (direction == 3)
				newHead[1] += 1;
			else if (direction == 4)
				newHead[0] += 1;

			// wall collision
			if (newHead[0] < 0 || newHead[1] < 0 || newHead[0] > n - 1 || newHead[1] > n - 1)
				winState = 0;
			snake.add(0, newHead); // moves in the direction

			// food check
			if (newHead[0] == food[0] && newHead[1] == food[1]) {
				score++;
				if (score % 5 == 0)
					gameSpeed++;
				foodGen();
			} else
				snake.remove(snake.size() - 1); // remove last part

			// self collision
			for (int i = 1; i < snake.size(); i++) {
				if (newHead[0] == snake.get(i)[0] && newHead[1] == snake.get(i)[1]) {
					winState = 0;
				}
			}
		} else if (winState == 0) {
			snake.remove(snake.size() - 1);
		} else {
			// you win yay
		}
	}

	// food generation
	public void foodGen() {
		boolean pass = false;
		while (pass == false) {
			// generation
			int i = (int) (Math.random() * n);
			int j = (int) (Math.random() * n);
			// check
			pass = true;
			for (int[] bodyPart : snake) {
				if (i == bodyPart[0] && j == bodyPart[1]) {
					pass = false;
					break; // regenerate
				}
			}
			// finalization
			food[0] = i;
			food[1] = j;
			if (snake.size() == n * n) { // win check
				winState = 1;
				pass = true;
			}
		}
	}

	// restart
	public void restart() {
		// game/board
		gameSpeed = n / 10;
		pause = false;
		score = 0;
		winState = -1;
		// snake
		snake = new ArrayList<int[]>();
		direction = 0;
		int[] start = { (int) (Math.random() * n), (int) (Math.random() * n) };
		snake.add(start);
		// food
		food = new int[2]; // food location
		foodGen(); // generating first food
	}

	class KeyHandler extends KeyAdapter {

		public void keyReleased(KeyEvent e) {
			// game exit
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				System.exit(0);
			if (winState == -1) {
				// pause
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					pause = !pause;
					repaint();
				}
				// player inputs
				else if ((e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP) && direction != 3)
					direction = 1; // up
				else if ((e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT) && direction != 4)
					direction = 2; // left
				else if ((e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN) && direction != 1)
					direction = 3; // down
				else if ((e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT) && direction != 2)
					direction = 4; // right
			}
			if (winState != -1) {
				// restart
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					restart();
					repaint();
				}
			}
		}

	}
}