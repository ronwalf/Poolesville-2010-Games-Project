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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import net.volus.ronwalf.phs2010.games.util.Board;

public class BoardPanel<E> extends JPanel {
	
	public class Cell extends JPanel {
		final public int i;
		final public int j;
		
		public Cell(final int i, final int j) {
			this.i = i;
			this.j = j;
			setBackground(painter.getColor());
			
			addMouseListener(new MouseAdapter(){
	            public void mousePressed(MouseEvent e){
	            	System.out.println("Clicked!");
	                if (listener != null) {
	                	listener.cellPressed(Cell.this);
	                }
	            }
	        });
			
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			//System.out.println("Painting cell: " + i + ", " + j + ", " + board.get(i, j));
			int size = Math.min(getHeight(), getWidth());
			painter.paint((Graphics2D) g, size, board.get(i, j));
		}
		
	}
	
	private Board<E> board;
	final private PiecePainter<E> painter;
	private BoardListener<E> listener;
	
	public BoardPanel(Board<E> board, PiecePainter<E> painter) {
		this.board = board;
		this.painter = painter;
		this.listener = null;
		
		GridLayout layout = new GridLayout(board.getSize(), board.getSize());
		layout.setHgap(2);
		layout.setVgap(2);
		setLayout(layout);
		setBackground(Color.black);
		
		for (int i = 0; i < board.getSize(); i++) {
			for (int j = 0; j < board.getSize(); j++) {
				add(new Cell(i, j));
			}	
		}
	}
	
	public void setBoard(Board<E> board) {
		this.board = board;
		repaint();
	}
	
	public void setListener(BoardListener<E> listener) {
		this.listener = listener;
	}


}
