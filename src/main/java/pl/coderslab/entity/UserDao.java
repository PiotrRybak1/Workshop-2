package pl.coderslab.entity;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Scanner;

public class UserDao {
    private static final String CREATE_USER_QUERY = """
            INSERT INTO users( email,username, password) 
            VALUES (?, ?, ?);
            """;

    private static final String READ_USER = """
            select id,email,username, password from users
            where id = ?;
            """;
    private static final String UPDATE_USER = """
            update users
            set email = ?,
            username = ?,
            password = ?
            where id= ?;
            """;
    private static final String FIND_ALL = """
            select * from users;
            """;

    private static final String DELETE_USER = """
            delete from users
            where id = ?;
            """;


    public User create(User user) {

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement preStat = conn.prepareStatement(CREATE_USER_QUERY, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preStat.setString(1, user.getEmail());
            preStat.setString(2, user.getUserName());
            preStat.setString(3, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            preStat.executeUpdate();
            ResultSet rs = preStat.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                user.setId(id);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return user;

    }

    public User read(int userID) {
        User user = new User();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement preStat = conn.prepareStatement(READ_USER)) {
            preStat.setInt(1, userID);
            ResultSet rs = preStat.executeQuery();
            while (rs.next()) {
                user = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return user;
    }

    public void update(User user) {
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement preStat = conn.prepareStatement(UPDATE_USER)) {
            preStat.setString(1, user.getEmail());
            preStat.setString(2, user.getUserName());
            preStat.setString(3, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            preStat.setInt(4, user.getId());
            int counter = preStat.executeUpdate();
            System.out.println("Update values  = " + counter);
        } catch (SQLException e) {
            System.out.println(e);
        }

    }

    public void delete(int userId) {
        Scanner scanner = new Scanner(System.in);
        boolean verdict = areYouSure(scanner);
        if (verdict) {
            try (Connection conn = DbUtil.getConnection();
                 PreparedStatement statement = conn.prepareStatement(DELETE_USER)) {
                statement.setInt(1, userId);
                statement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean areYouSure(Scanner scanner) {
        System.out.println("Are you sure you want to delete this record? ");
        System.out.println("Enter T if yes or enter N if no");
        boolean isTrue = false;
        String decision = scanner.nextLine();
        if (decision.equalsIgnoreCase("T"))
            isTrue = true;
        else if (decision.equalsIgnoreCase("N")) {
            isTrue = false;
        } else System.out.println("Invalid entry, enter T if yes or enter N if no");
        return isTrue;
    }

    private User[] addToArray(User u, User[] users) {
        User[] tmpUsers = Arrays.copyOf(users, users.length + 1);
        tmpUsers[users.length] = u;
        return tmpUsers;
    }

    public User[] findAll() {

        User[] resultTab = new User[0];
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement statement = conn.prepareStatement(FIND_ALL)) {
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                resultTab = addToArray(new User(set.getInt(1), set.getString(2), set.getString(3), set.getString(4)), resultTab);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultTab;
    }


}