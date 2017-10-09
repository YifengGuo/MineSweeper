import java.util.Scanner;

public class MineSweeper {
    public static Board board;
    public static void main(String[] args) {
        System.out.println("Please input the length and width of MineSweeper: ");
        Scanner scanner = new Scanner(System.in);
        int len = scanner.nextInt();
        int width = scanner.nextInt();
        board = new Board(len, width);
        board.cheat();
        board.printBoard();
        while (!board.fatal) {
            System.out.println("Please input next move coordinate:");
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            board.revealEntry(x, y);
            if (board.fatal) {
                return;
            }
        }
    }
}
