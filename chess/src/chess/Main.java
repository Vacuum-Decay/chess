package chess;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Things to do
 * Makes sure the pieces can only do legal moves
 * 		Make an en passant checker
 * Make a function that checks if the king is in checkmate or check
 * 	This should be easy. Do a bfs that stops when you encounter a piece, check if that piece can capture the king.
 * 	If the diagonals / columns/row is blocked, stop searching
 *  Then, check for horsey angles
 *  Let's do movement for some easier pieces first
 *  king
 *  knight
 *  pawn
 *  rook
 *  bishop
 *  
 *  need to make castling feature
 *  Need to include pawn promotion
 */


public class Main {
	tileState[][] chessBoard = new tileState[8][8];
	int whitesTurn = 1;
	int blacksTurn = -1;
	int turnTracker = whitesTurn;
	int[] enPassantTileCoordinate = {-1, -1};
	
	public static void main(String args[]) {
		Main game = new Main();
		
		game.setUpBoard();
		
		game.runGame();
	}
	
	//For enpassant, there is only 1 square each turn that can possibly be en passanted at any time. So, just keep track of the square
	
	private void runGame() {
		Scanner scanner = new Scanner(System.in);
		boolean isCheckmate = false;
		
		//Overall game loop
		while(true) {
			printBoard();
			System.out.println("Type escape to quit the program");
			System.out.print("Select the piece you want to move by entering its tile coordinates: ");
//			System.out.println("TurnTracker: " + turnTracker);
			String input = scanner.nextLine();
			
			if(input == "escape"  || isCheckmate) {
				break;
			}
			
			String coordinates;
			if(input.length() < 2 ) coordinates = "zz"; 
			else coordinates = input.substring(0, 2);
			
			char fileSource = coordinates.charAt(0);
			char rankSource = coordinates.charAt(1);
			int rank = 8 - Character.getNumericValue(rankSource);
			int file = fileSource - 'a';
			
//			System.out.println(rank + ", " + file);
			
			//Loop for forcing the user to select a valid piece
			while(file < 0 || file > 7 || rank < 0 || rank > 7 || (!didUserPickOwnPiece(rank, file, turnTracker)) || input.length() < 2) {
				System.out.print("Select the piece you want to move by entering its tile coordinates: ");
				input = scanner.nextLine();
				if(input.equals("escape")) break;
				coordinates = input.substring(0, 2);
				fileSource = coordinates.charAt(0);
				rankSource = coordinates.charAt(1);
				rank = 8 - Character.getNumericValue(rankSource);
				file = fileSource - 'a';
				System.out.println("file:" + file + ", rank: " + rank);
			}
			
			
			//Loop that makes user choose valid coordinates for moving a piece
			
			System.out.println("If you want to move another piece, enter \"b\"");
			System.out.print("Select where you want the piece to move: ");
			input = scanner.nextLine();
			if(input.equals("b")) {
				continue;
			}
			coordinates = input.substring(0, 2);
			int fileDestination = coordinates.charAt(0) - 'a';
			int rankDestination = 8 - Character.getNumericValue(coordinates.charAt(1));
			
			//This loop will also check if the move destination is within bounds of the board
			while(fileDestination < 0 || fileDestination > 7 || rankDestination < 0 || rankDestination > 7 || !movePiece(rank, file, rankDestination, fileDestination)) {
				printBoard();
				System.out.println("If you want to move another piece, first enter \"b\"");
				System.out.print("Select where you want the piece to move: ");
				input = scanner.nextLine();
				if(input.equals("b")) {
					turnTracker *= -1;
					break;
				}
				if(input.length() >= 2) {
					coordinates = input.substring(0, 2);
					fileDestination = coordinates.charAt(0) - 'a';
					rankDestination = 8 - Character.getNumericValue(coordinates.charAt(1));
				}
			}
			turnTracker *= -1;
		}
		scanner.close();
	}
	
	
	//I have to make this return false if the user makes an illegal move with the piece they've chosen.
	private boolean movePiece(int rank, int file, int rankDestination, int fileDestination) {
//		if(!choseValidPiece)
		if(file < 0 || file > 7 || rank < 0 || rank > 7) return false;
		
		tileState movedPiece = chessBoard[rank][file];
		tileState destinationTileState = chessBoard[rankDestination][fileDestination];
		
		if(turnTracker == whitesTurn && (destinationTileState == tileState.WHITE_BISHOP ||
										 destinationTileState == tileState.WHITE_KING   ||
										 destinationTileState == tileState.WHITE_KNIGHT ||
										 destinationTileState == tileState.WHITE_PAWN   ||
										 destinationTileState == tileState.WHITE_QUEEN  ||
										 destinationTileState == tileState.WHITE_ROOK)) {
			System.out.println("Choose another tile. You can't move onto your own pieces.");
			return false;
		}

		if(turnTracker == whitesTurn && (destinationTileState == tileState.BLACK_BISHOP ||
										 destinationTileState == tileState.BLACK_KING   ||
										 destinationTileState == tileState.BLACK_KNIGHT ||
										 destinationTileState == tileState.BLACK_PAWN   ||
										 destinationTileState == tileState.BLACK_QUEEN  ||
										 destinationTileState == tileState.BLACK_ROOK)) {
			System.out.println("Choose another tile. You can't move onto your own pieces.");
			return false;
		}
		boolean isValidMove = true;
		
		switch(movedPiece) {
			case tileState.BLACK_PAWN:
			case tileState.WHITE_PAWN:
				isValidMove = validatePawnMove(rank, file, rankDestination, fileDestination, chessBoard);
				break;
			case tileState.BLACK_ROOK:
			case tileState.WHITE_ROOK:
				System.out.println("We are moving a rook.");
				isValidMove = validateRookMove(rank, file, rankDestination, fileDestination, chessBoard);
				break;
			case tileState.BLACK_KNIGHT:
			case tileState.WHITE_KNIGHT:
				isValidMove = validateKnightMove(rank, file, rankDestination, fileDestination);
				break;
			case tileState.BLACK_BISHOP:
			case tileState.WHITE_BISHOP:
				isValidMove = validateBishopMove(rank, file, rankDestination, fileDestination, chessBoard);
				break;
			case tileState.BLACK_QUEEN:
			case tileState.WHITE_QUEEN:
				isValidMove = validateQueenMove(rank, file, rankDestination, fileDestination, chessBoard);
				break;
			case tileState.WHITE_KING:
			case tileState.BLACK_KING:
				isValidMove = validateKingMove(rank, file, rankDestination, fileDestination, chessBoard);
				break;
			default:
				System.out.println("Should not be here. Record this as a bug.");
		}
		if(!isValidMove) {
			System.out.println("Invalid move!");
			return false;
		}
		
		chessBoard[rankDestination][fileDestination] = movedPiece;
		chessBoard[rank][file] = tileState.EMPTY;
		return true;
	}
	
