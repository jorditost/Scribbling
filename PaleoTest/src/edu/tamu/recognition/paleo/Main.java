package edu.tamu.recognition.paleo;


import java.awt.Color;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import edu.tamu.core.gui.TestApplication;
import edu.tamu.core.sketch.IBeautifiable.Type;
import edu.tamu.core.sketch.ISegmenter;
import edu.tamu.core.sketch.Interpretation;
import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Segmentation;
import edu.tamu.core.sketch.Shape;
import edu.tamu.core.sketch.Sketch;
import edu.tamu.core.sketch.Stroke;
import edu.tamu.core.util.RegisterSimplTypes;
import edu.tamu.recognition.paleo.CircleFit;
import edu.tamu.recognition.paleo.Fit;
import edu.tamu.recognition.paleo.FitList;
import edu.tamu.recognition.paleo.OrigPaleoSketchRecognizer;
import edu.tamu.recognition.paleo.PaleoConfig;
import edu.tamu.recognition.paleo.PaleoSketchRecognizer;
import edu.tamu.recognition.paleo.PolylineFit;
import edu.tamu.recognition.paleo.RectangleFit;
import edu.tamu.recognition.paleo.StrokeFeatures;
import edu.tamu.segmentation.paleo.PaleoSegmenter;




public class Main {

    /**
     * @param args
     */
	public static DrawingGUI drawingGUI;
	
    public static void main(String[] args) throws IOException {
    	
	
	
     //########### Configure Segmentation Settings ###############
    	
  	PaleoConfig config = PaleoConfig.allOff();
  	config = setCustomConfig(config);

    //Socket
	Requester client = new Requester();
	client.port = 4000;
    	
	Provider server = new Provider();
	
	int a=0;
	while(a == 0) {			
		
	//System.out.println("bla");
		
		//System.out.println("WAITING FOR DATA");
		client.run();
		if(client.messageList.size()>0) {

			System.out.println("Beautifying Data");
		
			for(int i=0; i<client.messageList.size(); i++) {
		
				System.out.println("############Got Points################");
				String socketString = client.messageList.get(i);
				//List<Point> points = parseSocketData(socketString);
				//Stroke stroke = new Stroke(points);
		
				System.out.println(socketString);
				//String senderString = convertStrokeIntoShape(stroke, config);
				
				server.run("blabla");
			}
		}
		else  {
			System.out.println("Message List Empty");
		}
	}
    
//	EchoClient client = new EchoClient();
//	client.run(senderString);
//			
			
	//drawWithGUI(config);
   

    
   }

    public static PaleoConfig setCustomConfig(PaleoConfig config) {
    
      	config.setLineTestOn(true);
      	config.setArcTestOn(true);				//incomplete circle
      	config.setEllipseTestOn(true);
      	config.setCircleTestOn(true);	
      	config.setCurveTestOn(true);			//up to 5-degree bezier curve
      	config.setHelixTestOn(false);
      	
      	config.setSpiralTestOn(false);
      	config.setArrowTestOn(false);
      	config.setComplexTestOn(false);			//complex shapes possible!
      	config.setPolylineTestOn(true);
      	config.setPolygonTestOn(true);
      	config.setRectangleTestOn(true);
      	config.setSquareTestOn(true);
      
      	config.setDiamondTestOn(false);
      	config.setDotTestOn(false);
      	config.setWaveTestOn(false);			//sinusoidal curve
      	config.setGullTestOn(false);
      	config.setBlobTestOn(false);
      	config.setInfinityTestOn(false);
      	config.setNBCTestOn(false);
    
      	return config;
    }
    
