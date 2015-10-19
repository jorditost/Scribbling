package edu.tamu.recognition.paleo;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import ecologylab.serialization.ElementState;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.TranslationScope;
import edu.tamu.core.sketch.STranslationScope;
import edu.tamu.core.sketch.Sketch;
import edu.tamu.core.sketch.Stroke;
import edu.tamu.core.util.RegisterSimplTypes;

public class DrawingGUI extends JFrame implements WindowListener {

	JSketchCanvas canvas;
	
	public DrawingGUI()
	{
		canvas = new JSketchCanvas();
		
		canvas.setPreferredSize(new Dimension(500,500));
		add(canvas);
		
		try {
			canvas.setSketch(Sketch.deserialize(new File("test.xml")));
		} catch (SIMPLTranslationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addWindowListener(this);
		pack();
	}
	


	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		try {
			canvas.getSketch().serialize("test.xml");
		} catch (SIMPLTranslationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("done");
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public Sketch getSketch()
	{
		return canvas.getSketch();
	}
	
	public boolean isStrokeFinished()  {
		return canvas.isStrokeFinished();
	}
	
	public void setStrokeFinished(boolean strokeFinished)  {
		canvas.setStrokeFinished(strokeFinished);
	}
	
	public void clear()  {
		canvas.clear();
	}
	
	public Stroke getCurrentStroke() {
		return canvas.getCurrentStroke();
	}
	
	public void setCurrentStroke(Stroke currentStroke) {
		canvas.setCurrentStroke(currentStroke);
	}
	
	public void setJavaShape(java.awt.Shape javaShape) {
		canvas.setJavaShape(javaShape);
	}
}
