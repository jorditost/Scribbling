/*
 * Scribbling
 * Development Prototype #1
 */

static int       JAVA_MODE              = 1;
static int       ANDROID_MODE           = 2;

static boolean   TEST                   = true;
static int       MODE                   = JAVA_MODE;
static boolean   DRAW_DETECT_POINTS     = true;

static float     DRAWING_STROKE_WEIGHT  = (MODE == ANDROID_MODE) ? 4 : 1;
static float     START_END_DIST         = 100;
static float     DRAWING_MIN_SIZE       = 20;
static float     ELLIPSE_DETECT_RATIO   = 70;

boolean isUserTouching;
PVector vMinX, vMaxX, vMinY, vMaxY;
PVector vTouchStart, vTouchEnd;

void setup() {
  
  if (MODE == ANDROID_MODE) {
    println("Android Mode");
    // Force orientation to landscape mode
    orientation(LANDSCAPE); // horizontal
  } else {
    println("Java Mode");
    size(800, 800);
  }
  
  background(255);
  stroke(0);
  
  rectMode(CENTER);
  ellipseMode(CENTER);
  
  isUserTouching = false;
}

void draw() {
  if (isUserTouching) {
     
    noFill();
    strokeWeight(DRAWING_STROKE_WEIGHT);
    stroke(0);
    
    // Analize the object being drawn to find patterns
    checkDrawingCoords();
    
    // Draw
    line(pmouseX, pmouseY, mouseX, mouseY);
  }  
}

/////////////////////
// Event Functions
/////////////////////

void mousePressed() {
  
  isUserTouching = true;
  
  // Initialize vectors
  vMinX = new PVector(mouseX, mouseY);
  vMaxX = new PVector(mouseX, mouseY);
  vMinY = new PVector(mouseX, mouseY);
  vMaxY = new PVector(mouseX, mouseY);
  
  // Point where touch starts
  vTouchStart = new PVector(mouseX, mouseY);
}  

void mouseReleased() {
  isUserTouching = false;
  
  // Point where touch ends
  vTouchEnd = new PVector(mouseX, mouseY);
  
  checkDrawing();
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
  
  //if (mouseX < vMinX.x) vMinX.set(mouseX, mouseY);
  //if (mouseX > vMaxX.x) vMaxX.set(mouseX, mouseY);
  //if (mouseY < vMinY.y) vMinY.set(mouseX, mouseY);
  //if (mouseY > vMaxY.y) vMaxY.set(mouseX, mouseY);
}

// Function that checks drawing after completion/mouseReleased
void checkDrawing() {
  if (!isScrawl()) {
    drawCircle();
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

void drawCircle() {
  PVector vCenterX = PVector.lerp(vMinX, vMaxX, 0.5);
  PVector vCenterY = PVector.lerp(vMinY, vMaxY, 0.5);
  PVector vCenter  = PVector.lerp(vCenterX, vCenterY, 0.5);
  float   radius   = 0.5*(vMinX.dist(vMaxX) + vMinY.dist(vMaxY));
  
  // Test helpers
  if (TEST) {
    
    if (DRAW_DETECT_POINTS) {
      drawCircleDetectionPoints(vCenterX, vCenterY, vCenter, radius);
    }
    
    // In test mode draw beautified circle in red
    stroke(255,0,0);
    noFill();
  }
  
  // Draw beautified circle
  ellipse(vCenter.x, vCenter.y, radius, radius);
}

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

