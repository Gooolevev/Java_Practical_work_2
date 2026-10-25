package test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Запускает консольную программу с заданным вводом и возвращает всё, что она напечатала.
 */
final class ConsoleRunner {

    private ConsoleRunner() {
    }

    /**
     * @param program    код, который нужно выполнить (обычно вызов main)
     * @param inputLines строки, которые "вводит" пользователь
     * @return весь вывод программы, переводы строк нормализованы к "\n"
     */
    static String run(Runnable program, String... inputLines) {
        InputStream oldIn = System.in;
        PrintStream oldOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            String input = String.join("\n", inputLines) + "\n";
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
            program.run();
        } finally {
            System.setIn(oldIn);
            System.setOut(oldOut);
        }

        return new String(buffer.toByteArray(), StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
