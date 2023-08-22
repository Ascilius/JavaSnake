import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

/*
To-Do:
 - better UI
 - debug screen
 - quick direction change bug
    - going up, turn left, then down quickly
 */

public class SnakeDriver {
	public static void main(String[] args) {
		JFrame frame = new JFrame("Snake");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		SnakePanel panel = new SnakePanel(screenSize.getWidth(), screenSize.getHeight());
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setUndecorated(true);
		frame.setVisible(true);
	}
}


