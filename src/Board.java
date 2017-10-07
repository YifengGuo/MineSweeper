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
                    System.out.print("? ");
                } else if (board[i][j].hasMine && board[i][j].getRevealed()) {
                    System.out.print("M ");
                    fatal = true;
                } else if (!board[i][j].hasMine && board[i][j].getRevealed()) {
                    int adjacentMines = calculateAdjacentMines(board, i, j);
                    board[i][j].setAdjacentMines(adjacentMines);
                    System.out.print(adjacentMines + " ");
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
        printBoard();
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
