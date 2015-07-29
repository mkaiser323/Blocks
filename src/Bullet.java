import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Bullet extends Ship implements KeyListener{
	public boolean isFired;

	public Bullet(Point[] shape, Point location, double rotation){
		super(shape, location, rotation);//make the points and bullet object in asteroid, use rotation of ship, location is shipTip
		isFired = false;
	}
	
	public void shoot(){
		position.x += 30 * Math.cos(Math.toRadians(rotation));
		position.y += 30 * Math.sin(Math.toRadians(rotation));

	}
	
	public boolean isOutOfBounds(){
		return position.x >= Game.width || position.x <= 0 || position.y >= Game.height || position.y <= 0;
	}
	public void keyPressed(KeyEvent e){
		if(e.getKeyCode() == KeyEvent.VK_SPACE){
			isFired = true;
		}
	}
	
	public void keyReleased(KeyEvent e){
		if(e.getKeyCode() == KeyEvent.VK_SPACE){
		}
	}
	public void keyTyped(KeyEvent e){}
}