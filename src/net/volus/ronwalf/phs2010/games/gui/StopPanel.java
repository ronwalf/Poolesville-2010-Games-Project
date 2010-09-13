package net.volus.ronwalf.phs2010.games.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.volus.ronwalf.phs2010.games.core.SearchController;
import net.volus.ronwalf.phs2010.games.core.StoppedException;

public class StopPanel extends JPanel {

	private long waitMillis = 5 * 1000;
	private long startTime;
	private boolean stopped;
	
	private StopPanelController controller;
	
	private class StopPanelController implements SearchController {

		public void check() throws StoppedException {
			if (isStopped()) {
				throw new StoppedException();
			}
		}

		public boolean isStopped() {
			return stopped || (System.currentTimeMillis() > startTime + waitMillis);
		}
		
		public void start() {
			stopped = false;
			startTime = System.currentTimeMillis();
		}
		
	}
	
	public StopPanel() {
		int defaultTime = 5;
		
		controller = new StopPanelController();
		
		final JLabel label = new JLabel("Search time: " + defaultTime + " seconds");
		add(label);
		
		final JSlider timeSlider = new JSlider(1, 30, defaultTime);
		timeSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				int wtime = timeSlider.getValue();
				waitMillis =wtime * 1000;
				label.setText("Search time: " + wtime + " seconds");
			}
			
		});
		
		add(timeSlider);
		
		final JButton stopButton = new JButton("Stop!");
		stopButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				stopped = true;
			}
			
		});
		
		add(stopButton);
		
	}
	
	public SearchController getController() {
		return controller;
	}
	
}
