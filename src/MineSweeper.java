import java.util.Scanner;

public class MineSweeper {
    public static final Board board = new Board(5,5);
    public static void main(String[] args) {
        board.cheat();
        board.printBoard();
        Scanner scanner = new Scanner(System.in);
        while (!board.fatal) {
            System.out.println("Please input next move coordinate:");
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            board.revealEntry(x, y);
        }
    }
}
