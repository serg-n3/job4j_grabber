package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private static String retrieveDescription(String link) throws IOException {
        Document doc = Jsoup.connect(link).get();
        return doc.select(".vacancy-description__text").first().text();
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Connection connection = Jsoup.connect("%s%s".formatted(link, i));
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                try {
                    posts.add(createPost(row));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        return posts;
    }

    private Post createPost(Element row) throws IOException {
        Element titleElement = row.select(".vacancy-card__title").first();
        Element linkElement = titleElement.child(0);
        String vacancyName = titleElement.text();
        String date = row.selectFirst(".vacancy-card__date").child(0).attr("datetime");
        String vacancyLink = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        return new Post(vacancyName, vacancyLink, retrieveDescription(vacancyLink), dateTimeParser.parse(date));
    }
}
