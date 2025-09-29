//Made by Joseph Wang and Vibhu Krishnaswamy
package chess;

import java.util.ArrayList;

public class Chess {

        enum Player { white, black }
        private static ArrayList<ReturnPiece> pieces;
        static Player currentTurn = Player.white;
    
	/**
	 * Plays the next move for whichever player has the turn.
	 * 
	 * @param move String for next move, e.g. "a2 a3"
	 * 
	 * @return A ReturnPlay instance that contains the result of the move.
	 *         See the section "The Chess class" in the assignment description for details of
	 *         the contents of the returned ReturnPlay instance.
	 */
        
        
	public static ReturnPlay play(String move) {

	    ReturnPlay rp = new ReturnPlay();
	    // You really only need to assign pieces to pieceOnBoard before you return
	    rp.piecesOnBoard = pieces;
	    
	    if(move.length() != 5) {
	    	rp.message = ReturnPlay.Message.ILLEGAL_MOVE;
	    	return rp;
	    }
	    
	    String sourceCoordinates = move.substring(0, 2);
	    
	    char fileSource = sourceCoordinates.charAt(0);
		char rankSource = sourceCoordinates.charAt(1);
		int rank = Character.getNumericValue(rankSource);
		int file = fileSource;
		boolean emptyTile = true;
//		System.out.println("File source: " + file + ". Rank source: " + rank);
		ReturnPiece pickedPiece = null;
	    for(ReturnPiece pieceIterator : pieces) {
	    	int currentPieceFile = pieceIterator.pieceFile.name().charAt(0);
//			System.out.println("File source: " + currentPieceFile);
	    	if(currentPieceFile == file && pieceIterator.pieceRank == rank) {
	    		pickedPiece = pieceIterator;
	    		emptyTile = false;
	    		break;
	    	}
	    }
	    
	    if(emptyTile) {
	    	rp.message = ReturnPlay.Message.ILLEGAL_MOVE;
	    	return rp;
	    }
	    
	    boolean validMove = didUserPickOwnPiece(pickedPiece);
	    
	    if(!validMove) {
	    	System.out.println("You did not pick your own piece!");
	    	rp.message = ReturnPlay.Message.ILLEGAL_MOVE;
	    	return rp;
	    }
	    
	    if(pickedPiece.pieceType == ReturnPiece.PieceType.WN || pickedPiece.pieceType == ReturnPiece.PieceType.BN ) {
	    	validMove = checkKnightMove(pickedPiece, move.charAt(3), Character.getNumericValue(move.charAt(4)) );
	    }
	    
	    if(pickedPiece.pieceType == ReturnPiece.PieceType.WB || pickedPiece.pieceType == ReturnPiece.PieceType.BB) {
	    	validMove = checkBishopMove(pickedPiece, move.charAt(3), Character.getNumericValue(move.charAt(4)));
	    }
	    
	    if(pickedPiece.pieceType == ReturnPiece.PieceType.WR || pickedPiece.pieceType == ReturnPiece.PieceType.BR) {
	    	validMove = checkRookMove(pickedPiece, move.charAt(3), Character.getNumericValue(move.charAt(4)));
	    }
	    
	    if(pickedPiece.pieceType == ReturnPiece.PieceType.WK || pickedPiece.pieceType == ReturnPiece.PieceType.BK) {
	    	validMove = checkKingMove(pickedPiece, move.charAt(3), Character.getNumericValue(move.charAt(4)));
	    }
	    
	    if(pickedPiece.pieceType == ReturnPiece.PieceType.WQ || pickedPiece.pieceType == ReturnPiece.PieceType.BQ) {
	    	validMove = checkQueenMove(pickedPiece, move.charAt(3), Character.getNumericValue(move.charAt(4)));
	    }
	    
	    if(pickedPiece.pieceType == ReturnPiece.PieceType.WP || pickedPiece.pieceType == ReturnPiece.PieceType.BP) {
	    	validMove = checkPawnMove(pickedPiece, move.charAt(3), Character.getNumericValue(move.charAt(4)));
	    }
	    
	    if(!validMove) {
	    	rp.message = ReturnPlay.Message.ILLEGAL_MOVE;
	    	return rp;
	    }
	    
	    boolean selfCheck = checkForCheck() {
	    	
	    }
	    
	    if(validMove) {
	    	//I would prefer to use charAt and a character, but the enum valueOf function takes Strings
	    	ReturnPiece potentialCapture = null;
	    	potentialCapture = findPieceAt(move.charAt(3), Integer.parseInt(move.substring(4, 5)));
	    	pieces.remove(potentialCapture);
	    	pickedPiece.pieceFile = ReturnPiece.PieceFile.valueOf(move.substring(3, 4));
	    	pickedPiece.pieceRank = Integer.parseInt(move.substring(4, 5));
	    	
	    	
	    	
		    rp.message = null;
		    currentTurn = (currentTurn == Player.white) ? Player.black : Player.white;
	    } else {
	    	rp.message = ReturnPlay.Message.ILLEGAL_MOVE;
	    }
	    return rp;
	}


	
	
	
	/**
	 * This method should reset the game, and start from scratch.
	 */
	public static void start() {
		/* FILL IN THIS METHOD */
		
		String[][] board = PlayChess.makeBlankBoard();
		pieces = new ArrayList<ReturnPiece>();
		currentTurn = Player.white;
		
	   for (ReturnPiece.PieceFile f : ReturnPiece.PieceFile.values()) {
	        ReturnPiece p = new ReturnPiece();
	        p.pieceType = ReturnPiece.PieceType.WP;
	        p.pieceFile = f;
	        p.pieceRank = 2;
	        pieces.add(p);
	    }

	    // Black pawns
	    for (ReturnPiece.PieceFile f : ReturnPiece.PieceFile.values()) {
	        ReturnPiece p = new ReturnPiece();
	        p.pieceType = ReturnPiece.PieceType.BP;
	        p.pieceFile = f;
	        p.pieceRank = 7;
	        pieces.add(p);
	    }

	    // White back rank
	    ReturnPiece wr1 = new ReturnPiece(); wr1.pieceType = ReturnPiece.PieceType.WR; wr1.pieceFile = ReturnPiece.PieceFile.a; wr1.pieceRank = 1; pieces.add(wr1);
	    ReturnPiece wn1 = new ReturnPiece(); wn1.pieceType = ReturnPiece.PieceType.WN; wn1.pieceFile = ReturnPiece.PieceFile.b; wn1.pieceRank = 1; pieces.add(wn1);
	    ReturnPiece wb1 = new ReturnPiece(); wb1.pieceType = ReturnPiece.PieceType.WB; wb1.pieceFile = ReturnPiece.PieceFile.c; wb1.pieceRank = 1; pieces.add(wb1);
	    ReturnPiece wq  = new ReturnPiece(); wq.pieceType  = ReturnPiece.PieceType.WQ; wq.pieceFile  = ReturnPiece.PieceFile.d; wq.pieceRank  = 1; pieces.add(wq);
	    ReturnPiece wk  = new ReturnPiece(); wk.pieceType  = ReturnPiece.PieceType.WK; wk.pieceFile  = ReturnPiece.PieceFile.e; wk.pieceRank  = 1; pieces.add(wk);
	    ReturnPiece wb2 = new ReturnPiece(); wb2.pieceType = ReturnPiece.PieceType.WB; wb2.pieceFile = ReturnPiece.PieceFile.f; wb2.pieceRank = 1; pieces.add(wb2);
	    ReturnPiece wn2 = new ReturnPiece(); wn2.pieceType = ReturnPiece.PieceType.WN; wn2.pieceFile = ReturnPiece.PieceFile.g; wn2.pieceRank = 1; pieces.add(wn2);
	    ReturnPiece wr2 = new ReturnPiece(); wr2.pieceType = ReturnPiece.PieceType.WR; wr2.pieceFile = ReturnPiece.PieceFile.h; wr2.pieceRank = 1; pieces.add(wr2);

	    // Black back rank
	    ReturnPiece br1 = new ReturnPiece(); br1.pieceType = ReturnPiece.PieceType.BR; br1.pieceFile = ReturnPiece.PieceFile.a; br1.pieceRank = 8; pieces.add(br1);
	    ReturnPiece bn1 = new ReturnPiece(); bn1.pieceType = ReturnPiece.PieceType.BN; bn1.pieceFile = ReturnPiece.PieceFile.b; bn1.pieceRank = 8; pieces.add(bn1);
	    ReturnPiece bb1 = new ReturnPiece(); bb1.pieceType = ReturnPiece.PieceType.BB; bb1.pieceFile = ReturnPiece.PieceFile.c; bb1.pieceRank = 8; pieces.add(bb1);
	    ReturnPiece bq  = new ReturnPiece(); bq.pieceType  = ReturnPiece.PieceType.BQ; bq.pieceFile  = ReturnPiece.PieceFile.d; bq.pieceRank  = 8; pieces.add(bq);
	    ReturnPiece bk  = new ReturnPiece(); bk.pieceType  = ReturnPiece.PieceType.BK; bk.pieceFile  = ReturnPiece.PieceFile.e; bk.pieceRank  = 8; pieces.add(bk);
	    ReturnPiece bb2 = new ReturnPiece(); bb2.pieceType = ReturnPiece.PieceType.BB; bb2.pieceFile = ReturnPiece.PieceFile.f; bb2.pieceRank = 8; pieces.add(bb2);
	    ReturnPiece bn2 = new ReturnPiece(); bn2.pieceType = ReturnPiece.PieceType.BN; bn2.pieceFile = ReturnPiece.PieceFile.g; bn2.pieceRank = 8; pieces.add(bn2);
	    ReturnPiece br2 = new ReturnPiece(); br2.pieceType = ReturnPiece.PieceType.BR; br2.pieceFile = ReturnPiece.PieceFile.h; br2.pieceRank = 8; pieces.add(br2);

//		PlayChess.printPiecesOnBoard(pieces, board);
		PlayChess.printBoard(pieces);
		System.out.println("Make your move");
	}
	
