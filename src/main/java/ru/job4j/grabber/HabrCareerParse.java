package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    private final DateTimeParser dateTimeParser;

    private static final int PAGECOUNT = 5;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private static String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Elements element = document.select(".vacancy-description__text");
        return element.text();
    }

    public List<Post> createPost() throws IOException {
        List<Post> postList = new ArrayList<>();
        for (int i = 1; i < PAGECOUNT; i++) {
            postList.addAll(list(String.format(PAGE_LINK + "?page=%s", i)));
        }
        return postList;
    }

    public static void main(String[] args) throws IOException {
        HabrCareerParse habrCareerParse = new HabrCareerParse(new HabrCareerDateTimeParser());
        List<Post> list = habrCareerParse.createPost();
        list.forEach(System.out::println);
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> list = new ArrayList<>();
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Elements rows = document.select(".vacancy-card__inner");
        rows.forEach(row -> {
            Element titleElement = row.select(".vacancy-card__title").first();
            Element linkElement = titleElement.child(0);
            String vacancyName = titleElement.text();
            String vacancyDate = row.select(".vacancy-card__date time").first().attr("datetime");
            String newLink = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
            String description;
            try {
                description = retrieveDescription(link);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Post post = new Post(vacancyName, newLink, description, dateTimeParser.parse(vacancyDate));
            list.add(post);
        });

        return list;
    }
}
