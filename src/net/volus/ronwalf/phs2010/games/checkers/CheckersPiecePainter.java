package net.volus.ronwalf.phs2010.games.checkers;

import java.awt.Color;
import java.awt.Graphics2D;

import net.volus.ronwalf.phs2010.games.gui.PiecePainter;

public class CheckersPiecePainter implements PiecePainter<CheckersPiece> {

	public final static CheckersPiecePainter instance = new CheckersPiecePainter();
	
	public Color getColor() { return Color.white; }
	
	public void paint(Graphics2D g, int size, CheckersPiece piece) {
		
		if (piece == null)
			return;
		
		Color fill = Color.red;
		if (piece.player() != 0)
			fill = Color.white;
			
		paintO(g, size, fill);
		if (piece.isKing())
			paintX(g, size);
	}
	
	private void paintO(Graphics2D g, int size, Color fill) {
		int w = Math.max(2, size/10);
		g.setColor(Color.black);
		g.fillOval(1, 1, size-2, size-2);
		g.setColor(fill);
		g.fillOval(w, w, size-2*w, size-2*w);
	}
	
	private void paintX(Graphics2D g, int size) {
		g.setColor(Color.black);
		int w = Math.max(2, size/10);
		int[] xPoints1 = {w, size - 1, size - w, 1};
		int[] yPoints1 = {1, size - w, size - 1, w};
		g.fillPolygon(xPoints1, yPoints1, xPoints1.length);
		
		int[] xPoints2 = {1, size - w, size - 1, w};
		int[] yPoints2 = {size - w, 1, w, size - 1};
		g.fillPolygon(xPoints2, yPoints2, xPoints2.length);
	}

}
