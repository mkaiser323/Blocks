import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.ImageIcon;

class Blocks extends Game implements KeyListener{

	static int timer, motion, targetIndex, factor1, factor2;
	
	int startedWaitingAt, highScore;
	public static double score, lives;
	private String scoreMessage, highScoreMessage;
	//screen states:
	private boolean welcomeScreen, playGame, gameIsOver, paused, mainMenu, repeat, gameCurrentlyInPlay;
	//buttons:
	boolean highScores = false, n = false, m = false, l = false,
			p = false, q = false, c = false, h = false, howToPlay = false, 
			spaceBar = false, right = false, left = false, enter = false, escape = false, quitMessage = false;
	public static String fileName = HighScore.fileName;
	public static int[] choice = new int[5];
	static Color shipColor = Color.blue;
	public static final int SHIP_SIZE = 50;
	static final double MAX_POINTS = 20;//max possible points per question
	static double pointsLeft;//points left in the question
	static final int damage = 100;//damage per hit
	boolean correctBlock, incorrectBlock, showPoints, mistakesRound, wait;
	
	private void setDefaults(Graphics brush){
		targetIndex = 3;
		factor1 = 1;
		factor2 = 1;
		Question.resetValues();
		gameCurrentlyInPlay = false;
		gameIsOver = false;
		mainMenu = false;
		beingHit = false;
		paused = false;
		repeat = false;// determines whether or not the last question will have to be repeated at some point later
		correctBlock = false;
		showPoints = false;
		incorrectBlock = false;
		p = true;
		mistakesRound = false; 
		wait = false;
		timer = 1;
		lives = Game.width;
		score = 0;
		motion = 0;
		pointsLeft = MAX_POINTS;
		blocksList.clear();
		reloadblocks(brush);
		loadChoices();
		myShip.setLocation(shipLocation);
		myShip.rotation = 270;
	}

	//SHIP
	//points
	Point nose = new Point(SHIP_SIZE, SHIP_SIZE/2);
	Point rightWing = new Point(0, SHIP_SIZE);
	Point center = new Point(5*SHIP_SIZE/8, SHIP_SIZE/2);
	Point leftWing = new Point(0, 0);	
	
	//Ship object(s)
	Point[] shape = {nose, rightWing, center, leftWing};
	Point shipLocation = new Point(Game.width / 2, Game.height - 3*SHIP_SIZE/2);
	double rotation = 270.0;
	Ship myShip = new Ship(shape, shipLocation, rotation);

		
	//BLOCKS
	//shapes
	double blockWidth = 150, blockHeight = 25;
	Point nw = new Point(0,0),
			ne = new Point(blockWidth, 0),
			se = new Point(blockWidth, blockHeight),
			sw = new Point(0, blockHeight);
	Point[] blockShape ={nw, ne, se, sw};
	
	Point location1 = new Point(-50, -10),
			location2 = new Point(75, -50),
			location3 = new Point(150, -5),
			location4= new Point(200, -25),
			location5 = new Point(700, -50),
			location6 = new Point(500, 0),
			location7 = new Point(650, -50),
			location8 = new Point(600, -30),
			location9 = new Point(400, -10),
			location10 = new Point(300, -45);
	Point[] locations = {location1, location2, location3, location4, location5, location6, location7, location8};
	
	Block block1 = new Block(blockShape, location1, 0),
			block2 = new Block(blockShape, location2, 0),
			block3 = new Block(blockShape, location3, 0),
			block4 = new Block(blockShape, location4, 0),
			block5 = new Block(blockShape, location5, 0),	
			block6 = new Block(blockShape, location6, 0),	
			block7 = new Block(blockShape, location7, 0),	
			block8 = new Block(blockShape, location8, 0),
			block9 = new Block(blockShape, location9, 0),
			block10 = new Block(blockShape, location10, 0);
	Block[] blocks = {block1, block2, block3, block4, block5};
	
	//arrayList of blocks
	public static ArrayList<Block> blocksList = new ArrayList<>();
		
