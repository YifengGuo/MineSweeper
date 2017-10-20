import java.util.ArrayList;
import java.util.List;
/**
 * Created by guoyifeng on 10/6/17.
 */
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
                board[i][j] = new Entry(i, j);
            }
        }
    }

    /**
     * print current game map based on known info
     */
    public void printBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (!board[i][j].getRevealed()) {
                    if (board[i][j].getClear()) {
                        board[i][j].setClue("C");
                    } else if(guaranteeMine(board, i, j)) {
                        board[i][j].setClue("X");
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

    /**
     * return number of mines among adjacent entries
     * @param board
     * @param i
     * @param j
     * @return
     */
    private int calculateAdjacentMines(Entry[][] board, int i, int j) {
        int adjacentMines = 0;
        List<Entry> adjacentEntries = getAdjacentEntries(board, i, j);
        for (Entry e : adjacentEntries) {
            if (e.hasMine) {
                adjacentMines++;
            }
        }
        return adjacentMines;
    }

    /**
     * reveal an entry
     * @param i
     * @param j
     */
    public void revealEntry(int i, int j) {
        board[i][j].setRevealed();
        setCellClear(board, i, j);
        for (int a = 0; a < board.length; a++) {
            for (int b = 0; b < board[0].length; b++) {
                if (!board[a][b].getRevealed()) {
                    if (guaranteeMine(board, a, b)) {
                        board[a][b].setClue("X");
                    }
                }
            }
        }

        for (int a = 0; a < board.length; a++) {
            for (int b = 0; b < board[0].length; b++) {
                if (board[a][b].getRevealed()) {
                    guaranteeClearEntries(board, a, b);
                }
            }
        }

        for (int a = 0; a < board.length; a++) {
            for (int b = 0; b < board[0].length; b++) {
                if (board[a][b].getRevealed()) {
                    detectMineByClearEntries(board, a, b);
                }
            }
        }
        printBoard();
    }

    /**
     * if current entry's number of adjacent mines is 0
     * then all the adjacent unrevealed entries are clear
     * @param board
     * @param i
     * @param j
     */
    public void setCellClear(Entry[][] board, int i, int j) {
        board[i][j].adjacentMines = calculateAdjacentMines(board, i, j);
        List<Entry> adjacentEntries = getAdjacentEntries(board, i, j);
        if (!board[i][j].hasMine && board[i][j].adjacentMines == 0) {
            for (Entry e : adjacentEntries) {
                if (!e.getRevealed()) {
                    e.setClear();
                }
            }
        }
    }

    /**
     * if current entry's adjacent entries are all revealed and its
     * number of adjacent mines is not zero, then the current
     * entry must be a mine, set its clue "X"
     * @param board
     * @param i
     * @param j
     * @return
     */
    public boolean guaranteeMine(Entry[][] board, int i, int j) {
        if (!board[i][j].getRevealed()) {
            if (adjacentNoZero(board, i, j)) {
                return true;
            }
        }
        return false;
    }

    /**
     * determine if current entry's adjacent entries are all revealed and
     * none of them is 0 regarding as number of adjacent mines
     * @param board
     * @param i
     * @param j
     * @return
     */
    public boolean adjacentNoZero(Entry[][] board, int i, int j) {
        boolean flag = true;
        List<Entry> adjacentEntries = getAdjacentEntries(board, i, j);
        for (Entry e : adjacentEntries) {
            if (e.getRevealed()) {
                if (e.adjacentMines == 0) {
                    flag = false;
                }
            } else {
                flag = false;
            }
//            if (e.getRevealed() && e.adjacentMines == 0) {
//                flag = false;
//            }
        }
        return flag;
    }

    /**
     * get board[i][j] all adjacent Entry and store in a List
     * @param board
     * @param i
     * @param j
     * @return
     */
    public List<Entry> getAdjacentEntries(Entry[][] board, int i, int j) {
        List<Entry> adjacentEntries = new ArrayList<>();
        // top
        if (i - 1 >= 0) {
            adjacentEntries.add(board[i - 1][j]);
        }
        // bottom
        if (i + 1 < board.length) {
            adjacentEntries.add(board[i + 1][j]);
        }
        // left
        if (j - 1 >= 0) {
            adjacentEntries.add(board[i][j - 1]);
        }
        // right
        if (j + 1 < board[0].length) {
            adjacentEntries.add(board[i][j + 1]);
        }
        // top-left
        if (i - 1 >= 0 && j - 1 >= 0) {
            adjacentEntries.add(board[i - 1][j - 1]);
        }
        // top-right
        if (i - 1 >= 0 && j + 1 < board[0].length) {
            adjacentEntries.add(board[i - 1][j + 1]);
        }
        // bottom-left
        if (i + 1 < board.length && j - 1 >= 0) {
            adjacentEntries.add(board[i + 1][j - 1]);
        }
        // bottom-right
        if (i + 1 < board.length && j + 1 < board[0].length) {
            adjacentEntries.add(board[i + 1][j + 1]);
        }
        return adjacentEntries;
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

    /**
     * if the number of known mines is equal or greater than number of practical adjacent mines
     * we can guarantee that all the other adjacent entries with ? clue is clear
     * @param board
     * @param i
     * @param j
     */
    public void guaranteeClearEntries(Entry[][] board, int i, int j) {
        if (board[i][j].getRevealed()) {
            List<Entry> adjacentEntries = getAdjacentEntries(board, i, j); // get adjacent entries
            int minesNum = calculateAdjacentMines(board, i, j);

            int knownMinesNum = 0;
            for (Entry e : adjacentEntries) {
                if (!e.getRevealed() && e.getClue().equals("X")) {
                    knownMinesNum++;
                }
            }
            if (knownMinesNum >= minesNum) {
                for (Entry e : adjacentEntries) {
                    if (!e.getRevealed() && e.getClue().equals("?")) {
                        e.setClear();
                    }
                }
            }
        }
        // printBoard();
//        List<Entry> adjacentEntries = getAdjacentEntries(board, i, j); // get adjacent entries
//        int minesNum = Integer.MAX_VALUE;
//        if (board[i][j].getRevealed()) {
//            minesNum = calculateAdjacentMines(board, i, j);
//        }
//        int knownMinesNum = 0;
//        for (Entry e : adjacentEntries) {
//            if (!e.getRevealed() && e.getClue().equals("X")) {
//                knownMinesNum++;
//            }
//        }
//        // if the number of known mines is equal or greater than number of practical adjacent mines
//        // we can guarantee that all the other adjacent entries with ? clue is clear
//        if (board[i][j].getRevealed() && knownMinesNum >= minesNum) {
//            for (Entry e : adjacentEntries) {
//                if (!e.getRevealed() && e.getClue().equals("?")) {
//                    e.setClear();
//                }
//            }
//        }
        //printBoard();
    }

    /**
     * if number of non-mine entries among current revealed entry's adjacent entries is greater than or equals to
     * total number of adjacent entries minus real number of adjacent mines, then we can guarantee all the other
     * adjacent uncertain entries are mines
     * In other word, if we can determine all the safe entries of current revealed entry, then all the other uncertain
     * adjacent entries are mines
     * @param board
     * @param i
     * @param j
     */
    public void detectMineByClearEntries(Entry[][] board, int i, int j) {
        List<Entry> adjacentEntries = getAdjacentEntries(board, i, j);
        int adjacentEntryCount = adjacentEntries.size(); // number of current entry's adjacent entries
        int nonMineNum = 0; // record number of non-mine (C or revealed safe entries) entries among adjacent entries
        int unknownNum = 0;
        for (Entry e : adjacentEntries) {
            if (!e.getClue().equals("X") && !e.getClue().equals("?")) {
                nonMineNum++;
            }
            if (e.getClue().equals("?")) {
                unknownNum++;
            }
        }
        int adjacentMines = calculateAdjacentMines(board, i, j);
        if (nonMineNum >= adjacentEntryCount - adjacentMines) {
            for (Entry e : adjacentEntries) {
                if (e.getClue().equals("?")) {
                    e.setClue("X");
                }
            }
        }
    }

    /**
     * return current Entry given its coordinates
     * @param board
     * @param x
     * @param y
     * @return
     */
    public Entry getCurrentEntry(Entry[][] board, int x, int y) {
        Entry cur = board[x][y];
        return cur;
    }

    /**
     * return the collection view of all entries in the board
     * @param board
     * @return
     */
    public List<Entry> getEntryList(Entry[][] board) {
        List<Entry> list = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                list.add(board[i][j]);
            }
        }
        return list;
    }

    /**
     * return the collection view of all clear entries in the board
     * @param board
     * @return
     */
    public List<Entry> getClearEntryList(Entry[][] board) {
        List<Entry> list = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j].getClue().equals("C")) {
                    list.add(board[i][j]);
                }
            }
        }
        return list;
    }

    /**
     * return the collection view of all unknown entries in the board
     * @param board
     * @return
     */
    public List<Entry> getUnknownEntryList(Entry[][] board) {
        List<Entry> list = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j].getClue().equals("?")) {
                    list.add(board[i][j]);
                }
            }
        }
        return list;
    }

    /**
     * if board has no entry which is unknown or clear by not revealed,
     * the board is solved
     * @param board
     * @return if the board is solved or not
     */
    public boolean isSolved(Entry[][] board) {
        if (this.fatal == true) {
            return false;
        }
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j].getClue().equals("?") || board[i][j].getClue().equals("C")) {
                    return false;
                }
            }
        }
        return true;
    }
}
