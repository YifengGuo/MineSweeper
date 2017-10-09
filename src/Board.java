public class Board {
    private int width;
    private int len;
    public Entry[][] board;
    public boolean fatal = false; // record if mine has revealed

    public Board(int width, int len) {
        this.width = width;
        this.len = len;
        board = new Entry[width][len];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = new Entry();
            }
        }
    }

    public void printBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (!board[i][j].getRevealed()) {
                    board[i][j].setClue("?");
                    if (board[i][j].getClear()) {
                        board[i][j].setClue("C");
                    }
                    System.out.print(board[i][j].getClue() + " ");
                } else if (board[i][j].hasMine && board[i][j].getRevealed()) {
                    board[i][j].setClue("M");
                    System.out.print(board[i][j].getClue() + " ");
                    fatal = true;
                } else if (!board[i][j].hasMine && board[i][j].getRevealed()) {
                    int adjacentMines = calculateAdjacentMines(board, i, j);
                    board[i][j].setAdjacentMines(adjacentMines);
                    board[i][j].setClue(String.valueOf(adjacentMines));
                    System.out.print(board[i][j].getClue() + " ");
                    // setCellClear(board, i, j);
                } else {
                    continue;
                }
            }
            System.out.println();
        }
        System.out.println();
        if (fatal) {
            System.out.println("Meet mine, GAME OVER!");
            return;
        }
    }

    private int calculateAdjacentMines(Entry[][] board, int i, int j) {
        int adjacentMines = 0;
        // top
        if (i - 1 >= 0 && board[i - 1][j].hasMine) {
            adjacentMines++;
        }
        // bottom
        if (i + 1 < board.length && board[i + 1][j].hasMine) {
            adjacentMines++;
        }
        // left
        if (j - 1 >= 0 && board[i][j - 1].hasMine) {
            adjacentMines++;
        }
        // right
        if (j + 1 < board[0].length && board[i][j + 1].hasMine) {
            adjacentMines++;
        }
        // top-left
        if (i - 1 >= 0 && j - 1 >= 0 && board[i - 1][j - 1].hasMine) {
            adjacentMines++;
        }
        // top-right
        if (i - 1 >= 0 && j + 1 < board[0].length && board[i - 1][j + 1].hasMine) {
            adjacentMines++;
        }
        // bottom-left
        if (i + 1 < board.length && j - 1 >= 0 && board[i + 1][j - 1].hasMine) {
            adjacentMines++;
        }
        // bottom-right
        if (i + 1 < board.length && j + 1 < board[0].length && board[i + 1][j + 1].hasMine) {
            adjacentMines++;
        }
        return adjacentMines;
    }

    public void revealEntry(int i, int j) {
        board[i][j].setRevealed();
        setCellClear(board, i, j);
        printBoard();
    }

    public void setCellClear(Entry[][] board, int i, int j) {
        board[i][j].adjacentMines = calculateAdjacentMines(board, i, j);
        if (!board[i][j].hasMine && board[i][j].adjacentMines == 0) {
            // top
            if (i - 1 >= 0 && !board[i - 1][j].getRevealed()) {
                // board[i - 1][j].setClue("C");
                board[i - 1][j].setClear();
            }
            // bottom
            if (i + 1 < board.length && !board[i + 1][j].getRevealed()) {
                // board[i + 1][j].setClue("C");
                board[i + 1][j].setClear();
            }
            // left
            if (j - 1 >= 0 && !board[i][j - 1].getRevealed()) {
                // board[i][j - 1].setClue("C");
                board[i][j - 1].setClear();
            }
            // right
            if (j + 1 < board[0].length && !board[i][j + 1].getRevealed()) {
                // board[i][j + 1].setClue("C");
                board[i][j + 1].setClear();
            }
            // top-left
            if (i - 1 >= 0 && j - 1 >= 0 && !board[i - 1][j - 1].getRevealed()) {
                // board[i - 1][j - 1].setClue("C");
                board[i - 1][j - 1].setClear();
            }
            // top-right
            if (i - 1 >= 0 && j + 1 < board[0].length && !board[i - 1][j + 1].getRevealed()) {
                // board[i - 1][j + 1].setClue("C");
                board[i - 1][j + 1].setClear();
            }
            // bottom-left
            if (i + 1 < board.length && j - 1 >= 0 && !board[i + 1][j - 1].getRevealed()) {
                // board[i + 1][j - 1].setClue("C");
                board[i + 1][j - 1].setClear();
            }
            // bottom-right
            if (i + 1 < board.length && j + 1 < board[0].length && !board[i + 1][j + 1].getRevealed()) {
                // board[i + 1][j + 1].setClue("C");
                board[i + 1][j + 1].setClear();
            }
        }
    }

    /**
     * print whole entry[][] with all info shown
     */
    public void cheat() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j].hasMine) {
                    System.out.print("M ");
                } else {
                    int mines = calculateAdjacentMines(board, i, j);
                    System.out.print(mines + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

//    public static void main(String[] args) {
//        Board board = new Board(5, 5);
//        board.printBoard();
//        board.revealEntry(0,0);
//        board.revealEntry(0,1);
//        board.revealEntry(0,2);
//        board.revealEntry(0,3);
//    }
}
