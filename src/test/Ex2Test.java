package test;

import main.Ex2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Ex2: ладья, слон и конь на одной доске. Проверяются цвета клеток, угроза слону
 * и число доступных ходов ладьи (занятая клетка, на которую можно побить, считается ходом).
 */
class Ex2Test {

    private static final String INVALID_CELL = "Ошибка: некорректная клетка!";

    private static String run(String rook, String bishop, String knight) {
        return ConsoleRunner.run(() -> Ex2.main(new String[0]), rook, bishop, knight);
    }

    // ---------- конкретные случаи ----------

    @Test
    void defaultExampleFromPrompts() {
        // ладья a1, слон a5, конь c3
        String output = run("a1", "a5", "c3");

        assertTrue(output.contains("--- Результаты ---"));
        assertTrue(output.contains("Клетка ладьи (a1): черная"));
        assertTrue(output.contains("Клетка коня (c3): черная"));
        assertTrue(output.contains("Цвета совпадают."));
        assertTrue(output.contains("Угроза слону: ДА"));
        assertTrue(output.contains("Число доступных ходов ладьи: 11"));
    }

    @Test
    void knightBetweenOnSameFileBlocksThreat() {
        // ладья a1, слон a5, конь a3 стоит между ними
        String output = run("a1", "a5", "a3");

        assertTrue(output.contains("Угроза слону: НЕТ"));
        // вправо по горизонтали 7 клеток + вверх a2 и побить коня на a3
        assertTrue(output.contains("Число доступных ходов ладьи: 9"));
    }

    @Test
    void knightBetweenOnSameRankBlocksThreat() {
        // ладья a1, слон h1, конь d1 стоит между ними
        String output = run("a1", "h1", "d1");

        assertTrue(output.contains("Угроза слону: НЕТ"));
        // b1, c1, d1 (побить коня) + вся вертикаль a2..a8
        assertTrue(output.contains("Число доступных ходов ладьи: 10"));
    }

    @Test
    void knightOnSameLineButNotBetweenDoesNotBlock() {
        // ладья a1, слон d1, конь h1: конь стоит за слоном
        String output = run("a1", "d1", "h1");

        assertTrue(output.contains("Угроза слону: ДА"));
    }

    @Test
    void knightOnSameFileOutsideSegmentDoesNotBlock() {
        // ладья a3, слон a5, конь a1: конь с другой стороны от ладьи
        String output = run("a3", "a5", "a1");

        assertTrue(output.contains("Угроза слону: ДА"));
    }

    @Test
    void bishopNotOnRookLineIsNotThreatened() {
        String output = run("a1", "b2", "c3");

        assertTrue(output.contains("Угроза слону: НЕТ"));
        assertTrue(output.contains("Число доступных ходов ладьи: 14"));
    }

    @Test
    void rookInCenterSurroundedByOwnPieces() {
        // ладья d4, слон d5 (над ней), конь e4 (справа)
        String output = run("d4", "d5", "e4");

        assertTrue(output.contains("Угроза слону: ДА"));
        assertTrue(output.contains("Цвета разные."));
        // вправо: только e4 (1), влево: c4 b4 a4 (3), вверх: только d5 (1), вниз: d3 d2 d1 (3)
        assertTrue(output.contains("Число доступных ходов ладьи: 8"));
    }

    @Test
    void rookOnFreeBoardAlwaysHasFourteenMoves() {
        // фигуры далеко от линий ладьи d4: вертикаль d и горизонталь 4 свободны
        String output = run("d4", "g7", "b2");

        assertTrue(output.contains("Число доступных ходов ладьи: 14"));
        assertTrue(output.contains("Клетка ладьи (d4): черная"));
        assertTrue(output.contains("Клетка коня (b2): черная"));
        assertTrue(output.contains("Цвета совпадают."));
    }

    @Test
    void differentColorsReported() {
        // a1 черная, b1 белая
        String output = run("a1", "h8", "b1");

        assertTrue(output.contains("Клетка ладьи (a1): черная"));
        assertTrue(output.contains("Клетка коня (b1): белая"));
        assertTrue(output.contains("Цвета разные."));
        assertFalse(output.contains("Цвета совпадают."));
    }

