/*
Copyright (c) 2010 Ron Alford <ronwalf@volus.net>
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:
1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products
   derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package net.volus.ronwalf.phs2010.games.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.volus.ronwalf.phs2010.games.core.SearchController;

public class StopPanel extends JPanel {

	private static final DecimalFormat df = new DecimalFormat("#.##");
	private static final int sfrac = 4; // Fractions of a second to step the timeer
	private long waitMillis = 500;
	private long startTime;
	private boolean stopped = true;
	
	private JButton forceButton;
	private StopPanelController controller;
	
	private class StopPanelController implements SearchController {

		public synchronized boolean isStopped() {
			if (!stopped)
				stopped = (System.currentTimeMillis() > startTime + waitMillis);
			updateForce();
			return stopped;
		}
		
		public void start() {
			stopped = false;
			startTime = System.currentTimeMillis();
			updateForce();
		}
		
		public void stop() {
			stopped = true;
			updateForce();
		}
		
	}
	
	public StopPanel() {
		super(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;
		
		controller = new StopPanelController();
		
		
		final JLabel label = new JLabel("Search time: " + df.format(waitMillis/1000.0) + " seconds");
		add(label, c);
		
		final JSlider timeSlider = new JSlider(1, sfrac*10, (int) (sfrac*waitMillis/1000));
		timeSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				waitMillis = timeSlider.getValue() * 1000 / sfrac;
				
				label.setText("Search time: " + df.format(waitMillis/1000.0) + " seconds");
			}
			
		});
		
		add(timeSlider, c);
		
		forceButton = new JButton("Force Move!");
		forceButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				stopped = true;
				updateForce();
			}
			
		});
		add(forceButton, c);
		updateForce();
		
	}
	
	private void updateForce() {
		SwingUtilities.invokeLater(new Runnable(){

			public void run() {
				forceButton.setEnabled(!stopped);
			}
			
		});
	}
	
	public SearchController getController() {
		return controller;
	}
	
}
