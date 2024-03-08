package DAL;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

import static java.sql.DriverManager.registerDriver;

public class ProductDAO {
    static ArrayList<Product> products = new ArrayList<>();
    Connection connection;

    public ProductDAO(Connection connection) {
        this.connection = connection;
    }

    public void getProductDataViaConsole() {
        //make the client enters that data from console
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Do you want to add a product? (yes/no)||(y/n)");
            String answer = scanner.nextLine();
            if (answerIsNo(answer)) break;
            System.out.println("Enter the product name: ");
            String name = scanner.nextLine();
            System.out.println("Enter the product price: ");
            double price = scanner.nextDouble();
            scanner.nextLine(); // consume the newline character
            products.add(new Product(name, price));
        }
    }

    /**
     * This method inserts a list of products into the products table
     *
     * @param stmt: the prepared statement
     */
    public void bulkInsertProducts(PreparedStatement stmt) {
        try {
            for (Product product : products) {
                insertProduct(product.getName(), product.getPrice(), stmt);
                System.out.println("Products added successfully");
            }
            commitTransaction(connection);
        } catch (SQLException e) {
            rollbackTransaction(connection);
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }

    }
    //bulk insert a list of products

    /**
     * This method inserts a product into the products table
     *
     * @param productName   : the name of the product
     * @param productPrice: the price of the product
     * @throws SQLException : if an error occurs
     */
    private void insertProduct(String productName, double productPrice, PreparedStatement preparedStatement) throws SQLException {
        try {
            preparedStatement.setString(1, productName);
            preparedStatement.setDouble(2, productPrice);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This method to Create products table in the database
     *
     * @throws SQLException: if an error occurs
     */
    public void createTableStatement(Connection connection) throws SQLException {
        //create a table
        var stmt = getStatement(connection);
        String createTableSql = "CREATE TABLE IF NOT EXISTS products (id SERIAL PRIMARY KEY, name VARCHAR(100), price DECIMAL(10, 2))";
        stmt.executeUpdate(createTableSql);
    }

    public Statement getStatement(Connection connection) throws SQLException {
        return connection.createStatement();
    }

    public void alterProduct(PreparedStatement stmt) throws SQLException {
        var product = getProductToBeUpdated();
        try {
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setInt(3, product.getId());
            var updated = stmt.executeUpdate();
            commitTransaction(connection);
            System.out.printf("Product updated successfully. %d row(s) affected%n\n", updated);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            rollbackTransaction(connection);
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    private Product getProductToBeUpdated() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the product Id to be Updated : ");
        Integer id = scanner.nextInt();
        System.out.println("Enter the new product name: ");
        String newName = scanner.nextLine();
        System.out.println("Enter the new product price: ");
        double newPrice = scanner.nextDouble();
        scanner.nextLine(); // consume the newline character
        return new Product(id, newName, newPrice);
    }

    public void queryAndPrintProduct(Statement stmt) throws SQLException {
        //query the table
        String query = "SELECT * FROM products";
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            System.out.println("id: " + rs.getInt("id") + " name: " + rs.getString("name") + " price: " + rs.getDouble("price"));
        }

    }

    //delete a product
    public void deleteProduct(PreparedStatement stmt) throws SQLException {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the product Id to be Updated : ");
            Integer id = scanner.nextInt();
            scanner.nextLine(); // consume the newline character
            stmt.setInt(1, id);
            var deleted = stmt.executeUpdate();
            commitTransaction(connection);
            System.out.printf("Product deleted successfully. %d row(s) affected%n\n", deleted);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            rollbackTransaction(connection);
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    //commit the transaction
    private void commitTransaction(Connection connection) {
        try {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //rollback the transaction
    private void rollbackTransaction(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean answerIsNo(String answer) {
        return answer.equals("no")
                || answer.equals("n");
    }
}
