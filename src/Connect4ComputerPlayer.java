/**
 * computer player class part of model representation of model-view-controller game
 * contains static evaluation and alpha beta pruning
 * @author Max Zhuang
 */
public class Connect4ComputerPlayer extends Player{
	private int depth;
	private static final int[] WEIGHTS = {0, 10, 100, 1000, 100000000};  // a weighting system I researched and came up with, each index represents how many checkers in a row

	/**
	 * Constructer
	 * @param name String name
	 * @param depth int depth of the search that will be conducted
	 */
	public Connect4ComputerPlayer(String name, int depth) {
		super(name);
		this.depth = depth;
	}

	@Override
	public int getMove(Connect4State state, Connect4View view) {
		Connect4GameState gameState = new Connect4GameState(state.getPlayerNum(), state.getPlayers(), state.getBoard());
		int move = pickMove(gameState, depth, -Integer.MAX_VALUE, Integer.MAX_VALUE).move;  // gives the column to move to
		view.reportMove(move, gameState.getPlayerToMove().getName());
		return move;
	}
	
	/**
	 * selects move with alpha beta pruning
	 * @param gameState instance of Connect4GameState which will be the game model that is currently being played
	 * @param depth of the depth searched
	 * @param minValue is alpha
	 * @param maxValue is beta
	 * @return instance of Connect4MoveSD which is the move itself
	 */
	private Connect4MoveSD pickMove(Connect4GameState gameState, int depth, int minValue, int maxValue) {
		
		// used Connect4MoveSD object to save the value and column of the move
		Connect4MoveSD theMove = null;  // current move
		Connect4MoveSD bestMove = new Connect4MoveSD(-Integer.MAX_VALUE, -1);  // best possible move
		
		for (int col = 0; bestMove.value < maxValue && col < Connect4GameState.COLS; col++) {
			if(gameState.isValidMove(col)){
				gameState.makeMove(col);
					if(gameState.isWinner()){
						theMove = new Connect4MoveSD(Integer.MAX_VALUE, col);  // set to max value since winning is the best move
					}
					else if(gameState.isFull()){
						theMove = new Connect4MoveSD(-Integer.MAX_VALUE, col);	// I hate draws
					}
				else {
					if(depth > 0) { 
						theMove = pickMove(gameState, depth - 1, -maxValue, -minValue);  // switch perspective
						theMove.value = -theMove.value;
						theMove.move = col;
					}
					else {
						theMove = new Connect4MoveSD(eval(gameState), col);
					}
				}
					
				// update if better value
				if (theMove.value > bestMove.value){
					bestMove = theMove;
					minValue = Math.max(minValue, bestMove.value);
				}
			
				gameState.undoMove(col); // reset
			}
			
		}
		return bestMove;
	}
	
	/**
	 * helper method to calculate all possible vertical 4 in a row and weights them according to strength
	 * @param gameBoard is the matrix that is current game being played
	 * @param me the char represents the first person perspective of player's checker
	 * @param oppo the char that represents the opposition's checker
	 * @return int the score from the opposition's score subtracted by player's score
	 */
	private static int evalVertical(char [][] gameBoard, char me, char oppo){
		int myCount, oppoCount;
		int myScore = 0;
		int oppoScore = 0;
		
		for (int col = 0; col < Connect4State.COLS; col++){
			for (int rows = 0; rows < Connect4State.ROWS - 3; rows++){
				
				myCount = 0;
				oppoCount = 0;
				
				for (int i = rows; i < (rows+4); i ++){
					if (gameBoard[i][col] == me){
						myCount ++;
					}
					if (gameBoard[i][col] == oppo){
						oppoCount ++;
					}
				}
				
				if (oppoCount == 0){
					myScore += WEIGHTS[myCount];
				}
				else if (myCount == 0){
					oppoScore += WEIGHTS[oppoCount];
				}
			}
		}
		return oppoScore - myScore;
	}
	
