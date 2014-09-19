/*
 * Scribbling
 * Development Prototype #1
 */

import processing.serial.*;
import cc.arduino.*;

// Test vars
static boolean   TEST                   = false;
static boolean   DRAW_DETECT_POINTS     = false;

// Global P5 Modes
static int       JAVA_MODE              = 1;
static int       ANDROID_MODE           = 2;
static int       MODE                   = JAVA_MODE;

// Mode Switching
static int       DRAWING_MODE           = 1;
static int       GESTURE_MODE           = 2;

// Drawing vars
static float     DRAWING_STROKE_WEIGHT  = (MODE == ANDROID_MODE) ? 4 : 1;
static float     START_END_DIST         = 100;
static float     DRAWING_MIN_SIZE       = 20;
static float     ELLIPSE_DETECT_RATIO   = 70;

int inputMode;
boolean restartDrawingMode;
boolean isUserTouching;
PVector vMinX, vMaxX, vMinY, vMaxY;
PVector vTouchStart, vTouchEnd;
color drawColor = #000000;

ArrayList<ScribblingForm> forms;

// Arduino
Arduino arduino;

// Buttons
int modeButtonPin = 2;
int redButtonPin = 3;
int greenButtonPin = 4;
int blueButtonPin = 5;

// LEDs
int modeLED       = 9;
int redLED        = 10;
int greenLED      = 11;
int blueLED       = 12;

int modeButtonState = Arduino.LOW;
int modeButtonReading;
int modeButtonPrevious = Arduino.HIGH;

int redButtonState = 0;
int greenButtonState = 0;
int blueButtonState = 0;

long time = 0;         // the last time the output pin was toggled
long debounce = 200;   // the debounce time, increase if the output flickers


void setup() {
  
  if (MODE == ANDROID_MODE) {
    println("Android Mode");
    // Force orientation to landscape mode
    orientation(LANDSCAPE); // horizontal
  } else {
    println("Java Mode");
    size(800, 800);
  }
  
  println("Initialized in DRAWING mode");
  inputMode = DRAWING_MODE;
      
  smooth();
  
  background(255);
  stroke(0);
  
  rectMode(CENTER);
  ellipseMode(CENTER);
  
  forms = new ArrayList<ScribblingForm>();
  
  isUserTouching = false;
  
  // Configure Arduino port
  println(Arduino.list());
  arduino = new Arduino(this, Arduino.list()[4], 57600);
  
  arduino.pinMode(modeButtonPin, Arduino.INPUT);
  arduino.pinMode(redButtonPin, Arduino.INPUT);
  arduino.pinMode(greenButtonPin, Arduino.INPUT);
  arduino.pinMode(blueButtonPin, Arduino.INPUT);
  
  // LEDs
  arduino.pinMode(modeLED, Arduino.OUTPUT);
  arduino.pinMode(redLED, Arduino.OUTPUT);
  arduino.pinMode(greenLED, Arduino.OUTPUT);
  arduino.pinMode(blueLED, Arduino.OUTPUT);
  
  //arduino.digitalWrite(modeLED, Arduino.HIGH);
}

