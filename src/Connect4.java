import java.util.Scanner;

/**
 * controller for the game
 * @author Max Zhuang
 */
public class Connect4 {
	public static void main(String args[]){
			
			Player winner;
			Connect4View view;
			
			Scanner input = new Scanner(System.in);
			String answer = "";
			while (!(answer.contains("Text") || answer.contains("nah"))){
				System.out.println("Text View or nah?");
				answer = input.nextLine();
			}

			if (answer.contains("Text")){
				view = new Connect4Text();
			} else {
				view = new Connect4ViewGraphical();
			}
			
			Player [] players = new Player[2];
			players[1] = makePlayer(view, "first");  // red goes first
			players[0] = makePlayer(view, "second");
			
			// create state
			Connect4GameState state = new Connect4GameState(1, players, view);
			view.display(state);
			
			while (!state.gameIsOver()) {
				int move = state.getPlayerToMove().getMove(state, view);
				state.makeMove(move);
				view.display(state);
			}

			winner = state.getWinner();
			if (state.isFull() && winner == null){ 
				view.reportToUser("GAME OVER! draw!");
			}
			else{
				view.reportToUser("GAME OVER! " + winner.getName()+" wins!");
			}
			
		}
		
	/**
	 * helper method to make the player
	 * @param view the Connect4View object of the graphic
	 * @param player the String that is the player name that gets entered
	 * @return player object of the according type of player
	 */
		public static Player makePlayer(Connect4View view, String player){
			
			String playerName = view.getAnswer("Enter the name of the " + player + " player." + "\n(Write 'Computer' in the name of a computer) ");
			if(playerName.contains("Computer")) {
				int depth = view.getIntAnswer("Depth of search? ");
				return new Connect4ComputerPlayer(playerName, depth);
			}
			else
				return new Connect4HumanPlayer(playerName);
		}
	
}