	/**
	 * helper method to calculate all possible horizontal 4 in a row and weights them according to strength
	 * @param gameBoard is the matrix that is current game being played
	 * @param me the char represents the first person perspective of player's checker
	 * @param oppo the char that represents the opposition's checker
	 * @return int the score from the opposition's score subtracted by player's score
	 */
	private static int evalHorizontal(char [][] gameBoard, char me, char oppo){
		int myCount, oppoCount;
		int myScore = 0;
		int oppoScore = 0;
		
		for(int rows = 0; rows < Connect4GameState.ROWS; rows++) {
			for(int col = 0; col < Connect4GameState.COLS - 3; col++) {
				
				myCount = 0;
				oppoCount = 0;
				
				for (int i = col; i < (col+4); i++){
					if (gameBoard[rows][i] == me){
						myCount++;
					}
					if (gameBoard[rows][i] == oppo){
						oppoCount++;
					}
				}
				
				if (oppoCount == 0){
					myScore += WEIGHTS[myCount];
				}
				else if (myCount == 0){
					oppoScore += WEIGHTS[oppoCount];
				}
			}
		}
		
		return oppoScore - myScore;
	}
	
	/**
	 * helper method to calculate all possible diagonal 4 in a row and weights them according to strength
	 * @param gameBoard is the matrix that is current game being played
	 * @param me the char represents the first person perspective of player's checker
	 * @param oppo the char that represents the opposition's checker
	 * @return int the score from the opposition's score subtracted by player's score
	 */
	private static int evalDiagonal(char [][] gameBoard, char me, char oppo){
		int myCount, oppoCount;
		int myScore = 0;
		int oppoScore = 0;
		
	// diagonal1
			for(int rows= 0; rows < Connect4GameState.ROWS - 3; rows++){
				for(int col = 0; col <Connect4GameState.COLS - 3; col++){
					
					myCount = 0;
					oppoCount = 0;
				
					for(int i = rows, j=col; i < (rows+4); i++, j++){
							if (gameBoard[i][j] == me){
								myCount++;
							}
							if (gameBoard[i][j] == oppo){
								oppoCount++;
							}
						}
					
					if (oppoCount == 0){
						myScore += WEIGHTS[myCount];
					}
					else if (myCount == 0){
						oppoScore += WEIGHTS[oppoCount];
					}
				}
			}
			
			// diagonal2
			for (int rows = Connect4GameState.ROWS - 1; rows > 2 ; rows--){
				for(int col = 0; col< Connect4GameState.COLS - 3; col++) {
							
					myCount = 0;
					oppoCount = 0;
							
					for(int i = rows, j = col; i > rows - 4; i--, j++) {
						if (gameBoard[i][j] == me){
							myCount++;
						}
						if (gameBoard[i][j] == oppo){
							oppoCount++;
						}
					}	
							
					if (oppoCount == 0){
						myScore += WEIGHTS[myCount];
					}
					else if (myCount == 0){
						oppoScore += WEIGHTS[oppoCount];
					}					
				}
			}
		
			return oppoScore - myScore;
	}
	
	/**
	 * method to calculate static evaluation of the board
	 * @param game the Connect4GameState is the state of the current game being played
	 * @return int the total score from the opposition's score subtracted by player's score (addition of smaller components that calcualte this)
	 */
	public static int eval(Connect4GameState game){
		char [][] gameBoard = game.getBoard();
		int finalScore;
		
		char me = Connect4State.CHECKERS[game.getPlayerNum()];
		char oppo = Connect4State.CHECKERS[1- game.getPlayerNum()];
		
		// calculate scores for every type row
		int verticalScore = evalVertical(gameBoard, me, oppo);
		int horizontalScore = evalHorizontal(gameBoard, me, oppo);
		int diagonalScore = evalDiagonal(gameBoard, me, oppo);
		
		// final score, subtract opposition's score from own
		finalScore = verticalScore + horizontalScore + diagonalScore;
		return finalScore;
	}


}
