package io;

import java.sql.SQLException;

public class Starter {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Server server = new Server(8899);
    }

}
