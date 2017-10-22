import sun.security.ec.ECPublicKeyImpl;

import java.util.Random;
/**
 * Created by guoyifeng on 10/6/17.
 */
public class Entry {
    public boolean revealed;
    public boolean hasMine;
    public int adjacentMines = Integer.MAX_VALUE;
    private boolean isClear;

    public int x; // x coordinate
    public int y; // y coordinate

    private String clue;

    public void setRevealed() {
        revealed = true;
    }

    public boolean getRevealed() {
        return revealed == true;
    }

    public void setAdjacentMines(int n) {
        adjacentMines = n;
    }

    public void setClue(String s) {
        clue = s;
    }

    public String getClue() {
        return clue;
    }

    public void setClear() {isClear = true; }
    public boolean getClear() {return isClear == true;}

    public Entry(int x, int y) {
        this.x = x;
        this.y = y;
        Random rand = new Random();
        double prob = rand.nextDouble();
        if (prob >= 0.9) {
            hasMine = true;
        }
        clue = "?";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Entry)) {
            return false;
        }
        Entry e = (Entry) o;
        return e.x == x
                && e.y == y;
    }
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
