import java.awt.*;
import java.awt.event.*;
import java.awt.KeyEventDispatcher;

public class Ship extends Polygon implements KeyListener {
	boolean leftKey, rightKey;
	
	//CONSTRUCTOR
	public Ship(Point[] shipPoints, Point shipLocation, double rotation) {
		super(shipPoints, shipLocation, rotation);
	}
	
	//KEY_LISTENER METHODS
	public void keyPressed(KeyEvent e) {
		
		int keyCode = e.getKeyCode();
		switch (keyCode){
		case KeyEvent.VK_LEFT:
			leftKey = true;
			break;
		case KeyEvent.VK_RIGHT:
			rightKey = true;
			break;
		}
	}
	
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode){
		case KeyEvent.VK_LEFT:
			leftKey = false;
			alreadyMoved = false;
			break;
		case KeyEvent.VK_RIGHT:
			rightKey = false;
			alreadyMoved = false;
			break;
		}
	}
	
	public void keyTyped(KeyEvent e) {}//not used
	
	//PAINT METHOD
	public void paint(Graphics brush, boolean beingHit, int timer, double lives, boolean myShip){
        if(beingHit && myShip || lives <=Game.width * .25 && timer % 30 <= 15 && myShip){
            Blocks.shipColor = Color.red;//flicker effect
        } else {Blocks.shipColor = Color.blue;}
        brush.setColor(Blocks.shipColor);
        brush.fillPolygon(Ship.getXPoints(getPoints()), Ship.getYPoints(getPoints()), getPoints().length);
	}
	
	//MOVE METHOD 
	
	//Variables 
	//an array of locations on the screen:
	int block1 = 71, block2 = 231, block3 = 391, block4 = 551, block5 = 711;
	int[] location = {block1, block2, block3, block4, block5};
	int l = 2;
	boolean alreadyMoved = false;//prevents the ship from moving more than once per button press
	
	//Method
	public void move(){
		position.x = location[l];

		if(leftKey && !alreadyMoved){
			l--;
			if(l < 0){
				l = location.length - 1;
			}
			alreadyMoved = true;
		}
		if(rightKey && !alreadyMoved){
			l++;
			if(l > location.length - 1){
				l = 0;
			}
			alreadyMoved = true;
			
		}
	}
}