	//laser shape
	public static final int BULLET_WIDTH = 2;
	public static final int BULLET_LENGTH = 75;
	Point s1 = new Point(0,0),
			s2 = new Point(0,BULLET_WIDTH),
			s3 = new Point(BULLET_LENGTH,BULLET_WIDTH),
			s4 = new Point(BULLET_LENGTH,0);
	Point[] laser ={s1,s2,s3,s4};
	Point shipTip = new Point(600, 400);
	Bullet bullet = new Bullet(laser, shipTip, myShip.rotation);
	
	//miniBlocks
	//shape
	int size = 30;
	Point topLeft = new Point(0, 0),
			topRight = new Point(size, 0),
			bottomRight = new Point(size, size),
			bottomLeft = new Point(0, size);
	Point[] miniBlockShape = {topLeft, topRight, bottomRight, bottomLeft};
	
	//locations
	Point miniLocation1 = new Point(150, -30),
			miniLocation2 = new Point(300, -30),
			miniLocation3 = new Point(450, -30),
			miniLocation4 = new Point(600, -30);
	Point[] miniLocations = {miniLocation1, miniLocation2, miniLocation3, miniLocation4};
	
	Block miniBlock1 = new Block(miniBlockShape, miniLocation1, 0),
			miniBlock2 = new Block(miniBlockShape, miniLocation2, 0),
			miniBlock3 = new Block(miniBlockShape, miniLocation3, 0),
			miniBlock4 = new Block(miniBlockShape, miniLocation4, 0);
	Block[] miniBlocks = {miniBlock1, miniBlock2, miniBlock3, miniBlock4};
	
	ArrayList<Block> miniBlockList = new ArrayList<>();

	

	//CONSTRUCTOR
	public Blocks() {
		super("blocks!",800,600);
		this.setFocusable(true);
		this.requestFocus();
		this.addKeyListener(myShip);
		this.addKeyListener(this);
		addKeyListener(bullet);
		welcomeScreen = true;
		playGame = false;
		//put contents of blocks into arrayList:
		for(int i = 0; i < blocks.length; i++ ){
			blocksList.add(blocks[i]);			
		}
		for(int i = 0; i < miniBlocks.length; i++ ){
			miniBlockList.add(miniBlocks[i]);			
		}
	}

	//PAINT METHOD
	public void paint(Graphics brush) {
		this.addKeyListener(this);
		if(timer >= 800){
			timer = 0;
		} else {timer++;}
		drawBackground(brush);	
		//welcome screen
		if(welcomeScreen){
			welcomeScreen(brush);
			bullet.isFired = false;
			if(enter){
				welcomeScreen = false;//hide welcome screen
				mainMenu = true;
				playGame = true;
				paused = true;
			}
		}
		
		if(playGame){
			if(!paused){//unpaused
				newGame(brush);
				if(escape && p){
					paused = true;
					gameCurrentlyInPlay = true;
					c = false;
				}
				if(lives <= 0){//conditions required to end game
					gameIsOver = true;
				}
				mainMenu = true;
			}
			if (paused) {//paused
				if(mainMenu){
					menu(brush);
				}
				
				//********************NAVIGATION*********************//
				
				//----------Press [L] to Learn how to play----------//
				if(l && !highScores){
					mainMenu = false;//hide menu
					howToPlay = true;//display tutorial
				}
				if(howToPlay){
					howToPlay(brush);
				}
				//----------Press [N] to start a new game----------//
				if(n){
					setDefaults(brush);
					mainMenu = false;//hide menu
					paused = false;//exit from pause
					playGame = true;
				}
				
				//----------Press [H] to view high scores----------//
				if(h && !howToPlay){					
					mainMenu = false; //hide menu
					highScores = true;
				}
				if(highScores){
					topFive(brush);
				}
				if(m){//returning to the main menu
					highScores = false;//hide high scores
					howToPlay = false;//hide tutorial
					mainMenu = true;//return to main menu
				}

				if(gameCurrentlyInPlay && mainMenu){
					brush.setColor(Color.white);
					brush.setFont(new Font("Courier New", Font.BOLD, 32));
					if( timer % 80 >= 40){//blinking effect
						brush.drawString("Paused. Press [Esc] to Continue", 115, height - 100);
					}
					if(escape && c){//if c is pressed, hide menu and resume game
						mainMenu = false; 
						paused = false;
						p = false;
					}
					
					//----------------Press [Q] to Quit----------------//
					quitMessage = true;
					if(q){
						gameIsOver = true;
						q = false;
					}
				}
			} 
		}
		//game over screen
		if(gameIsOver ){
			bullet.isFired = false;
			playGame = false;
			gameCurrentlyInPlay = false;
			gameOver(brush);
			if(m){
				gameIsOver = false;
				playGame = true;
				paused = true;
			}
		}
	}

