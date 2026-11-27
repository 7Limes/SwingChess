import java.util.HashSet;
import java.util.Set;

public class Pawn extends Piece {
    private boolean isFirstMove = true;

    public Pawn(Faction faction) {
        super(faction, Assets.IMG_PAWN_WHITE, Assets.IMG_PAWN_BLACK);
    }

    @Override
    public Set<Position> getPossibleMoves(Board board, int x, int y) {
        Set<Position> positions = new HashSet<>();

        int yDir = faction == Faction.WHITE ? -1 : 1;
        Position front = new Position(x, y+yDir);
        Piece frontPiece = board.getCell(front);

        if (isFirstMove) {
            // Can move 2 spaces on first move
            Position front2 = new Position(x, y+yDir*2);
            if (frontPiece == null && board.getCell(front2) == null) {
                positions.add(front2);
            }
        }

        // Capture opponent pieces
        Position frontLeft = new Position(x-1, y+yDir);
        Position frontRight = new Position(x+1, y+yDir);
        Piece frontLeftPiece = board.getCell(frontLeft);
        Piece frontRightPiece = board.getCell(frontRight);

        if (frontLeftPiece != null && frontLeftPiece.faction != faction)
            positions.add(frontLeft);
        if (frontRightPiece != null && frontRightPiece.faction != faction)
            positions.add(frontRight);

        // Move forward
        if (frontPiece == null) {
            positions.add(front);
        }

        return positions;
    }

    @Override
    public Set<Position> getAttackedPositions(Board board, int x, int y) {
        // Pawns attack diagonally forward, whether or not anything is there
        Set<Position> positions = new HashSet<>();
        int yDir = faction == Faction.WHITE ? -1 : 1;

        if (!board.isOutOfBounds(x-1, y+yDir)) positions.add(new Position(x-1, y+yDir));
        if (!board.isOutOfBounds(x+1, y+yDir)) positions.add(new Position(x+1, y+yDir));

        return positions;
    }

    @Override
    public void onMove() {
        isFirstMove = false;
    }
}
