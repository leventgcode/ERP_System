package com.example.erp_system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.time.Year;

public class InvoicesController {

    @FXML private ComboBox<Customer> customerComboBox;
    @FXML private ComboBox<Product> productComboBox;
    @FXML private TextField quantityField;
    @FXML private Label messageLabel;
    @FXML private Label totalLabel;

    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> colCartProduct;
    @FXML private TableColumn<CartItem, Integer> colCartQuantity;
    @FXML private TableColumn<CartItem, Double> colCartUnitPrice;
    @FXML private TableColumn<CartItem, Double> colCartSubtotal;

    @FXML private TableView<Invoice> invoiceTable;
    @FXML private TableColumn<Invoice, Integer> colInvId;
    @FXML private TableColumn<Invoice, String> colInvNumber;
    @FXML private TableColumn<Invoice, String> colInvCustomer;
    @FXML private TableColumn<Invoice, Double> colInvTotal;
    @FXML private TableColumn<Invoice, String> colInvDate;

    private final ObservableList<CartItem> cartList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Sepet tablosu sütunları
        colCartProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colCartQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCartUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colCartSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        cartTable.setItems(cartList);

        // Faturalar tablosu sütunları
        colInvId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colInvNumber.setCellValueFactory(new PropertyValueFactory<>("invoiceNumber"));
        colInvCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colInvTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colInvDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        // ComboBox görünüm ayarları
        customerComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Customer c) {
                return c == null ? "" : c.getFullName();
            }
            @Override
            public Customer fromString(String s) { return null; }
        });

        productComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Product p) {
                return p == null ? "" : p.getName() + " (Stok: " + p.getStockQuantity() + " - " + p.getPrice() + " TL)";
            }
            @Override
            public Product fromString(String s) { return null; }
        });

        loadCustomers();
        loadProducts();
        loadInvoices();
    }

    private void loadCustomers() {
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        String query = "SELECT * FROM customers";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                customers.add(new Customer(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address")
                ));
            }
            customerComboBox.setItems(customers);
        } catch (Exception e) {
            showMessage("Müşteriler yüklenemedi!", true);
        }
    }

    private void loadProducts() {
        ObservableList<Product> products = FXCollections.observableArrayList();
        String query = "SELECT * FROM products WHERE stock_quantity > 0";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("id"),
                        rs.getString("barcode"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock_quantity")
                ));
            }
            productComboBox.setItems(products);
        } catch (Exception e) {
            showMessage("Ürünler yüklenemedi!", true);
        }
    }

    private void loadInvoices() {
        ObservableList<Invoice> invoices = FXCollections.observableArrayList();
        String query = """
                SELECT i.id, i.invoice_number,
                       COALESCE(c.full_name, 'Silinmiş Müşteri') AS customer_name,
                       s.total_amount, i.created_at
                FROM invoices i
                JOIN sales s ON i.sale_id = s.id
                LEFT JOIN customers c ON s.customer_id = c.id
                ORDER BY i.id DESC
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                invoices.add(new Invoice(
                        rs.getInt("id"),
                        rs.getString("invoice_number"),
                        rs.getString("customer_name"),
                        rs.getDouble("total_amount"),
                        rs.getString("created_at")
                ));
            }
            invoiceTable.setItems(invoices);
        } catch (Exception e) {
            showMessage("Faturalar yüklenemedi!", true);
            e.printStackTrace();
        }
    }

    @FXML
    protected void onAddToCartClick(ActionEvent event) {
        Product selectedProduct = productComboBox.getValue();
        String qtyText = quantityField.getText().trim();

        if (selectedProduct == null || qtyText.isEmpty()) {
            showMessage("Hata: Lütfen bir ürün seçin ve adet girin!", true);
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(qtyText);
            if (quantity <= 0) {
                showMessage("Hata: Adet 0'dan büyük olmalıdır!", true);
                return;
            }
        } catch (NumberFormatException e) {
            showMessage("Hata: Geçerli bir adet sayısı girin!", true);
            return;
        }

        // Sepette bu ürün zaten varsa toplam adedi stokla karşılaştır
        CartItem existingItem = null;
        for (CartItem item : cartList) {
            if (item.getProductId() == selectedProduct.getId()) {
                existingItem = item;
                break;
            }
        }

        int totalRequested = quantity + (existingItem != null ? existingItem.getQuantity() : 0);
        if (totalRequested > selectedProduct.getStockQuantity()) {
            showMessage("Hata: Yetersiz stok! Mevcut stok: " + selectedProduct.getStockQuantity(), true);
            return;
        }

        if (existingItem != null) {
            existingItem.addQuantity(quantity);
            cartTable.refresh();
        } else {
            cartList.add(new CartItem(
                    selectedProduct.getId(),
                    selectedProduct.getName(),
                    quantity,
                    selectedProduct.getPrice()
            ));
        }

        quantityField.clear();
        updateTotalLabel();
        showMessage("Ürün sepete eklendi.", false);
    }

    @FXML
    protected void onRemoveFromCartClick(ActionEvent event) {
        CartItem selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("Hata: Çıkarmak için sepetten bir ürün seçin!", true);
            return;
        }
        cartList.remove(selected);
        updateTotalLabel();
    }

    @FXML
    protected void onCompleteSaleClick(ActionEvent event) {
        Customer selectedCustomer = customerComboBox.getValue();
        if (selectedCustomer == null) {
            showMessage("Hata: Lütfen bir müşteri seçin!", true);
            return;
        }
        if (cartList.isEmpty()) {
            showMessage("Hata: Sepet boş! Önce ürün ekleyin.", true);
            return;
        }

        double totalAmount = calculateTotal();

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Transaction başlat

            try {
                // 1. Satış kaydını oluştur (sales)
                int saleId = -1;
                String saleQuery = "INSERT INTO sales (customer_id, user_id, total_amount) VALUES (?, ?, ?)";
                try (PreparedStatement psSale = conn.prepareStatement(saleQuery, Statement.RETURN_GENERATED_KEYS)) {
                    psSale.setInt(1, selectedCustomer.getId());
                    psSale.setNull(2, Types.INTEGER);
                    psSale.setDouble(3, totalAmount);
                    psSale.executeUpdate();

                    try (ResultSet generatedKeys = psSale.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            saleId = generatedKeys.getInt(1);
                        }
                    }
                }

                // 2. Satılan ürünleri kaydet (sale_items) ve stoktan düş (products)
                String itemQuery = "INSERT INTO sale_items (sale_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
                String stockQuery = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE id = ?";

                try (PreparedStatement psItem = conn.prepareStatement(itemQuery);
                     PreparedStatement psStock = conn.prepareStatement(stockQuery)) {
                    for (CartItem item : cartList) {
                        psItem.setInt(1, saleId);
                        psItem.setInt(2, item.getProductId());
                        psItem.setInt(3, item.getQuantity());
                        psItem.setDouble(4, item.getUnitPrice());
                        psItem.executeUpdate();

                        psStock.setInt(1, item.getQuantity());
                        psStock.setInt(2, item.getProductId());
                        psStock.executeUpdate();
                    }
                }

                // 3. Faturayı oluştur (invoices)
                String invoiceNumber = "INV-" + Year.now().getValue() + "-" + String.format("%04d", saleId);
                String invoiceQuery = "INSERT INTO invoices (sale_id, invoice_number) VALUES (?, ?)";
                try (PreparedStatement psInv = conn.prepareStatement(invoiceQuery)) {
                    psInv.setInt(1, saleId);
                    psInv.setString(2, invoiceNumber);
                    psInv.executeUpdate();
                }

                conn.commit(); // Her şey başarılıysa onayla

                cartList.clear();
                updateTotalLabel();
                customerComboBox.setValue(null);
                productComboBox.setValue(null);
                loadProducts(); // Güncel stokları yenile
                loadInvoices(); // Fatura listesini yenile
                showMessage("Başarılı! Fatura kesildi: " + invoiceNumber, false);

            } catch (Exception ex) {
                conn.rollback(); // Hata olursa tüm işlemleri geri al
                throw ex;
            }
        } catch (Exception e) {
            showMessage("Satış hatası: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    private double calculateTotal() {
        double total = 0;
        for (CartItem item : cartList) {
            total += item.getSubtotal();
        }
        return total;
    }

    private void updateTotalLabel() {
        totalLabel.setText(String.format("Toplam: %.2f TL", calculateTotal()));
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        messageLabel.setText(message);
    }
}