package edu.tamu.recognition.paleo;
import java.awt.Dimension;

import javax.swing.JLayeredPane;
import javax.swing.OverlayLayout;

import edu.tamu.core.sketch.Sketch;
import edu.tamu.core.sketch.Stroke;

public class JSketchCanvas extends JLayeredPane {
	
	public static final int SKETCH = 1;
	public static final int BACKGROUND = 2;
	public static final int EVERYTHING = SKETCH | BACKGROUND;

	protected BackgroundImagePanel background;
	protected JDrawPanel drawPanel;
	
	public JSketchCanvas()
	{
		setLayout(new OverlayLayout(this));
		
		background = new BackgroundImagePanel();
		add(background, 0);
		
		drawPanel = new JDrawPanel();
		add(drawPanel, 0);
	}
	
	public JSketchCanvas(Sketch s)
	{
		this();
		drawPanel.setSketch(s);
	}
	
	public void setSketch(Sketch s)
	{
		drawPanel.setSketch(s);
	}
	
	public Sketch getSketch()
	{
		return drawPanel.getSketch();
	}
	
	@Override
	public void setPreferredSize(Dimension d)
	{
		super.setPreferredSize(d);
		drawPanel.setPreferredSize(d);
		background.setPreferredSize(d);
	}
	
	public void setStrokeWidth(float width)
	{
		drawPanel.setStrokeWidth(width);
	}
	
	public void clear()
	{
		clear(EVERYTHING);
	}
	
	public void clear(int howMuch)
	{
		if ((howMuch & BACKGROUND) != 0)
		{
			background.clear();
		}
		
		if ((howMuch & SKETCH) != 0)
		{
			drawPanel.clear();
		}
	}
	
	public boolean isStrokeFinished()  {

		if(drawPanel.isStrokeFinished())
			System.out.println("Stroke Finished");
		
		return drawPanel.isStrokeFinished();
	}
	
	public void setStrokeFinished(boolean strokeFinished)  {
		drawPanel.setStrokeFinished(strokeFinished);
	}
	
	public Stroke getCurrentStroke() {
		return drawPanel.getCurrentStroke();
	}
	
	public void setCurrentStroke(Stroke currentStroke) {
		drawPanel.setCurrentStroke(currentStroke);
	}
	
	public void setJavaShape(java.awt.Shape javaShape) {
		drawPanel.setJavaShape(javaShape);
	}
}
