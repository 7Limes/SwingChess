import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Set;

public class Board extends JPanel {
    private static final Color BG_COLOR_1 = new Color(225, 225, 225);
    private static final Color BG_COLOR_2 = new Color(200, 200, 200);
    private static final Color CAN_MOVE_COLOR = new Color(150, 225, 255, 128);

    private int size, cellSize;
    private final CapturedPiecesBar whitePieceBar, blackPieceBar;

    private final Piece[][] cells = new Piece[8][8];

    private Piece selectedPiece = null;
    private Position selectedPiecePos = null;

    public Board(int size, CapturedPiecesBar whitePieceBar, CapturedPiecesBar blackPieceBar) {
        resize(size);
        this.size = size;
        this.cellSize = size / 8;

        this.whitePieceBar = whitePieceBar;
        this.blackPieceBar = blackPieceBar;

        this.addMouseListener(new MouseEvents());

        reset();
    }

    /** Resets the board to its starting state. */
    public void reset() {
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

    public void resize(int newSize) {
        Dimension newDim = new Dimension(newSize, newSize);
        this.setPreferredSize(newDim);
        this.setMaximumSize(newDim);
        this.size = newSize;
        this.cellSize = size / 8;
        repaint();
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

    }

    private void drawCheckerboard(Graphics2D g2) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i+j) % 2 == 0) {
                    g2.setColor(BG_COLOR_1);
                }
                else {
                    g2.setColor(BG_COLOR_2);
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
                if (p != null) {
                    p.draw(
                            g2,
                            j*cellSize,
                            i*cellSize,
                            cellSize,
                            this
                    );
                }
            }
        }
    }

    private void drawPossibleMoves(Graphics2D g2) {
        Set<Position> movePositions = selectedPiece.getPossibleMoves(this, selectedPiecePos.x, selectedPiecePos.y);
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

        Set<Position> movePositions = selectedPiece.getPossibleMoves(Board.this, selectedPiecePos.x, selectedPiecePos.y);
        for (Position pos : movePositions) {
            if (pos.x == x && pos.y == y) {
                return true;
            }
        }
        return false;
    }

    /** Tries to move the currently selected piece to the given position.
     *  Returns false if the move is not possible.
     *  */
    private boolean tryMakeMove(int x, int y) {
        if (!isPossibleMove(x, y)) {
            return false;
        }

        cells[selectedPiecePos.y][selectedPiecePos.x] = null;

        Piece capturedPiece = getCell(x, y);
        selectedPiece.onMove();
        cells[y][x] = selectedPiece;

        whitePieceBar.tryAddCapturedPiece(capturedPiece);
        blackPieceBar.tryAddCapturedPiece(capturedPiece);

        selectedPiece = null;
        selectedPiecePos = null;
        repaint();

        return true;
    }

    class MouseEvents implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            int cellX = mouseEvent.getX() / cellSize;
            int cellY = mouseEvent.getY() / cellSize;

            if (tryMakeMove(cellX, cellY)) {
                return;
            }

            Piece newSelectedPiece = getCell(cellX, cellY);
            if (newSelectedPiece != null) {
                IO.println("Selected piece at " + cellX + ", " + cellY);
                selectedPiece = newSelectedPiece;
                selectedPiecePos = new Position(cellX, cellY);
                repaint();
            }
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {

        }
    }
}
