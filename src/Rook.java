import java.util.HashSet;
import java.util.Set;

public class Rook extends Piece {
    public Rook(Faction faction) {
        super(faction, Assets.IMG_ROOK_WHITE, Assets.IMG_ROOK_BLACK);
    }

    @Override
    public Set<Position> getPossibleMoves(Board board, int x, int y) {
        Set<Position> positions = new HashSet<>();

        scanDirection(board, positions, x, y, -1, 0);
        scanDirection(board, positions, x, y, 1, 0);
        scanDirection(board, positions, x, y, 0, -1);
        scanDirection(board, positions, x, y, 0, 1);

        return positions;
    }
}
