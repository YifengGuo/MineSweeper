import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Board_logic {
    private int width;
    private int len;
    public boolean fatal = false;
    // values in board, -1 for mine
    private int[][] value;
    // if it is revealed
    private boolean[][] isClear;
    // if it is labelled as mine
    private boolean[][] islabel;
    // entries
    public Entry[][] entries;
    // mines of number
    private int minesNum = 0;
    // remaining mines
    private int minesLeft = 0;


    public Board_logic(int width, int len) {
        this.width = width;
        this.len = len;
        this.value  = new int[width][len];
        this.isClear = new boolean[width][len];
        this.islabel = new boolean[width][len];
        boolean[][] hasMine = new boolean[width][len];
        // generate mines
        this.entries = new Entry[width][len];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < len; j++) {
                this.isClear[i][j] = false;
                this.islabel[i][j] = false;
                this.entries[i][j] = new Entry(i ,j);
                Random r = new Random();
                double p = r.nextDouble();
                if (p >= 0.8) {
                    // it's a mine
                    hasMine[i][j] = true;
                    minesNum++;
                }
            }
        }
        minesLeft = minesNum;
        // Construct board
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < len; j++) {

                if (hasMine[i][j]) {
                    value[i][j] = -1;
                } else {
                    //top
                    if ((i-1) >= 0 && hasMine[i-1][j])
                        value[i][j]++;
                    //bottom
                    if ((i+1) < width && hasMine[i+1][j])
                        value[i][j]++;
                    //left
                    if ((j-1) >= 0 && hasMine[i][j-1])
                        value[i][j]++;
                    //right
                    if ((j+1) < len && hasMine[i][j+1])
                        value[i][j]++;
                    //up left
                    if (i-1 >= 0 && j-1 >= 0 && hasMine[i-1][j-1])
                        value[i][j]++;
                    //up right
                    if (i-1 >= 0 && j+1 < len && hasMine[i-1][j+1])
                        value[i][j]++;
                    //down left
                    if (i+1 < width && j-1 >= 0 && hasMine[i+1][j-1])
                        value[i][j]++;
                    //down right
                    if (i+1 < width && j+1 < len && hasMine[i+1][j+1])
                        value[i][j]++;
                }
            }
        }
    }
    public void print() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < len; j++) {
                if (value[i][j] == -1)
                    System.out.print("* ");
                else
                    System.out.print(value[i][j] + " ");
            }
            System.out.println();
        }
    }
    public boolean isSolved() {
        for (int i = 0; i < width; i ++) {
            for (int j = 0; j < len; j ++) {
                if (!isClear[i][j] && !islabel[i][j])
                    return false;
            }
        }
        return true;
    }

    public Entry randomUnclearedEntry() {
        Random r = new Random();
        List<Entry> le = getUncNodes();
        int index = r.nextInt(le.size());
        return le.get(index);
    }
    public int revealNode(int x, int y) {
        isClear[x][y] = true;
        if (value[x][y] == -1) fatal = true;
        return value[x][y];
    }

    public List<Entry> getAdjNodes(int i, int j) {
        List<Entry> le = new ArrayList<Entry>();
        if ((i-1) >= 0 )
            le.add(entries[i-1][j]);
        //bottom
        if ((i+1) < width )
            le.add(entries[i+1][j]);
        //left
        if ((j-1) >= 0 )
            le.add(entries[i][j-1]);
        //right
        if ((j+1) < len )
            le.add(entries[i][j+1]);
        //up left
        if (i-1 >= 0 && j-1 >= 0)
            le.add(entries[i-1][j-1]);
        //up right
        if (i-1 >= 0 && j+1 < len)
            le.add(entries[i-1][j+1]);
        //down left
        if (i+1 < width && j-1 >= 0)
            le.add(entries[i+1][j-1]);
        //down right
        if (i+1 < width && j+1 < len)
            le.add(entries[i+1][j+1]);
        return le;
    }

    public List<Entry> getUncNodes() {
        List<Entry> le = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < len; j++) {
                if (!isClear[i][j] && !islabel[i][j])
                    le.add(entries[i][j]);
            }
        }
        return le;
    }

    public boolean[][] getCleared(){
        return isClear;
    }
    public boolean[][] getLabeled(){
        return islabel;
    }
    public int getWidth() {
        return width;
    }
    public int getLen() {
        return len;
    }

    public void update(Queue<Entry> hasMine) {
        List<Entry> le = new ArrayList<>(hasMine);
        for (Entry e:le) {
            if (!islabel[e.x][e.y]) {
                islabel[e.x][e.y] = true;
                minesLeft--;
            }
        }
    }
    public List<Entry> getAdjUncNodes(int i, int j) {
        List<Entry> le = getAdjNodes(i, j);
        List<Entry> res = new ArrayList<>();
        for (Entry e : le) {
            if (!isClear[e.x][e.y] && !islabel[e.x][e.y])
                res.add(e);
        }
        return res;
    }
    public void printG() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < len; j++) {
                if (isClear[i][j])
                    System.out.print(value[i][j] + " ");
                else if(islabel[i][j])
                    System.out.print("X" + " ");
                else
                    System.out.print("?" + " ");
            }
            System.out.println();
        }
    }
    public getMinesNum() {
        return minesNum;
    }
}