    public static List<Point> parseSocketData(String socketString) {
    	
        List<Point> points = new ArrayList<Point>();
        
    	String pointListString[] = socketString.split("\\|");
    	
    	for(int i=0; i<pointListString.length;i++)  {
    		
    		String pointString = pointListString[i];
    		String point[] = pointString.split(" ");
    		
    		float xCoord = Float.valueOf(point[0]).floatValue();
    		float yCoord = Float.valueOf(point[1]).floatValue();
    		Point nextPoint = new Point(xCoord,yCoord);
    		
    		points.add(nextPoint);
    	}
        
//        points.add(new Point(0, 0));
//        points.add(new Point(30, 5));
//        points.add(new Point(65, 0));
//        points.add(new Point(85, 0));
//        points.add(new Point(100, 0));
//        
//        points.add(new Point(106, 10));
//        points.add(new Point(103, 20));
//        points.add(new Point(108, 30));
//        points.add(new Point(102, 40));
//        points.add(new Point(106, 50));
//        points.add(new Point(104, 60));
//        points.add(new Point(102, 70));
//        points.add(new Point(106, 90));
//        points.add(new Point(102, 110));
//        points.add(new Point(101, 120));
//        points.add(new Point(103, 140));
//        points.add(new Point(104, 160));
//        points.add(new Point(105, 180));
//        points.add(new Point(106, 190));
//        points.add(new Point(107, 200));
//        
//        points.add(new Point(100, 200));
//        points.add(new Point(90, 200));
//        points.add(new Point(80, 200));
//        points.add(new Point(70, 200));
//        points.add(new Point(50, 200));
//        points.add(new Point(40, 200));
//        points.add(new Point(30, 200));
//        points.add(new Point(12, 200));
//        points.add(new Point(10, 200));
//        points.add(new Point(-1, 200));
//        
//        points.add(new Point(0, 160));
//        points.add(new Point(5, 120));
//        points.add(new Point(3, 100));
//        points.add(new Point(2, 40));
//        points.add(new Point(1, 20));
//        points.add(new Point(7, 10));
//        points.add(new Point(10, 0));
    	
    	return points;
    }
    