	private static boolean didUserPickOwnPiece(ReturnPiece chosenPiece) {
		if((currentTurn == Player.black) && (chosenPiece.pieceType.name().equals("WP") || 
												chosenPiece.pieceType.name().equals("WR") ||
												chosenPiece.pieceType.name().equals("WN") ||
												chosenPiece.pieceType.name().equals("WB") ||
												chosenPiece.pieceType.name().equals("WQ") ||
												chosenPiece.pieceType.name().equals("WK") ) ) {
			System.out.println("Choose a black piece.");
			return false;
		}
		
		if((currentTurn == Player.white) && (chosenPiece.pieceType.name().equals("BP") ||
												chosenPiece.pieceType.name().equals("BR") ||
												chosenPiece.pieceType.name().equals("BN") ||
												chosenPiece.pieceType.name().equals("BB") || 
												chosenPiece.pieceType.name().equals("BQ") ||
												chosenPiece.pieceType.name().equals("BK") ) ) {
			System.out.println("Choose a white piece.");
			return false;
		}
		
		return true;
	}
	
	private static ReturnPiece findPieceAt(char file, int rank) {
		for (ReturnPiece pieceIterator : pieces) {
			if(pieceIterator.pieceFile.name().charAt(0) == file && pieceIterator.pieceRank == rank) {
				return pieceIterator;
			}
		}
		return null;
	}
	
