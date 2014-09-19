/*
 * scribblingDP1PenController
 * This sketch controls the Pen attached to the Arduino Board and
 * sends the values via OSC to the scribblingDP1Recognizer sketch.
 * 
 * It's a test using only the first lateral button
 */

/*
 * Scribbling
 * Development Prototype #1
 */

import processing.serial.*;
import cc.arduino.*;

import oscP5.*;
import netP5.*;

// Mode Switching
static int       DRAWING_MODE           = 1;
static int       GESTURE_MODE           = 2;

int inputMode;
boolean restartDrawingMode;
color drawColor = #000000;

// OSC
OscP5 oscP5;
NetAddress receiverLocation;
String receiverIP = "194.94.235.221"; //"192.168.1.34";
int receiverPort  = 9000;

// Arduino
Arduino arduino;

// Buttons
int modeButtonPin = 3; // First lateral button

// LEDs
int modeLED       = 9;

int modeButtonState = Arduino.LOW;
int modeButtonReading;
int modeButtonPrevious = Arduino.HIGH;

int button1State = 0;
int button2State = 0;
int button3State = 0;

long time = 0;         // the last time the output pin was toggled
long debounce = 200;   // the debounce time, increase if the output flickers

void setup() {
  
  size(150, 150);
  
  // Start oscP5, listening for incoming messages at port 8000
  oscP5 = new OscP5(this, 8000);
  
  // Processing receiver (Android device)
  receiverLocation = new NetAddress(receiverIP, receiverPort);
  
  // Configure Arduino port
  println(Arduino.list());
  arduino = new Arduino(this, Arduino.list()[4], 57600);
  
  arduino.pinMode(modeButtonPin, Arduino.INPUT);
  
  // LEDs
  arduino.pinMode(modeLED, Arduino.OUTPUT);
  
  // Initialize in DRAWING MODE
  inputMode = DRAWING_MODE;
  
  modeButtonState = Arduino.HIGH;
  arduino.digitalWrite(modeLED, modeButtonState);
}

void draw() {
  
  background(255);
  
  if (inputMode == DRAWING_MODE) {
    stroke(drawColor);
    strokeWeight(20);
    ellipse(0.5*width, 0.5*height, width-30, height-30);
  }
  
  // Control MODE SWITCHING
  checkMode();
}  

void checkMode() {
  
  modeButtonReading = arduino.digitalRead(modeButtonPin);
  
  restartDrawingMode = false;
    
  // if the input just went from LOW and HIGH and we've waited long enough to ignore
  // any noise on the circuit, toggle the output pin and remember the time
  
  if (modeButtonReading == Arduino.HIGH && modeButtonPrevious == Arduino.LOW && millis() - time > debounce) {
    // Swith to GESTURE MODE
    if (modeButtonState == Arduino.HIGH) {
      inputMode = GESTURE_MODE;
      modeButtonState = Arduino.LOW;
      println("Switch Mode: GESTURE");
    // Swith to DRAWING MODE
    } else {
      inputMode = DRAWING_MODE;
      modeButtonState = Arduino.HIGH;
      restartDrawingMode = true;
      println("Switch Mode: DRAWING");
    }
    sendMessage("/mode", inputMode);
    time = millis();    
  }

  arduino.digitalWrite(modeLED, modeButtonState);

  modeButtonPrevious = modeButtonReading;
}

void sendMessage(String addrPattern, int value) {

  OscMessage myMessage = new OscMessage(addrPattern);
  
  myMessage.add(value);
  println("Message '" + addrPattern + "' sent with value: " + value);

  // Send the message
  oscP5.send(myMessage, receiverLocation);
  delay(250);
  oscP5.send(myMessage, receiverLocation); 
}

// Test
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
