package main;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Ex2 {

    static class Cell {
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

    static class Grid {
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

    static final int[][] ROOK_DIRECTIONS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    public static void main(String[] args) {
        Grid grid = new Grid();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите клетку ладьи (например, a1): ");
        String rookPos = scanner.nextLine().trim().toLowerCase();

        System.out.print("Введите клетку слона (например, a5): ");
        String bishopPos = scanner.nextLine().trim().toLowerCase();

        System.out.print("Введите клетку коня (например, c3): ");
        String knightPos = scanner.nextLine().trim().toLowerCase();

        Cell rook = grid.getCell(rookPos);
        Cell bishop = grid.getCell(bishopPos);
        Cell knight = grid.getCell(knightPos);

        if (rook == null || bishop == null || knight == null) {
            System.out.println("Ошибка: некорректная клетка!");
            return;
        }

        System.out.println("\n--- Результаты ---");

        System.out.println("Клетка ладьи (" + rook.name + "): " + rook.color);
        System.out.println("Клетка коня (" + knight.name + "): " + knight.color);
        if (rook.color.equals(knight.color)) {
            System.out.println("Цвета совпадают.");
        } else {
            System.out.println("Цвета разные.");
        }

        boolean threat = checkThreat(rook, bishop, knight);
        if (threat) {
            System.out.println("Угроза слону: ДА");
        } else {
            System.out.println("Угроза слону: НЕТ");
        }

        int rookMovesCount = countRookMoves(rook, bishop, knight);
        System.out.println("Число доступных ходов ладьи: " + rookMovesCount);
    }

    private static boolean checkThreat(Cell rook, Cell bishop, Cell knight) {
        if (rook.x == bishop.x) {
            boolean blocked = (knight.x == rook.x) &&
                    (knight.y > Math.min(rook.y, bishop.y) && knight.y < Math.max(rook.y, bishop.y));
            return !blocked;
        }

        if (rook.y == bishop.y) {
            boolean blocked = (knight.y == rook.y) &&
                    (knight.x > Math.min(rook.x, bishop.x) && knight.x < Math.max(rook.x, bishop.x));
            return !blocked;
        }

        return false;
    }

    private static int countRookMoves(Cell rook, Cell bishop, Cell knight) {
        int count = 0;

        for (int[] dir : ROOK_DIRECTIONS) {
            int curX = rook.x + dir[0];
            int curY = rook.y + dir[1];

            while (curX >= 0 && curX < 8 && curY >= 0 && curY < 8) {
                if ((curX == bishop.x && curY == bishop.y) || (curX == knight.x && curY == knight.y)) {
                    count++;
                    break;
                }
                count++;
                curX += dir[0];
                curY += dir[1];
            }
        }

        return count;
    }
}