	private static boolean checkKnightMove(ReturnPiece knight, char toFile, int toRank) {
	    char fromFile = knight.pieceFile.name().charAt(0);
	    int fromRank = knight.pieceRank;

	    int fileDiff = Math.abs(toFile - fromFile);
	    int rankDiff = Math.abs(toRank - fromRank);

	    // L-shaped?
	    boolean validShape = (fileDiff == 1 && rankDiff == 2) ||
	                         (fileDiff == 2 && rankDiff == 1);

	    if (!validShape) return false;

	    // check destination
	    ReturnPiece target = findPieceAt(toFile, toRank);
	    if (target != null) {
	        boolean sameColor = (knight.pieceType.name().charAt(0) == target.pieceType.name().charAt(0));
	        if (sameColor) return false; // blocked by own piece
	    }

	    return true;
	}
	
	private static boolean checkBishopMove(ReturnPiece bishop, char toFile, int toRank) {
	    char fromFile = bishop.pieceFile.name().charAt(0);
	    int fromRank = bishop.pieceRank;

	    int fileDiff = toFile - fromFile;
	    int rankDiff = toRank - fromRank;

	    if (Math.abs(fileDiff) != Math.abs(rankDiff)) {
	        return false; // not diagonal
	    }

	    int fileStep = (fileDiff > 0) ? 1 : -1;
	    int rankStep = (rankDiff > 0) ? 1 : -1;

	    char f = (char)(fromFile + fileStep);
	    int r = fromRank + rankStep;
	    while (f != toFile && r != toRank) {
	        if (findPieceAt(f, r) != null) return false; // blocked
	        f += fileStep;
	        r += rankStep;
	    }

	    // check destination
	    ReturnPiece target = findPieceAt(toFile, toRank);
	    if (target != null) {
	        boolean sameColor = (bishop.pieceType.name().charAt(0) == target.pieceType.name().charAt(0));
	        if (sameColor) return false;
	    }

	    return true;
	}
	
