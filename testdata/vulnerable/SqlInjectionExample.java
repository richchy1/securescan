import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlInjectionExample {

    public void findUser(Statement stmt, String name) throws SQLException {
        ResultSet result = stmt.executeQuery(
                "SELECT * FROM users WHERE name='" + name + "'"
        );
    }
}