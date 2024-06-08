package de.jonas.refersystem;

import java.sql.*;
import java.util.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataBasePool {
    HikariDataSource hikari;

    public void init() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("db-hikari");
        config.setDriverClassName("org.mariadb.jdbc.Driver");
        config.setJdbcUrl("jdbc:mariadb://192.168.88.100:3306/referSystem");
        config.setUsername("referSystem");
        config.setPassword("r0S7p5KmmJWmp2iaKZtlEDrBHFzqNLCvA6M3dN6WbhF6uB7JwmRVcYjEVJUuB3jIZ6j0OtIwgHDRfaGu9PQyY2LRN9sPfzhGG6ffYrZkWZgrzfNiAaZ0pcGr1adR3lHH");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(1);
        config.setMaxLifetime(0);
        config.setKeepaliveTime(120000);
        config.setConnectionTimeout(30000);

        this.hikari = new HikariDataSource(config);
        deregisterDriver("org.mariadb.jdbc.Driver");
    }

    public void shutdown() {
        if (this.hikari != null) {
            this.hikari.close();
        }
    }

    private static void deregisterDriver(String driverClassName) {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getName().equals(driverClassName)) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }

    public Connection getConnection() throws SQLException {
        if (this.hikari == null) {
            throw new SQLException("Unable to get a connection from the pool. (hikari is null)");
        }

        Connection connection = this.hikari.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to get a connection from the pool. (getConnection returned null)");
        }

        return connection;
    }

    public void createTableRewards() throws SQLException {
        Connection con = getConnection();
        String sqlCreate = "CREATE TABLE IF NOT EXISTS rewards (`uuid` UUID NOT NULL," +
                " `reward` INTEGER NOT NULL) ENGINE = InnoDB;";

        Statement stmt = con.createStatement();
        stmt.execute(sqlCreate);
    }

    public void createTableUUIDS() throws SQLException {
        Connection con = getConnection();
        String sqlCreate = "CREATE TABLE IF NOT EXISTS uuids (`uuid` UUID NOT NULL," +
                " `InvitedUUID` UUID NOT NULL) ENGINE = InnoDB;";

        Statement stmt = con.createStatement();
        stmt.execute(sqlCreate);
    }

    public static void setTableFromDBRewards(DataBasePool pool, UUID uuid) {
        String querry = "INSERT INTO `rewards` (`uuid`, `reward`) VALUES (?, 1);";

        try {
            Connection con = pool.getConnection();
            PreparedStatement sel = con.prepareStatement(querry);
            sel.setObject(1, uuid);
            sel.executeUpdate();
            sel.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setTableFromDBUUIDS(DataBasePool pool, UUID uuid, UUID invitedUuid) {
        String querry = "INSERT INTO `uuids` (`uuid`, `InvitedUUID`) VALUES (?, ?);";

        try {
            Connection con = pool.getConnection();
            PreparedStatement sel = con.prepareStatement(querry);
            sel.setObject(1, uuid);
            sel.setObject(2, invitedUuid);
            sel.executeUpdate();
            sel.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setTableFromDBRewards(DataBasePool pool, UUID uuid, int reward) {
        String querry = "INSERT INTO `rewards` (`uuid`, `reward`) VALUES (?, ?);";

        try {
            Connection con = pool.getConnection();
            PreparedStatement sel = con.prepareStatement(querry);
            sel.setObject(1, uuid);
            sel.setObject(2, reward);
            sel.executeUpdate();
            sel.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static UUID getTableFromDBRewards(DataBasePool pool, UUID uuid) {
        String querry = "SELECT `uuids`.`uuid` FROM `uuids` WHERE `uuids`.`InvitedUUID` = ?;";

        try {
            Connection con = pool.getConnection();
            PreparedStatement sel = con.prepareStatement(querry);
            sel.setObject(1, uuid);
            ResultSet res = sel.executeQuery();
            res.first();
            UUID inv = (UUID) res.getObject("uuid");
            sel.close();
            con.close();
            return inv;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static List<Integer> getRewardsLeft(DataBasePool pool, UUID uuid) {
        String querry = "DELETE FROM `rewards` WHERE `rewards`.`uuid` = ? RETURNING `rewards`.`uuid`, `rewards`.`reward`;";

        try {
            Connection con = pool.getConnection();
            PreparedStatement sel = con.prepareStatement(querry);
            sel.setObject(1, uuid);
            ResultSet res = sel.executeQuery();
            List<Integer> list = new ArrayList<>();
            for (boolean a = res.first(); a; a = res.next() ) {
                list.add(res.getInt("reward"));
            }
            sel.close();
            con.close();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
