import java.util.HashSet;
import java.util.Set;

public class Knight extends Piece {
    public Knight(Faction faction) {
        super(faction, Assets.IMG_KNIGHT_WHITE, Assets.IMG_KNIGHT_BLACK);
    }

    @Override
    public Set<Position> getPossibleMoves(Board board, int x, int y) {
        Set<Position> positions = new HashSet<>();

        tryAddPos(board, positions, x+1, y+2);
        tryAddPos(board, positions, x+2, y+1);
        tryAddPos(board, positions, x+1, y-2);
        tryAddPos(board, positions, x+2, y-1);
        tryAddPos(board, positions, x-1, y+2);
        tryAddPos(board, positions, x-2, y+1);
        tryAddPos(board, positions, x-1, y-2);
        tryAddPos(board, positions, x-2, y-1);

        return positions;
    }
}
