import java.util.ArrayList;


public class Question {
	/*This class is used to store a Question consisting of two factors in a single object
	 * */
	
	private static int factor1 =1;
	private static int factor2 =1;
	private int answer, f1, f2;
	private static int temp1;
	private static int temp2;
	static boolean wasCorrect, mistakesRound = false;
	/**this boolean is true under the following circumstances:
	 * 1- the player has hit the wrong block even once
	 * 2- the player took too long to hit the right block 
	 * 
	 * it is set back to false as soon as:
	 * 1- the mistake has been added to the arrayList
	 * 2- a new question is being asked*/
	static ArrayList<Question> mistakes = new ArrayList<>();
	static int n = 3;//number of objects required in arrayList for Mistakes round to be activated
	
	public Question(int factor1, int factor2 ){
		this.f1 = factor1;
		this.f2 = factor2;
	}
	
	/**updates the question object, updating factor1 and factor2, according to the following rules:
	 * 1- if the player got the previous question right, the question is updated normally
	 * 2- if the previous question is wrong, and there are less than n Questions in the ArrayList, repeat it
	 * 3- if there are n questions in the arrayList, activate Mistakes round*/
	public static void update(){
		//determine whether to switch to mistakes round or normal round:

		if(!mistakesRound){
			//NORMAL ROUNDS:
			//Previous question was right:
			if(wasCorrect){
				if(factor2 == factor1){
					factor1++;
					factor2 = 1;
				} else {factor2++;}
				Blocks.score += Blocks.pointsLeft;
			} else {
				//Previous question was wrong, add it to mistakes ArrayList
				addMistake(new Question(factor1, factor2));
				if(mistakes.size() >= n){
					mistakesRound = true;
					temp1 = factor1;
					temp2 = factor2;
					factor1 = mistakes.get(0).f1;
					factor2 = mistakes.get(0).f2;
					Blocks.blocksList.clear();
					Blocks.targetIndex = Blocks.timer % 5;
					Blocks.loadChoices();
				}
			}
		} else {
			//MISTAKE ROUNDS:
			//previous question was right:
			if(wasCorrect){
				mistakes.remove(0);
				if(mistakes.isEmpty()){
					mistakesRound = false;
					factor1 = temp1;
					factor2 = temp2;
				}  else {
					factor1 = mistakes.get(0).f1;
					factor2 = mistakes.get(0).f2;
				}
				Blocks.replenishment += 25;
			} else {
				//previous question was wrong:
				//do nothing
				//addMistake(new Question(factor1, factor2));
			}
		}
		
		//in either case, if an answer was correct, take the following steps:
		if(wasCorrect){
			Blocks.blocksList.clear();
			Blocks.targetIndex = Blocks.timer % 5;
			Blocks.loadChoices();
		}
	}
	
	//get methods for factors
	public static int getFactor1(){
		return factor1;
	}
	
	public static int getFactor2(){
		return factor2;
	}
	
	//adds a question object to the arrayList
	public static void addMistake(Question mistake){
		mistakes.add(mistake);
	}
	
	//removes the given question from the arrayList
	public static void removeMistake(Question mistake){
		mistakes.remove(mistake);
	}
	
	public static void checkBlock(boolean blockIndexEqualsTargetIndex){
		wasCorrect = blockIndexEqualsTargetIndex;
	}
	
	public static void printValues(){
		String questionString = "";
		if(mistakesRound){
			questionString += "Mistakes round is currently in session + \n";
		}
		questionString = "Current Question: " + factor1 + " X " + factor2 + "\n";
		questionString += "Mistakes currently in list: \n";
		for(int i = 0; i < mistakes.size(); i++){
			questionString += mistakes.get(i).f1 + " X " + mistakes.get(i).f2 + "\n";
		}

		System.out.println(questionString);
	}
	
	public static void resetValues(){
		factor1 = 1;
		factor2 = 1;
		mistakes.clear();
		mistakesRound = false;
	}
}
