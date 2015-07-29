class Point implements Cloneable {
  double x,y;
  public Point(double inX, double inY) { 
	  x = inX; 
	  y = inY; 
	  
  }
  
  public Point clone() {
	  return new Point(x, y);
  }
  
  public Point adjustPoint(int x, int y){
	  return new Point(this.x + x, this.y + y);  
  }
}