package task.lab_assighment13;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddItemController {

    @FXML
    private Button addItemButton;

    @FXML
    private Button editItemButton;

    @FXML
    private Button removeItemButton;

    @FXML
    private TextField itemNameTextField;

    @FXML
    private TextField itemIdTextField;

    @FXML
    private TextField itemPriceTextField;

    @FXML
    private TableColumn<Product, Integer> productIdColumn;

    @FXML
    private TableColumn<Product, String> productNameColumn;

    @FXML
    private TableColumn<Product, Double> productPriceColumn;

    @FXML
    private TableView<Product> tableView;

    public void initialize() {
        // Set up column bindings
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        productPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        addItemButton.setOnAction(this::Add_handler);
        editItemButton.setOnAction(this::Remove_handler);
        removeItemButton.setOnAction(this::Edit_handler);

        // Load data into table view
        loadData();
    }

    @FXML
    void Add_handler(ActionEvent event) {

        String name = itemNameTextField.getText();
        double price = Double.parseDouble(itemPriceTextField.getText());

        String sql = "INSERT INTO products (product_name, price) VALUES (?, ?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.executeUpdate();


            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void Edit_handler(ActionEvent event) {
        Product selectedProduct = tableView.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            String name = itemNameTextField.getText();
            double price = Double.parseDouble(itemPriceTextField.getText());

            String sql = "UPDATE products SET product_name = ?, price = ? WHERE id = ?";
            try (Connection conn = DBConnector.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setDouble(2, price);
                pstmt.setInt(3, selectedProduct.getId());
                pstmt.executeUpdate();

                // Refresh table view after editing data
                loadData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // Inform the user to select an item for editing
            JOptionPane.showMessageDialog(null, "Please select an item to edit.");
        }
    }

    @FXML
    void Remove_handler(ActionEvent event) {
        Product selectedProduct = tableView.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            String sql = "DELETE FROM products WHERE id = ?";
            try (Connection conn = DBConnector.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, selectedProduct.getId());
                pstmt.executeUpdate();

                // Refresh table view after removing data
                loadData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // Inform the user to select an item for removal
            JOptionPane.showMessageDialog(null, "Please select an item to remove.");
        }
    }


    private void loadData() {
        tableView.getItems().clear(); // Clear existing data
        String sql = "SELECT * FROM products";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("product_name");
                double price = rs.getDouble("price");
                tableView.getItems().add(new Product(id, name, price));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
