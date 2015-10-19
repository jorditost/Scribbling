package edu.tamu.recognition.paleo;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.File;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPort;
import com.illposed.osc.OSCPortOut;

import edu.tamu.core.sketch.Interpretation;
import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Stroke;
import edu.tamu.core.util.RegisterSimplTypes;

public class OscMessageListenerPaleo implements OSCListener {

	private static List<Point> pointList;
	private static DrawingGUI drawingGUI;
	private static PaleoConfig config;

	// #############################

	public void instantiatePaleo() {

		config = PaleoConfig.allOff();
		config = setCustomConfig(config);

	}

	public void acceptMessage(java.util.Date time, OSCMessage message) {

		// get message
		System.out.println("Paleo");
		System.out.println("Message received!");
		// System.out.println(new Float(message.getArguments()).toString());
		Object[] args = message.getArguments();

		// if (args == null) return; //try this

		pointList = new ArrayList<Point>();

		int i = 1;
		int xCoord = 0;
		int yCoord = 0;
		Point point;

		for (Object a : args) {
			System.out.println(a);
			System.out.println(a.getClass());

			if (a.getClass().toString().equals("class java.lang.Integer")) {
				// something
				int intvalue = ((Integer) a).intValue();

				if (i == 1) {

					xCoord = intvalue;
				} else if (i == 2) {

					yCoord = intvalue;
					point = new Point((int) (xCoord), (int) (yCoord));
					pointList.add(point);
					System.out.println("Point " + xCoord + " " + yCoord);
					xCoord = 0;
					yCoord = 0;
					i = 0;

				}
				i++;
			}
		}

		// process

		// send back

		OSCPort sender = null;
		try {
			sender = new OSCPortOut();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// pointList.clear();
		// pointList.add(new Point(0, 0));
		// pointList.add(new Point(30, 5));
		// pointList.add(new Point(65, 0));
		// pointList.add(new Point(85, 0));
		// pointList.add(new Point(100, 0));
		//
		// pointList.add(new Point(106, 10));
		// pointList.add(new Point(103, 20));
		// pointList.add(new Point(108, 30));
		// pointList.add(new Point(102, 40));
		// pointList.add(new Point(106, 50));
		// pointList.add(new Point(104, 60));
		// pointList.add(new Point(102, 70));
		// pointList.add(new Point(106, 90));
		// pointList.add(new Point(102, 110));
		// pointList.add(new Point(101, 120));
		// pointList.add(new Point(103, 140));
		// pointList.add(new Point(104, 160));
		// pointList.add(new Point(105, 180));
		// pointList.add(new Point(106, 190));
		// pointList.add(new Point(107, 200));
		//
		// pointList.add(new Point(100, 200));
		// pointList.add(new Point(90, 200));
		// pointList.add(new Point(80, 200));
		// pointList.add(new Point(70, 200));
		// pointList.add(new Point(50, 200));
		// pointList.add(new Point(40, 200));
		// pointList.add(new Point(30, 200));
		// pointList.add(new Point(12, 200));
		// pointList.add(new Point(10, 200));
		// pointList.add(new Point(-1, 200));
		//
		// pointList.add(new Point(0, 160));
		// pointList.add(new Point(5, 120));
		// pointList.add(new Point(3, 100));
		// pointList.add(new Point(2, 40));
		// pointList.add(new Point(1, 20));
		// pointList.add(new Point(7, 10));
		// pointList.add(new Point(10, 0));

		if (!pointList.isEmpty()) {

			Stroke stroke = new Stroke(pointList);
			convertStrokeIntoShape(sender, stroke, config);
		} else {
			System.out.println("Nothing was send");
			// nothing was send
			Object argsBack[] = new Object[1];
			argsBack[0] = "NoShape";

			OSCMessage msg = new OSCMessage("/clean", argsBack);

			try {
				((OSCPortOut) sender).send(msg);
			} catch (Exception e) {
				System.out.println("Couldn't send");
			}
		}

	}

	public static PaleoConfig setCustomConfig(PaleoConfig config) {

		config.setLineTestOn(true);
		config.setArcTestOn(false); // incomplete circle
		config.setEllipseTestOn(false);
		config.setCircleTestOn(false);
		config.setCurveTestOn(false); // up to 5-degree bezier curve
		config.setHelixTestOn(false);

		config.setSpiralTestOn(false);
		config.setArrowTestOn(false);
		config.setComplexTestOn(false); // complex shapes possible!
		config.setPolylineTestOn(true);
		config.setPolygonTestOn(false);
		config.setRectangleTestOn(false);
		config.setSquareTestOn(false);

		config.setDiamondTestOn(false);
		config.setDotTestOn(false);
		config.setWaveTestOn(false); // sinusoidal curve
		config.setGullTestOn(false);
		config.setBlobTestOn(false);
		config.setInfinityTestOn(false);
		config.setNBCTestOn(false);

		return config;
	}

	public static void convertStrokeIntoShape(OSCPort sender, Stroke stroke,
			PaleoConfig config) {

		// Start Recognition
		OrigPaleoSketchRecognizer origPaleo = new OrigPaleoSketchRecognizer(
				stroke, config);
		origPaleo.recognize();
		FitList fitList = origPaleo.getFits();
		if (!(fitList == null)) {
			if (fitList.size() > 0) {

				System.out.println("Number of Fits: " + fitList.size());

				String senderString = new String();

				// for(int i=0; i<fitList.size(); i++) {

				Fit fit = fitList.get(0);

				if (fit.getName().equals("Arc")) {

					ArcFit arcFit = (ArcFit) fit;
					Interpretation interpretation = arcFit.getShape()
							.getInterpretation();
					System.out.println("### Name:" + interpretation.label
							+ "###");
					System.out.println("Confidence:"
							+ interpretation.confidence);

					java.awt.Shape javaShape = arcFit.m_shape;

					senderString += "Arc|";

				}

				else if (fit.getName().equals("Curve")) {

					CurveFit curveFit = (CurveFit) fit;
					// can we change the minimum degree field?

					Interpretation interpretation = curveFit.getShape()
							.getInterpretation();
					System.out.println("### Name:" + interpretation.label
							+ "###");
					System.out.println("Confidence:"
							+ interpretation.confidence);

					java.awt.Shape javaShape = curveFit.m_shape;
					Point2D[] controlPoints = curveFit.m_P;

					senderString += "Curve|";

					for (int a = 0; a < controlPoints.length; a++) {

						Point2D point = controlPoints[a];

						senderString += point.getX() + " ";
						senderString += point.getY() + "|";
					}

					System.out.println(senderString);
					// drawingGUI.setJavaShape(javaShape);

				} else if (fit.getName().equals("Line")) {

					LineFit lineFit = (LineFit) fit;

					Interpretation interpretation = lineFit.getShape()
							.getInterpretation();
					System.out.println("### Name:" + interpretation.label
							+ "###");
					System.out.println("Confidence:"
							+ interpretation.confidence);

					java.awt.Shape javaShape = lineFit.m_shape;

					List<Point> pointList = new ArrayList<Point>();
					float[] coords = new float[2];
					for (PathIterator iterator = javaShape
							.getPathIterator(null); !iterator.isDone(); iterator
							.next()) {

						int type = iterator.currentSegment(coords);
						float coord_x = coords[0];
						float coord_y = coords[1];

						Point point = new Point(coord_x, coord_y);
						pointList.add(point);
					}

					Object args[] = new Object[pointList.size() * 2 + 1];
					System.out.println(("Poly size message " + (pointList
							.size() * 2 + 1)));
					args[0] = "Line";
					int a = 1;
					for (int i = 0; i < pointList.size(); i++) {

						Point point = pointList.get(i);
						int coord_x = (int) point.getX();
						int coord_y = (int) point.getY();

						args[a] = coord_x;
						args[a + 1] = coord_y;
						a = a + 2;
					}

					OSCMessage msg = new OSCMessage("/clean", args);

					try {
						((OSCPortOut) sender).send(msg);
					} catch (Exception e) {
						System.out.println("Couldn't send");
					}

					// System.out.println(senderString);
					// drawingGUI.setJavaShape(javaShape);

				}

				else if (fit.getName().equals("Polyline")) {

					PolylineFit polylineFit = (PolylineFit) fit;

					Interpretation interpretation = polylineFit.getShape()
							.getInterpretation();
					System.out.println("### Name:" + interpretation.label
							+ "###");
					System.out.println("Confidence:"
							+ interpretation.confidence);

					java.awt.Shape javaShape = polylineFit.m_shape;

					senderString += "Polyline|";

					List<Point> pointList = new ArrayList<Point>();
					float[] coords = new float[2];
					for (PathIterator iterator = javaShape
							.getPathIterator(null); !iterator.isDone(); iterator
							.next()) {

						int type = iterator.currentSegment(coords);
						float coord_x = coords[0];
						float coord_y = coords[1];

						Point point = new Point(coord_x, coord_y);
						pointList.add(point);
					}

					int pointListSize = pointList.size();
					int sizeOfOSCMesage = pointListSize * 2 + 1;
					Object args[] = new Object[sizeOfOSCMesage];

					System.out
							.println(("Poly size message " + sizeOfOSCMesage));
					args[0] = "Polyline";
					int a = 1;
					for (int i = 0; i < pointList.size(); i++) {

						Point point = pointList.get(i);
						int coord_x = (int) point.getX();
						int coord_y = (int) point.getY();

						args[a] = coord_x;
						args[a + 1] = coord_y;
						a = a + 2;
					}

					OSCMessage msg = new OSCMessage("/clean", args);

					try {
						((OSCPortOut) sender).send(msg);
					} catch (Exception e) {
						System.out.println("Couldn't send");
					}

					System.out.println(senderString);
					// drawingGUI.setJavaShape(javaShape);
				}

				else if (fit.getName().equals("Rectangle")) {

					RectangleFit rectangleFit = (RectangleFit) fit;

					Interpretation interpretation = rectangleFit.getShape()
							.getInterpretation();
					System.out.println("### Name:" + interpretation.label
							+ "###");
					System.out.println("Confidence:"
							+ interpretation.confidence);

					java.awt.Shape javaShape = rectangleFit.m_shape;

					senderString += "Rectangle|";

					List<Point> pointList = new ArrayList<Point>();
					float[] coords = new float[2];
					int numberStrokes = 1;
					for (PathIterator iterator = javaShape
							.getPathIterator(null); !iterator.isDone(); iterator
							.next()) {

						if (numberStrokes < 6) {

							int type = iterator.currentSegment(coords);
							float coord_x = coords[0];
							float coord_y = coords[1];

							Point point = new Point(coord_x, coord_y);
							pointList.add(point);
						}
						numberStrokes++;
					}

					int pointListSize = pointList.size();
					int sizeOfOSCMesage = pointListSize * 2 + 1;
					Object args[] = new Object[sizeOfOSCMesage];

					args[0] = "Rectangle";
					int a = 1;
					for (int i = 0; i < pointList.size(); i++) {

						Point point = pointList.get(i);
						int coord_x = (int) point.getX();
						int coord_y = (int) point.getY();

						args[a] = coord_x;
						args[a + 1] = coord_y;
						a = a + 2;
					}

					OSCMessage msg = new OSCMessage("/clean", args);

					try {
						((OSCPortOut) sender).send(msg);
					} catch (Exception e) {
						System.out.println("Couldn't send");
					}

					System.out.println(senderString);
					// drawingGUI.setJavaShape(javaShape);

				}

				else if (fit.getName().equals("Square")) {

					SquareFit squareFit = (SquareFit) fit;

					Interpretation interpretation = squareFit.getShape()
							.getInterpretation();
					System.out.println("### Name:" + interpretation.label
							+ "###");
					System.out.println("Confidence:"
							+ interpretation.confidence);

					java.awt.Shape javaShape = squareFit.m_shape;

					senderString += "Square|";

					List<Point> pointList = new ArrayList<Point>();
					float[] coords = new float[2];
					int numberStrokes = 1;
					for (PathIterator iterator = javaShape
							.getPathIterator(null); !iterator.isDone(); iterator
							.next()) {

						if (numberStrokes < 6) {

							int type = iterator.currentSegment(coords);
							float coord_x = coords[0];
							float coord_y = coords[1];

							Point point = new Point(coord_x, coord_y);
							pointList.add(point);
						}
						numberStrokes++;
					}

					int pointListSize = pointList.size();
					int sizeOfOSCMesage = pointListSize * 2 + 1;
					Object args[] = new Object[sizeOfOSCMesage];

					args[0] = "Square";
					int a = 1;
					for (int i = 0; i < pointList.size(); i++) {

						Point point = pointList.get(i);
						int coord_x = (int) point.getX();
						int coord_y = (int) point.getY();

						args[a] = coord_x;
						args[a + 1] = coord_y;
						a = a + 2;
					}

					OSCMessage msg = new OSCMessage("/clean", args);

					try {
						((OSCPortOut) sender).send(msg);
					} catch (Exception e) {
						System.out.println("Couldn't send");
					}

					System.out.println(senderString);
					// drawingGUI.setJavaShape(javaShape);

				}

				else if (fit.getName().equals("Circle")) {

					CircleFit circleFit = (CircleFit) fit;
					java.awt.Shape javaShape = circleFit.m_shape;

					Interpretation interpretation = circleFit.getShape()
							.getInterpretation();
					System.out.println("### Name:" + interpretation.label
							+ "###");
					System.out.println("Confidence:"
							+ interpretation.confidence);

					Point2D center = circleFit.getCenter();
					double radius = circleFit.getRadius();

					Object args[] = new Object[4];

					args[0] = "Circle";
					args[1] = (int)center.getX();
					args[2] = (int)center.getY();
					args[3] = (int)radius;

					OSCMessage msg = new OSCMessage("/clean", args);

					try {
						((OSCPortOut) sender).send(msg);
					} catch (Exception e) {
						System.out.println("Couldn't send");
					}

					// System.out.println(senderString);
					// drawingGUI.setJavaShape(javaShape);

				}

				else if (fit.getName().equals("Ellipse")) {

					EllipseFit ellipseFit = (EllipseFit) fit;
					java.awt.Shape javaShape = ellipseFit.m_shape;

					Interpretation interpretation = ellipseFit.getShape()
							.getInterpretation();
					System.out.println("### Name:" + interpretation.label
							+ "###");
					System.out.println("Confidence:"
							+ interpretation.confidence);

					Point2D center = ellipseFit.getCenter();
					double majorAxisLength = ellipseFit.m_majorAxisLength;
					double minorAxisLength = ellipseFit.m_minorAxisLength;
					double majorAxisAngle = ellipseFit.m_majorAxisAngle;
					// CvPoint center, CvSize axes, double angle, double
					// startAngle, double endAngle,

					Object args[] = new Object[6];

					args[0] = "Ellipse";
					args[1] = center.getX();
					args[2] = center.getY();
					args[3] = majorAxisLength;
					args[4] = minorAxisLength;
					args[5] = majorAxisAngle;

					OSCMessage msg = new OSCMessage("/clean", args);

					try {
						((OSCPortOut) sender).send(msg);
					} catch (Exception e) {
						System.out.println("Couldn't send");
					}

					System.out.println("Ellipse");
					// drawingGUI.setJavaShape(javaShape);
				}

				else if (fit.getName().equals("Polygon")) {

					PolygonFit polygonFit = (PolygonFit) fit;

					Interpretation interpretation = polygonFit.getShape()
							.getInterpretation();
					System.out.println("### Name:" + interpretation.label
							+ "###");
					System.out.println("Confidence:"
							+ interpretation.confidence);

					java.awt.Shape javaShape = polygonFit.m_shape;

					senderString += "Polygon|";

					List<Point> pointList = new ArrayList<Point>();
					float[] coords = new float[2];
					for (PathIterator iterator = javaShape
							.getPathIterator(null); !iterator.isDone(); iterator
							.next()) {

						int type = iterator.currentSegment(coords);
						float coord_x = coords[0];
						float coord_y = coords[1];

						Point point = new Point(coord_x, coord_y);
						pointList.add(point);
					}

					int pointListSize = pointList.size();
					int sizeOfOSCMesage = pointListSize * 2 + 1;
					Object args[] = new Object[sizeOfOSCMesage];

					args[0] = "Polygon";
					int a = 1;
					for (int i = 0; i < pointList.size(); i++) {

						Point point = pointList.get(i);
						int coord_x = (int) point.getX();
						int coord_y = (int) point.getY();

						args[a] = coord_x;
						args[a + 1] = coord_y;
						a = a + 2;
					}

					OSCMessage msg = new OSCMessage("/clean", args);

					try {
						((OSCPortOut) sender).send(msg);
					} catch (Exception e) {
						System.out.println("Couldn't send");
					}

					System.out.println(senderString);
					// drawingGUI.setJavaShape(javaShape);

				}

				else {
					System.out.println(fit.getName());
				}

			}
		} else {

			// No Shape recognized
			Object args[] = new Object[1];
			args[0] = "NoShape";

			OSCMessage msg = new OSCMessage("/clean", args);

			try {
				((OSCPortOut) sender).send(msg);
			} catch (Exception e) {
				System.out.println("Couldn't send");
			}
		}

	}

	public static void drawWithGUI(PaleoConfig config) {

		RegisterSimplTypes.register();
		drawingGUI = new DrawingGUI();
		drawingGUI.setVisible(true);

		drawingGUI.clear();

		while (true) {

			if (drawingGUI.isStrokeFinished()) {

				System.out.println("Stroke Finished");
				drawingGUI.setStrokeFinished(false);

				Stroke stroke = drawingGUI.getCurrentStroke();

				// Start Recognition
				OrigPaleoSketchRecognizer origPaleo = new OrigPaleoSketchRecognizer(
						stroke, config);
				origPaleo.recognize();
				FitList fitList = origPaleo.getFits();

				System.out.println("Number of Fits: " + fitList.size());

				// for(int i=0; i<fitList.size(); i++) {

				Fit fit = fitList.get(0);

				if (fit.getName().equals("Arc")) {

					ArcFit arcFit = (ArcFit) fit;
					Interpretation interpretation = arcFit.getShape()
							.getInterpretation();
					System.out.println("### Name:" + interpretation.label
							+ "###");
					System.out.println("Confidence:"
							+ interpretation.confidence);

					java.awt.Shape javaShape = arcFit.m_shape;

					String arcString = new String();
					arcString += "Arc|";

				}

				else if (fit.getName().equals("Curve")) {

					CurveFit curveFit = (CurveFit) fit;
					// can we change the minimum degree field?

					Interpretation interpretation = curveFit.getShape()
							.getInterpretation();
					System.out.println("### Name:" + interpretation.label
							+ "###");
					System.out.println("Confidence:"
							+ interpretation.confidence);

					java.awt.Shape javaShape = curveFit.m_shape;
					Point2D[] controlPoints = curveFit.m_P;

					String curveString = new String();
					curveString += "Curve|";

					for (int a = 0; a < controlPoints.length; a++) {

						Point2D point = controlPoints[a];

						curveString += point.getX() + " ";
						curveString += point.getY() + "|";
					}

					System.out.println(curveString);
					drawingGUI.setJavaShape(javaShape);

				} else if (fit.getName().equals("Line")) {

					LineFit lineFit = (LineFit) fit;

					Interpretation interpretation = lineFit.getShape()
							.getInterpretation();
					System.out.println("### Name:" + interpretation.label
							+ "###");
					System.out.println("Confidence:"
							+ interpretation.confidence);

					java.awt.Shape javaShape = lineFit.m_shape;

					String lineString = new String();
					lineString += "Line|";

					float[] coords = new float[2];
					for (PathIterator iterator = javaShape
							.getPathIterator(null); !iterator.isDone(); iterator
							.next()) {

						int type = iterator.currentSegment(coords);
						float coord_x = coords[0];
						float coord_y = coords[1];

						lineString += coord_x + " ";
						lineString += coord_y + "|";
					}

					System.out.println(lineString);
					drawingGUI.setJavaShape(javaShape);

				}

				else if (fit.getName().equals("Polyline")) {

					PolylineFit polylineFit = (PolylineFit) fit;

					Interpretation interpretation = polylineFit.getShape()
							.getInterpretation();
					System.out.println("### Name:" + interpretation.label
							+ "###");
					System.out.println("Confidence:"
							+ interpretation.confidence);

					java.awt.Shape javaShape = polylineFit.m_shape;

					String polylineString = new String();
					polylineString += "Polyline|";

					float[] coords = new float[2];
					for (PathIterator iterator = javaShape
							.getPathIterator(null); !iterator.isDone(); iterator
							.next()) {

						int type = iterator.currentSegment(coords);
						float coord_x = coords[0];
						float coord_y = coords[1];

						polylineString += coord_x + " ";
						polylineString += coord_y + "|";
					}

					System.out.println(polylineString);
					drawingGUI.setJavaShape(javaShape);
				}

				else if (fit.getName().equals("Rectangle")) {

					RectangleFit rectangleFit = (RectangleFit) fit;

					Interpretation interpretation = rectangleFit.getShape()
							.getInterpretation();
					System.out.println("### Name:" + interpretation.label
							+ "###");
					System.out.println("Confidence:"
							+ interpretation.confidence);

					java.awt.Shape javaShape = rectangleFit.m_shape;

					String rectangleString = new String();
					rectangleString += "Rectangle|";

					float[] coords = new float[2];
					int numberStrokes = 1;
					for (PathIterator iterator = javaShape
							.getPathIterator(null); !iterator.isDone(); iterator
							.next()) {

						if (numberStrokes < 6) {

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

				else if (fit.getName().equals("Square")) {

					SquareFit squareFit = (SquareFit) fit;

					Interpretation interpretation = squareFit.getShape()
							.getInterpretation();
					System.out.println("### Name:" + interpretation.label
							+ "###");
					System.out.println("Confidence:"
							+ interpretation.confidence);

					java.awt.Shape javaShape = squareFit.m_shape;

					String squareString = new String();
					squareString += "Square|";

					float[] coords = new float[2];
					int numberStrokes = 1;
					for (PathIterator iterator = javaShape
							.getPathIterator(null); !iterator.isDone(); iterator
							.next()) {

						if (numberStrokes < 6) {

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

				else if (fit.getName().equals("Circle")) {

					CircleFit circleFit = (CircleFit) fit;
					java.awt.Shape javaShape = circleFit.m_shape;

					Interpretation interpretation = circleFit.getShape()
							.getInterpretation();
					System.out.println("### Name:" + interpretation.label
							+ "###");
					System.out.println("Confidence:"
							+ interpretation.confidence);

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

				else if (fit.getName().equals("Ellipse")) {

					EllipseFit ellipseFit = (EllipseFit) fit;
					java.awt.Shape javaShape = ellipseFit.m_shape;

					Interpretation interpretation = ellipseFit.getShape()
							.getInterpretation();
					System.out.println("### Name:" + interpretation.label
							+ "###");
					System.out.println("Confidence:"
							+ interpretation.confidence);

					Point2D center = ellipseFit.getCenter();
					double majorAxisLength = ellipseFit.m_majorAxisLength;
					double minorAxisLength = ellipseFit.m_minorAxisLength;
					double majorAxisAngle = ellipseFit.m_majorAxisAngle;
					// CvPoint center, CvSize axes, double angle, double
					// startAngle, double endAngle,

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

				else if (fit.getName().equals("Polygon")) {

					PolygonFit polygonFit = (PolygonFit) fit;

					Interpretation interpretation = polygonFit.getShape()
							.getInterpretation();
					System.out.println("### Name:" + interpretation.label
							+ "###");
					System.out.println("Confidence:"
							+ interpretation.confidence);

					java.awt.Shape javaShape = polygonFit.m_shape;

					String polygonString = new String();
					polygonString += "Polygon|";

					float[] coords = new float[2];
					for (PathIterator iterator = javaShape
							.getPathIterator(null); !iterator.isDone(); iterator
							.next()) {

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
		// }

	}

}
