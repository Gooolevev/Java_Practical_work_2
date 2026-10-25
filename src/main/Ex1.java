package main;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class Cell {
    final int x;
    final int y;
    final String name;
    final String color;

    Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.name = "" + (char) ('a' + x) + (char) ('1' + y);
        this.color = (x + y) % 2 == 0 ? "черная" : "белая";
    }
}

class Grid {
    Map<String, Cell> board = new HashMap<>();

    public Grid() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Cell cell = new Cell(x, y);
                board.put(cell.name, cell);
            }
        }
    }

    public Cell getCell(String name) {
        return board.get(name);
    }
}

public class Ex1 {
    static final int[][] DELTAS = {
            {1, 2}, {-1, 2}, {1, -2}, {-1, -2},
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1}
    };

    public static void main(String[] args) {
        Grid grid = new Grid();

        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите клетку коня (например, e4, a1, h8): ");
        String startPos = scanner.nextLine().trim().toLowerCase();

        Cell startCell = grid.getCell(startPos);

        if (startCell == null) {
            System.out.println("Ошибка: такой клетки нет на доске!");
            return;
        }

        System.out.println("Конь стоит на клетке: " + startCell.name + " (" + startCell.color + ")");
        System.out.println("Возможные ходы:");

        int count = 0;

        for (int[] delta : DELTAS) {
            int targetX = startCell.x + delta[0];
            int targetY = startCell.y + delta[1];

            if (targetX >= 0 && targetX < 8 && targetY >= 0 && targetY < 8) {
                String targetName = "" + (char) ('a' + targetX) + (char) ('1' + targetY);
                Cell targetCell = grid.getCell(targetName);

                count++;
                System.out.println("-> " + targetCell.name + " (" + targetCell.color + ")");
            }
        }

        System.out.println("Итого доступных ходов: " + count);
    }
}

