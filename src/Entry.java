import java.util.Random;

public class Entry {
    public boolean revealed;
    public boolean hasMine;
    public int adjacentMines;
    private boolean isClear;

    private String clue;

    public void setRevealed() {
        revealed = true;
    }

    public boolean getRevealed() {
        return revealed;
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
    public boolean getClear() {return isClear;}

    public Entry() {
        Random rand = new Random();
        double prob = rand.nextDouble();
        if (prob >= 0.9) {
            hasMine = true;
        }
    }
}
