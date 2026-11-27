import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.Set;

public abstract class Piece {
    public enum Faction {
        WHITE,
        BLACK
    }

    protected Faction faction;
    protected Image img_white, img_black;

    public Piece(Faction faction, Image img_white, Image img_black) {
        this.faction = faction;
        this.img_white = img_white;
        this.img_black = img_black;
    }

    /**
     * Draws this piece scaled to fill a square area.
     * The piece doesn't know where it is; the Board tells it where to draw.
     *
     * @param px       pixel x of the top-left corner to draw at
     * @param py       pixel y of the top-left corner to draw at
     * @param size     width/height to draw in pixels
     * @param observer component being painted on, may be null
     *                 since our images are fully loaded by ImageIO.read
     */
    public void draw(Graphics2D g2, int px, int py, int size, ImageObserver observer) {
        Image img = this.faction == Faction.WHITE ? this.img_white : this.img_black;
        g2.drawImage(img, px, py, size, size, observer);
    }

    public abstract Set<Position> getPossibleMoves(Board board, int x, int y);

    /**
     * Returns the squares this piece attacks (could capture on if an enemy were there).
     * For most pieces this is the same as their possible moves; pieces where
     * the two differ (King, Pawn) override this.
     */
    public Set<Position> getAttackedPositions(Board board, int x, int y) {
        return getPossibleMoves(board, x, y);
    }

    public void onMove() {};

    /** Tries to add the given position if the move is possible.
     * Returns the piece at that position
     */
    protected Piece tryAddPos(Board board, Set<Position> positions, int x, int y) {
        if (board.isOutOfBounds(x, y)) return null;

        Piece piece = board.getCell(x, y);
        if (piece == null || piece.faction != faction) {
            positions.add(new Position(x, y));
        }

        return piece;
    }

    /** Scans the board for available moves in a single direction, adding to `positions` along the way */
    protected void scanDirection(Board board, Set<Position> positions, int x, int y, int xDir, int yDir) {
        while (true) {
            x += xDir;
            y += yDir;

            if (board.isOutOfBounds(x, y)) break;

            Piece piece = tryAddPos(board, positions, x, y);

            if (piece != null) break;
        }
    }
}
