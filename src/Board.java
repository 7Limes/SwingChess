import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Set;

public class Board extends JPanel {
    private static final Color BOARD_COLOR_1 = new Color(225, 225, 225);
    private static final Color BOARD_COLOR_2 = new Color(200, 200, 200);
    private static final Color CAN_MOVE_COLOR = new Color(150, 225, 255, 128);

    private static final double PIECE_ANIM_DURATION = 0.25;

    private Runnable onMoveEvent;  // Event that external sources can hook into

    private int size, cellSize;
    private final CapturedPiecesBar whitePieceBar, blackPieceBar;

    private final Piece[][] cells = new Piece[8][8];

    private Piece selectedPiece = null;
    private Position selectedPiecePos = null;

    private Piece.Faction turn = Piece.Faction.WHITE;

    private final Timer animTimer = new Timer(16, new PieceMoveAnimator());
    private boolean isAnimating = false;
    private long animStartTime = 0;
    private Position animStartPos, animEndPos;
    private Piece animPiece;  // The piece being animated

    public Board(int size, CapturedPiecesBar whitePieceBar, CapturedPiecesBar blackPieceBar) {
        resize(size);

        this.whitePieceBar = whitePieceBar;
        this.blackPieceBar = blackPieceBar;

        this.addMouseListener(new MouseEvents());

        reset();
    }

    /** Resets the board to its starting state. */
    public void reset() {
        stopAnimation();

        // Clear everything first so reset() also works mid-game
        for (Piece[] row : cells) {
            java.util.Arrays.fill(row, null);
        }

        // Black on top (rows 0-1), white on the bottom (rows 6-7)
        placeBackRank(0, Piece.Faction.BLACK);
        placeBackRank(7, Piece.Faction.WHITE);
        for (int col = 0; col < 8; col++) {
            cells[1][col] = new Pawn(Piece.Faction.BLACK);
            cells[6][col] = new Pawn(Piece.Faction.WHITE);
        }

        turn = Piece.Faction.WHITE;

        repaint();
    }

    public boolean isOutOfBounds(int x, int y) {
        return (x < 0 || x >= 8 || y < 0 || y >= 8);
    }

    /** Gets the piece at a specified position. */
    public Piece getCell(int x, int y) {
        if (isOutOfBounds(x, y)) return null;
        return cells[y][x];
    }

    public Piece getCell(Position pos) {
        return getCell(pos.x, pos.y);
    }

    /** Returns the faction whose turn it is */
    public Piece.Faction getTurn() {
        return turn;
    }

    public void resize(int newSize) {
        // Snap to a multiple of 8 so the cells exactly fill the panel
        newSize -= newSize % 8;
        Dimension newDim = new Dimension(newSize, newSize);
        this.setPreferredSize(newDim);
        this.setMaximumSize(newDim);
        this.size = newSize;
        this.cellSize = size / 8;
        // Changing preferred/max size doesn't trigger a layout pass on its own
        revalidate();
        repaint();
    }

    public void setOnMoveEvent(Runnable func) {
        this.onMoveEvent = func;
    }

    /** Places rook, knight, bishop, queen, king, bishop, knight, rook across a row. */
    private void placeBackRank(int row, Piece.Faction faction) {
        cells[row][0] = new Rook(faction);
        cells[row][1] = new Knight(faction);
        cells[row][2] = new Bishop(faction);
        cells[row][3] = new Queen(faction);
        cells[row][4] = new King(faction);
        cells[row][5] = new Bishop(faction);
        cells[row][6] = new Knight(faction);
        cells[row][7] = new Rook(faction);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        drawCheckerboard(g2);

        if (selectedPiece != null) {
            drawPossibleMoves(g2);
        }

        drawPieces(g2);

        // Flush the graphics pipeline to prevent stuttering
        Toolkit.getDefaultToolkit().sync();
    }

