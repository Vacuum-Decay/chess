//Made by Joseph Wang and Vibhu Krishnaswamy
package chess;

import java.util.ArrayList;

public class Chess {

		private static class Tile {
			ReturnPiece.PieceFile file;
			int rank;
		}
		
		/**
		 * The problem with en passant right now is that in order to decide if we need to create a square
		 * where a pawn can capture by en passant, we need to know if the move is legal.
		 * So, we will just create the en passant tile when we figure out the move is legal
		 * or set the tile to null if there are no opportunities for en passant.
		 * 
		 * I should not assign en passant tile to be null where there is an illegal move,
		 * as there may still be a valid en passant move from the previous turn
		 * 
		 * Therefore, I should only assign en passant move to null when a legal move is made
		 * and the move made was not a pawn moving two tiles forward
		 * 
		 * Otherwise, when a legal move is made, I assign en passant tile to the tile
		 * a pawn passed over to move two squares.
		 */
		private static Tile enPassantTile = new Tile();
		private static boolean isBlackInCheck = false;
		private static boolean isWhiteInCheck = false;
		
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
	    	System.out.println(rp.message.toString());
	    	return rp;
	    }
	    
	    
	    
	    //Parses the input. getting the piece that is being moved.
	    
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
	    
	    //Checks if the user chose a tile without a piece
