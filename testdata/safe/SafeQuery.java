import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

public class SafeQuery {

    public void findUser(Connection connection, String name) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM users WHERE name=?"
        );

        stmt.setString(1, name);
        ResultSet result = stmt.executeQuery();
    }
}