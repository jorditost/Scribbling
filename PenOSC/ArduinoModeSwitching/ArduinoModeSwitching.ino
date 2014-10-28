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

int modeButtonState = HIGH;
int modeButtonReading;
int modeButtonPrevious = LOW;

int redButtonState = 0;
int greenButtonState = 0;
int blueButtonState = 0;

String drawColor = "";

// the following variables are long's because the time, measured in miliseconds,
// will quickly become a bigger number than can be stored in an int.
long time = 0;         // the last time the output pin was toggled
long debounce = 200;   // the debounce time, increase if the output flickers

void setup() {
  
  // Buttons
  pinMode(modeButtonPin, INPUT);
  pinMode(redButtonPin, INPUT);
  pinMode(greenButtonPin, INPUT);
  pinMode(blueButtonPin, INPUT);
  
  // LEDs
  pinMode(modeLED, OUTPUT);
  pinMode(redLED, OUTPUT);
  pinMode(greenLED, OUTPUT);
  pinMode(blueLED, OUTPUT);
}

void loop() {
  
  // Control MODE SWITCHING
  checkMode();
  
  // Control color
  checkColor();
}

void checkMode() {
  
  modeButtonReading = digitalRead(modeButtonPin);
    
  // if the input just went from LOW and HIGH and we've waited long enough
  // to ignore any noise on the circuit, toggle the output pin and remember
  // the time
  if (modeButtonReading == HIGH && modeButtonPrevious == LOW && millis() - time > debounce) {
    if (modeButtonState == HIGH)
      modeButtonState = LOW;  // switch DRAWING MODE off
    else
      modeButtonState = HIGH; // switch DRAWING MODE on
    
    Serial.println("MODE: " + modeButtonState);
    time = millis();    
  }

  digitalWrite(modeLED, modeButtonState);

  modeButtonPrevious = modeButtonReading;
}

void checkColor() {
  
  /*if (modeButtonState == LOW) {
    digitalWrite(redLED, LOW);
    digitalWrite(greenLED, LOW);
    digitalWrite(blueLED, LOW);
    return;
  }*/
  
  redButtonState = digitalRead(redButtonPin);
  greenButtonState = digitalRead(greenButtonPin);
  blueButtonState = digitalRead(blueButtonPin);
    
  if (redButtonState == HIGH) {   
    Serial.println("RED");   
    digitalWrite(redLED, HIGH);
    digitalWrite(greenLED, LOW);
    digitalWrite(blueLED, LOW);
  } else if (greenButtonState == HIGH) {
    Serial.println("GREEN");
    digitalWrite(redLED, LOW);
    digitalWrite(greenLED, HIGH);
    digitalWrite(blueLED, LOW); 
  } else if (blueButtonState == HIGH) {
    Serial.println("BLUE");
    digitalWrite(redLED, LOW);
    digitalWrite(greenLED, LOW);
    digitalWrite(blueLED, HIGH); 
  }
}