//	    if(emptyTile) {
//	    	rp.message = ReturnPlay.Message.ILLEGAL_MOVE;
//	    	System.out.println(rp.message.toString());
//	    	return rp;
//	    }
	    
	    boolean validMove = didUserPickOwnPiece(pickedPiece);
	    
	    //Checks if the user picked a tile without a piece or with the opponents piece
	    if(!validMove || emptyTile) {
	    	System.out.println("You did not pick your own piece!");
	    	rp.message = ReturnPlay.Message.ILLEGAL_MOVE;
	    	System.out.println(rp.message.toString());
	    	return rp;
	    }
	    
	    
	    
	    //Checks if the move made is valid for the piece picked.
	    char toFileChar = move.charAt(3);
	    int  toRankInt  = Character.getNumericValue(move.charAt(4));_
	    if(pickedPiece.pieceType == ReturnPiece.PieceType.WN || pickedPiece.pieceType == ReturnPiece.PieceType.BN ) {
	    	validMove = checkKnightMove(pickedPiece, toFileChar, toRankInt);
	    }
	    
	    if(pickedPiece.pieceType == ReturnPiece.PieceType.WB || pickedPiece.pieceType == ReturnPiece.PieceType.BB) {
	    	validMove = checkBishopMove(pickedPiece, toFileChar, toRankInt);
	    }
	    
	    if(pickedPiece.pieceType == ReturnPiece.PieceType.WR || pickedPiece.pieceType == ReturnPiece.PieceType.BR) {
	    	validMove = checkRookMove(pickedPiece, toFileChar, toRankInt);
	    }
	    
	    if(pickedPiece.pieceType == ReturnPiece.PieceType.WK || pickedPiece.pieceType == ReturnPiece.PieceType.BK) {
	    	validMove = checkKingMove(pickedPiece, toFileChar, toRankInt);
	    }
	    
	    if(pickedPiece.pieceType == ReturnPiece.PieceType.WQ || pickedPiece.pieceType == ReturnPiece.PieceType.BQ) {
	    	validMove = checkQueenMove(pickedPiece, toFileChar, toRankInt);
	    }
	    
	    if(pickedPiece.pieceType == ReturnPiece.PieceType.WP || pickedPiece.pieceType == ReturnPiece.PieceType.BP) {
	    	validMove = checkPawnMove(pickedPiece, toFileChar, toRankInt);
	    }
	    
	    
	    
	    if(!validMove) {
	    	rp.message = ReturnPlay.Message.ILLEGAL_MOVE;
	    	System.out.println(rp.message.toString());
	    	return rp;
	    }
	    
	    if( (currentTurn == Player.white && isWhiteInCheck) ||
	    	(currentTurn == Player.black && isBlackInCheck) ) {
	    	
	    	rp.message = ReturnPlay.Message.ILLEGAL_MOVE;
	    	return rp;
	    }
	    
	    if(validMove) {
	    	//I would prefer to use charAt and a character, but the enum valueOf function takes Strings
	    	ReturnPiece potentialCapture = null;
	    	potentialCapture = findPieceAt(move.charAt(3), Integer.parseInt(move.substring(4, 5)));
	    	pieces.remove(potentialCapture);
	    	pickedPiece.pieceFile = ReturnPiece.PieceFile.valueOf(move.substring(3, 4));
	    	pickedPiece.pieceRank = Integer.parseInt(move.substring(4, 5));
	    	
		    rp.message = null;
		    if( (currentTurn == Player.white && isBlackInCheck) || 
		    	(currentTurn == Player.black && isWhiteInCheck) ) {
		    	rp.message = ReturnPlay.Message.CHECK;
		    }
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
	        if (king.pieceType.name().charAt(0) == target.pieceType.name().charAt(0)) {
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
	    
	    //Move one square case
	    if (toFile == fromFile && toRank == fromRank + direction) {
	        if (findPieceAt(toFile, toRank) == null) {
	            return true;
	        }
	        return false; 
	    }
	    
	    //Move two squares case
	    if(toFile == fromFile && toRank == 2 * direction + fromRank && fromRank == startRank) {
	    	enPassantTile.file = pawn.pieceFile;
	    	enPassantTile.rank = fromRank + direction;
	    	if(findPieceAt(toFile, toRank) == null && findPieceAt(toFile, toRank - direction) == null) return true;
	    	return false;	
	    }
	   
	    //Capture case
	    if (Math.abs(toFile - fromFile) == 1 && toRank == fromRank + direction) {
	        ReturnPiece target = findPieceAt(toFile, toRank);
	        if (target != null && target.pieceType.name().charAt(0) != pawn.pieceType.name().charAt(0) ||
	        		(toRank == enPassantTile.rank && toFile == enPassantTile.file.name().charAt(0)) ) {
	            return true;
	        }
	        return false; 
	    }
	    
	    try {
	    	assert false;
	    } catch (AssertionError e){
	    	System.out.println("Logic should not have reached here\n" + e.getMessage());
	    }
	    return false;
	}
	/**
	 * This function works by calling if a king is in check. It is up to the user to 
	 * find the king of the correct color and pass that as a parameter.
	 *  
	 * @param pieces
	 * @param king
	 * @return boolean
	 */
	private static boolean isKingInCheck(ArrayList<ReturnPiece> pieces, Player playerColor) {
		ReturnPiece whiteKing = new ReturnPiece();
		for(ReturnPiece piece : pieces) if(piece.pieceType.equals(ReturnPiece.PieceType.WK)) whiteKing = piece;
		ReturnPiece blackKing = new ReturnPiece();
		for(ReturnPiece piece : pieces) if(piece.pieceType.equals(ReturnPiece.PieceType.BK)) blackKing = piece;
		ReturnPiece king = (playerColor == Player.white) ? whiteKing : blackKing; 
		for(ReturnPiece piece : pieces) {
			switch(piece.pieceType) {
				case WP: 
			    	if(checkPawnMove(piece, king.pieceFile.name().charAt(0), king.pieceRank)) return true;
			        break;
			    case WN:
			    	if(checkKnightMove(piece, king.pieceFile.name().charAt(0), king.pieceRank)) return true;
			        break;
			    case WB:
			    	if(checkBishopMove(piece, king.pieceFile.name().charAt(0), king.pieceRank)) return true;
			    	break;
			    case WR:
			    	if(checkRookMove(piece, king.pieceFile.name().charAt(0), king.pieceRank)) return true;
			        break;
			    case WQ:
			    	if(checkQueenMove(piece, king.pieceFile.name().charAt(0), king.pieceRank)) return true;
			        break;
			    case WK:
			    	if(checkKingMove(piece, king.pieceFile.name().charAt(0), king.pieceRank)) return true;
			        break;
			
				default:
					System.out.println("Code logic should not reach here");
					break;
			}
		}
		
		if(king.pieceType == ReturnPiece.PieceType.WK) {
			for( ReturnPiece piece : pieces) {
				switch(piece.pieceType) {
				    case BP: 
				    	if(checkPawnMove(piece, king.pieceFile.name().charAt(0), king.pieceRank)) return true;
				        break;
				    case BN: 
				    	if(checkKnightMove(piece, king.pieceFile.name().charAt(0), king.pieceRank)) return true;
				        break;
				    case BB:
				    	if(checkBishopMove(piece, king.pieceFile.name().charAt(0), king.pieceRank)) return true;
				    	break;
				    case BR:
				    	if(checkRookMove(piece, king.pieceFile.name().charAt(0), king.pieceRank)) return true;
				        break;
				    case BQ:
				    	if(checkQueenMove(piece, king.pieceFile.name().charAt(0), king.pieceRank)) return true;
				        break;
				    case BK:
				    	if(checkKingMove(piece, king.pieceFile.name().charAt(0), king.pieceRank)) return true;
				        break;
					default:
						System.out.println("Code logic should not reach here");
						break;
				}
			}
		}
		
		return false;
	}
}
