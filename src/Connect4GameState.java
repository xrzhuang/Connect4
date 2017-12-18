import java.util.Arrays;

/**
 * Represents the state of the Connect 4 game  
 * It is the model in the model-view-controller pattern.
 * @author Max Zhuang
 */
public class Connect4GameState implements Connect4State{
	private char[][] gameBoard;  // hold the game board
	private int playerToMoveNum;  // 0 or 1 for first and second player
	private Player [] players;  // array of the two players
	private Connect4View view;  // the view object
	
	/**
	 * Constructs a game in the initial state
	 * @param playerNum the player whose move it is
	 * @param thePlayers the player objects
	 * @param theView view object
	 */
	public Connect4GameState(int playerNum, Player [] thePlayers, Connect4View theView){
		gameBoard = new char[ROWS][COLS];  // create game board
		for (char [] slot: gameBoard){  // fill it up with empties
			Arrays.fill(slot, EMPTY);
		}
		initialize(playerNum, thePlayers, gameBoard);
		view = theView;
	}
	
	/**
	 * Constructs a game with a given state
	 * @param playerNum the player whose move it is
	 * @param thePlayers the player objects
	 * @param initBoard is the given game board
	 */
	public Connect4GameState(int playerNum, Player [] thePlayers, char [][] initBoard)   {
		initialize(playerNum, thePlayers, initBoard);
	}
	
	/**
	 * initializes game
	 * @param playerNum the player whose move it is
	 * @param thePlayers the player objects
	 * @param initBoard is the given game board
	 */
	private void initialize(int playerNum, Player [] thePlayers, char [][] initBoard) {
		gameBoard = new char[ROWS][COLS];
		for (int row = 0; row < ROWS; row++) {
			for (int column = 0; column < COLS; column++) {
				gameBoard[row][column] = initBoard[row][column];
			} 
		}
		playerToMoveNum = playerNum;
		players = thePlayers;
	}

	@Override
	public char[][] getBoard() {
		return gameBoard;
	}

	@Override
	public Player[] getPlayers() {
		return players;  
	}

	@Override
	public int getPlayerNum() {
		return playerToMoveNum; 
	}

	@Override
	public Player getPlayerToMove() {
		return players[playerToMoveNum];  
	}

	@Override
	public boolean isValidMove(int col) {
		if (gameBoard[ROWS - 1][col] == EMPTY){  // check if the top of column is full
			return true;
		}
		return false;
	}
	
	/**
	 * helper method to return the first available row in a column
	 * @param playerNum the player whose move it is
	 * @return row int row that is the row
	 */
	private int availableRow(int col){
		for (int row = 0; row < ROWS; row++){
			if (gameBoard[row][col] == EMPTY){
				return row;
			}
		}
		return -1;  // nothing is available
		}
	

	@Override
	public void makeMove(int col) {
		
		// check for valid move
		if (isValidMove(col)){
			int row = availableRow(col);
			gameBoard[row][col] = CHECKERS[getPlayerNum()];
			
		// switch players, red goes first!
			playerToMoveNum = 1 - playerToMoveNum;

		}
		else {
			throw new IllegalStateException("full column");
		}
	}

	@Override
	public boolean isFull() {
		for (int column = 0; column < COLS; column++){  // check all columns for valid moves
			if (isValidMove(column)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * method to return the winner of the game
	 * @return Player object of the winner
	 */
	public Player getWinner() {
		
		int player0Count;
		int player1Count;
		
		// check every space vertically
		for (int col = 0; col< COLS; col++){
			for (int rows = 0; rows < ROWS-3; rows++){  // ROWS-3 because it's impossible to have a 4 in a row starting past that point!
				
				player0Count = 0;
				player1Count = 0;
				
				for (int i = rows; i < rows + 4; i ++){ 
					
					// increase the counts
					if (gameBoard[i][col] == CHECKER0){
						player0Count ++;
					}
					if (gameBoard[i][col] == CHECKER1){
						player1Count ++;
					}
						
				}
				
				// return the player with 4 in a row
				if (player0Count == 4){
					return players[0];
				}
				if (player1Count == 4){
					return players[1];		
				}
			}
		}
		
		// same thing horizontally
		for(int rows = 0; rows < ROWS; rows++) {
			for(int col = 0; col < COLS - 3; col++) {
				
				// reset the counts
				player0Count = 0;
				player1Count = 0;
				
				for (int i = col; i < col + 4; i++){
					if (gameBoard[rows][i] == CHECKER0){
						player0Count++;
					}
					if (gameBoard[rows][i] == CHECKER1){
						player1Count++;
					}
				}
				
				if (player0Count == 4){
					return players[0];
				}
				if (player1Count == 4){
					return players[1];
				}
			}
		}
		
		// diagonal1
		for(int rows = 0; rows < ROWS - 3; rows++){
			for(int col = 0; col < COLS - 3; col++){
				
				player0Count = 0;
				player1Count = 0;
			
				for(int i = rows, j = col; i < rows + 4; i++, j++){
						if (gameBoard[i][j] == CHECKER0){
							player0Count++;
						}
						if (gameBoard[i][j] == CHECKER1){
							player1Count++;
						}
					}
				
				if (player0Count == 4){
					return players[0];
				}
				if (player1Count == 4){
					return players[1];
				}
			}
		}

		// diagonal2
		for (int rows = ROWS - 1; rows > 2 ; rows--){
			for(int col = 0; col< COLS - 3; col++) {
				
				player0Count = 0;
				player1Count = 0;
				
				for(int i = rows, j = col; i > rows - 4; i--, j++) {
					if (gameBoard[i][j] == CHECKER0){
						player0Count++;
					}
					if (gameBoard[i][j] == CHECKER1){
						player1Count++;
					}
				}	
				
				if (player0Count == 4){
					return players[0];
				}
				if (player1Count == 4){
					return players[1];	
				}
			}	
		}
	
		// when no winners are found
		return null;
	}
	
	/**
	 * undo the move
	 * @param column the column to undo
	 * @param stateEval the int static evaluation value before the move was made
	 */
	public void undoMove(int column){
		int topRow = ROWS - 1;
		while (gameBoard[topRow][column] == EMPTY && topRow > 0){
			topRow--;
		}
		gameBoard[topRow][column] = EMPTY;
		
		// reset
		playerToMoveNum = 1 - playerToMoveNum;
	}

	@Override
	public boolean isWinner() {
		if (getWinner() != null){
			return true;
		}
		return false;
	}

	@Override
	public boolean gameIsOver() {
		if (isWinner() || isFull()){
			return true;
		}
		return false;
	}
	
	/**
	 * test function to check the static evaluation function versus hand calculations
	 * @param args - I still don't know what this means lol
	 */
	public static void main(String[] args){
		Player[] players = new Player[2];
		players[0] = new Connect4HumanPlayer("Test1");
		players[1] = new Connect4HumanPlayer("Test2");
		Connect4View view = new Connect4ViewGraphical();

		// Initialize test game
		Connect4GameState test = new Connect4GameState(0, players, view);
		while (!test.gameIsOver()){
			int column = test.getPlayerToMove().getMove(test, view);
			test.makeMove(column);
			int evaluation = Connect4ComputerPlayer.eval(test);
			System.out.println("eval is");
			System.out.println(evaluation);
			view.display(test);

		}
	}
	
}