    private void drawCheckerboard(Graphics2D g2) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i+j) % 2 == 0) {
                    g2.setColor(BOARD_COLOR_1);
                }
                else {
                    g2.setColor(BOARD_COLOR_2);
                }

                g2.fillRect(
                        j*cellSize,
                        i*cellSize,
                        cellSize, cellSize
                );
            }
        }
    }

    private void drawPieces(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = cells[i][j];
                if (p == null || (isAnimating && p == animPiece)) continue;
                p.draw(g2, j * cellSize, i * cellSize, cellSize, this);
            }
        }

        // Draw the moving piece last so it slides over the other pieces
        if (isAnimating) {
            drawAnimatedPiece(g2);
        }
    }

    /** Draws the animating piece at its current interpolated position, based on elapsed time. */
    private void drawAnimatedPiece(Graphics2D g2) {
        double elapsed = (System.nanoTime() - animStartTime) / 1e9;
        double t = Math.min(1.0, elapsed / PIECE_ANIM_DURATION);
        double easedT = easeInOutCubic(t);

        double px = lerp(animStartPos.x * cellSize, animEndPos.x * cellSize, easedT);
        double py = lerp(animStartPos.y * cellSize, animEndPos.y * cellSize, easedT);

        animPiece.draw(g2, (int) Math.round(px), (int) Math.round(py), cellSize, this);
    }

    private static double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    private static double easeInOutCubic(double x) {
        return x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2;
    }

    private void stopAnimation() {
        animTimer.stop();
        isAnimating = false;
        animPiece = null;
    }

    /** Returns true if any piece of `byFaction` attacks the given position. */
    public boolean isSquareAttacked(Position pos, Piece.Faction byFaction) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = cells[i][j];
                if (piece != null && piece.faction == byFaction
                        && piece.getAttackedPositions(this, j, i).contains(pos)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Returns the position of the given faction's king, or null if it isn't on the board. */
    private Position findKing(Piece.Faction faction) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = cells[i][j];
                if (piece instanceof King && piece.faction == faction) {
                    return new Position(j, i);
                }
            }
        }
        return null;
    }

    /** Returns true if the given faction's king is currently attacked. */
    public boolean isInCheck(Piece.Faction faction) {
        Position kingPos = findKing(faction);
        if (kingPos == null) return false;
        Piece.Faction enemy = faction == Piece.Faction.WHITE ? Piece.Faction.BLACK : Piece.Faction.WHITE;
        return isSquareAttacked(kingPos, enemy);
    }

    /**
     * Returns the moves for the piece at (x, y) that don't leave its own king in check.
     * Each candidate move is played on the board, tested, then undone.
     */
    public Set<Position> getLegalMoves(int x, int y) {
        Piece piece = getCell(x, y);
        if (piece == null) return Set.of();

        Set<Position> legalMoves = new HashSet<>();
        for (Position move : piece.getPossibleMoves(this, x, y)) {
            // Simulate (onMove() is deliberately not called, so pawns keep their first-move state)
            Piece captured = cells[move.y][move.x];
            cells[move.y][move.x] = piece;
            cells[y][x] = null;

            if (!isInCheck(piece.faction)) {
                legalMoves.add(move);
            }

            // Undo
            cells[y][x] = piece;
            cells[move.y][move.x] = captured;
        }
        return legalMoves;
    }

    private void drawPossibleMoves(Graphics2D g2) {
        Set<Position> movePositions = getLegalMoves(selectedPiecePos.x, selectedPiecePos.y);
        g2.setColor(CAN_MOVE_COLOR);

        for (Position pos : movePositions) {
            g2.fillRect(pos.x*cellSize, pos.y*cellSize, cellSize, cellSize);
        }
    }

    /** Returns true if the currently selected piece can be moved to the given position. */
    private boolean isPossibleMove(int x, int y) {
        if (selectedPiece == null) {
            return false;
        }

        return getLegalMoves(selectedPiecePos.x, selectedPiecePos.y).contains(new Position(x, y));
    }

    /** Tries to select a piece at the given position. */
    private void trySelectPiece(int x, int y) {
        Piece newSelectedPiece = getCell(x, y);
        if (newSelectedPiece != null && newSelectedPiece.faction == turn) {
            IO.println("Selected piece at " + x + ", " + y);
            selectedPiece = newSelectedPiece;
            selectedPiecePos = new Position(x, y);
            repaint();
        }
    }

    /** Tries to move the currently selected piece to the given position.
     *  Returns false if the move is not possible.
     *  */
    private void tryMakeMove(int x, int y) {
        if (!isPossibleMove(x, y)) {
            return;
        }

        cells[selectedPiecePos.y][selectedPiecePos.x] = null;

        Piece capturedPiece = getCell(x, y);
        selectedPiece.onMove();
        cells[y][x] = selectedPiece;

        whitePieceBar.tryAddCapturedPiece(capturedPiece);
        blackPieceBar.tryAddCapturedPiece(capturedPiece);

        // Setup animation
        animPiece = selectedPiece;
        animStartPos = selectedPiecePos;
        animEndPos = new Position(x, y);
        animStartTime = System.nanoTime();
        isAnimating = true;
        animTimer.start();

        selectedPiece = null;
        selectedPiecePos = null;
        repaint();

        // Swap turns
        turn = turn == Piece.Faction.WHITE ? Piece.Faction.BLACK : Piece.Faction.WHITE;

        // Run external event
        if (onMoveEvent != null) onMoveEvent.run();
    }

    class MouseEvents implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {

        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            if (!isAnimating) {
                int cellX = mouseEvent.getX() / cellSize;
                int cellY = mouseEvent.getY() / cellSize;
                trySelectPiece(cellX, cellY);
            }
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            if (!isAnimating) {
                int cellX = mouseEvent.getX() / cellSize;
                int cellY = mouseEvent.getY() / cellSize;
                tryMakeMove(cellX, cellY);
            }
        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {

        }
    }

    class PieceMoveAnimator implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            double elapsed = (System.nanoTime() - animStartTime) / 1e9;
            if (elapsed >= PIECE_ANIM_DURATION) {
                stopAnimation();
            }

            repaint();
        }
    }
}
