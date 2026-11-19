import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Assets {
    public static final Image IMG_PAWN_WHITE;
    public static final Image IMG_PAWN_BLACK;
    public static final Image IMG_ROOK_WHITE;
    public static final Image IMG_ROOK_BLACK;
    public static final Image IMG_KNIGHT_WHITE;
    public static final Image IMG_KNIGHT_BLACK;
    public static final Image IMG_BISHOP_WHITE;
    public static final Image IMG_BISHOP_BLACK;
    public static final Image IMG_QUEEN_WHITE;
    public static final Image IMG_QUEEN_BLACK;
    public static final Image IMG_KING_WHITE;
    public static final Image IMG_KING_BLACK;

    static {
        try {
            IMG_PAWN_WHITE = ImageIO.read(new File("assets/w_Pawn.png"));
            IMG_PAWN_BLACK = ImageIO.read(new File("assets/b_Pawn.png"));
            IMG_ROOK_WHITE = ImageIO.read(new File("assets/w_Rook.png"));
            IMG_ROOK_BLACK = ImageIO.read(new File("assets/b_Rook.png"));
            IMG_KNIGHT_WHITE = ImageIO.read(new File("assets/w_Knight.png"));
            IMG_KNIGHT_BLACK = ImageIO.read(new File("assets/b_Knight.png"));
            IMG_BISHOP_WHITE = ImageIO.read(new File("assets/w_Bishop.png"));
            IMG_BISHOP_BLACK = ImageIO.read(new File("assets/b_Bishop.png"));
            IMG_QUEEN_WHITE = ImageIO.read(new File("assets/w_Queen.png"));
            IMG_QUEEN_BLACK = ImageIO.read(new File("assets/b_Queen.png"));
            IMG_KING_WHITE = ImageIO.read(new File("assets/w_King.png"));
            IMG_KING_BLACK = ImageIO.read(new File("assets/b_King.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
