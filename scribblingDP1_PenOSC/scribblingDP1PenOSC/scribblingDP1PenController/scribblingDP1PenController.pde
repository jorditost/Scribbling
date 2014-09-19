/*
 * scribblingDP1PenController
 * This sketch controls the Pen attached to the Arduino Board and
 * sends the values via OSC to the scribblingDP1Recognizer sketch.
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
  
  size(150, 150);
  
  // Start oscP5, listening for incoming messages at port 8000
  oscP5 = new OscP5(this, 8000);
  
  // Processing receiver (Android device)
  receiverLocation = new NetAddress(receiverIP, receiverPort);
  
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
  
  // Control color
  checkColor();
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

void checkColor() {
  
  if (inputMode == GESTURE_MODE) {
    arduino.digitalWrite(redLED, Arduino.LOW);
    arduino.digitalWrite(greenLED, Arduino.LOW);
    arduino.digitalWrite(blueLED, Arduino.LOW);
    return;
  }
  
  redButtonState = arduino.digitalRead(redButtonPin);
  greenButtonState = arduino.digitalRead(greenButtonPin);
  blueButtonState = arduino.digitalRead(blueButtonPin);
    
  if ((redButtonState == Arduino.HIGH && drawColor != #FF0000) || (restartDrawingMode && drawColor == #FF0000)) {     
    arduino.digitalWrite(redLED, Arduino.HIGH);
    arduino.digitalWrite(greenLED, Arduino.LOW);
    arduino.digitalWrite(blueLED, Arduino.LOW);
    println("Change color - RED");
    drawColor = #FF0000;
    sendMessage("/color", 1);
    
  } else if ((greenButtonState == Arduino.HIGH && drawColor != #00FF00) || (restartDrawingMode && drawColor == #00FF00)) {
    arduino.digitalWrite(redLED, Arduino.LOW);
    arduino.digitalWrite(greenLED, Arduino.HIGH);
    arduino.digitalWrite(blueLED, Arduino.LOW);
    println("Change color - GREEN");
    drawColor = #00FF00;
    sendMessage("/color", 2);
    
  } else if ((blueButtonState == Arduino.HIGH && drawColor != #0000FF) || (restartDrawingMode && drawColor == #0000FF)) {
    arduino.digitalWrite(redLED, Arduino.LOW);
    arduino.digitalWrite(greenLED, Arduino.LOW);
    arduino.digitalWrite(blueLED, Arduino.HIGH);
    println("Change color - BLUE");
    drawColor = #0000FF;
    sendMessage("/color", 3);
  }
}

void sendMessage(String addrPattern, int value) {

  OscMessage myMessage = new OscMessage(addrPattern);
  
  myMessage.add(value);
  println("Message '" + addrPattern + "' sent with value: " + value);

  // Send the message
  // TO DO: Confirmation protocoll
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
    sendMessage("/mode", inputMode);
  } 
  
  if (key == 'r') {
    println("Color changed - RED");
    drawColor = #FF0000;
    sendMessage("/color", 1);
  } else if (key == 'g') {
    println("Color changed - GREEN");
    drawColor = #00FF00;
    sendMessage("/color", 2);
  } else if (key == 'b') {
    println("Color changed - BLUE");
    drawColor = #0000FF;
    sendMessage("/color", 3);
  } else if (key == 'k') {
    println("Color changed - BLACK");
    drawColor = #000000;
    sendMessage("/color", 0);
  }
}
