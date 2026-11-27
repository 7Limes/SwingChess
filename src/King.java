import java.util.HashSet;
import java.util.Set;

public class King extends Piece {
    private static final int[][] OFFSETS = {
            {-1, -1}, {0, -1}, {1, -1},
            {-1,  0},          {1,  0},
            {-1,  1}, {0,  1}, {1,  1},
    };

    public King(Faction faction) {
        super(faction, Assets.IMG_KING_WHITE, Assets.IMG_KING_BLACK);
    }

    /** A king attacks its 8 neighbors, whether or not they're occupied. */
    @Override
    public Set<Position> getAttackedPositions(Board board, int x, int y) {
        Set<Position> positions = new HashSet<>();
        for (int[] o : OFFSETS) {
            if (!board.isOutOfBounds(x + o[0], y + o[1])) {
                positions.add(new Position(x + o[0], y + o[1]));
            }
        }
        return positions;
    }

    /** Squares the king can reach, ignoring check. Board.getLegalMoves filters out unsafe ones. */
    @Override
    public Set<Position> getPossibleMoves(Board board, int x, int y) {
        Set<Position> positions = new HashSet<>();
        for (int[] o : OFFSETS) {
            tryAddPos(board, positions, x + o[0], y + o[1]);
        }
        return positions;
    }
}
