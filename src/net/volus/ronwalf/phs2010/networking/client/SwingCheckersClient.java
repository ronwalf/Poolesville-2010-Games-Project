/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package net.volus.ronwalf.phs2010.networking.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import net.volus.ronwalf.phs2010.games.checkers.CheckersGame;
import net.volus.ronwalf.phs2010.games.checkers.CheckersMove;
import net.volus.ronwalf.phs2010.games.checkers.CheckersPiece;
import net.volus.ronwalf.phs2010.games.checkers.CheckersPiecePainter;
import net.volus.ronwalf.phs2010.games.checkers.CheckersState;
import net.volus.ronwalf.phs2010.games.checkers.heuristics.CheckersCountHeuristic;
import net.volus.ronwalf.phs2010.games.core.GamePlayer;
import net.volus.ronwalf.phs2010.games.core.impl.AlphaBetaFactory;
import net.volus.ronwalf.phs2010.games.core.impl.BestNextMoveFactory;
import net.volus.ronwalf.phs2010.games.core.impl.MinimaxFactory;
import net.volus.ronwalf.phs2010.games.core.impl.RandomMoveFactory;
import net.volus.ronwalf.phs2010.games.gui.BoardPanel;
import net.volus.ronwalf.phs2010.games.util.Board;
import net.volus.ronwalf.phs2010.games.util.SimpleController;
import net.volus.ronwalf.phs2010.networking.message.GameResult;
import net.volus.ronwalf.phs2010.networking.message.GameState;
import net.volus.ronwalf.phs2010.networking.message.MessageFactory;
import net.volus.ronwalf.phs2010.networking.message.Users;

