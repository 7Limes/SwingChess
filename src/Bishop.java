import java.util.HashSet;
import java.util.Set;

public class Bishop extends Piece {
    public Bishop(Faction faction) {
        super(faction, Assets.IMG_BISHOP_WHITE, Assets.IMG_BISHOP_BLACK);
    }

    @Override
    public Set<Position> getPossibleMoves(Board board, int x, int y) {
        Set<Position> positions = new HashSet<>();

        scanDirection(board, positions, x, y, -1, -1);
        scanDirection(board, positions, x, y, 1, -1);
        scanDirection(board, positions, x, y, -1, 1);
        scanDirection(board, positions, x, y, 1, 1);

        return positions;
    }
}
