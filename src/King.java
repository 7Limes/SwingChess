import java.util.HashSet;
import java.util.Set;

public class King extends Piece {
    public King(Faction faction) {
        super(faction, Assets.IMG_KING_WHITE, Assets.IMG_KING_BLACK);
    }

    private Set<Position> getAttackedPositions(Board board) {
        Set<Position> attackedPositions = new HashSet<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board.getCell(j, i);
                if (piece == null) continue;
                if (piece instanceof King) continue;  // TODO: don't do this

                if (piece.faction != this.faction) {
                    Set<Position> pieceMoves = piece.getPossibleMoves(board, j, i);
                    attackedPositions.addAll(pieceMoves);
                }
            }
        }

        return attackedPositions;
    }

    private void tryAddSafePos(Board board, Set<Position> positions, Set<Position> attackedPositions, int x, int y) {
        if (!attackedPositions.contains(new Position(x, y))) {
            tryAddPos(board, positions, x, y);
        }
    }

    @Override
    public Set<Position> getPossibleMoves(Board board, int x, int y) {
        Set<Position> positions = new HashSet<>();
        Set<Position> attackedPositions = getAttackedPositions(board);

        tryAddSafePos(board, positions, attackedPositions, x-1, y);
        tryAddSafePos(board, positions, attackedPositions, x+1, y);
        tryAddSafePos(board, positions, attackedPositions, x, y-1);
        tryAddSafePos(board, positions, attackedPositions, x, y+1);
        tryAddSafePos(board, positions, attackedPositions, x-1, y-1);
        tryAddSafePos(board, positions, attackedPositions, x+1, y-1);
        tryAddSafePos(board, positions, attackedPositions, x-1, y+1);
        tryAddSafePos(board, positions, attackedPositions, x+1, y+1);

        return positions;
    }
}
