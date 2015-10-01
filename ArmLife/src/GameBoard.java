import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GameBoard extends JPanel implements ActionListener {
	
	private int generation;
	private int cellSize;
	private int width, height;
	private boolean[][] cells;

	Timer stepTimer;
	
	public GameBoard (int x, int y, int timeStep, int cSize) {
		generation = 0;
		cellSize = cSize;
		this.width = x;
		this.height = y;
		cells = new boolean[width][height];

		stepTimer = new Timer(timeStep, this);
		stepTimer.stop();
		
		setPreferredSize(new Dimension(cellSize*x, cellSize*y)); // Window sized to game board
		setBackground(Color.white);
		
		initBoard();
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == stepTimer) {
			stepGeneration();
		}
	}
	
	private void initBoard () {
		Random r = new Random(); // for setting random state
		for (int i=0; i<width; i++) {
			for (int j=0; j<height; j++) {
				cells[i][j] = r.nextDouble() <= 0.1 ? true : false;
				/*  Can also be written as:
				 * 	double test = r.nextDouble();
				 *  if (test <= 0.2) {
				 *  	cells[i][j] = true;
				 *  } else {
				 *  	cells[i][j] = false;
				 *  }
				 */
			}
		}
		repaint();
	}
	
	private void initBoard (boolean[][] board) { // Overloaded method definition
		cells = board;
	}
	
	public void paintComponent (Graphics g) {
		this.removeAll();
		super.paintComponent(g);
		
		g.setColor(Color.black);
		for (int i=0; i<width; i++) { // Syntactic sugar
			for (int j=0; j<height; j++) {
				if (cells[i][j]) {
					g.fillRect(i*cellSize, j*cellSize, cellSize, cellSize); // Draw rectangle at (xpos, ypos, width, height)
				}
			}
		}
	}
	
	public void stepGeneration () {
		for (int i=0; i<width; i++) {
			for (int j=0; j<height; j++) {
				int neighborCount = getNeighbors(i, j);
				if (cells[i][j]) {
					if (neighborCount < 2 || neighborCount > 3) {
						cells[i][j] = false; // death by underpopulation (<2) or overcrowding (>3)
					}
				} else {
					if (neighborCount == 3) {
						cells[i][j] = true; // life through reproduction
					}
				}
			}
		}
		
		generation++;
		
		repaint();
	}
	
	private int getNeighbors (int x, int y) {
		int count = 0;
		if (countNeighbor (x-1, y-1)) { count++; } // top left
		if (countNeighbor (x, y-1)) { count++; } // top mid
		if (countNeighbor (x+1, y-1)) { count++; } // top right
		if (countNeighbor (x-1, y)) { count++; } // left
		if (countNeighbor (x+1, y)) { count++; } // right
		if (countNeighbor (x-1, y+1)) { count++; } // bot left
		if (countNeighbor (x, y+1)) { count++; } // bot mid
		if (countNeighbor (x+1, y+1)) { count++; } // bot right
		return count;
	}
	
	private boolean countNeighbor (int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			return false;
		} else {
			return cells[x][y];
		}
	}
	
	public int getGeneration () {
		return generation;
	}
	
	public Timer getStepTimer () {
		return stepTimer;
	}
	
	public static void main (String[] args) {
		DisplayWindow d = new DisplayWindow();
	    GameBoard board = new GameBoard(100, 100, 300, 5); // 100 x 100 cells with 500ms timestep
	    ButtonPanel buttons = new ButtonPanel(board);
	    Container content = d.getContentPane();
	    content.add(board, "Center");
	    content.add(buttons, "North");
	    d.showFrame();
	}

}
