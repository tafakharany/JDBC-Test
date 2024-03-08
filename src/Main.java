import DAL.ProductDAO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) throws SQLException {
        var connection = getConnection();
        var productDao = new ProductDAO(connection);
        try {
            //prepare statements for the queries
            PreparedStatement insertStmt = connection.prepareStatement("INSERT INTO products (name, price) VALUES (?, ?)");
            PreparedStatement updateStmt = connection.prepareStatement("UPDATE products SET name = ?, price = ? WHERE name = ? Where id = ?");
            PreparedStatement deleteStmt = connection.prepareStatement("DELETE FROM products WHERE Id = ?");

            Scanner scanner = new Scanner(System.in);
            System.out.println("Select an option: ");
            System.out.println("1. Add a product");
            System.out.println("2. Get all products");
            System.out.println("3. Alter a product");
            System.out.println("4. Delete a product:");
            System.out.println("5.Exit");
            System.out.print("Enter an Option: ");
            var command = scanner.nextInt();
            switch (command) {
                case 1:
                    productDao.getProductDataViaConsole();
                    productDao.bulkInsertProducts(insertStmt);
                    break;
                case 2:
                    productDao.queryAndPrintProduct(productDao.getStatement(connection));
                    break;
                case 3:
                    productDao.alterProduct(updateStmt);
                    break;
                case 4:
                    productDao.deleteProduct(deleteStmt);
                    break;
                case 5:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid command");
                    break;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            productDao.closeConnection(connection);
        }
    }

    /**
     * This method returns a connection to the postgres database
     *
     * @return Connection
     * @throws SQLException: if the connection fails
     */
    public static Connection getConnection() throws SQLException {
        String username = "taha_admin";
        String password = "P@ssw0rd";
        String url = "jdbc:postgresql://localhost:5432/eShopDb_Java";
        DriverManager.registerDriver(new org.postgresql.Driver());
        Connection connection = DriverManager.getConnection(url, username, password);
        connection.setAutoCommit(false);
        return connection;
    }
}