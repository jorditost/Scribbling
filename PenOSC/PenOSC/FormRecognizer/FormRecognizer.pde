/*
 * scribblingDP1Recognizer
 * This sketch controls the Sketch Recognition.
 * It listens OSC messages from the scribblingDP1PenController 
 * that manage mode switching and stroke color.
 */

import de.voidplus.dollar.*;

import oscP5.*;
import netP5.*;

// Test vars
static boolean   TEST                   = false;
static boolean   DRAW_DETECT_POINTS     = false;

// Global P5 Modes
static int       JAVA_MODE              = 1;
static int       ANDROID_MODE           = 2;
static int       MODE                   = ANDROID_MODE;

// Mode Switching
static int       DRAWING_MODE           = 1;
static int       GESTURE_MODE           = 2;

// Drawing vars
static float     DRAWING_STROKE_WEIGHT  = (MODE == ANDROID_MODE) ? 4 : 1;
static float     START_END_DIST         = 100;
static float     DRAWING_MIN_SIZE       = 20;
static float     ELLIPSE_DETECT_RATIO   = 70;

int inputMode; 
boolean isUserTouching;
color drawColor = #000000;

// OSC Protocol
OscP5 oscP5;
int listenerPort = 9000;

// Gesture Recognition
OneDollar one;
String gestureName;
PVector position, centroid;

ArrayList<ScribblingForm> forms;

void setup() {
  
  if (MODE == ANDROID_MODE) {
    println("Android Mode");
    // Force orientation to landscape mode
    orientation(LANDSCAPE); // horizontal
  } else {
    println("Java Mode");
    //size(800, 800);
  }
  
  println("Initialized in DRAWING mode");
  inputMode = DRAWING_MODE;
      
  smooth();
  
  background(255);
  stroke(0);
  
  rectMode(CENTER);
  ellipseMode(CENTER);
  
  // Start oscP5, listening for incoming messages at port 9000
  oscP5 = new OscP5(this, listenerPort);
  
  // Forms
  forms = new ArrayList<ScribblingForm>();
  
  gestureName = "-";
  position = new PVector(-10,-10);
  centroid = new PVector(-10,-10);
  
  one = new OneDollar(this);
  one.setVerbose(true);          // activate verbose mode
  println(one);                  // print the settings
  
  isUserTouching = false;
  
  // Add drawing forms
  // http://depts.washington.edu/aimgroup/proj/dollar/unistrokes.gif
  one.add("triangle", new Integer[] {137,139,135,141,133,144,132,146,130,149,128,151,126,155,123,160,120,166,116,171,112,177,107,183,102,188,100,191,95,195,90,199,86,203,82,206,80,209,75,213,73,213,70,216,67,219,64,221,61,223,60,225,62,226,65,225,67,226,74,226,77,227,85,229,91,230,99,231,108,232,116,233,125,233,134,234,145,233,153,232,160,233,170,234,177,235,179,236,186,237,193,238,198,239,200,237,202,239,204,238,206,234,205,230,202,222,197,216,192,207,186,198,179,189,174,183,170,178,164,171,161,168,154,160,148,155,143,150,138,148,136,148} );
  one.add("rectangle", new Integer[] {78,149,78,153,78,157,78,160,79,162,79,164,79,167,79,169,79,173,79,178,79,183,80,189,80,193,80,198,80,202,81,208,81,210,81,216,82,222,82,224,82,227,83,229,83,231,85,230,88,232,90,233,92,232,94,233,99,232,102,233,106,233,109,234,117,235,123,236,126,236,135,237,142,238,145,238,152,238,154,239,165,238,174,237,179,236,186,235,191,235,195,233,197,233,200,233,201,235,201,233,199,231,198,226,198,220,196,207,195,195,195,181,195,173,195,163,194,155,192,145,192,143,192,138,191,135,191,133,191,130,190,128,188,129,186,129,181,132,173,131,162,131,151,132,149,132,138,132,136,132,122,131,120,131,109,130,107,130,90,132,81,133,76,133} );
  one.add("circle", new Integer[] {127,141,124,140,120,139,118,139,116,139,111,140,109,141,104,144,100,147,96,152,93,157,90,163,87,169,85,175,83,181,82,190,82,195,83,200,84,205,88,213,91,216,96,219,103,222,108,224,111,224,120,224,133,223,142,222,152,218,160,214,167,210,173,204,178,198,179,196,182,188,182,177,178,167,170,150,163,138,152,130,143,129,140,131,129,136,126,139} );
  
  // Bind callbacks
  one.bind("circle triangle rectangle","detected");
  
  // Add gestures
  one.add("delete", new Integer[] {123,129,123,131,124,133,125,136,127,140,129,142,133,148,137,154,143,158,145,161,148,164,153,170,158,176,160,178,164,183,168,188,171,191,175,196,178,200,180,202,181,205,184,208,186,210,187,213,188,215,186,212,183,211,177,208,169,206,162,205,154,207,145,209,137,210,129,214,122,217,118,218,111,221,109,222,110,219,112,217,118,209,120,207,128,196,135,187,138,183,148,167,157,153,163,145,165,142,172,133,177,127,179,127,180,125} );
  one.bind("delete", "clearForms");
}