	private boolean validatePawnMove(int sourceRow, int sourceColumn, int destinationRow, int destinationColumn, tileState[][] chessBoard) {
		int direction = (turnTracker == whitesTurn) ? -1 : 1;
		int startRow =  (turnTracker == whitesTurn) ?  6 : 1;
		
		tileState destination = chessBoard[destinationRow][destinationColumn];
		
		int rowDifference = destinationRow - sourceRow;
		int columnDifference = destinationColumn - sourceColumn;
		
		//for pawns moving one square
		if(columnDifference == 0 && rowDifference == direction) {
			return destination == tileState.EMPTY;
		}
		
		if(columnDifference == 0 && sourceRow == startRow && rowDifference == 2 * direction) {
			return destination == tileState.EMPTY && chessBoard[sourceRow + direction][sourceColumn] == tileState.EMPTY;
		}
		
		if(Math.abs(columnDifference) == 1 && rowDifference == direction) {
			return destination != tileState.EMPTY &&
				   ( (turnTracker == whitesTurn && !didUserPickOwnPiece(destinationRow, destinationColumn, turnTracker)) ||
					 (turnTracker == blacksTurn && !didUserPickOwnPiece(destinationRow, destinationColumn, turnTracker)) );
		}
		
		return false;
	}
	
	
	private boolean validateQueenMove(int sourceRow, int sourceColumn, int destinationRow, int destinationColumn, tileState[][] chessBoard) {
		if( (Math.abs(destinationRow - sourceRow) != Math.abs(destinationColumn - sourceColumn)) &&  (sourceRow != destinationRow && sourceColumn != destinationColumn) ) return false;
		int rowStep = Integer.compare(destinationRow, sourceRow);
		int columnStep = Integer.compare(destinationColumn, sourceColumn);
		
		int row = sourceRow + rowStep;
		int column = sourceColumn + columnStep;
		
		while(row != destinationRow && column != destinationColumn) {
			if(chessBoard[row][column] != tileState.EMPTY) {
				return false;
			}
			row += rowStep;
			column += columnStep;
		}
		return true;
	}
	
	private boolean validateBishopMove(int sourceRow, int sourceColumn, int destinationRow, int destinationColumn, tileState[][] chessboard) {
		if(Math.abs(destinationRow - sourceRow) != Math.abs(destinationColumn - sourceColumn)) return false;
		
		int rowStep = Integer.compare(destinationRow, sourceRow);
		int columnStep = Integer.compare(destinationColumn, sourceColumn);
		
		int row = sourceRow + rowStep;
		int column = sourceColumn + columnStep;
		
		while(row != destinationRow && column != destinationColumn) {
			if(chessBoard[row][column] != tileState.EMPTY) {
				return false;
			}
			row += rowStep;
			column += columnStep;
		}
		return true;
	}
	