	//MAIN METHOD
	public static void main (String[] args) {
		Blocks b = new Blocks();
		b.repaint();
	}
	
	//************************************************PRIVATE METHODS****************************************************************//
	
	//checks for collisions between ship and blocks
	boolean alreadyHit = false;
	private boolean shipWasHit(){
		for(int a = 0; a < blocksList.size(); a ++){
			for(int i = 0; i < myShip.getPoints().length; i++){
				if (!alreadyHit && (blocksList.get(a).contains(myShip.getPoints()[i]) || myShip.contains(blocksList.get(a).getPoints()[i])) && i != 2 && timer >= 10){
					alreadyHit = true;
					return true;
				}
			}
		}
		return false;//return false by default
	}
	//check for collisions between bullet and blocks
	private boolean blockWasHit(Graphics brush){
		for(int b = 0; b < blocksList.size(); b++){
			if(b >= blocksList.size()){
				b = blocksList.size() - 1;
			}					
			if(b >= 0){
				for(int i = 0; i < bullet.getPoints().length; i++){
					if (	(bullet.isFired &&
								(
								(blocksList.get(b).contains(bullet.getPoints()[i]))
								|| (blocksList.get(b).contains(myShip.getPoints()[0]))
								|| (bullet.contains(blocksList.get(b).getPoints()[i]))
								)
							)
						){//if block a is hit
						Question.checkBlock(b == targetIndex);
						updateQuestion();
						//regardless of which one was hit:
						bullet.isFired = false;
						return true;
					}
				}
			}
		}
		return false;//return false by default
	}
	
	/*a method that takes the block index, and two factors, and updates the score and 
	 * question depending on whether the correct block was hit on time; adds problems to
	 * an arrayList every time it's wrong
	 */
	private void updateQuestion(){
		Question.update();
		mistakesRound = Question.mistakesRound;
		factor1 = Question.getFactor1();
		factor2 = Question.getFactor2();
	}
	 
	//A method that takes a string, x and y coordinate, and time, and 
	//displays the string at that location for that amount of time. 
	boolean showMessage = true, messageTimerWasInitialized = false;
	int messageTimer;

	
	
	static double replenishment = 0;
	private void replenish(Graphics brush){
		brush.setColor(Color.cyan);
		brush.fillRect((int)lives-5, 5, (int)replenishment, 30);
		if(replenishment + lives-10 > Game.width -10){
			replenishment = (Game.width - 10) - (lives - 10);
		}

		if(replenishment > 0){
			replenishment -= .1;
		}
	}
	

	Color bulletColor = Color.white;
	private void shoot(Graphics brush){
		brush.setColor(bulletColor);
		if(bullet.isFired){
			brush.fillPolygon(bullet.getX(bullet.getPoints()), bullet.getY(bullet.getPoints()), bullet.getPoints().length);
			bullet.shoot();
			if(bullet.isOutOfBounds() ){
				bullet.isFired = false;
			}
		} 
		if(!bullet.isFired){
			bullet.position.x = myShip.getPoints()[0].x + 25*Math.cos(Math.toRadians(myShip.rotation)) -15;
			bullet.position.y = myShip.getPoints()[0].y + 25* Math.sin(Math.toRadians(myShip.rotation));
			bullet.rotation = myShip.rotation;
		}
	}
	

	//paints all blocks in the arrayList
	private void paintblocks(ArrayList<Block> blocksList, Graphics brush){
		if(!this.blocksList.isEmpty()){ //move blocks only if there are blocks to move
			for(int i = 0; i < blocksList.size(); i++){
				brush.setColor(Color.gray);
				if(i >= blocksList.size()){
					i = blocksList.size() - 1;
				}
				if(i>=0){
					blocksList.get(i).paint(brush, choice[i], targetIndex, i);
					blocksList.get(i).move(i);
				}
			}
		}
	}
	
