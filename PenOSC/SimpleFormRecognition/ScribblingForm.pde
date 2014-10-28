/*
 * ScribblingForm Class Definition
 */
 
class ScribblingForm extends Draggable {
  
  //float x;
  //float y;
  float radius;
  color formColor;
  
  ScribblingForm(float x, float y, float radius, color formColor) {
    super(x, y, radius, radius);
    
    //this.x         = x;
    //this.y         = y;
    this.radius    = radius;
    this.formColor = formColor;
  }
  
  public void display() {
    stroke(formColor);
    
//    if (dragging) fill(formColor, 150);
//    else if (rollover) fill(formColor, 100);
//    else fill(formColor, 50);
    
    ellipse(x, y, radius, radius);
  }
}
