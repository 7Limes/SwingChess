import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class Main {
    private static final int BOARD_START_WIDTH = 800;
    private static final int BAR_HEIGHT = 50;

    private static final Color BG_COLOR = new Color(55, 55, 55);

    private static CapturedPiecesBar blackPieceBar;
    private static CapturedPiecesBar whitePieceBar;
    private static Board board;

    private static JLabel statusLabel;
    private static JButton restartButton;

    public static void main(String[] args) {
        JFrame mainFrame = new JFrame("Chess");
        mainFrame.setSize(BOARD_START_WIDTH, BOARD_START_WIDTH+BAR_HEIGHT*2);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.addComponentListener(new ResizeListener());

        JPanel columnPanel = createColumnPanel();
        columnPanel.setBackground(BG_COLOR);

        mainFrame.add(columnPanel);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private static JPanel createColumnPanel() {
        JPanel columnPanel = new JPanel();
        columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.Y_AXIS));

        JPanel controlsPanel = createControlsPanel();
        columnPanel.add(controlsPanel);

        blackPieceBar = new CapturedPiecesBar(Piece.Faction.WHITE, BOARD_START_WIDTH, BAR_HEIGHT, true);
        whitePieceBar = new CapturedPiecesBar(Piece.Faction.BLACK, BOARD_START_WIDTH, BAR_HEIGHT, false);
        board = new Board(BOARD_START_WIDTH, blackPieceBar, whitePieceBar);
        board.setAlignmentX(Component.CENTER_ALIGNMENT);

        columnPanel.add(Box.createVerticalGlue());
        columnPanel.add(blackPieceBar);
        columnPanel.add(board);
        columnPanel.add(whitePieceBar);
        columnPanel.add(Box.createVerticalGlue());
        return columnPanel;
    }

    private static JPanel createControlsPanel() {
        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.setBackground(BG_COLOR);
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        statusLabel = new JLabel("White to Move");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(statusLabel.getFont().deriveFont(18f));
        controlsPanel.add(statusLabel, BorderLayout.WEST);

        restartButton = new JButton("Restart");
        restartButton.setFont(restartButton.getFont().deriveFont(18f));
        controlsPanel.add(restartButton, BorderLayout.EAST);

        // Stop the vertical BoxLayout in columnPanel from stretching this panel taller
        controlsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, controlsPanel.getPreferredSize().height));

        return controlsPanel;
    }

    static class ResizeListener implements ComponentListener {
        @Override
        public void componentResized(ComponentEvent componentEvent) {
            JFrame frame = (JFrame) componentEvent.getComponent();
            Dimension newSize = frame.getContentPane().getSize();
            int newBoardSize = Math.min(newSize.width, newSize.height-2*BAR_HEIGHT);
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