	//array of choices to display on the blocks
	ArrayList <Integer> possibleChoices = new ArrayList<>();
	public static void loadChoices(){
		factor1 = Question.getFactor1();
		factor2 = Question.getFactor2();
		//1 X 1:
		if(factor1 * factor2 == 1){
			choice[0] = 6;
			choice[2] = 2;
			choice[1] = 3;
			choice[3] = 4;
			choice[4] = 5;
		} else if (factor2 == 1){//Any number X 1:
			if (factor1 <= 2){//Numbers 1 and 2 multiplied by one
				choice[0] = 3;
				choice[2] = 4;
				choice[1] = 5;
				choice[3] = 6;
				choice[4] = 7;
			} else {//the rest multiplied by one
				choice[0] = factor2 * (factor1 - 2);
				choice[2] = factor2 * (factor1 - 1);
				choice[1] = factor2 * (factor1 + 1);
				choice[3] = factor2 * (factor1 + 2);
				choice[4] = factor2 * (factor1 + 3);
			}
		} else if (factor2 == 2){//Any number X 2
			choice[0] = 1 * factor1;
			choice[2] = 6 * factor1;
			choice[1] = 3 * factor1;
			choice[3] = 4 * factor1;
			choice[4] = 5 * factor1;
		} else if(factor1 != factor2){//numbers greater than 2:
			choice[0] = factor1 * (factor2 - 1);
			choice[2] = factor1 * (factor2 + 1);
			choice[1] = (factor1 - 1) * factor2;
			choice[3] = (factor1 - 2) * factor2;
			choice[4] = (factor1 - 1) * (factor2 + 1);
			
			//check to make sure that none of them are equal to the right answer yet
			for(int c = 0; c < choice.length; c++){
				if(choice[c] == factor1 * factor2){
					choice[c] += (factor1 - 1);
				}
			}
		} else {//when factor1 == factor2
			choice[0] = factor1 * (factor2 - 1);
			choice[1] = factor1 * (factor2 - 2);
			choice[2] = (factor1 + 1) * (factor2 - 1);
			choice[3] = (factor1 + 1) * (factor2 - 2);
			choice[4] = (factor1 + 2) * (factor2 - 2);
			
		}
		choice[targetIndex] = factor1 * factor2;
	}
	
