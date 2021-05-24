package authorization;

import authorization.AuthService;

import java.sql.*;

public class SimpleAuthService implements AuthService {
//    String getNicknameQuery = "SELECT * FROM users WHERE login = 'login' AND password = 'password'";
    String changeNameQuery = "UPDATE users SET login = LOGIN WHERE login = OLD_Login";

    public static Connection connection;
    public static Statement statement;
    public static ResultSet resultSet;

    public static void setConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:NICKNAMES.s2db");
    }

    public static void createDb() throws SQLException {
        statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS 'users'" +
                "('login' TEXT PRIMARY KEY, 'password' TEXT, 'nickname' TEXT);");
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) throws SQLException {
        resultSet = statement.executeQuery("SELECT * FROM users WHERE login = '" + login + "' AND password = '" + password + "'");
        String nickname = resultSet.getString("nickname");
        return nickname;
    }

//    @Override
//    public String changeNicknameByLogin(String newLogin, String oldLogin) throws Exception {
//        resultSet = statement.executeQuery("UPDATE users SET login = '" + newLogin + "' WHERE login'" + oldLogin + "'");
//        return newLogin;
//    }

    public static void writeDB() throws SQLException {
        try {
            statement.execute("INSERT INTO 'users' ('login', 'password', 'nickname') VALUES ('qwe', 'qwe', 'qwe')");
            statement.execute("INSERT INTO 'users' ('login', 'password', 'nickname') VALUES ('asd', 'asd', 'asd')");
            statement.execute("INSERT INTO 'users' ('login', 'password', 'nickname') VALUES ('zxc', 'zxc', 'zxc')");
        } catch (SQLException e){
            System.out.println("Sth is nearly to be wrong");
        }
    }

    public SimpleAuthService() throws SQLException, ClassNotFoundException {
        setConnection();
        createDb();
        writeDB();
    }

    public void closeDB() throws SQLException {
        resultSet.close();
        statement.close();
        connection.close();
    }

//    public String checkNull(String st) throws Exception {
//        if(st == null){
//            throw new Exception("Your login/password is invalid");
//        }
//        return st.trim();
//    }

//    private class UserData {
//        String login;
//        String password;
//        String nickname;
//
//        public UserData(String login, String password, String nickname) {
//            this.login = login;
//            this.password = password;
//            this.nickname = nickname;
//        }
//    }
//
//    private List<UserData> users;



//    public SimpleAuthService() {
//        users = new ArrayList<>();
//        users.add(new UserData("qwe", "qwe", "qwe"));
//        users.add(new UserData("asd", "asd", "asd"));
//        users.add(new UserData("zxc", "zxc", "zxc"));
//        for (int i = 1; i < 10; i++) {
//            users.add(new UserData("login" + i, "pass" + i, "nick" + i));
//        }
//    }

}