/**
 * Simple chat client based on Swing & MINA that implements the chat protocol.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class SwingCheckersClient extends JFrame {
    private static final long serialVersionUID = 1538675161745436968L;

//    private JTextField inputText;

    private JButton loginButton;

    private JButton startButton;
//    private JButton quitButton;
    
    private PlayerSelector<CheckersState, CheckersMove> playerSelector;

    private JButton closeButton;

    private JTextField serverField;

    private JTextField nameField;

    private BoardPanel<CheckersPiece> boardP;
    
    private JTextArea area;

    private OpponentSelector opponents;
    
    private PlayerInfo<CheckersState> playerInfo;

    private SwingCheckersClientHandler handler;
    
    private String username;

    
    public SwingCheckersClient() {
        super("Checkers!");
        
        
        loginButton = new JButton(new LoginAction());
        loginButton.setText("Connect");
//        quitButton = new JButton(new LogoutAction());
//        quitButton.setText("Disconnect");
        startButton = new JButton(new StartGameAction());
        startButton.setText("Start Game");
        playerSelector = new PlayerSelector<CheckersState, CheckersMove>(CheckersGame.instance);
        playerInfo = new PlayerInfo<CheckersState>();
        closeButton = new JButton(new QuitAction());
        closeButton.setText("Quit");
        boardP = new BoardPanel<CheckersPiece>(new Board<CheckersPiece>(8), CheckersPiecePainter.instance);
        area = new JTextArea(10, 50);
        area.setLineWrap(true);
        area.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(area);
        nameField = new JTextField(10);
        nameField.setEditable(false);
        serverField = new JTextField(10);
        serverField.setEditable(false);

        JPanel h = new JPanel();
        h.setLayout(new BoxLayout(h, BoxLayout.LINE_AXIS));
        h.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel nameLabel = new JLabel("Name: ");
        JLabel serverLabel = new JLabel("Server: ");
        h.add(nameLabel);
        h.add(Box.createRigidArea(new Dimension(10, 0)));
        h.add(nameField);
        h.add(Box.createRigidArea(new Dimension(10, 0)));
        h.add(Box.createHorizontalGlue());
        h.add(Box.createRigidArea(new Dimension(10, 0)));
        h.add(serverLabel);
        h.add(Box.createRigidArea(new Dimension(10, 0)));
        h.add(serverField);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel center = new JPanel();
        //left.setLayout(new BoxLayout(left, BoxLayout.PAGE_AXIS));
        center.setLayout( new BorderLayout() );
        
		center.add(boardP, BorderLayout.CENTER);
		//left.add(Box.createRigidArea(new Dimension(0, 5)));
		//left.add(Box.createHorizontalGlue());
		center.add(scrollPane, BorderLayout.SOUTH);
//        left.add(Box.createRigidArea(new Dimension(0, 5)));
//        left.add(Box.createHorizontalGlue());
//        left.add(inputText);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.add(loginButton);
//        right.add(Box.createRigidArea(new Dimension(0, 5)));
//        right.add(quitButton);
        right.add(startButton);
        right.add(playerSelector);
        right.add(playerInfo);
        right.add(Box.createHorizontalGlue());
        right.add(Box.createRigidArea(new Dimension(0, 25)));
        right.add(closeButton);
        right.add(Box.createVerticalGlue());

        opponents = new OpponentSelector();
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(new JScrollPane(opponents.getList()));
        left.add(Box.createRigidArea(new Dimension(120,0)));
        
        p.add(left);
        p.add(Box.createRigidArea(new Dimension(10, 0)));
        p.add(center);
        p.add(Box.createRigidArea(new Dimension(10, 0)));
        p.add(right);

        getContentPane().add(h, BorderLayout.NORTH);
        getContentPane().add(p);

        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handler.quit();
                dispose();
            }
        });
        setLoggedOut();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public class LoginAction extends AbstractAction {
        private static final long serialVersionUID = 3596719854773863244L;

        public void actionPerformed(ActionEvent e) {

            ConnectDialog dialog = new ConnectDialog(SwingCheckersClient.this);
            dialog.pack();
            dialog.setVisible(true);

            if (dialog.isCancelled()) {
                return;
            }

            SocketAddress address = parseSocketAddress(dialog
                    .getServerAddress());
            username = dialog.getUsername();

            handler = new SwingCheckersClientHandler(SwingCheckersClient.this);
            nameField.setText(username);
            serverField.setText(dialog.getServerAddress());

            if (!handler.connect(address, dialog.isUseSsl(), username)) {
                JOptionPane.showMessageDialog(SwingCheckersClient.this,
                        "Could not connect to " + dialog.getServerAddress()
                                + ". ");
            }
        }
    }
    
    private class StartGameAction extends AbstractAction{
    	
    	private static final long serialVersionUID = 1655291424630954560L;
    	
		public void actionPerformed(ActionEvent e) {
			Collection<String> opps = opponents.getOpponents();
	    	handler.send(MessageFactory.instance.startGame("checkers", opps), 
	    			SwingCheckersClientHandler.errorCloseBack);
		}
    	
    }
//
//    private class LogoutAction extends AbstractAction {
//        private static final long serialVersionUID = 1655297424639924560L;
//
//        public void actionPerformed(ActionEvent e) {
//            try {
//                client.quit();
//                setLoggedOut();
//            } catch (Exception e1) {
//                JOptionPane.showMessageDialog(SwingCheckersClient.this,
//                        "Session could not be closed.");
//            }
//        }
//    }

    private class QuitAction extends AbstractAction {
        private static final long serialVersionUID = -6389802816912005370L;

        public void actionPerformed(ActionEvent e) {
            if (handler != null) {
                handler.quit();
            }
            SwingCheckersClient.this.dispose();
            System.exit(0);
        }
    }

    private void setLoggedOut() {
//        inputText.setEnabled(false);
//        quitButton.setEnabled(false);
    	startButton.setEnabled(false);
        loginButton.setEnabled(true);
    }

    private void setLoggedIn() {
        area.setText("");
//        inputText.setEnabled(true);
        startButton.setEnabled(true);
//        quitButton.setEnabled(true);
        loginButton.setEnabled(false);
    }

    public void log(String text) {
        area.append(text);
    }

    private void notifyError(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private SocketAddress parseSocketAddress(String s) {
        s = s.trim();
        int colonIndex = s.indexOf(":");
        if (colonIndex > 0) {
            String host = s.substring(0, colonIndex);
            int port = parsePort(s.substring(colonIndex + 1));
            System.out.println("Host: " + host);
            System.out.println("Port: " + port);
            return new InetSocketAddress(host, port);
        } else {
            int port = parsePort(s.substring(colonIndex + 1));
            return new InetSocketAddress(port);
        }
    }

    private int parsePort(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Illegal port number: " + s);
        }
    }

    public void connected() {
//        client.login();
    }

    public void disconnected() {
        log("Connection closed.\n");
        setLoggedOut();
    }
    
    public void setGameFinished(final GameResult msg) {
    	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SwingCheckersClient.this.setTitle("Checkers! " + msg.getMessage());
				playerInfo.gameFinished(msg);
			}
		});
    }
    
    public void setGameState(final GameState state) {
    	
    	final CheckersState cstate = CheckersState.parse(state
				.getState());
    	
    	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SwingCheckersClient.this.setTitle("Checkers! "
						+ state.getTurn() + "'s turn");
				playerInfo.stateChanged(state, cstate);
			}
		});

		
		boardP.setBoard(cstate.getBoard());
		if (state.getTurn().equals(username)) {
			final GamePlayer<CheckersState, CheckersMove> player = playerSelector
					.getPlayer(new SimpleController(state
							.getTimeLimit()));
			Runnable searchThread = new Runnable() {

				public void run() {
					CheckersMove move = player.move(cstate);
					handler.send(MessageFactory.instance.gameMove(state.getName(), move.toString()));
				}

			};
			new Thread(searchThread).start();

		}
    }
    
    public void setUsers(final Users users) {
    	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				opponents.setUsers(users.users());
			}
    	});
    }

    public void error(String message) {
        notifyError(message + "\n");
    }

    public void loggedIn() {
        setLoggedIn();
        log("You have joined the server.\n");
    }

    public void loggedOut() {
        log("You have left the server.\n");
        setLoggedOut();
    }


    public static void main(String[] args) {
    	
    	AlphaBetaFactory.register();
    	MinimaxFactory.register();
		BestNextMoveFactory.register();
		RandomMoveFactory.register();
		
		CheckersCountHeuristic.register();
		
    	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SwingCheckersClient client = new SwingCheckersClient();
				client.pack();
				client.setVisible(true);
				client.setSize(800,600);
			}
    	});
    }
}
