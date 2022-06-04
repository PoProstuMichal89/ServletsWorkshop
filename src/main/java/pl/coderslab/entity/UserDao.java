package pl.coderslab.entity;

import org.mindrot.jbcrypt.BCrypt;
import pl.coderslab.DbUtil;

import java.sql.*;
import java.util.Arrays;

public class UserDao {

    // Tworzenie użytkownika
    private static final String CreateUser =            // prywatny - tylko do użycia  w tej klasie/ statyczny - bo kązdy obiekt tej klasy będzie realizował to zapytanie tak samo
            "INSERT INTO users(username, email, password) VALUES (?, ?, ?)";

    public User create(User user) {
        try (Connection conn = DbUtil.connect()) {
            PreparedStatement statement =
                    conn.prepareStatement(CreateUser, Statement.RETURN_GENERATED_KEYS);  //RETURN_GENERATED_KEYS -> ogólna metoda!
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getEmail());
            statement.setString(3, hashPassword(user.getPassword()));
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String hashPassword(String password) {
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());

        return hashed;
    }


    // odczytywanie użytkownika

    public static final String readUser = "SELECT id, username, email, password FROM users WHERE id = ?";

    public User read(int userId) {
        try (Connection conn = DbUtil.connect()) {
            PreparedStatement statement = conn.prepareStatement(readUser);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User();  //tworzy wewnątrz metody nowy obiekt usera
                user.setId(resultSet.getInt("id")); //ustawia w nowym obiekcie to co otrzyma z bazy danych. odczyt nastąpi na poziomie obikeu klasy User
                user.setUserName(resultSet.getString("username"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                return user; // zwraca obiekt user z ustawionymi parametrami z bazy danych. W MainDAO przypisujemy sobie tego useara do nowego obiektu do odczytu
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //UPDATE usera
    public static final String updateQuery = "UPDATE users SET username= ?, email =?, password=? WHERE id =?";

    public void update(User user) {

        try (Connection conn = DbUtil.connect()) {
            PreparedStatement stm = conn.prepareStatement(updateQuery);
            stm.setString(1, user.getUserName()); // najpierw  w klasie USer za pomocą setterów ustawiamy nowe dane. Potem wywołujemy na obiekcie UserDAO metodę update i przekazujemy jej zmodyfikowany obiekt. Metoda pobierze dane z obiektu poprzez gettery i przekaże je jako paramet zapytania
            stm.setString(2, user.getEmail());
            stm.setString(3, user.getPassword());
            stm.setInt(4, user.getId());
            stm.executeUpdate(); //jeśli któryś z parametrów nie zostanie zdefiniowany w MainDao to wtedy automatycznie zostanie pdostawiony parametr który był wcześniej (u

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //All users
    private static final String findAllCustomersQuery = "SELECT*FROM users";

    public User[] readAllUsers() {

        try (Connection conn = DbUtil.connect()) {
            User[] users = new User[0];
            PreparedStatement stm = conn.prepareStatement(findAllCustomersQuery);
            ResultSet result = stm.executeQuery();
            while (result.next()) {
                User user = new User();
                user.setId(result.getInt("id"));
                user.setUserName(result.getString("username"));
                user.setEmail(result.getString("email"));
                user.setPassword(result.getString("password"));
                users = addToArray(users, user);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private  User[] addToArray(User[] users, User user ){ // czy ona pownna być tylko prywatna a nie stayczna - dlaczego?
        users= Arrays.copyOf(users, users.length+1);
        users[users.length-1] = user;
        return users;
    }

    private static final String deleteQuery = "DELETE FROM users WHERE id=?";
    public void delete(int userId){
        try(Connection conn = DbUtil.connect()){
            PreparedStatement stm = conn.prepareStatement(deleteQuery);
            stm.setInt(1, userId);
            stm.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }









}
