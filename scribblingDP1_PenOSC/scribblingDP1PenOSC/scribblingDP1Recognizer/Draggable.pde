// Click and Drag an object
// Daniel Shiffman 

// A class for a draggable thing

class Draggable {
  boolean dragging = false; // Is the object being dragged?
  boolean rollover = false; // Is the mouse over the ellipse?
  
  float x,y,w,h;          // Location and size
  float offsetX, offsetY; // Mouseclick offset
  
  public Draggable() {}
  
  public Draggable(float tempX, float tempY, float tempW, float tempH) { 
    x = tempX;
    y = tempY;
    w = tempW;
    h = tempH;
    offsetX = 0;
    offsetY = 0;
  }

  // Is a point inside the rectangle (for click)?
  void clicked(int mx, int my) {
    if (isOver(mx, my)) {
      dragging = true;
      // If so, keep track of relative location of click to corner of rectangle
      offsetX = x-mx;
      offsetY = y-my;
    }
  }
  
  // Is a point inside the draggable object (for rollover)
  void rollover(int mx, int my) {
    if (isOver(mx, my)) {
      rollover = true;
    } else {
      rollover = false;
    }
  }

  // Stop dragging
  void stopDragging() {
    dragging = false;
  }
  
  // Drag object
  void drag(int mx, int my) {
    if (dragging) {
      println("drag it!");
      x = mx + offsetX;
      y = my + offsetY;
    }
  }
  
  boolean isOver(int mx, int my) { 
    return (mx > (x-w/2) && mx < (x+w/2) && my > (y-h/2) && my < (y+h/2));
  }
}
