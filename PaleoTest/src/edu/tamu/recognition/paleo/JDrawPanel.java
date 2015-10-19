package edu.tamu.recognition.paleo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Shape;
import edu.tamu.core.sketch.Sketch;
import edu.tamu.core.sketch.Stroke;

public class JDrawPanel extends JPanel implements MouseListener, MouseMotionListener {

	protected Sketch sketch;
	protected Stroke currentStroke;
	
	protected Shape testShape;
	
	protected java.awt.Stroke drawStroke;
	java.awt.Shape javaShape;
	
	protected boolean strokeFinished;
	
	public JDrawPanel()
	{
		sketch = new Sketch();
		addMouseListener(this);
		addMouseMotionListener(this);
		strokeFinished = false;
		System.out.println("Stroke false");
		javaShape = null;
	}
	
	public JDrawPanel(Sketch s)
	{
		this();
		setSketch(s);
	}
	
	public Sketch getSketch()
	{
		return sketch;
	}
	
	public void setSketch(Sketch s)
	{
		sketch = s;
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		setForeground(Color.black);
		strokeFinished = false;
		currentStroke = null;
		currentStroke = new Stroke();
		currentStroke.addPoint(new Point(e));
		currentStroke.setStroke(drawStroke);
		//System.out.println("New Stroke");
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		sketch.add(currentStroke);
		
		strokeFinished = true;
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		currentStroke.addPoint(new Point(e));
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
	
	public void paintComponent(Graphics g)
	{
		//System.out.println("Paint");
		sketch.paint(g);
		
		if(javaShape != null) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(Color.red);
			g2.setStroke(new BasicStroke(3));
			g2.draw(javaShape);
			javaShape = null;
		}
		
		if (currentStroke != null)
			currentStroke.paint(g);
	}
	
	public void setStrokeWidth(float width)
	{
		drawStroke = new java.awt.BasicStroke(width);
	}
	
	public void clear()
	{
		sketch.clear();
		currentStroke = null;
		repaint();
	}
	
	public boolean isStrokeFinished()  {
		return strokeFinished;
	}
	
	public void setStrokeFinished(boolean strokeFinished)  {
		this.strokeFinished = strokeFinished;
	}
	

	public void setCurrentStroke(Stroke currentStroke) {
		 this.currentStroke = currentStroke;
		 repaint();
	}
	
	public Stroke getCurrentStroke() {
		return currentStroke;
	}
	
	public void setJavaShape(java.awt.Shape javaShape) {
		
		this.javaShape = javaShape;	
		 repaint();
	}
}
