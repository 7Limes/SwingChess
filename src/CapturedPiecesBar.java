import javax.swing.*;
import java.awt.*;

/**
 * Draws captured pieces in a horizontal bar.
 */
public class CapturedPiecesBar extends JPanel {
    private static final Color BG_COLOR = new Color(70, 70, 70);
    private static final int PADDING = 5;
    private static final int BORDER_THICKNESS = 3;

    private final Piece.Faction displayedFaction;
    private final int imageSize;
    private final boolean borderOnBottom;

    private int capturedPawns = 0;
    private int capturedRooks = 0;
    private int capturedBishops = 0;
    private int capturedKnights = 0;
    private int capturedQueens = 0;

    private final Image pawnImage;
    private final Image rookImage;
    private final Image bishopImage;
    private final Image knightImage;
    private final Image queenImage;

    public CapturedPiecesBar(Piece.Faction displayedFaction, int width, int height, boolean borderOnBottom) {
        this.displayedFaction = displayedFaction;
        this.borderOnBottom = borderOnBottom;

        this.setPreferredSize(new Dimension(width, height));
        resize(width, height);

        this.imageSize = height - 2*PADDING;

        if (displayedFaction == Piece.Faction.WHITE) {
            pawnImage = Assets.IMG_PAWN_WHITE;
            rookImage = Assets.IMG_ROOK_WHITE;
            bishopImage = Assets.IMG_BISHOP_WHITE;
            knightImage = Assets.IMG_KNIGHT_WHITE;
            queenImage = Assets.IMG_QUEEN_WHITE;
        }
        else {
            pawnImage = Assets.IMG_PAWN_BLACK;
            rookImage = Assets.IMG_ROOK_BLACK;
            bishopImage = Assets.IMG_BISHOP_BLACK;
            knightImage = Assets.IMG_KNIGHT_BLACK;
            queenImage = Assets.IMG_QUEEN_BLACK;
        }
    }

    public void tryAddCapturedPiece(Piece piece) {
        if (piece == null) return;
        if (piece.faction != displayedFaction) return;

        if (piece instanceof Pawn) capturedPawns++;
        else if (piece instanceof Rook) capturedRooks++;
        else if (piece instanceof Bishop) capturedBishops++;
        else if (piece instanceof Knight) capturedKnights++;
        else if (piece instanceof Queen) capturedQueens++;

        repaint();
    }

    public void resize(int newWidth, int newHeight) {
        Dimension newSize = new Dimension(newWidth, newHeight);
        this.setPreferredSize(newSize);
        this.setMaximumSize(newSize);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(BG_COLOR);
        g2.fillRect(0, 0, getWidth(), getHeight());

        drawPieces(g2);

        g2.setColor(Color.BLACK);
        if (borderOnBottom) {
            g2.fillRect(0, getHeight()-BORDER_THICKNESS, getWidth(), BORDER_THICKNESS);
        }
        else {
            g2.fillRect(0, 0, getWidth(), BORDER_THICKNESS);
        }
    }

    private int drawPieceRow(Graphics2D g2, Image img, int count, int drawX) {
        for (int i = 0; i < count; i++) {
            g2.drawImage(img, drawX, PADDING, imageSize, imageSize, this);
            drawX += imageSize/3;
        }

        if (count > 0) {
            drawX += imageSize*3/4;
        }
        return drawX;
    }

    private void drawPieces(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int drawX = PADDING;

        drawX = drawPieceRow(g2, queenImage, capturedQueens, drawX);
        drawX = drawPieceRow(g2, knightImage, capturedKnights, drawX);
        drawX = drawPieceRow(g2, bishopImage, capturedBishops, drawX);
        drawX = drawPieceRow(g2, rookImage, capturedRooks, drawX);
        drawPieceRow(g2, pawnImage, capturedPawns, drawX);
    }
}
