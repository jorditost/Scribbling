package edu.tamu.recognition.paleo;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import edu.tamu.core.sketch.BoundingBox;
import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Shape;
import edu.tamu.core.sketch.Stroke;
import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Stroke;
import edu.tamu.core.util.QuadPath;

import com.jhlabs.image.BoxBlurFilter;
import com.jhlabs.image.GaussianFilter;
import com.jhlabs.image.PolarFilter;
import com.jhlabs.image.RaysFilter;
import com.jhlabs.image.RippleFilter;



public class BackgroundImagePanel extends JPanel
{
	/**
	 * Generated ID
	 */
	private static final long serialVersionUID = 5942451348530713893L;

	/**
	 * Path to image (so it can be saved)
	 */
	private String path;
	
	/**
	 * Image to draw as background
	 */
	private BufferedImage bg;
	
	/**
	 * Width and height of the image
	 */
	private Dimension imageSize;
	
	/**
	 * List of strokes to highlight
	 */
	private ArrayList<Stroke> highlightStrokes = new ArrayList<Stroke>();
	
	/**
	 * Shape to highlight
	 */
	private Shape highlightShape = null;
	
	/**
	 * Color of highlight
	 */
	private static final Color HIGHLIGHT_COLOR = new Color(255, 150, 100);
	
	/**
	 * Areas to highlight because they are wrong/there's something missing
	 */
	private ArrayList<BoundingBox> areasWrong = new ArrayList<BoundingBox>();	
	
	/**
	 * Color of wrongness
	 */
	private static Color INCORRECT_COLOR = new Color(255,0,0,50);
	
	/**
	 * Stroke to highlight
	 */
	private BasicStroke highlightStroke = new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	
	/**
	 * BufferedImage to create beautiful highlighted strokes
	 */
	private BufferedImage highlightImage;
	
	private BoxBlurFilter boxFilter = new BoxBlurFilter(3,3,3);
	private BoundingBox highlightBox;
	private boolean drawBlur = false;
	
	public BackgroundImagePanel()
	{
		path = "";
		imageSize = new Dimension();
		initializePanel();
	}
	
	public BackgroundImagePanel(String filename)
	{
		this();
		
		path = filename;
		setImage(filename);		
	}
	
	private void initializePanel()
	{
		setOpaque(false);
	}
	
	public void setImage(String filename)
	{
		path = filename;
		
		try {
            bg = ImageIO.read(new File(filename));
	        imageSize = new Dimension(bg.getWidth(), bg.getHeight());
		} catch (IOException e) {
			e.printStackTrace();
        }
	}
	
	/**
	 * Get an array of pixels representing the image
	 * @return
	 */
	public int[] getPixels()
	{
		int[] pixels = new int[(int)imageSize.getWidth() * (int)imageSize.getHeight()];
		if (imageSize.getWidth() > 0 && imageSize.getHeight() > 0)
		{
			bg.getRGB(0, 0, bg.getWidth(), bg.getHeight(), pixels, 0, bg.getWidth());
		}
		return pixels;
	}
	
	/**
	 * Set the image based on an array of pixels
	 * @param pixels
	 */
	public void setImage(int[] pixels)
	{
		if (imageSize.getWidth() > 0 && imageSize.getHeight() > 0)
		{
			bg = new BufferedImage((int)imageSize.getWidth(), (int)imageSize.getHeight(), BufferedImage.TYPE_INT_ARGB);
			bg.setRGB(0, 0, (int)imageSize.getWidth(), (int)imageSize.getHeight(), pixels, 0, (int)imageSize.getWidth());
		}
	}
	
	/**
	 * Set the size of the image
	 */
	public void setImageSize(Dimension size)
	{
		imageSize = new Dimension(size);
	}
	
	public Dimension getImageSize()
	{
		return imageSize;
	}
	
	public void clear()
	{
		path = "";
		bg = null;
		imageSize = new Dimension();
	}
	
	public BufferedImage getImage()
	{
		return bg;
	}
	
	public String getImagePath()
	{
		return path;
	}
	
	public void clearHighlightStrokes()
	{
		highlightStrokes.clear();
		repaint();
	}
	
	public ArrayList<Stroke> getHighlightedStrokes()
	{
		return highlightStrokes;
	}
	
	public void setHighlightStrokes(ArrayList<Stroke> strokes)
	{
		highlightStrokes = new ArrayList<Stroke>(strokes);
		repaint();
	}
	
	public void clearHighlightShape()
	{
		highlightShape = null;
		repaint();
	}
	
