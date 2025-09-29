package chess;

public enum tileState {
	EMPTY("."),
	BLACK_PAWN("p"),
	BLACK_ROOK("r"),
	BLACK_KNIGHT("n"),
	BLACK_BISHOP("b"),
	BLACK_QUEEN("q"),
	BLACK_KING("k"),
	WHITE_PAWN("P"),
	WHITE_ROOK("R"),
	WHITE_KNIGHT("N"),
	WHITE_BISHOP("B"),
	WHITE_QUEEN("Q"),
	WHITE_KING("K");
	
	private final String symbol;
	tileState(String symbol) {
		this.symbol = symbol;
	}
	
	public String getSymbol() {
		return symbol;
	}
}