	//is called from newGame() when blocksList is empty
	private void reloadblocks(Graphics brush){
		pointsLeft = MAX_POINTS;
		incorrectBlock = false;
		alreadyHit = false;
			for(int i = 0; i < blocks.length; i++ ){
				blocksList.add(blocks[i]);
			}
			for(int i = 0; i < blocksList.size(); i++ ){
				blocksList.get(i).setLocation(new Point( 45 + (i)*160, - 50));
			}
	}
	

	
	//returns the previous high score, if there is one; if there is none, returns 0; 
	private int getHighScore(){
		HighScore.checkFile();
		int highScore = 0;
		try{
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName));
			if(inputStream.readInt() != -1){//check if the file contains any valid scores
				String name = inputStream.readUTF();
				highScore = inputStream.readInt();
			}
			inputStream.close();
		} catch (Exception e){
			System.out.println(e.getMessage());
		}

		if((int)score > highScore){
			highScore = (int)score;
		}
		return highScore;
	}
	
	private void updateHighScore(){
		HighScore.checkFile();
		//check if current score is greater than the 5 highest scores stored in high score file
		if (HighScore.isHighScore((int)score)){
			HighScore.storeScore((int)score);
		}
	}
	boolean beingHit = false;
	boolean gettingHealth = false;
	int test;//delete this after done testing
	private void displayStats(Graphics brush){
		
		//deduct lives if hit by block
		if(shipWasHit() ){
			if(!beingHit){
				if(!mistakesRound){
					Question.addMistake(new Question(factor1, factor2));
				}
				lives -= damage ;//lives only decremented the first time, before
				//beingHit is changed to true
			}
			beingHit = true;
		} else {//once the ship is no longer in contact with the block
			//beingHit is set back to false so that another separate hit
			//will be able to deduct a life
				beingHit = false;
		}
		

			scoreMessage = "Score: " + (int)score;
		
		brush.setFont(new Font("Courier New", Font.BOLD, 20));
		brush.setColor(Color.black);
		brush.drawString("Health: " + (int)((lives / Game.width) * 100) + "%", 20, 25);
		brush.drawString(scoreMessage, 350, 25);
		brush.drawString("Press [Esc] to Pause", 545, 25);
	}
	int increase = 0;
	private void healthBar(Graphics brush){

		if(lives > 0){
			brush.setColor(Color.white);
			brush.fillRect(5, 5, Game.width-10, 30);
			
			
			brush.setColor(Color.blue);
			brush.fillRect(5, 5, (int)lives-10, 30);
		}
		
		if(replenishment > 0){
			replenish(brush);
			if(!mistakesRound){
				lives++;
				increase++;
				replenishment--;
				brush.setColor(Color.cyan);
				brush.setFont(new Font("Arial", Font.BOLD, 24));
				brush.drawString("+ " + increase, 395, 80);
			} else {
				increase = 0;
			}
		} else {

			if(lives > Game.width){
				lives = Game.width;
			}
		}

	}


	//SCREENS--------------------------------------------------------------------------------------------------------------//
	
	
	//draw background
	private void drawBackground(Graphics brush){
		brush.setColor(Color.black);
		brush.fillRect(0, 0, width, height);
		
		if(mistakesRound){
			brush.setColor(new Color(255, 255, 255, 10));
			brush.fillRect(0, 0, width, height);
		}
		
		//stars
		int x_distanceBetweenStars = 90, y_distanceBetweenStars = 200, starSize = 2;
		int x, y;
		motion++;
		if(motion > y_distanceBetweenStars ){
			motion = 0;
		} 
		for( x = 0; x < Game.width; x += x_distanceBetweenStars){
			brush.setColor(Color.white);
			if(x % (2 * x_distanceBetweenStars) == 0){
				y = -3*y_distanceBetweenStars / 2;
			} else {
				y = -y_distanceBetweenStars;
			}
			for(; y < Game.height; y+= y_distanceBetweenStars){
				if( x == x_distanceBetweenStars * 3 && y == y_distanceBetweenStars * 1 ){
					brush.setColor(Color.blue);
				}
				if( x == x_distanceBetweenStars * 5 && y == y_distanceBetweenStars * 2){
					brush.setColor(Color.orange);
				}
				if( x == x_distanceBetweenStars * 8 && y == 3*y_distanceBetweenStars / 2){
					brush.setColor(Color.red);
				}
				if( x == x_distanceBetweenStars * 4 && y == y_distanceBetweenStars / 2){
					brush.setColor(Color.cyan);
				}
				brush.fillOval(x + x_distanceBetweenStars / 2, y + motion, starSize, starSize);
			}
		}
	}
	//Welcome screen 
	private void welcomeScreen(Graphics brush){		
		//Title
		String welcomeMessage = "Blocks";
		brush.setColor(Color.blue);
		brush.setFont(new Font("Courier New", Font.BOLD, 144));

		FontMetrics fm = brush.getFontMetrics();
		int x_string = (width - fm.stringWidth(welcomeMessage)) / 2;
		brush.drawString(welcomeMessage, x_string, 14*height/27);
		brush.drawRect(-1, 3*height/10, width + 1,   height/3);
		
		//enter prompt
		String enterPrompt = "Press [Enter] to continue.";
		brush.setColor(Color.white);		
		brush.setFont(new Font("Courier New", Font.BOLD, 32));
		fm = brush.getFontMetrics();
		x_string = (width - fm.stringWidth(enterPrompt)) / 2;
		if( timer % 80 >= 40){//flicker effect
			brush.drawString(enterPrompt, x_string, 13*height/16);	
		}
		
	}
	
	/*
	 * Main menu
	 * 
	 * this screen will be the first to show up after the welcome screen goes away
	 * it will also show up on the pause screen, the only difference being the 
	 * blinking continue prompt on the top
	 * 
	 * the offset variable allows me to adjust the y value of the text each time I invoke the method
	 */
	private void menu(Graphics brush){
		//title: "MAIN MENU"
		brush.setColor(Color.blue);
		brush.setFont(new Font("Courier New", Font.BOLD, 72));
		int title_y = 200;
		if(gameCurrentlyInPlay){
			title_y -= 50;
		}
		brush.drawString("Main Menu", 200, title_y);
		
		int fontSize = 33;
		int topGap = 50;
		int x = 95;
		brush.setColor(Color.lightGray);
		//Menu options:
		//l for learn how to play-- M button to return from there to main menu
		brush.setFont(new Font("Courier New", Font.BOLD, fontSize));
		brush.drawString("Press [L] to learn how to play", 
				x, (int)(title_y + topGap + fontSize*1.5) );

		brush.setColor(Color.white);
		//n for new game-- M button to return to main menu
		brush.drawString("Press [N] to start a new game", 
				x, (int)(title_y + topGap + 2*fontSize*1.5) );

		brush.setColor(Color.lightGray);
		//h for high scores-- M button to return to main menu
		brush.drawString("Press [H] to view high scores", 
				x, (int)(title_y + topGap + 3*fontSize*1.5) );
		
		//q to quit
		if(gameCurrentlyInPlay){
			brush.setColor(Color.white);
			brush.drawString("Press [Q] to Quit", 
					x, (int)(title_y + topGap + 4*fontSize*1.5) );
		}
	}
	
	private void topFive(Graphics brush){
		
		//Title:
		brush.setColor(Color.blue);
		brush.setFont(new Font("Courier New", Font.BOLD, 72));
		brush.drawString("High Scores:", 145, 150);
		
		HighScore.checkFile();
		//use an inputStream to read in the values
		try{
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName));
			
			//set font
			brush.setColor(Color.white);
			double fontSize = 44;
			brush.setFont(new Font("Courier New", Font.BOLD, (int)fontSize));
			//store them in an array
			HighScore[] highScore = new HighScore[5];
			String name;
			int score, i = 0;
			boolean endOfFile = false;
			while(!endOfFile && i < 5){
				score = inputStream.readInt();
				if(score == -1){
					endOfFile = true;
					name = "";//an empty value to store as the name with the -1
				} else {
					name = inputStream.readUTF();
					highScore[i] = new HighScore(name, score);
					//draw them to the screen
					String str = highScore[i].score + " " + highScore[i].name;
					brush.drawString(str, 155, 220 + (int)(i*(fontSize*1.5)));
					i++;
				}
			}
			inputStream.close();
		} catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		brush.setColor(Color.gray);
		//press m to return to the main menu
		brush.setFont(new Font("Courier New", Font.BOLD, 24));
		brush.drawString("Press [M] to return to the Main Menu", 135, 55);

	}
	
	//how to play
	boolean clockWise = true;
	private void howToPlay(Graphics brush){
		brush.setColor(Color.gray);
		//press m to return to the main menu
		brush.setFont(new Font("Courier New", Font.BOLD, 24));
		brush.drawString("Press [M] to return to the Main Menu", 135, 55);
		
		int x = 25, y_gap = 35, first_y = 90, sectionBreak = 25, sectionIncrement = 25;
		brush.setFont(new Font("Courier New", Font.BOLD, 30));
		
		brush.setColor(Color.lightGray);
		brush.drawString("Use the [left] and [right] arrow", x, first_y + y_gap);
		brush.drawString("keys to move the ship.", x, first_y + 2 * y_gap);
		
		brush.setColor(Color.white);
		brush.drawString("Use the [spacebar] to shoot.", x, first_y + 3 * y_gap + sectionBreak);

		sectionBreak += sectionIncrement;
		brush.setColor(Color.lightGray);
		brush.drawString("Shoot at the block with the right", x, first_y + 4 * y_gap + sectionBreak);
		brush.drawString("answer to make them all disappear.", x, 5 * y_gap + first_y + sectionBreak);
		
		sectionBreak += sectionIncrement;
		brush.setColor(Color.white);
		brush.drawString("The faster you hit the right block, ", x, 6 * y_gap + first_y + sectionBreak);
		brush.drawString("the more points you get.", x, 7 * y_gap + first_y + sectionBreak);
		
		sectionBreak += sectionIncrement;
		brush.setColor(Color.lightGray);
		brush.drawString("If you don't hit it before the circle ", x, 8 * y_gap + first_y + sectionBreak);
		brush.drawString("runs out, the question will repeat itself.", x, 9 * y_gap + first_y + sectionBreak);
	}
	
	
	//New Game
	boolean alreadyCleared = false;
	private void newGame(Graphics brush){		

		brush = (Graphics2D)brush;
		//arc
		if(mistakesRound){
			brush.setColor(Color.white);
		} else {
			brush.setColor(Color.blue);
		}
		pointsLeft -= .075;
		if(pointsLeft < 0 ){pointsLeft = 0;	} //if points run out
		if(pointsLeft > 1 ){
			brush.drawArc(20, 40, Game.width-40, Game.height-80, 0, (int)(432*(pointsLeft-1)/(MAX_POINTS)));
		}

		//levels
		brush.setFont(new Font("Courier New", Font.BOLD, 32));
		
		//bonus round message
		if(mistakesRound){
			brush.setColor(Color.cyan);;
			brush.drawString("Mistakes", 330, 160);
		} else {
			brush.setColor(Color.white);;
			brush.drawString("Level " + factor1, 335, 160);
		}
		
		
		//when a bonus round starts, remove all blocks from the screen and replace them
		if(mistakesRound && !alreadyCleared){
			blocksList.clear();
			alreadyCleared = true;
		}

		//graphical representation of the number of mistakes
		int startLoc = 342, y = 185, radius = 15, gap = 10, diameter = radius*2;
		brush.setFont(new Font("Courier New", Font.BOLD, 32));
		for(int f = 0; f < Question.mistakes.size(); f++){
			brush.setColor(Color.cyan);
			//brush.fillOval(startLoc + (diameter + gap) * f, y, diameter, diameter);
			int stringX = (startLoc + (diameter + gap) * f) + radius/2 + diameter/10, stringY = y + 3*radius/2;
			//brush.setColor(Color.black);
			brush.drawString("X", stringX, stringY);
		}
		
		
		//display question
		String question = factor1 + " X " + factor2;
		if(mistakesRound){
			brush.setColor(Color.cyan);
		} else {
			brush.setColor(shipColor);
		}
		brush.setFont(new Font("Courier New", Font.BOLD, 175));
		FontMetrics fm = brush.getFontMetrics();
		int x_string = (width - fm.stringWidth(question))/2;
		brush.drawString(question, x_string, 350);
		
		//ship
		brush.setColor(shipColor);
		myShip.paint(brush, beingHit, timer, lives, true);
		myShip.move();
		shoot(brush); 
		
		//blocks
		//only paint blocks when list is not empty
		if(!blocksList.isEmpty()){
			brush.setColor(Color.gray);
			paintblocks(blocksList, brush);
		} else {
			//reload blockArrayList when empty
			reloadblocks(brush);
		}
		
		
		if (blocksList.get(0).position.y < 600 && blocksList.get(0).position.y > 590){
			alreadyHit = false;
		}

		//bullet special effects
		if(blockWasHit(brush)){
			brush.setColor(bulletColor);
			last_x = (int)bullet.getPoints()[3].x;
			last_y = (int)bullet.getPoints()[3].y;
			lastPoint = (int)pointsLeft;
			startTime = timer;
			if(bullet.isFired){
				brush.drawOval(
					last_x - 50, last_y-50, 100, 100);
			}
			if(correctBlock){
				showPoints = true;
			} else {
				showPoints = false;
			}
			
		}
		
		//display amount of points received for a particular block
		if(showPoints){
			if(timer - startTime >= 15){
				startTime = 0;
				showPoints = false;
			}
			brush.setColor(bulletColor);
			brush.setFont(new Font("Arial", Font.BOLD, 32));
			brush.drawString(" + " + lastPoint,last_x, last_y);
		}
		
		//highlight area between block and ship
		shipPosition = (int)myShip.position.x;
		
		x_block = 8;
		if(shipPosition > 168){
			x_block =  168;
		}
		if(shipPosition > 328){
			x_block = 328;
		}
		if(shipPosition > 488){
			x_block = 488;
		}
		if(shipPosition > 648){
			x_block = 648;
		}
		
		brush.setColor(new Color(255, 255, 255, 15));
		brush.fillRect(x_block, 0, (int)blockWidth, Game.height);
		
		healthBar(brush);
		displayStats(brush);
	}
	int x_block, shipPosition;
	int startTime = 0;
	int last_x, last_y;
	int lastPoint;
	
	//Game over screen
	boolean scoreUpdated = false;
	private void gameOver(Graphics brush){
		
		//game over
		String gameOver = "GAME OVER";
		brush.setColor(Color.blue);
		int x_string = 50;
		brush.setFont(new Font("Courier New", Font.BOLD, 128));
		brush.drawString(gameOver, x_string, 5*height/16);

		
		//SCORE
		if(!scoreUpdated){
			updateHighScore();
			scoreUpdated = true;
		}
		brush.setFont(new Font("Courier New", Font.BOLD, 64));
		brush.setColor(Color.gray);
		x_string = 100;
		
		highScoreMessage = "High Score: " + getHighScore(); 
		brush.drawString(highScoreMessage, x_string, 17*height/32);

		scoreMessage = "Your Score: " + (int)score; 
		brush.drawString(scoreMessage, x_string, 21*height/32);
		
		
		//new game prompt
		x_string = 150;
		String enterPrompt = "Press [Enter] to play again";

		if( timer % 80 >= 40){//flicker effect
			brush.setColor(Color.black);
		} else {
			brush.setColor(Color.white);
		}		brush.setFont(new Font("Courier New", Font.BOLD, 32));
		brush.drawString(enterPrompt, x_string, 13*height/16);
		
		playGame = false;
		if(enter){
			setDefaults(brush);
			playGame = true;
			scoreUpdated = false;
		}
		
		brush.setColor(Color.gray);
		//press m to return to the main menu
		brush.setFont(new Font("Courier New", Font.BOLD, 24));
		brush.drawString("Press [M] to return to the Main Menu", 135, 55);

	}

	//KEY_LISTENER METHODS-------------------------------------------------------------------------------------------//
	public void keyTyped(KeyEvent e) {e.consume();}


