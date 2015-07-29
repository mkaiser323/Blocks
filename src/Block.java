import java.awt.*;
import java.util.ArrayList;


public class Block extends Polygon {

	public Block(Point[] points, Point location, double rotation) {
		super(points, location, rotation);
	}
	
	//paint method
	Color blockColor = Color.darkGray;
	Color numberColor = Color.white;
	public void paint(Graphics brush, int choice, int targetIndex, int i){
		        
        if(i % 2 == 0){
        	blockColor = Color.white;
        	numberColor = Color.black;
        } else {
        	blockColor = Color.white;
        	numberColor = Color.black;
        }

        brush.setColor(blockColor);
        brush.fillPolygon(Block.getXPoints(getPoints()), Block.getYPoints(getPoints()), getPoints().length);
        
        brush.setColor(numberColor);
        brush.setFont(new Font("Arial", Font.BOLD, 20));
        brush.drawString(choice + "", (int)position.x + 30, (int)position.y + 13);
        brush.setColor(Color.gray);
        infiniteScreen();
	}
	


	//move method
	public void move(int i){
		position.y += 1.7;
	}
}
