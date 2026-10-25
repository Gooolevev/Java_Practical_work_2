package test;

import main.Ex1;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Ex1: ходы коня из заданной клетки.
 */
class Ex1Test {

    private static final String NO_SUCH_CELL = "Ошибка: такой клетки нет на доске!";

    private static String run(String input) {
        return ConsoleRunner.run(() -> Ex1.main(new String[0]), input);
    }

    private static String cellName(int x, int y) {
        return "" + (char) ('a' + x) + (char) ('1' + y);
    }

    private static String colorOf(int x, int y) {
        return (x + y) % 2 == 0 ? "черная" : "белая";
    }

    /** Строки вида "-> b3 (белая)" из вывода программы. */
    private static List<String> moveLines(String output) {
        List<String> result = new ArrayList<>();
        for (String line : output.split("\n")) {
            if (line.startsWith("-> ")) {
                result.add(line);
            }
        }
        return result;
    }

    @Test
    void cornerA1HasTwoMoves() {
        String output = run("a1");

        assertTrue(output.contains("Конь стоит на клетке: a1 (черная)"));
        assertEquals(List.of("-> b3 (белая)", "-> c2 (белая)"), moveLines(output));
        assertTrue(output.contains("Итого доступных ходов: 2"));
    }

    @Test
    void cornerH8HasTwoMoves() {
        String output = run("h8");

        assertTrue(output.contains("Конь стоит на клетке: h8 (черная)"));
        List<String> moves = moveLines(output);
        Collections.sort(moves);
        assertEquals(List.of("-> f7 (белая)", "-> g6 (белая)"), moves);
        assertTrue(output.contains("Итого доступных ходов: 2"));
    }

    @Test
    void centerE4HasEightMoves() {
        String output = run("e4");

        assertTrue(output.contains("Конь стоит на клетке: e4 (белая)"));

        List<String> moves = moveLines(output);
        Collections.sort(moves);
        assertEquals(List.of(
                "-> c3 (черная)", "-> c5 (черная)",
                "-> d2 (черная)", "-> d6 (черная)",
                "-> f2 (черная)", "-> f6 (черная)",
                "-> g3 (черная)", "-> g5 (черная)"
        ), moves);
        assertTrue(output.contains("Итого доступных ходов: 8"));
    }

    @Test
    void edgeA4HasFourMoves() {
        String output = run("a4");

        assertEquals(4, moveLines(output).size());
        assertTrue(output.contains("Итого доступных ходов: 4"));
    }

    @Test
    void everySquareMatchesIndependentCalculation() {
        int[][] jumps = {{1, 2}, {2, 1}, {2, -1}, {1, -2}, {-1, -2}, {-2, -1}, {-2, 1}, {-1, 2}};
        int totalMoves = 0;

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                String start = cellName(x, y);

                List<String> expected = new ArrayList<>();
                for (int[] jump : jumps) {
                    int tx = x + jump[0];
                    int ty = y + jump[1];
                    if (tx >= 0 && tx < 8 && ty >= 0 && ty < 8) {
                        expected.add("-> " + cellName(tx, ty) + " (" + colorOf(tx, ty) + ")");
                        // конь всегда попадает на клетку другого цвета
                        assertNotEquals(colorOf(x, y), colorOf(tx, ty));
                    }
                }

                String output = run(start);
                List<String> actual = moveLines(output);

                Collections.sort(expected);
                Collections.sort(actual);
                assertEquals(expected, actual, "ходы из " + start);
                assertTrue(output.contains("Итого доступных ходов: " + expected.size()), start);
                assertTrue(output.contains("Конь стоит на клетке: " + start + " (" + colorOf(x, y) + ")"), start);

                totalMoves += actual.size();
            }
        }

        // известный факт: на доске 8x8 у коня суммарно 336 ходов со всех клеток
        assertEquals(336, totalMoves);
    }

    @Test
    void inputIsTrimmedAndCaseInsensitive() {
        String expected = run("e4");

        assertEquals(expected, run("E4"));
        assertEquals(expected, run("  e4  "));
        assertEquals(expected, run(" E4"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"z9", "a9", "a0", "i1", "e", "e44", "44", "", "   ", "e-4", "а1"})
    void invalidCellPrintsErrorAndNoMoves(String input) {
        String output = run(input);

        assertTrue(output.contains(NO_SUCH_CELL), "для ввода '" + input + "'");
        assertFalse(output.contains("Возможные ходы"));
        assertFalse(output.contains("Итого"));
    }
}
