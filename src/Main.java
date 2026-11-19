import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class Main {
    private static final int BOARD_START_WIDTH = 800;
    private static final int BAR_HEIGHT = 50;

    private static CapturedPiecesBar blackPieceBar;
    private static CapturedPiecesBar whitePieceBar;
    private static Board board;

    public static void main(String[] args) {
        JFrame mainFrame = new JFrame("Chess");
        mainFrame.setSize(BOARD_START_WIDTH, BOARD_START_WIDTH+BAR_HEIGHT*2);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.addComponentListener(new ResizeListener());

        JPanel columnPanel = createColumnPanel();

        mainFrame.add(columnPanel);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private static JPanel createColumnPanel() {
        JPanel columnPanel = new JPanel();
        columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.Y_AXIS));

        blackPieceBar = new CapturedPiecesBar(Piece.Faction.WHITE, BOARD_START_WIDTH, BAR_HEIGHT, true);
        whitePieceBar = new CapturedPiecesBar(Piece.Faction.BLACK, BOARD_START_WIDTH, BAR_HEIGHT, false);
        board = new Board(BOARD_START_WIDTH, blackPieceBar, whitePieceBar);
        board.setAlignmentX(Component.CENTER_ALIGNMENT);

        columnPanel.add(blackPieceBar);
        columnPanel.add(board);
        columnPanel.add(whitePieceBar);
        return columnPanel;
    }

    static class ResizeListener implements ComponentListener {
        @Override
        public void componentResized(ComponentEvent componentEvent) {
            Dimension newSize = componentEvent.getComponent().getSize();
            int newBoardSize = Math.min(newSize.width, newSize.height-3*BAR_HEIGHT);
            board.resize(newBoardSize);
            blackPieceBar.resize(newSize.width, BAR_HEIGHT);
            whitePieceBar.resize(newSize.width, BAR_HEIGHT);
        }

        @Override
        public void componentMoved(ComponentEvent componentEvent) {}
        @Override
        public void componentShown(ComponentEvent componentEvent) {}
        @Override
        public void componentHidden(ComponentEvent componentEvent) {}
    }
}
