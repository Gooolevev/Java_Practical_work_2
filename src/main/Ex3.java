package main;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Ex3 {

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

    public static void main(String[] args) {
        Grid grid = new Grid();
        Scanner scanner = new Scanner(System.in);

        // 1. Ввод начальной и конечной клеток
        System.out.print("Введите начальную клетку ферзя (например, b1): ");
        String fromPos = scanner.nextLine().trim().toLowerCase();

        System.out.print("Введите целевую клетку (например, g6): ");
        String toPos = scanner.nextLine().trim().toLowerCase();

        Cell from = grid.getCell(fromPos);
        Cell to = grid.getCell(toPos);

        if (from == null || to == null) {
            System.out.println("Ошибка: некорректная клетка!");
            return;
        }

        // 2. Определение угрозы / корректности хода
        boolean threat = checkQueenMove(from, to);

        // 3. Сравнение цветов клеток
        boolean colorsMatch = from.color.equals(to.color);
        String colorStatus = colorsMatch ? "цвета совпадают" : "цвета разные";
        String threatStatus = threat ? "угроза есть" : "угрозы нет";

        // 4. Вывод результата
        System.out.println("\n--- Результат проверки ---");
        System.out.println("Начальная клетка " + from.name + ": " + from.color);
        System.out.println("Конечная клетка " + to.name + ": " + to.color);
        System.out.println(from.name + " → " + to.name + ": " + threatStatus + ", " + colorStatus);
    }

    // Ферзь бьет клетку, если смещение идет по вертикали, горизонтали или диагонали
    private static boolean checkQueenMove(Cell from, Cell to) {
        if (from.x == to.x && from.y == to.y) {
            return false; // ход на ту же самую клетку не считается
        }

        int dx = Math.abs(from.x - to.x);
        int dy = Math.abs(from.y - to.y);

        boolean sameRowOrCol = (from.x == to.x) || (from.y == to.y);
        boolean sameDiagonal = (dx == dy);

        return sameRowOrCol || sameDiagonal;
    }
}