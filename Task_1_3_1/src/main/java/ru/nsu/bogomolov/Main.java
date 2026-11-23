package ru.nsu.bogomolov;

import java.io.IOException;
import ru.nsu.bogomolov.search.SubstringFinder;

/** Запуск поиска подстроки из командной строки. */
public class Main {

    /** Предотвращает создание экземпляров класса запуска. */
    private Main() {
    }

    /**
     * Выводит список позиций найденной подстроки.
     *
     * @param args имя файла и искомая подстрока
     * @throws IOException если файл не удалось открыть или прочитать
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Использование: <имя файла> <подстрока>");
            return;
        }
        System.out.println(new SubstringFinder().find(args[0], args[1]));
    }
}
