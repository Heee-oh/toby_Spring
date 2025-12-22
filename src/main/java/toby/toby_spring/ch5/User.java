package toby.toby_spring.ch5;

import java.util.Objects;

public class User {

    String id;
    String name;
    String password;

    Level level;
    int login;
    int recommend;

    public User() {
    }

    public User(String id, String name, String password, Level level, int login, int recommend) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.level = level;
        this.login = login;
        this.recommend = recommend;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void setLogin(int login) {
        this.login = login;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }

    public Level getLevel() {
        return level;
    }

    public int getLogin() {
        return login;
    }

    public int getRecommend() {
        return recommend;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        User user = (User) object;
        return Objects.equals(getId(), user.getId()) && Objects.equals(getName(), user.getName()) && Objects.equals(getPassword(), user.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getPassword());
    }
}