    @Test
    void inputIsTrimmedAndCaseInsensitive() {
        String expected = run("a1", "a5", "c3");

        assertEquals(expected, run("A1", "A5", "C3"));
        assertEquals(expected, run("  a1 ", " a5", "c3  "));
    }

    @ParameterizedTest
    @CsvSource({
            "z9, a5, c3",
            "a1, i5, c3",
            "a1, a5, c9",
            "a0, a5, c3",
            "a1, 55, c3",
            "a1, a5, cc3"
    })
    void invalidCellPrintsError(String rook, String bishop, String knight) {
        String output = run(rook, bishop, knight);

        assertTrue(output.contains(INVALID_CELL), rook + " " + bishop + " " + knight);
        assertFalse(output.contains("--- Результаты ---"));
        assertFalse(output.contains("Угроза слону"));
    }

    // ---------- случайные расстановки против независимого расчёта ----------

    @Test
    void randomPlacementsMatchIndependentCalculation() {
        Random random = new Random(2024);
        int checked = 0;

        while (checked < 1000) {
            int[] rook = randomSquare(random);
            int[] bishop = randomSquare(random);
            int[] knight = randomSquare(random);

            if (sameSquare(rook, bishop) || sameSquare(rook, knight) || sameSquare(bishop, knight)) {
                continue;
            }
            checked++;

            String description = name(rook) + " " + name(bishop) + " " + name(knight);
            String output = run(name(rook), name(bishop), name(knight));

            assertTrue(output.contains("Клетка ладьи (" + name(rook) + "): " + color(rook)), description);
            assertTrue(output.contains("Клетка коня (" + name(knight) + "): " + color(knight)), description);
            assertTrue(output.contains(color(rook).equals(color(knight))
                    ? "Цвета совпадают." : "Цвета разные."), description);
            assertTrue(output.contains(rookAttacksBishop(rook, bishop, knight)
                    ? "Угроза слону: ДА" : "Угроза слону: НЕТ"), description);
            assertTrue(output.contains("Число доступных ходов ладьи: " + rookMoves(rook, bishop, knight)),
                    description);
        }
    }

    // ---------- вспомогательные функции (независимая реализация) ----------

    private static int[] randomSquare(Random random) {
        return new int[]{random.nextInt(8), random.nextInt(8)};
    }

    private static boolean sameSquare(int[] a, int[] b) {
        return a[0] == b[0] && a[1] == b[1];
    }

    private static String name(int[] square) {
        return "" + (char) ('a' + square[0]) + (char) ('1' + square[1]);
    }

    private static String color(int[] square) {
        return (square[0] + square[1]) % 2 == 0 ? "черная" : "белая";
    }

    /** Идём от ладьи к слону по линии; если по пути встретили коня, угрозы нет. */
    private static boolean rookAttacksBishop(int[] rook, int[] bishop, int[] knight) {
        int dx = Integer.signum(bishop[0] - rook[0]);
        int dy = Integer.signum(bishop[1] - rook[1]);
        if (dx != 0 && dy != 0) {
            return false; // не на одной вертикали/горизонтали
        }

        int x = rook[0] + dx;
        int y = rook[1] + dy;
        while (x != bishop[0] || y != bishop[1]) {
            if (x == knight[0] && y == knight[1]) {
                return false;
            }
            x += dx;
            y += dy;
        }
        return true;
    }

    /** Идём по четырём лучам; занятая клетка считается ходом (побить), дальше луч закрыт. */
    private static int rookMoves(int[] rook, int[] bishop, int[] knight) {
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        int count = 0;

        for (int[] d : directions) {
            int x = rook[0] + d[0];
            int y = rook[1] + d[1];
            while (x >= 0 && x < 8 && y >= 0 && y < 8) {
                count++;
                boolean occupied = (x == bishop[0] && y == bishop[1]) || (x == knight[0] && y == knight[1]);
                if (occupied) {
                    break;
                }
                x += d[0];
                y += d[1];
            }
        }
        return count;
    }
}
