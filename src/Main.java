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

    public static void main(String[] args) {
        JFrame mainFrame = new JFrame("Chess");
        mainFrame.setSize(BOARD_START_WIDTH, BOARD_START_WIDTH+BAR_HEIGHT*2);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel columnPanel = createColumnPanel();
        columnPanel.setBackground(BG_COLOR);
        columnPanel.addComponentListener(new ResizeListener());

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.add(createControlsPanel(), BorderLayout.NORTH);
        rootPanel.add(columnPanel, BorderLayout.CENTER);

        mainFrame.add(rootPanel);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private static JPanel createColumnPanel() {
        JPanel columnPanel = new JPanel();
        columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.Y_AXIS));

        blackPieceBar = new CapturedPiecesBar(Piece.Faction.WHITE, BOARD_START_WIDTH, BAR_HEIGHT);
        whitePieceBar = new CapturedPiecesBar(Piece.Faction.BLACK, BOARD_START_WIDTH, BAR_HEIGHT);
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

        JLabel statusLabel = new JLabel("White to move");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(statusLabel.getFont().deriveFont(18f));
        controlsPanel.add(statusLabel, BorderLayout.WEST);
        board.setOnMoveEvent(() -> {
            String newText = board.getTurn() == Piece.Faction.WHITE ? "White to move" : "Black to move";
            statusLabel.setText(newText);
        });

        board.setOnWinEvent(winner -> {
            String newText = switch (winner) {
                case WHITE -> "White Wins!";
                case BLACK -> "Black Wins!";
                case null -> "Stalemate";
            };
            statusLabel.setText(newText);
        });

        JButton restartButton = new JButton("Restart");
        restartButton.setFont(restartButton.getFont().deriveFont(18f));
        restartButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(controlsPanel, "Are you sure you want to restart?", "Confirm Restart", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                board.reset();
                whitePieceBar.reset();
                blackPieceBar.reset();
                statusLabel.setText("White to move");
            }
        });
        controlsPanel.add(restartButton, BorderLayout.EAST);

        return controlsPanel;
    }

    static class ResizeListener implements ComponentListener {
        @Override
        public void componentResized(ComponentEvent componentEvent) {
            Dimension newSize = componentEvent.getComponent().getSize();
            int availableHeight = newSize.height - 2*BAR_HEIGHT;
            int newBoardSize = Math.max(0, Math.min(newSize.width, availableHeight));
            board.resize(newBoardSize);
            blackPieceBar.setBarSize(newSize.width, BAR_HEIGHT);
            whitePieceBar.setBarSize(newSize.width, BAR_HEIGHT);
        }

        @Override
        public void componentMoved(ComponentEvent componentEvent) {}
        @Override
        public void componentShown(ComponentEvent componentEvent) {}
        @Override
        public void componentHidden(ComponentEvent componentEvent) {}
    }
}