void draw() {
  
  // Check Arduino
  arduinoLoop();
  
  if (isUserTouching) {
    
    if (inputMode == DRAWING_MODE) {
      
      // Analize the object being drawn to find patterns
      checkDrawingCoords();
      
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
    // Initialize vectors
    vMinX = new PVector(mouseX, mouseY);
    vMaxX = new PVector(mouseX, mouseY);
    vMinY = new PVector(mouseX, mouseY);
    vMaxY = new PVector(mouseX, mouseY);
    
    // Point where touch starts
    vTouchStart = new PVector(mouseX, mouseY);
  
  } else if (inputMode == GESTURE_MODE) {
    for (int i=0; i<forms.size(); i++) {
      ScribblingForm form = forms.get(i);
      form.clicked(mouseX,mouseY);
    }
  }
}  

void mouseReleased() {
  isUserTouching = false;
  
  if (inputMode == DRAWING_MODE) {
    // Point where touch ends
    vTouchEnd = new PVector(mouseX, mouseY);
    
    // Function that check
    checkDrawingAfterCompletion();
    
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

/////////////////////
// Check Functions
/////////////////////

// Function that analizes the object being drawn to find patterns
void checkDrawingCoords() {
  
  // Check circle position
  if (mouseX < vMinX.x) {
    vMinX.x = mouseX;
    vMinX.y = mouseY;
  }
  if (mouseX > vMaxX.x) {
    vMaxX.x = mouseX;
    vMaxX.y = mouseY;
  }
  if (mouseY < vMinY.y) {
    vMinY.x = mouseX;
    vMinY.y = mouseY;
  }
  if (mouseY > vMaxY.y) {
    vMaxY.x = mouseX;
    vMaxY.y = mouseY;
  }
}

// Function that checks drawing after completion/mouseReleased.
// If it's a known form it will add the form to our array
void checkDrawingAfterCompletion() {
  
  if (!isScrawl()) {
    
    PVector vCenterX = PVector.lerp(vMinX, vMaxX, 0.5);
    PVector vCenterY = PVector.lerp(vMinY, vMaxY, 0.5);
    PVector vCenter  = PVector.lerp(vCenterX, vCenterY, 0.5);
    float   radius   = 0.5*(vMinX.dist(vMaxX) + vMinY.dist(vMaxY));
    
    // Test helpers
    if (DRAW_DETECT_POINTS) {
      drawCircleDetectionPoints(vCenterX, vCenterY, vCenter, radius);
    }
    
    // Create ScribblingForm
    forms.add(new ScribblingForm(vCenter.x, vCenter.y, radius, drawColor));
  }
}

// Function that detects if the drawing is a scrawl
boolean isScrawl() {
  
  float startEndDist = vTouchStart.dist(vTouchEnd);
  float distX = vMinX.dist(vMaxX);
  float distY = vMinY.dist(vMaxY);
  
  boolean isOpen = startEndDist > START_END_DIST;
  boolean isPoint = (distX < DRAWING_MIN_SIZE && distY < DRAWING_MIN_SIZE);
  boolean isEllipse = Math.abs(distX - distY) > ELLIPSE_DETECT_RATIO;
  
  // TO DO: Detect distance between min/max points and centers
  return (isOpen || isPoint || isEllipse);
}

////////////////////
// Draw Functions
////////////////////

void drawCircleDetectionPoints(PVector vCenterX, PVector vCenterY, PVector vCenter, float radius) {
  
  noStroke();
  
  // X-Values: Blue
  fill(0,0,255);
  ellipse(vMinX.x, vMinX.y, 15, 15);
  ellipse(vMaxX.x, vMaxX.y, 15, 15);
  
  // Y-Values: Green
  fill(0,255,0);
  ellipse(vMinY.x, vMinY.y, 15, 15);
  ellipse(vMaxY.x, vMaxY.y, 15, 15);
  
  noFill();
  strokeWeight(DRAWING_STROKE_WEIGHT);
  
  // Center X
  stroke(0,0,255);
  ellipse(vCenterX.x, vCenterX.y, 10, 10);
  
  // Center Y
  stroke(0,255,0);
  ellipse(vCenterY.x, vCenterY.y, 10, 10);
  
  // Beautify circle
  
  // Get center
  noStroke();
  fill(255,0,0);
  ellipse(vCenter.x, vCenter.y, 10, 10);
}

///////////////////////
// Button Functions
///////////////////////

void arduinoLoop() {
  // Control MODE SWITCHING
  checkMode();
  
  // Control color
  checkColor();
}

void checkMode() {
  
  modeButtonReading = arduino.digitalRead(modeButtonPin);
  
  restartDrawingMode = false;
    
  // if the input just went from LOW and HIGH and we've waited long enough
  // to ignore any noise on the circuit, toggle the output pin and remember
  // the time
  if (modeButtonReading == Arduino.HIGH && modeButtonPrevious == Arduino.LOW && millis() - time > debounce) {
    if (modeButtonState == Arduino.HIGH) {
      inputMode = GESTURE_MODE;
      modeButtonState = Arduino.LOW;  // switch DRAWING MODE off
    } else {
      inputMode = DRAWING_MODE;
      modeButtonState = Arduino.HIGH; // switch DRAWING MODE on
      restartDrawingMode = true;
    }
    time = millis();    
  }

  arduino.digitalWrite(modeLED, modeButtonState);

  modeButtonPrevious = modeButtonReading;
}

void checkColor() {
  
  if (modeButtonState == Arduino.LOW) {
    arduino.digitalWrite(redLED, Arduino.LOW);
    arduino.digitalWrite(greenLED, Arduino.LOW);
    arduino.digitalWrite(blueLED, Arduino.LOW);
    return;
  }
  
  redButtonState = arduino.digitalRead(redButtonPin);
  greenButtonState = arduino.digitalRead(greenButtonPin);
  blueButtonState = arduino.digitalRead(blueButtonPin);
    
  if (redButtonState == Arduino.HIGH || (restartDrawingMode && drawColor == #FF0000)) {     
    arduino.digitalWrite(redLED, Arduino.HIGH);
    arduino.digitalWrite(greenLED, Arduino.LOW);
    arduino.digitalWrite(blueLED, Arduino.LOW);
    println("Change color - RED");
    drawColor = #FF0000;
  } else if (greenButtonState == Arduino.HIGH || (restartDrawingMode && drawColor == #00FF00)) {
    arduino.digitalWrite(redLED, Arduino.LOW);
    arduino.digitalWrite(greenLED, Arduino.HIGH);
    arduino.digitalWrite(blueLED, Arduino.LOW);
    println("Change color - GREEN");
    drawColor = #00FF00;
  } else if (blueButtonState == Arduino.HIGH || (restartDrawingMode && drawColor == #0000FF)) {
    arduino.digitalWrite(redLED, Arduino.LOW);
    arduino.digitalWrite(greenLED, Arduino.LOW);
    arduino.digitalWrite(blueLED, Arduino.HIGH);
    println("Change color - BLUE");
    drawColor = #0000FF;
  }
}

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