    public static String convertStrokeIntoShape(Stroke stroke, PaleoConfig config) {
    
		//Start Recognition
		OrigPaleoSketchRecognizer origPaleo = new OrigPaleoSketchRecognizer(stroke, config);
		origPaleo.recognize();
		FitList fitList = origPaleo.getFits(); 
		
		System.out.println("Number of Fits: " + fitList.size());
		
		String senderString = new String();
		
		//for(int i=0; i<fitList.size(); i++) {
			
			Fit fit = fitList.get(0);
			
			if(fit.getName().equals("Arc")) {
			
				ArcFit arcFit = (ArcFit)fit;
				Interpretation interpretation = arcFit.getShape().getInterpretation();
				System.out.println("### Name:" + interpretation.label + "###");
				System.out.println("Confidence:" + interpretation.confidence);
				
				java.awt.Shape javaShape = arcFit.m_shape;
				
				senderString += "Arc|";
				
			}
			
			else if(fit.getName().equals("Curve")) {
			
				CurveFit curveFit = (CurveFit)fit;
				//can we change the minimum degree field?
				
				Interpretation interpretation = curveFit.getShape().getInterpretation();
				System.out.println("### Name:" + interpretation.label + "###");
				System.out.println("Confidence:" + interpretation.confidence);
				
				java.awt.Shape javaShape = curveFit.m_shape;
				Point2D[] controlPoints = curveFit.m_P;
				
				senderString += "Curve|";
				
				for(int a=0; a<controlPoints.length;a++) {
					
					Point2D point = controlPoints[a];
					
					senderString += point.getX() + " ";
					senderString += point.getY() + "|";
				}
				
				System.out.println(senderString);
				//drawingGUI.setJavaShape(javaShape);

			}
			else if(fit.getName().equals("Line")) {
				
				LineFit lineFit = (LineFit)fit;
				
				Interpretation interpretation = lineFit.getShape().getInterpretation();
				System.out.println("### Name:" + interpretation.label + "###");
				System.out.println("Confidence:" + interpretation.confidence);
				
				java.awt.Shape javaShape = lineFit.m_shape;
				

				senderString += "Line|";
				
				float[] coords = new float[2];
				for(PathIterator iterator = javaShape.getPathIterator(null); !iterator.isDone(); iterator.next()) {
					
					int type = iterator.currentSegment(coords);
					float coord_x = coords[0];
					float coord_y = coords[1];
					
					senderString += coord_x + " ";
					senderString += coord_y + "|";
				}
				
				System.out.println(senderString);	
				//drawingGUI.setJavaShape(javaShape);

			}
							
			else if(fit.getName().equals("Polyline")) {
				
				PolylineFit polylineFit = (PolylineFit)fit;
				
				Interpretation interpretation = polylineFit.getShape().getInterpretation();
				System.out.println("### Name:" + interpretation.label + "###");
				System.out.println("Confidence:" + interpretation.confidence);
				
				java.awt.Shape javaShape = polylineFit.m_shape;
				
	
				senderString += "Polyline|";
				
				float[] coords = new float[2];
				for(PathIterator iterator = javaShape.getPathIterator(null); !iterator.isDone(); iterator.next()) {
					
					int type = iterator.currentSegment(coords);
					float coord_x = coords[0];
					float coord_y = coords[1];
					
					senderString += coord_x + " ";
					senderString += coord_y + "|";
				}
				
				System.out.println(senderString);	
				//drawingGUI.setJavaShape(javaShape);
			}
			
			else if(fit.getName().equals("Rectangle")) {
				
				RectangleFit rectangleFit = (RectangleFit)fit;
				
				Interpretation interpretation = rectangleFit.getShape().getInterpretation();
				System.out.println("### Name:" + interpretation.label + "###");
				System.out.println("Confidence:" + interpretation.confidence);
				
				java.awt.Shape javaShape = rectangleFit.m_shape;
				

				senderString += "Rectangle|";
				
				float[] coords = new float[2];
				int numberStrokes = 1;
				for(PathIterator iterator = javaShape.getPathIterator(null); !iterator.isDone(); iterator.next()) {
					
					if(numberStrokes < 6) {
						
						int type = iterator.currentSegment(coords);
						float coord_x = coords[0];
						float coord_y = coords[1];
					
						senderString += coord_x + " ";
						senderString += coord_y + "|";
					}
					numberStrokes++;
				}
				
				System.out.println(senderString);
				//drawingGUI.setJavaShape(javaShape);

			}
			
			else if(fit.getName().equals("Square")) {
				
				SquareFit squareFit = (SquareFit)fit;
				
				Interpretation interpretation = squareFit.getShape().getInterpretation();
				System.out.println("### Name:" + interpretation.label + "###");
				System.out.println("Confidence:" + interpretation.confidence);
				
				java.awt.Shape javaShape = squareFit.m_shape;
				

				senderString += "Square|";
				
				float[] coords = new float[2];
				int numberStrokes = 1;
				for(PathIterator iterator = javaShape.getPathIterator(null); !iterator.isDone(); iterator.next()) {
					
					if(numberStrokes < 6) {
						
						int type = iterator.currentSegment(coords);
						float coord_x = coords[0];
						float coord_y = coords[1];
					
						senderString += coord_x + " ";
						senderString += coord_y + "|";
					}
					numberStrokes++;
				}
				
				System.out.println(senderString);
				//drawingGUI.setJavaShape(javaShape);

			}
			
			
			else if(fit.getName().equals("Circle")) {
				
				CircleFit circleFit = (CircleFit)fit;
				java.awt.Shape javaShape = circleFit.m_shape;
				
				Interpretation interpretation = circleFit.getShape().getInterpretation();
				System.out.println("### Name:" + interpretation.label + "###");
				System.out.println("Confidence:" + interpretation.confidence);
				
				Point2D center = circleFit.getCenter();
				double radius = circleFit.getRadius();
				

				senderString += "Circle|";
				senderString += center.getX() + " ";
				senderString += center.getY() + "|";
				senderString += radius + "|";
				
				System.out.println(senderString);
				//drawingGUI.setJavaShape(javaShape);

				
			}
			
			else if(fit.getName().equals("Ellipse")) {
				
				EllipseFit ellipseFit = (EllipseFit)fit;
				java.awt.Shape javaShape = ellipseFit.m_shape;
				
				Interpretation interpretation = ellipseFit.getShape().getInterpretation();
				System.out.println("### Name:" + interpretation.label + "###");
				System.out.println("Confidence:" + interpretation.confidence);
				
				Point2D center = ellipseFit.getCenter();
				double majorAxisLength = ellipseFit.m_majorAxisLength;
				double minorAxisLength = ellipseFit.m_minorAxisLength;
				double majorAxisAngle = ellipseFit.m_majorAxisAngle;
				//CvPoint center, CvSize axes, double angle, double startAngle, double endAngle,
				

				senderString += "Circle|";
				senderString += center.getX() + " ";
				senderString += center.getY() + "|";
				senderString += majorAxisLength + "|";
				senderString += minorAxisLength + "|";
				senderString += majorAxisAngle + "|";

				System.out.println(senderString);
				//drawingGUI.setJavaShape(javaShape);
			}
			
			else if(fit.getName().equals("Polygon")) {
				
				PolygonFit polygonFit = (PolygonFit)fit;
				
				Interpretation interpretation = polygonFit.getShape().getInterpretation();
				System.out.println("### Name:" + interpretation.label + "###");
				System.out.println("Confidence:" + interpretation.confidence);
				
				java.awt.Shape javaShape = polygonFit.m_shape;
				

				senderString += "Polygon|";
				
				float[] coords = new float[2];
				for(PathIterator iterator = javaShape.getPathIterator(null); !iterator.isDone(); iterator.next()) {
					
					int type = iterator.currentSegment(coords);
					float coord_x = coords[0];
					float coord_y = coords[1];
					
					senderString += coord_x + " ";
					senderString += coord_y + "|";
				}
				
				System.out.println(senderString);
				//drawingGUI.setJavaShape(javaShape);

			}
			
			else {
				System.out.println(fit.getName());
			}
		

    	return senderString;
    }
    
    
    public static void drawWithGUI(PaleoConfig config) {
    	
     	
    	RegisterSimplTypes.register();
    	drawingGUI = new DrawingGUI();
    	drawingGUI.setVisible(true);
    	
    	drawingGUI.clear();
    	
    	while(true) {
    
    		if(drawingGUI.isStrokeFinished()) {
    			
    			System.out.println("Stroke Finished");
    			drawingGUI.setStrokeFinished(false);
    			
    			Stroke stroke = drawingGUI.getCurrentStroke();
    			
    			//Start Recognition
    			OrigPaleoSketchRecognizer origPaleo = new OrigPaleoSketchRecognizer(stroke, config);
    			origPaleo.recognize();
    			FitList fitList = origPaleo.getFits(); 
    			
    			System.out.println("Number of Fits: " + fitList.size());
    			
    			//for(int i=0; i<fitList.size(); i++) {
    				
    				Fit fit = fitList.get(0);
    				
    				if(fit.getName().equals("Arc")) {
    				
    					ArcFit arcFit = (ArcFit)fit;
    					Interpretation interpretation = arcFit.getShape().getInterpretation();
    					System.out.println("### Name:" + interpretation.label + "###");
    					System.out.println("Confidence:" + interpretation.confidence);
    					
    					java.awt.Shape javaShape = arcFit.m_shape;
    					
    					String arcString = new String();
    					arcString += "Arc|";
    					
    				}
    				
    				else if(fit.getName().equals("Curve")) {
    				
    					CurveFit curveFit = (CurveFit)fit;
    					//can we change the minimum degree field?
    					
    					Interpretation interpretation = curveFit.getShape().getInterpretation();
    					System.out.println("### Name:" + interpretation.label + "###");
    					System.out.println("Confidence:" + interpretation.confidence);
    					
    					java.awt.Shape javaShape = curveFit.m_shape;
    					Point2D[] controlPoints = curveFit.m_P;
    					
    					String curveString = new String();
    					curveString += "Curve|";
    					
    					for(int a=0; a<controlPoints.length;a++) {
    						
    						Point2D point = controlPoints[a];
    						
    						curveString += point.getX() + " ";
    						curveString += point.getY() + "|";
    					}
    					
    					System.out.println(curveString);
    					drawingGUI.setJavaShape(javaShape);
    
    				}
    				else if(fit.getName().equals("Line")) {
    					
    					LineFit lineFit = (LineFit)fit;
    					
    					Interpretation interpretation = lineFit.getShape().getInterpretation();
    					System.out.println("### Name:" + interpretation.label + "###");
    					System.out.println("Confidence:" + interpretation.confidence);
    					
    					java.awt.Shape javaShape = lineFit.m_shape;
    					
    					String lineString = new String();
    					lineString += "Line|";
    					
    					float[] coords = new float[2];
    					for(PathIterator iterator = javaShape.getPathIterator(null); !iterator.isDone(); iterator.next()) {
    						
    						int type = iterator.currentSegment(coords);
    						float coord_x = coords[0];
    						float coord_y = coords[1];
    						
    						lineString += coord_x + " ";
    						lineString += coord_y + "|";
    					}
    					
    					System.out.println(lineString);	
    					drawingGUI.setJavaShape(javaShape);
    
    				}
    								
    				else if(fit.getName().equals("Polyline")) {
    					
    					PolylineFit polylineFit = (PolylineFit)fit;
    					
    					Interpretation interpretation = polylineFit.getShape().getInterpretation();
    					System.out.println("### Name:" + interpretation.label + "###");
    					System.out.println("Confidence:" + interpretation.confidence);
    					
    					java.awt.Shape javaShape = polylineFit.m_shape;
    					
    					String polylineString = new String();
    					polylineString += "Polyline|";
    					
    					float[] coords = new float[2];
    					for(PathIterator iterator = javaShape.getPathIterator(null); !iterator.isDone(); iterator.next()) {
    						
    						int type = iterator.currentSegment(coords);
    						float coord_x = coords[0];
    						float coord_y = coords[1];
    						
    						polylineString += coord_x + " ";
    						polylineString += coord_y + "|";
    					}
    					
    					System.out.println(polylineString);	
    					drawingGUI.setJavaShape(javaShape);
    				}
    				
    				else if(fit.getName().equals("Rectangle")) {
    					
    					RectangleFit rectangleFit = (RectangleFit)fit;
    					
    					Interpretation interpretation = rectangleFit.getShape().getInterpretation();
    					System.out.println("### Name:" + interpretation.label + "###");
    					System.out.println("Confidence:" + interpretation.confidence);
    					
    					java.awt.Shape javaShape = rectangleFit.m_shape;
    					
    					String rectangleString = new String();
    					rectangleString += "Rectangle|";
    					
    					float[] coords = new float[2];
    					int numberStrokes = 1;
    					for(PathIterator iterator = javaShape.getPathIterator(null); !iterator.isDone(); iterator.next()) {
    						
    						if(numberStrokes < 6) {
    							
    							int type = iterator.currentSegment(coords);
    							float coord_x = coords[0];
    							float coord_y = coords[1];
    						
    							rectangleString += coord_x + " ";
    							rectangleString += coord_y + "|";
    						}
    						numberStrokes++;
    					}
    					
    					System.out.println(rectangleString);
    					drawingGUI.setJavaShape(javaShape);
    
    				}
    				
    				else if(fit.getName().equals("Square")) {
    					
    					SquareFit squareFit = (SquareFit)fit;
    					
    					Interpretation interpretation = squareFit.getShape().getInterpretation();
    					System.out.println("### Name:" + interpretation.label + "###");
    					System.out.println("Confidence:" + interpretation.confidence);
    					
    					java.awt.Shape javaShape = squareFit.m_shape;
    					
    					String squareString = new String();
    					squareString += "Square|";
    					
    					float[] coords = new float[2];
    					int numberStrokes = 1;
    					for(PathIterator iterator = javaShape.getPathIterator(null); !iterator.isDone(); iterator.next()) {
    						
    						if(numberStrokes < 6) {
    							
    							int type = iterator.currentSegment(coords);
    							float coord_x = coords[0];
    							float coord_y = coords[1];
    						
    							squareString += coord_x + " ";
    							squareString += coord_y + "|";
    						}
    						numberStrokes++;
    					}
    					
    					System.out.println(squareString);
    					drawingGUI.setJavaShape(javaShape);
    
    				}
    				
    				
    				else if(fit.getName().equals("Circle")) {
    					
    					CircleFit circleFit = (CircleFit)fit;
    					java.awt.Shape javaShape = circleFit.m_shape;
    					
    					Interpretation interpretation = circleFit.getShape().getInterpretation();
    					System.out.println("### Name:" + interpretation.label + "###");
    					System.out.println("Confidence:" + interpretation.confidence);
    					
    					Point2D center = circleFit.getCenter();
    					double radius = circleFit.getRadius();
    					
    					String circleString = new String();
    					circleString += "Circle|";
    					circleString += center.getX() + " ";
    					circleString += center.getY() + "|";
    					circleString += radius + "|";
    					
    					System.out.println(circleString);
    					drawingGUI.setJavaShape(javaShape);
    
    					
    				}
    				
    				else if(fit.getName().equals("Ellipse")) {
    					
    					EllipseFit ellipseFit = (EllipseFit)fit;
    					java.awt.Shape javaShape = ellipseFit.m_shape;
    					
    					Interpretation interpretation = ellipseFit.getShape().getInterpretation();
    					System.out.println("### Name:" + interpretation.label + "###");
    					System.out.println("Confidence:" + interpretation.confidence);
    					
    					Point2D center = ellipseFit.getCenter();
    					double majorAxisLength = ellipseFit.m_majorAxisLength;
    					double minorAxisLength = ellipseFit.m_minorAxisLength;
    					double majorAxisAngle = ellipseFit.m_majorAxisAngle;
    					//CvPoint center, CvSize axes, double angle, double startAngle, double endAngle,
    					
    					String ellipseString = new String();
    					ellipseString += "Circle|";
    					ellipseString += center.getX() + " ";
    					ellipseString += center.getY() + "|";
    					ellipseString += majorAxisLength + "|";
    					ellipseString += minorAxisLength + "|";
    					ellipseString += majorAxisAngle + "|";
    
    					System.out.println(ellipseString);
    					drawingGUI.setJavaShape(javaShape);
    				}
    				
    				else if(fit.getName().equals("Polygon")) {
    					
    					PolygonFit polygonFit = (PolygonFit)fit;
    					
    					Interpretation interpretation = polygonFit.getShape().getInterpretation();
    					System.out.println("### Name:" + interpretation.label + "###");
    					System.out.println("Confidence:" + interpretation.confidence);
    					
    					java.awt.Shape javaShape = polygonFit.m_shape;
    					
    					String polygonString = new String();
    					polygonString += "Polygon|";
    					
    					float[] coords = new float[2];
    					for(PathIterator iterator = javaShape.getPathIterator(null); !iterator.isDone(); iterator.next()) {
    						
    						int type = iterator.currentSegment(coords);
    						float coord_x = coords[0];
    						float coord_y = coords[1];
    						
    						polygonString += coord_x + " ";
    						polygonString += coord_y + "|";
    					}
    					
    					System.out.println(polygonString);
    					drawingGUI.setJavaShape(javaShape);
    
    				}
    				
    				else {
    					System.out.println(fit.getName());
    				}
    			}
    		}
    	//}
      		
    }
    
}