// If it's a known form it will add the form to our array
void detected(String gesture, int x, int y, int c_x, int c_y){
  
  if (inputMode == GESTURE_MODE) return;
  
  gestureName = gesture;
  position.x = x;
  position.y = y;
  centroid.x = c_x;
  centroid.y = c_y;
  
  // Test helpers
  if (DRAW_DETECT_POINTS) {
    // Draw position / centroid
    //drawCircleDetectionPoints(PVector vCenterX, PVector vCenterY, PVector vCenter, float radius)
  }
  
  // Circle
  if (gestureName == "circle") {
    float radius = position.dist(centroid);
    forms.add(new ScribblingForm("circle", centroid.x, centroid.y, 2*radius, drawColor));
  
  // Rectangle
  } else if (gestureName == "rectangle") {
    float radius = position.dist(centroid);
    forms.add(new ScribblingForm("rectangle", centroid.x, centroid.y, 1.3*radius, drawColor));
    
  // Triangle
  } else if (gestureName == "triangle") {
    float radius = position.dist(centroid);
    forms.add(new ScribblingForm("triangle", centroid.x, centroid.y, 0.9*radius, drawColor));
  }
}

void clearForms(String gesture, int x, int y, int c_x, int c_y) {
  
  if (inputMode == GESTURE_MODE) return;
  
  println("DELETE FORMS");
  forms.clear();
}

void draw() {
  if (isUserTouching) {
    
    if (inputMode == DRAWING_MODE) {
      
      noFill();
      strokeWeight(DRAWING_STROKE_WEIGHT);
      stroke(drawColor);
      
      // Draw
      line(pmouseX, pmouseY, mouseX, mouseY);
    }
  }
  
  if (inputMode == GESTURE_MODE) {
      
    // Clean background
    background(255);
    
    displayForms();
  }
}

void displayForms() {
  
  for (int i=0; i<forms.size(); i++) {
    ScribblingForm form = forms.get(i);
    if (inputMode == GESTURE_MODE) {
      form.rollover(mouseX,mouseY);
      form.drag(mouseX,mouseY);
    }
    form.display();
  }
}

///////////////////////////
// Mouse/Touch Functions
///////////////////////////

void mousePressed() {
  
  isUserTouching = true;

  if (inputMode == DRAWING_MODE) {
    one.start(100);
  
  } else if (inputMode == GESTURE_MODE) {
    for (int i=0; i<forms.size(); i++) {
      ScribblingForm form = forms.get(i);
      form.clicked(mouseX,mouseY);
    }
  }
}

void mouseDragged() {
  one.update(100, mouseX, mouseY);
}

void mouseReleased() {
  isUserTouching = false;
  
  if (inputMode == DRAWING_MODE) {
    
    one.end(100);
    
    // Clean background
    background(255);
    
    // Display all forms
    displayForms();
    
  } else if (inputMode == GESTURE_MODE) {
    for (int i=0; i<forms.size(); i++) {
      ScribblingForm form = forms.get(i);
      form.stopDragging();
    }
  }
}

///////////////////////
// Control Listeners
///////////////////////

// Incoming OSC message from the scribblingDP1PenController sketch
void oscEvent(OscMessage theOscMessage) {
  
  // print the address pattern and the typetag of the received OscMessage
  print("### received an osc message.");
  print(", addrpattern: " + theOscMessage.addrPattern());
  print(", typetag: " + theOscMessage.typetag());
  println(", value: " + theOscMessage.get(0).intValue());
  
  // Check mode
  if (theOscMessage.checkAddrPattern("/mode") == true) {
    if (theOscMessage.checkTypetag("i")) {
      int value = theOscMessage.get(0).intValue();
      switch (value) {
        case 1:  inputMode = DRAWING_MODE; break;
        case 2:  inputMode = GESTURE_MODE; break;
        default: inputMode = DRAWING_MODE; break;
      }
      println("MODE CHANGED! - value: " + inputMode);
      return;
    }
  }
  
  // Check color
  if (theOscMessage.checkAddrPattern("/color") == true) {
    if (theOscMessage.checkTypetag("i")) {
      int value = theOscMessage.get(0).intValue();
      switch (value) {
        case 1:  drawColor = #FF0000; break;
        case 2:  drawColor = #00FF00; break;
        case 3:  drawColor = #0000FF; break;
        default: drawColor = #000000; break;
      }
      println("color changed! - value: " + value);
      return;
    }
  }
}

// Keyboard (Java Mode)
void keyPressed() {

  if (key == ' ') {
    
    if (inputMode == DRAWING_MODE) {
      inputMode = GESTURE_MODE;
      println("Mode changed - GESTURE");
    } else {
      inputMode = DRAWING_MODE;
      println("Mode changed - DRAWING");
    }
  } 
  
  if (key == 'r') {
    println("Color changed - RED");
    drawColor = #FF0000;
  } else if (key == 'g') {
    println("Color changed - GREEN");
    drawColor = #00FF00;
  } else if (key == 'b') {
    println("Color changed - BLUE");
    drawColor = #0000FF;
  } else if (key == 'k') {
    println("Color changed - BLACK");
    drawColor = #000000;
  }
}