	public Shape getHighlightedShape()
	{
		return highlightShape;
	}	
	
	public void setHighlightShape(Shape shape)
	{
		highlightShape = shape;
		repaint();
	}	
	
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		RenderingHints renderHints = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		renderHints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		renderHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
						RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		
		g2.setRenderingHints(renderHints);
		
		// paper color background
		g2.setBackground(new Color(251, 255, 235));
		g2.clearRect(0, 0, getWidth(), getHeight());
		
		// draw the grid for graph paper
		g2.setColor(new Color(197, 214, 162));
		for (int i = 0; i < getWidth(); i += 20)
		{
			g2.drawLine(i, 0, i, getHeight());
		}
		for (int i = 0; i < getHeight(); i += 20)
		{
			g2.drawLine(0, i, getWidth(), i);
		}
		
		// draw a background image if loaded
		if (bg != null)
		{
			g2.drawImage(bg, 
						 0, 0,
						 bg.getWidth(), 
						 bg.getHeight(),
					     0, 0,
					     bg.getWidth(),
					     bg.getHeight(),
					     null);
		}
		
		// draw highlighted strokes
		if (highlightStrokes.size() > 0)
		{
			if (drawBlur && highlightImage != null)
			{
				g2.drawImage(highlightImage, 
							 (int)highlightBox.x, 
							 (int)highlightBox.y, 
							 (int)highlightBox.x + (int)highlightBox.width, 
							 (int)highlightBox.y + (int)highlightBox.height,
							 0, 0, 
							 highlightImage.getWidth(), 
							 highlightImage.getHeight(), 
							 null);
			}
			else
			{
				g2.setColor(HIGHLIGHT_COLOR);
				g2.setStroke(highlightStroke);
				for (Stroke stroke : highlightStrokes)
				{
					QuadPath.drawPath(stroke, g2);
				}
			}
		}
		
		if(areasWrong.size() > 0) {
			g2.setColor(INCORRECT_COLOR);
			for (BoundingBox area : areasWrong) {
				g2.fillRect((int)area.x, (int)area.y, (int)area.width, (int)area.height);
				// draw some kind of ghostly, fuzzy outline or shape behind 
				//   the normal strokes where there is something missing/wrong	
			}
		}
	}
	
	private void makeHighlightImage()
	{		
		if (highlightStrokes.size() > 0)
		{
			// make a bounding box of all the strokes to limit the size of the image
			highlightBox = new BoundingBox(highlightStrokes.get(0).getBoundingBox().x,
										   highlightStrokes.get(0).getBoundingBox().y,
										   highlightStrokes.get(0).getBoundingBox().x + 
										   	   highlightStrokes.get(0).getBoundingBox().width,
										   highlightStrokes.get(0).getBoundingBox().y + 
										       highlightStrokes.get(0).getBoundingBox().height);
			for (Stroke stroke : highlightStrokes)
			{
				BoundingBox.union(stroke.getBoundingBox(), highlightBox, highlightBox);
			}
			
			// inflate the bounding box a few pixels to accommodate blurring
			highlightBox.x -= 10;
			highlightBox.y -= 10;
			highlightBox.width += 20;
			highlightBox.height += 20;
			
			highlightImage = 
				new BufferedImage((int)highlightBox.width, 
								  (int)highlightBox.height, 
								  BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g2 = (Graphics2D)highlightImage.getGraphics();
			
			g2.setColor(HIGHLIGHT_COLOR);
			g2.setStroke(highlightStroke);
			
			
						
			for (Stroke stroke : highlightStrokes)
			{
				Stroke newStroke = new Stroke();
				
				for (Point p : stroke.getPoints())
				{
					newStroke.addPoint(new Point(p.getX() - highlightBox.x,
											     p.getY() - highlightBox.y));
				}
				
				QuadPath.drawPath(newStroke, g2);
			}
					
			boxFilter.filter(highlightImage, highlightImage);
		}
	}
	
	/**
	 * WORK IN PROGRESS
	 * 
	 * add problematic areas that should be somehow visually indicated
	 * 
	 * @param areasToHighlight
	 */
	public void addHighlightWrongness(BoundingBox newArea) {
		areasWrong.add(newArea);
	}
	
	/**
	 * WORK IN PROGRESS
	 * 
	 * remove problematic areas
	 * 
	 * also, does array list think it's null when the final element is removed??
	 * 
	 * @param area
	 */
	public void clearHighlightWrongness() {
		areasWrong.clear();
	}
	
}