;
	int b = 0;
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch(keyCode){
		case KeyEvent.VK_ENTER:
			enter = true;
			break;
		case KeyEvent.VK_ESCAPE:
			escape = true;
			break;
		case KeyEvent.VK_P:
			p = true;
			break;
		case KeyEvent.VK_Q:
			q = true;
			break;
		case KeyEvent.VK_C:
			c = true;
			break;
		case KeyEvent.VK_H:
			h = true;
			break;

		case KeyEvent.VK_N:
			n = true;
			break;
		case KeyEvent.VK_M:
			m = true;
			break;
		case KeyEvent.VK_L:
			l = true;
			break;	
		case KeyEvent.VK_LEFT:
			left = true;
			break;
		case KeyEvent.VK_RIGHT:
			right = true;
			break;
		case KeyEvent.VK_SPACE:
			spaceBar = true;
			break;
		}
		e.consume();
	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch(keyCode){
		case KeyEvent.VK_ENTER:
			enter = false;
			break;
		case KeyEvent.VK_ESCAPE:
			escape = false;
			c = true;
			p = true;
			break;
		case KeyEvent.VK_P:
			p = false;
			break;
		case KeyEvent.VK_Q:
			q = false;
			break;
		case KeyEvent.VK_C:
			c = false;
			break;
		case KeyEvent.VK_H:
			h = false;
			break;
		case KeyEvent.VK_N:
			n = false;
			break;
		case KeyEvent.VK_M:
			m = false;
			break;
		case KeyEvent.VK_L:
			l = false;
			break;
		case KeyEvent.VK_LEFT:
			left = false;
			break;
		case KeyEvent.VK_RIGHT:
			right = false;
			break;
		case KeyEvent.VK_SPACE:
			spaceBar = false;
			break;
		}	
		e.consume();
	}
}