	//These do not need to validate if the tile is already occupied by a friendly unit, as movePiece already checks for this
	private boolean validateKingMove(int sourceRow, int sourceColumn, int destinationRow, int destinationColumn, tileState[][] chessBoard) {
		if(destinationRow > sourceRow + 1 || destinationColumn > sourceColumn + 1) return false;
		if(sourceRow == destinationRow && sourceColumn == destinationColumn) return false;
		return true;
	}
	
	private boolean validateKnightMove(int sourceRow, int sourceColumn, int destinationRow, int destinationColumn) {
		int rowDifference = Math.abs(destinationRow - sourceRow);
		int columnDifference = Math.abs(destinationColumn - sourceColumn);
		
		return (rowDifference == 2 && columnDifference == 1) || (rowDifference == 1 && columnDifference == 2);
	}
	
	private boolean validateRookMove(int sourceRow, int sourceColumn, int destinationRow, int destinationColumn, tileState[][] chessBoard) {
		if(sourceRow != destinationRow && sourceColumn != destinationColumn) return false;
		int rowStep = Integer.compare(destinationRow, sourceRow);
		int columnStep = Integer.compare(destinationColumn, sourceColumn);
		
		int row = sourceRow + rowStep;
		int column = sourceColumn + columnStep;
		
//		System.out.println();
//		int i = 0;
		
		while(row != destinationRow || column != destinationColumn) {
//			System.out.println(row + ", " + column);
			if(chessBoard[row][column] != tileState.EMPTY) {
//				System.out.println("The rook's movement is invalid");
				return false;
			}
//			System.out.println(i++);
			row += rowStep;
			column += columnStep;
		}
		
//		System.out.println("We have validated the rook's movement");
		return true;
	}
	
	private boolean didUserPickOwnPiece(int rank, int file, int turnTracker) {
		//do chessBoard[rank][file].getSymbol and set chessBoard[rankDestination][fileDestination] = to that
		tileState tile = chessBoard[rank][file];
		String tileSymbol = tile.getSymbol();
		if(tileSymbol == ".") {
			System.out.println("Choose a square with one of your pieces.");
			return false;
		}
			
		if((turnTracker == blacksTurn) && (tileSymbol.equals("P") || tileSymbol.equals("R") || tileSymbol.equals("N") || tileSymbol.equals("B") || tileSymbol.equals("Q") || tileSymbol.equals("K") ) ) {
			System.out.println("Choose a black piece.");
			return false;
		}
		
		if((turnTracker == whitesTurn) && (tileSymbol.equals("p") || tileSymbol.equals("r") || tileSymbol.equals("n") || tileSymbol.equals("b") || tileSymbol.equals("q") || tileSymbol.equals("k") ) ) {
			System.out.println("Choose a white piece.");
			return false;
		}
		
		return true;
	}
	
	private void setUpBoard() {
		
		for (int i = 0; i < 8; i++) {
		    for (int j = 0; j < 8; j++) {
		    	chessBoard[i][j] = tileState.EMPTY;
		    }
		}
		
		chessBoard[0][0] = chessBoard[0][7] = tileState.BLACK_ROOK;
		chessBoard[0][1] = chessBoard[0][6] = tileState.BLACK_KNIGHT;
		chessBoard[0][2] = chessBoard[0][5] = tileState.BLACK_BISHOP;
		chessBoard[0][3] = tileState.BLACK_QUEEN;
		chessBoard[0][4] = tileState.BLACK_KING;
		for(int i = 0; i < 8; i ++) chessBoard[1][i] = tileState.BLACK_PAWN;
		
		
		chessBoard[7][0] = chessBoard[7][7] = tileState.WHITE_ROOK;
		chessBoard[7][1] = chessBoard[7][6] = tileState.WHITE_KNIGHT;
		chessBoard[7][2] = chessBoard[7][5] = tileState.WHITE_BISHOP;
		chessBoard[7][3] = tileState.WHITE_QUEEN;
		chessBoard[7][4] = tileState.WHITE_KING;
		for(int i = 0; i < 8; i ++) chessBoard[6][i] = tileState.WHITE_PAWN;
		
	}
	
	
	private void printBoard() {
		System.out.print("   ");
		for(int i = 0; i < 8; i ++) System.out.print((char) ('a' + i) + " ");
		System.out.println("\n");
		for(int i = 0; i < 8; i++) {
			System.out.print((8 - i) + "  ");
			for(int j = 0; j < 8; j ++) {
				System.out.print(chessBoard[i][j].getSymbol() + " ");
			}
			System.out.println("  " + (8 - i));
		}
		System.out.print("\n   ");
		for(int i = 0; i < 8; i ++) System.out.print((char) ('a' + i) + " ");
		
		System.out.println("\n");
	}
	
	
}
