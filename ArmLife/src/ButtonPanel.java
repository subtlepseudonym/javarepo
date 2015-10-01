import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

class ButtonPanel extends JPanel implements ActionListener {
		JButton run;
		JButton pause;
		JButton step;
		
		GameBoard board;
		
		public ButtonPanel (GameBoard board) {
			run   = new JButton("Run");
			pause = new JButton("Pause");
			step  = new JButton("Step");
			
			this.board = board;

			this.add(run);
			this.add(pause);
			this.add(step);

			run.addActionListener(this);
			pause.addActionListener(this);
			step.addActionListener(this);
			
			setBackground(Color.white);
			
			repaint();
		}
		
		public void actionPerformed (ActionEvent e) {
			if (e.getSource() == run) {
				if (!board.getStepTimer().isRunning()) {
					board.getStepTimer().start();
				}
			} else if (e.getSource() == pause) {
				if (board.getStepTimer().isRunning()) {
					board.getStepTimer().stop();
				}
			} else if (e.getSource() == step) {
				board.stepGeneration();
				repaint();
			}
		}
		
		public void paintComponent (Graphics g) {
			super.paintComponent(g);
			String gen = "Generation: " + board.getGeneration();
			g.drawString(gen, 10, 20);
		}
	}