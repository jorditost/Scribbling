/*
 * ScribblingForm Class Definition
 */
 
class ScribblingForm extends Draggable {
  
  //float x;
  //float y;
  float size;
  color formColor;
  String formName;
  
  public ScribblingForm() {}
  
  public ScribblingForm(String formName, float x, float y, float size, color formColor) {
    super(x, y, size, size);
    
    this.formName = formName;
    //this.x  = x;
    //this.y  = y;
    this.size = size;
    this.size = size;
    
    this.formColor = formColor; 
  }
  
  public void display() {
    stroke(formColor);
    
    if (dragging) fill(formColor, 150);
    else if (rollover) fill(formColor, 100);
    else fill(formColor, 50);
    
    if (formName == "circle") {
      ellipse(x, y, size, size);
    } else if (formName == "rectangle") {
      rect(x, y, size, size);
    } else if (formName == "triangle") {
      triangle(x, y-size, x+size, y+(0.8*size), x-size, y+(0.8*size));
    }
  }
}
