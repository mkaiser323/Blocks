class Polygon {
  public Point[] shape;   // An array of points.
  public Point position;   // The offset mentioned above.
  public double rotation; // Zero degrees is due east.
  
  public Polygon(Point[] inShape, Point inPosition, double inRotation) {
    shape = inShape;
    position = inPosition;
    rotation = inRotation;
    
    // First, we find the shape's top-most left-most boundary, its origin.
    Point origin = shape[0].clone();
    for (Point p : shape) {
      if (p.x < origin.x) origin.x = p.x;
      if (p.y < origin.y) origin.y = p.y;
    }
    
    // Then, we orient all of its points relative to the real origin.
    for (Point p : shape) {
      p.x -= origin.x;
      p.y -= origin.y;
    }
  }
  
  // "getPoints" applies the rotation and offset to the shape of the polygon.
  public Point[] getPoints() {
    Point center = findCenter();
    Point[] points = new Point[shape.length];
    for (int i = 0; i < shape.length; i++) {
//    for (Point p : shape) {
      Point p = shape[i];
      double x = ((p.x-center.x) * Math.cos(Math.toRadians(rotation)))
               - ((p.y-center.y) * Math.sin(Math.toRadians(rotation)))
               + center.x/2 + position.x;
      double y = ((p.x-center.x) * Math.sin(Math.toRadians(rotation)))
               + ((p.y-center.y) * Math.cos(Math.toRadians(rotation)))
               + center.y/2 + position.y;
      points[i] = new Point(x,y);
    }
    return points;
  }
  
  // "contains" implements some magical math (i.e. the ray-casting algorithm).
  public boolean contains(Point point) {
    Point[] points = getPoints();
    double crossingNumber = 0;
    for (int i = 0, j = 1; i < shape.length; i++, j=(j+1)%shape.length) {
      if ((((points[i].x < point.x) && (point.x <= points[j].x)) ||
           ((points[j].x < point.x) && (point.x <= points[i].x))) &&
          (point.y > points[i].y + (points[j].y-points[i].y)/
           (points[j].x - points[i].x) * (point.x - points[i].x))) {
        crossingNumber++;
      }
    }
    return crossingNumber%2 == 1;
  }
  
  public void rotate(int degrees) {
	  rotation = (rotation+degrees)%360;
  }
  
  /*
  The following methods are private access restricted because, as this access
  level always implies, they are intended for use only as helpers of the
  methods in this class that are not private. They can't be used anywhere else.
  */
  
  // "findArea" implements some more magic math.
  private double findArea() {
    double sum = 0;
    for (int i = 0, j = 1; i < shape.length; i++, j=(j+1)%shape.length) {
      sum += shape[i].x*shape[j].y-shape[j].x*shape[i].y;
    }
    return Math.abs(sum/2);
  }
  
  // "findCenter" implements another bit of math.
  private Point findCenter() {
    Point sum = new Point(0,0);
    for (int i = 0, j = 1; i < shape.length; i++, j=(j+1)%shape.length) {
      sum.x += (shape[i].x + shape[j].x)
               * (shape[i].x * shape[j].y - shape[j].x * shape[i].y);
      sum.y += (shape[i].y + shape[j].y)
               * (shape[i].x * shape[j].y - shape[j].x * shape[i].y);
    }
    double area = findArea();
    return new Point(Math.abs(sum.x/(6*area)),Math.abs(sum.y/(6*area)));
  }
  
  //takes an array of Points and returns the x values of Points
  public static int[] getXPoints(Point[] shape){
	  int[] xs = new int[shape.length];
	  for(int i = 0; i < xs.length; i++){
		  xs[i] = (int)shape[i].x;
	  }
	  return xs;
  }
  
  //takes an array of Points and returns the y values of Points
  public static int[] getYPoints(Point[] shape){
	  int[] ys = new int[shape.length];
	  for(int i = 0; i < ys.length; i++){
		  ys[i] = (int)shape[i].y;
	  }
	  return ys;
  }
  
  //takes an array of Points and returns the x values of Points
  public int[] getX(Point[] shape){
	  int[] xs = new int[shape.length];
	  for(int i = 0; i < xs.length; i++){
		  xs[i] = (int)shape[i].x;
	  }
	  return xs;
  }
  
  //takes an array of Points and returns the y values of Points
  public int[] getY(Point[] shape){
	  int[] ys = new int[shape.length];
	  for(int i = 0; i < ys.length; i++){
		  ys[i] = (int)shape[i].y;
	  }
	  return ys;
  }
  
  public void infiniteScreen(){
      if(position.y < 0){
      	position.y = Game.height;
      }
      if(position.y > Game.height){
      	position.y = 0;
      }
      if(position.x < 0){
      	position.x = Game.width;
      }
      if(position.x > Game.width){
      	position.x = 0;
      }
  }
  
  public String toString(){
	  String toString = "";
	  for(int i = 0; i < shape.length; i++){
		  toString += "    (" + shape[i].x + ", " + shape[i].y + "), \n";
	  }
	  return toString;
  }
  
  public void setLocation(Point position){
	  this.position = position;
  }
}