package test;

import main.Ex3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Ex3: бьёт ли ферзь клетку и совпадает ли цвет клеток.
 */
class Ex3Test {

    private static final String INVALID_CELL = "Ошибка: некорректная клетка!";

    private static String run(String from, String to) {
        return ConsoleRunner.run(() -> Ex3.main(new String[0]), from, to);
    }

    private static String cellName(int x, int y) {
        return "" + (char) ('a' + x) + (char) ('1' + y);
    }

    private static String colorOf(int x, int y) {
        return (x + y) % 2 == 0 ? "черная" : "белая";
    }

    @Test
    void diagonalMoveB1ToG6() {
        String output = run("b1", "g6");

        assertTrue(output.contains("--- Результат проверки ---"));
        assertTrue(output.contains("Начальная клетка b1: белая"));
        assertTrue(output.contains("Конечная клетка g6: белая"));
        assertTrue(output.contains("b1 → g6: угроза есть, цвета совпадают"));
    }

    @ParameterizedTest(name = "{0} -> {1}: {2}, {3}")
    @CsvSource({
            // по вертикали
            "a1, a8, угроза есть, цвета разные",
            // по горизонтали
            "a1, h1, угроза есть, цвета разные",
            "d4, a4, угроза есть, цвета разные",
            // по диагонали
            "a1, h8, угроза есть, цвета совпадают",
            "h1, a8, угроза есть, цвета совпадают",
            "e4, c2, угроза есть, цвета совпадают",
            // ход коня ферзю недоступен
            "a1, b3, угрозы нет, цвета разные",
            "e4, f6, угрозы нет, цвета разные",
            // не на одной линии
            "a1, c2, угрозы нет, цвета разные",
            "a1, d2, угрозы нет, цвета совпадают"
    })
    void queenThreatAndColors(String from, String to, String threat, String colors) {
        String output = run(from, to);

        assertTrue(output.contains(from + " → " + to + ": " + threat + ", " + colors), output);
    }

    @Test
    void sameCellIsNotAThreat() {
        String output = run("e4", "e4");

        assertTrue(output.contains("e4 → e4: угрозы нет, цвета совпадают"));
    }

    @Test
    void inputIsTrimmedAndCaseInsensitive() {
        String expected = run("b1", "g6");

        assertEquals(expected, run("B1", "G6"));
        assertEquals(expected, run("  b1 ", " g6  "));
    }

    @ParameterizedTest
    @CsvSource({
            "z9, a1",
            "a1, z9",
            "a1, a9",
            "i1, a1",
            "a1, 11",
            "aa1, a1"
    })
    void invalidCellPrintsError(String from, String to) {
        String output = run(from, to);

        assertTrue(output.contains(INVALID_CELL), from + " " + to);
        assertFalse(output.contains("--- Результат проверки ---"));
    }

    @Test
    void allPairsOfCellsMatchIndependentCalculation() {
        for (int x1 = 0; x1 < 8; x1++) {
            for (int y1 = 0; y1 < 8; y1++) {
                for (int x2 = 0; x2 < 8; x2++) {
                    for (int y2 = 0; y2 < 8; y2++) {
                        int dx = Math.abs(x1 - x2);
                        int dy = Math.abs(y1 - y2);
                        boolean sameCell = dx == 0 && dy == 0;
                        boolean threat = !sameCell && (dx == 0 || dy == 0 || dx == dy);
                        boolean sameColor = colorOf(x1, y1).equals(colorOf(x2, y2));

                        String from = cellName(x1, y1);
                        String to = cellName(x2, y2);
                        String expectedLine = from + " → " + to + ": "
                                + (threat ? "угроза есть" : "угрозы нет") + ", "
                                + (sameColor ? "цвета совпадают" : "цвета разные");

                        String output = run(from, to);

                        assertTrue(output.contains(expectedLine), () -> expectedLine + "\n---\n" + output);
                        assertTrue(output.contains("Начальная клетка " + from + ": " + colorOf(x1, y1)));
                        assertTrue(output.contains("Конечная клетка " + to + ": " + colorOf(x2, y2)));
                    }
                }
            }
        }
    }
}
