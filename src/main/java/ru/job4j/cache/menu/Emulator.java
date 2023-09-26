package ru.job4j.cache.menu;

import ru.job4j.cache.DirFileCache;
import java.util.Scanner;

public class Emulator {
    private static final Integer ONE = 1;
    private static final Integer TWO = 2;
    private static final String SELECT = "Выберите пункт меню";

    private static final String MENU = """                                   
            1: загрузить содержимое файла в кэш
            2: получить содержимое файла из кэша""";

        public static void start() {
            String answ = "";
            Scanner scanner = new Scanner(System.in);
            boolean run = true;
            while (run) {
                System.out.println(MENU);
                System.out.println(SELECT);
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice == ONE) {
                    System.out.println("Загрузка файла в кэш");
                    System.out.println("Введите путь к файлу");
                    String answer = scanner.nextLine();
                    answ = answer;
                    DirFileCache dirFileCache = new DirFileCache(answer);
                    dirFileCache.get(answer);
                } else if (choice == TWO) {
                    System.out.println("Получение содержимого файла из кэша");
                    if (answ.isEmpty()) {
                        System.out.println("Загрузите файл в кэш");
                    } else {
                        DirFileCache dirFileCache = new DirFileCache(answ);
                        System.out.println(dirFileCache.get(answ));

                    }
                } else {
                    run = false;
                }
            }
        }

    public static void main(String[] args) {
        start();
    }
}
