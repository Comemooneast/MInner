import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

class Game extends JFrame {

    final String TITLE_GAME = "Minner";
    final String MAKE_FLAG = "F";
    final int BLOCK_SIZE = 30;
    final int FIELD_SIZE = 9;
    final int FIELD_X = 6;
    final int FIELD_Y = 26;
    final int START_LOCATION = 200;
    final int MOUSE_CLICK_LEFT = 1;
    final int MOUSE_CLICK_RIGHT = 3;
    final int NUMBER_MINES = 10;
    final int[] NUMBER_COLORS = {0x0000FF, 0x008000, 0xFF0000, 0x800000, 0x0};
    Cell[][] field = new Cell[FIELD_SIZE][FIELD_SIZE];
    Random random = new Random();
    int numbersOpenedCells;
    boolean winner, mineExplosion;
    int explosionX, explosionY;

    public static void main(String[] args) {
        new Game();
    }

    Game() {
        setTitle(TITLE_GAME);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(START_LOCATION, START_LOCATION, FIELD_SIZE * BLOCK_SIZE + FIELD_X, FIELD_SIZE * BLOCK_SIZE + FIELD_Y);
        setResizable(false);
        Canvas canvas = new Canvas();
        canvas.setBackground(Color.WHITE);
        canvas.addMouseListener((new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int x = e.getX() / BLOCK_SIZE;
                int y = e.getY() / BLOCK_SIZE;
                if (e.getButton() == MOUSE_CLICK_LEFT && !mineExplosion && !winner)
                    if (field[y][x].isNotOpen()) {
                        openCells(x, y);
                        winner = numbersOpenedCells == FIELD_SIZE * FIELD_SIZE - NUMBER_MINES;
                        if (mineExplosion) {
                            explosionX = x;
                            explosionY = y;
                        }

                    }
                if (e.getButton() == MOUSE_CLICK_RIGHT) field[y][x].inverseFlag();
                canvas.repaint();
            }
        }));
        add(BorderLayout.CENTER, canvas);
        setVisible(true);
        initField();
    }

    void openCells(int x, int y) {
        if (x < 0 || x > FIELD_SIZE - 1 || y < 0 || y > FIELD_SIZE - 1) return;
        if (!field[y][x].isNotOpen()) return;
        field[y][x].open();
        if (field[y][x].getCountBomb() > 0 || mineExplosion) return;
        for (int dx = -1; dx < 2; dx++)
            for (int dy = -1; dy < 2; dy++) openCells(x + dx, y + dy);
    }

    void initField() {
        int x, y, countMines = 0;
        for (x = 0; x < FIELD_SIZE; x++)
            for (y = 0; y < FIELD_SIZE; y++)
                field[y][x] = new Cell();
        while (countMines < NUMBER_MINES) {
            do {
                x = random.nextInt(FIELD_SIZE);
                y = random.nextInt(FIELD_SIZE);
            } while (field[y][x].isMined());
            field[y][x].mine();
            countMines++;
        }
        for (x = 0; x < FIELD_SIZE; x++)
            for (y = 0; y < FIELD_SIZE; y++)
                if (!field[y][x].isMined()) {
                    int count = 0;
                    for (int dx = -1; dx < 2; dx++)
                        for (int dy = -1; dy < 2; dy++) {
                            int nX = x + dx;
                            int nY = y + dy;
                            if (nX < 0 || nY < 0 || nX > FIELD_SIZE - 1 || nY > FIELD_SIZE - 1) {
                                nX = x;
                                nY = y;
                            }
                            count +=(field[nY][nX].isMined()) ? 1 : 0;
                        }
                    field[y][x].setCountBomb(count);
                }

    }
    class Cell {
        private int countBombNear;
        private boolean isOpen, isMine, isFlag;

        void open() {
            isOpen = true;
            mineExplosion = isMine;
            if (!isMine) numbersOpenedCells++;
        }
        void mine() { isMine = true; }

        void setCountBomb(int count) { countBombNear = count; }

        int getCountBomb() { return countBombNear; }

        boolean isNotOpen() { return !isOpen; }

        boolean isMined() { return isMine; }

        void inverseFlag() { isFlag = !isFlag; }

        void paintBomb(Graphics g, int x, int y, Color color) {
            g.setColor(color);
            g.fillRect(x*BLOCK_SIZE + 7, y*BLOCK_SIZE + 10, 18, 10);
            g.fillRect(x*BLOCK_SIZE + 11, y*BLOCK_SIZE + 6, 10, 18);
            g.fillRect(x*BLOCK_SIZE + 9, y*BLOCK_SIZE + 8, 14, 14);
            g.setColor(Color.white);
            g.fillRect(x*BLOCK_SIZE + 11, y*BLOCK_SIZE + 10, 4, 4);
        }

        void paintString(Graphics g, String str, int x, int y, Color color) {
            g.setColor(color);
            g.setFont(new Font("", Font.BOLD, BLOCK_SIZE));
            g.drawString(str, x*BLOCK_SIZE + 8, y*BLOCK_SIZE + 26);
        }
        void paint(Graphics g, int x, int y) {
            g.setColor(Color.lightGray);
            g.drawRect(x*BLOCK_SIZE, y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
            if (!isOpen) {
                if ((mineExplosion || winner) && isMine) paintBomb(g, x, y, Color.black);
                else {
                    g.setColor(Color.lightGray);
                    g.fill3DRect(x*BLOCK_SIZE, y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, true);
                    if (isFlag) paintString(g, MAKE_FLAG, x, y, Color.red);
                }
            } else
            if (isMine) paintBomb(g, x, y, mineExplosion? Color.red : Color.black);
            else
            if (countBombNear > 0)
                paintString(g, Integer.toString(countBombNear), x, y, new Color(NUMBER_COLORS[countBombNear - 1]));
        }
    }

    class Canvas extends JPanel {
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for (int x = 0; x < FIELD_SIZE; x++)
                for (int y = 0; y < FIELD_SIZE; y++) field[y][x].paint(g, x, y);
        }
    }



}