	private static boolean checkRookMove(ReturnPiece rook, char toFile, int toRank) {
		char fromFile = rook.pieceFile.name().charAt(0);
	    int fromRank = rook.pieceRank;
	    
	    if(fromFile != toFile && fromRank != toRank) {
	    	return false;
	    }
	    
	    if (fromFile == toFile) { // vertical
	        int step = (toRank > fromRank) ? 1 : -1;
	        for (int r = fromRank + step; r != toRank; r += step) {
	            if (findPieceAt(fromFile, r) != null) return false;
	        }
	    } else { // horizontal
	        int step = (toFile > fromFile) ? 1 : -1;
	        for (char f = (char)(fromFile + step); f != toFile; f += step) {
	            if (findPieceAt(f, fromRank) != null) return false;
	        }
	    }
	    
	    ReturnPiece target = findPieceAt(toFile, toRank);
	    if (target != null) {
	        // if same color → illegal
	        if (rook.pieceType.name().charAt(0) == target.pieceType.name().charAt(0)) {
	            return false;
	        }
	    }

	    return true;
	}
	
	private static boolean checkKingMove(ReturnPiece king, char toFile, int toRank) {
		char fromFile = king.pieceFile.name().charAt(0);
	    int fromRank = king.pieceRank;
	    if(Math.abs(fromFile - toFile) > 1 || Math.abs(fromRank - toRank) > 1) return false;
	    
	    ReturnPiece target = findPieceAt(toFile, toRank);
	    if (target != null) {
	        if (king.pieceType.name() == target.pieceType.name()) {
	            return false;
	        }
	    }

	    return true;
		
	}
	
	private static boolean checkQueenMove(ReturnPiece queen, char toFile, int toRank) {
		return checkBishopMove(queen, toFile, toRank) || checkRookMove(queen, toFile, toRank);
	}
	
	private static boolean checkPawnMove(ReturnPiece pawn, char toFile, int toRank) {
		char fromFile = pawn.pieceFile.name().charAt(0);
	    int fromRank = pawn.pieceRank;

	    boolean isWhite = pawn.pieceType.name().charAt(0) == 'W';
	    
	    int direction = isWhite ? 1 : -1;
	    int startRank = isWhite ? 2 :  7;
	    
	    if (toFile == fromFile && toRank == fromRank + direction) {
	        if (findPieceAt(toFile, toRank) == null) {
	            return true;
	        }
	        return false; // blocked
	    }
	    
	    if(toFile == fromFile && toRank == 2 * direction + fromRank && fromRank == startRank) {
	    	if(findPieceAt(toFile, toRank) == null && findPieceAt(toFile, toRank - direction) == null) return true;
	    	return false;	
	    }
	    
	    if (Math.abs(toFile - fromFile) == 1 && toRank == fromRank + direction) {
	        ReturnPiece target = findPieceAt(toFile, toRank);
	        if (target != null && target.pieceType.name().charAt(0) != pawn.pieceType.name().charAt(0)) {
	            return true; // capture enemy
	        }
	        return false; // no enemy to capture
	    }
	    
	    try {
	    	assert false;
	    } catch (AssertionError e){
	    	System.out.println("Logic should not have reached here\n" + e.getMessage());
	    }
	    return false;
	}
	
	private static checkForCheck() {
		return false;
	}
}
