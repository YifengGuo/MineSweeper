/**
 * Created by guoyifeng on 10/9/17.
 */
import java.util.Random;
import java.util.Scanner;
@SuppressWarnings("Duplicates")
public class MineSweeperV2 {
    public static Board board;
    public static void main(String[] args) {
        System.out.println("Please input the length and width of MineSweeper: ");
        Scanner scanner = new Scanner(System.in);
        int len = scanner.nextInt();
        int width = scanner.nextInt();
        board = new Board(len, width);
        board.cheat();
        board.printBoard();
        Random rand = new Random();
        int initRow = rand.nextInt(len);
        int initCol = rand.nextInt(width);
        System.out.println("Please reveal cell (" + initRow + ", " + initCol + ")");
        board.revealEntry(initRow, initCol);

        board.printBoard();
    }
}