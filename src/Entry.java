import java.util.Random;

public class Entry {
    public boolean revealed;
    public boolean hasMine;
    public int adjacentMines;

    public void setRevealed() {
        revealed = true;
    }

    public boolean getRevealed() {
        return revealed;
    }

    public void setAdjacentMines(int n) {
        adjacentMines = n;
    }

    public Entry() {
        Random rand = new Random();
        double prob = rand.nextDouble();
        if (prob >= 0.9) {
            hasMine = true;
        }
    }
}
