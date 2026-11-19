import java.util.HashSet;
import java.util.Set;

public class Queen extends Piece {
    public Queen(Faction faction) {
        super(faction, Assets.IMG_QUEEN_WHITE, Assets.IMG_QUEEN_BLACK);
    }

    @Override
    public Set<Position> getPossibleMoves(Board board, int x, int y) {
        Set<Position> positions = new HashSet<>();

        scanDirection(board, positions, x, y, -1, 0);
        scanDirection(board, positions, x, y, 1, 0);
        scanDirection(board, positions, x, y, 0, -1);
        scanDirection(board, positions, x, y, 0, 1);
        scanDirection(board, positions, x, y, -1, -1);
        scanDirection(board, positions, x, y, 1, -1);
        scanDirection(board, positions, x, y, -1, 1);
        scanDirection(board, positions, x, y, 1, 1);

        return positions;
    }
}
