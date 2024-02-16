package pl.coderslab.entity;

import java.util.Arrays;

public class MainDao {
    public static void main(String[] args) {
        User user2 = new User("user1@gmail145.com", "user315", "ps5452mjjhjgh");
        //user2.setId(3);
        UserDao userDao = new UserDao();
       // User user1Id = userDao.create(user2);
       // System.out.println(user1Id.getId());
        //System.out.println( userDao.read(7));
        //userDao.delete(3);
        int counter = userDao.findAll().length;
        System.out.println(Arrays.toString(userDao.findAll()));
        System.out.println("Table lenght = " +counter);
    }
}
