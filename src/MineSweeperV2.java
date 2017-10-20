/**
 * Created by guoyifeng on 10/9/17.
 */
import java.util.List;
import java.util.Random;
import java.util.Scanner;

@SuppressWarnings("Duplicates")
public class MineSweeperV2 {
    public static void main(String[] args) {
        System.out.println("Please input the length and width of MineSweeper: ");
        Scanner scanner = new Scanner(System.in);
        int len = scanner.nextInt();
        int width = scanner.nextInt();
        Board board = new Board(len, width);
        board.cheat();
        board.printBoard();
        Random rand = new Random();
        // first time query the user, for no information given, so choose the position randomly
        int x = rand.nextInt(len );
        int y = rand.nextInt(width);
        queryUser(board, x, y, scanner);
        while (!board.fatal) {
            if (board.isSolved(board.board)) {
                System.out.println("Success!");
                return;
            }
            List<Entry> clearEntryList = board.getClearEntryList(board.board);
            List<Entry> unknownEntryList = board.getUnknownEntryList(board.board);
            int CELSize = clearEntryList.size(); // size of clearEntryList
            int UELSize = unknownEntryList.size(); // size of unknownEntryList
            if (CELSize != 0) {
                Entry nextClearEntry = clearEntryList.get(rand.nextInt(CELSize));
                queryUser(board, nextClearEntry.x, nextClearEntry.y, scanner);
            } else if (UELSize != 0) { // currently no clear entries found and have unknown entries left
                // at current stage, just choose from a random ? entry
                Entry nextUnknownEntry = unknownEntryList.get(rand.nextInt(UELSize));
                queryUser(board, nextUnknownEntry.x, nextUnknownEntry.y, scanner);
            }
        }
        board.printBoard();
    }

    private static void queryUser(Board board,int x, int y, Scanner scanner) {
        System.out.println("Please input the number of mines of chosen cell at ("+ x +","+ y +"): ");
        int minesNum = scanner.nextInt();
        if (!board.getCurrentEntry(board.board, x, y).hasMine) {
            board.getCurrentEntry(board.board, x, y).setAdjacentMines(minesNum);
            board.revealEntry(x,y);
            board.printBoard();
        } else {
            System.out.println("Current position computer chose has a mine, GAME OVER!");
            board.fatal = true;
            return;
        }
    }


}