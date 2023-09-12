package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
public class HabrCareerParse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    private static String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Elements element = document.select(".vacancy-description__text");
        return element.text();
    }

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 6; i++) {
            String newLink = String.format(PAGE_LINK + "?page=%s", i);
                Connection connection = Jsoup.connect(newLink);
                Document document = connection.get();
                Elements rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> {
                    Element titleElement = row.select(".vacancy-card__title").first();
                    Element linkElement = titleElement.child(0);
                    String vacancyName = titleElement.text();
                    String vacancyDate = row.select(".vacancy-card__date time").first().attr("datetime");
                    String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                    String description;
                    try {
                         description = retrieveDescription(link);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.printf("%s %s %s%n%s%n", vacancyName, link, vacancyDate, description);
                });
            }

        }
    }
