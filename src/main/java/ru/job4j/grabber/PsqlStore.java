package ru.job4j.grabber;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {

    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("app.properties")) {
            cfg.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Class.forName(cfg.getProperty("driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            cnn = DriverManager.getConnection(
                    cfg.getProperty("url"),
                    cfg.getProperty("username"),
                    cfg.getProperty("password")
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement =
                     cnn.prepareStatement("INSERT INTO post (name, text, link, created) VALUES (?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.execute();
            try (ResultSet generatedKey = statement.getGeneratedKeys()) {
                if (generatedKey.next()) {
                    post.setId(generatedKey.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> postList = new ArrayList<>();
        try (PreparedStatement statement =
                cnn.prepareStatement("SELECT * FROM post")) {
            statement.execute();
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Post post = new Post(resultSet.getInt(1),
                            resultSet.getString("name"),
                            resultSet.getString("text"),
                            resultSet.getString("link"),
                            resultSet.getTimestamp("created").toLocalDateTime());
                    postList.add(post);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postList;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement statement =
                cnn.prepareStatement(("SELECT * FROM post WHERE id = ?"))) {
            statement.setInt(1, id);
            statement.execute();
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                     post = new Post(resultSet.getInt(1),
                            resultSet.getString("name"),
                            resultSet.getString("text"),
                            resultSet.getString("link"),
                            resultSet.getTimestamp("created").toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return post;
    }

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("app.properties")) {
            properties.load(in);
        }
        try (PsqlStore psqlStore = new PsqlStore(properties)) {
            psqlStore.save(new Post("one", "textOne", "linkONe", LocalDateTime.now()));
            psqlStore.save(new Post("two", "textTwo", "linkTwo", LocalDateTime.now()));
            List<Post> postList = psqlStore.getAll();
            for (Post post : postList) {
                System.out.println(post);
            }
            System.out.println(psqlStore.findById(1));
        }

    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }
}
