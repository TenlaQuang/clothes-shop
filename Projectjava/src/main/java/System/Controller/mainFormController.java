package System.Controller;

import System.Model.*;
import System.client.SocketHandle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class mainFormController implements Initializable {
    @FXML
    private TextField quantitylXL;

    @FXML
    private Button addBrand;

    @FXML
    private Button addType;

    @FXML
    private ImageView backtologin;

    @FXML
    private Label backtologinBtn;

    @FXML
    private Button customers_btn;

    @FXML
    private TableColumn<?, ?> customers_col_Total;

    @FXML
    private TableColumn<?, ?> customers_col_customerID;

    @FXML
    private TableColumn<?, ?> customers_col_date;

    @FXML
    private TableColumn<?, ?> customers_col_CusName;

    @FXML
    private AnchorPane customers_form;

    @FXML
    private TableView<Customers> customers_tableView;

    @FXML
    private Label dashboard_NC;

    @FXML
    private Label dashboard_NSP;

    @FXML
    private Label dashboard_TI;

    @FXML
    private Label dashboard_TotalI;

    @FXML
    private Button dashboard_btn;

    @FXML
    private AnchorPane dashboard_form;

    @FXML
    private Button inventory_addBtn;

    @FXML
    private ComboBox<String> inventory_brand;

    @FXML
    private Button inventory_btn;

    @FXML
    private Button inventory_clearBtn;

    @FXML
    private TableColumn<?, ?> inventory_col_brand;

    @FXML
    private TableColumn<?, ?> inventory_col_price;

    @FXML
    private TableColumn<?, ?> inventory_col_productID;

    @FXML
    private TableColumn<?, ?> inventory_col_productName;

    @FXML
    private TableColumn<?, ?> inventory_col_quantity;

    @FXML
    private TableColumn<?, ?> inventory_col_type;

    @FXML
    private Button inventory_deleteBtn;

    @FXML
    private AnchorPane inventory_form;

    @FXML
    private ImageView inventory_imageView;

    @FXML
    private Button inventory_importBtn;

    @FXML
    private TextField inventory_price;

    @FXML
    private TextField inventory_productName;

    @FXML
    private TableView<Product> inventory_tableView;

    @FXML
    private ComboBox<String> inventory_type;

    @FXML
    private Button inventory_updateBtn;

    @FXML
    private AnchorPane main_form;

    @FXML
    private TextField menu_amount;

    @FXML
    private Button menu_btn;

    @FXML
    private Label menu_change;

    @FXML
    private TableColumn<?, ?> menu_col_price;

    @FXML
    private TableColumn<?, ?> menu_col_productName;

    @FXML
    private TableColumn<?, ?> menu_col_size;

    @FXML
    private AnchorPane menu_form;

    @FXML
    private GridPane menu_gridPane;

    @FXML
    private Button menu_buyBtn;

    @FXML
    private Button menu_receiptBtn;

    @FXML
    private Button menu_removeBtn;

    @FXML
    private ScrollPane menu_scrollPane;

    @FXML
    private TableView<OrderItem> menu_tableView;

    @FXML
    private Label menu_total;

    @FXML
    private TextField quantityl;

    @FXML
    private TextField quantitym;

    @FXML
    private TextField quantitys;

    @FXML
    private AnchorPane suport_form;

    @FXML
    private Button support;

    private SocketHandle socketHandle;
    private ObservableList<Product> productList;
    private static SessionFactory sessionFactory;
    public mainFormController() {
        if (sessionFactory == null) {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        }
    }

    private ObservableList<Product> cardListData = FXCollections.observableArrayList();

//    @FXML
//    public void initialize() {
//        productList = FXCollections.observableArrayList();
//        inventory_tableView.setItems(productList);
//
//        inventory_col_productID.setCellValueFactory(new PropertyValueFactory<>("productID"));
//        inventory_col_productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
//        inventory_col_brand.setCellValueFactory(new PropertyValueFactory<>("brand"));
//        inventory_col_type.setCellValueFactory(new PropertyValueFactory<>("type"));
//        inventory_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
//        inventory_col_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
//
//        loadProductData();
//    }

    private void loadProductData() {
        try (Session session = sessionFactory.openSession()) {
            List<Product> products = session.createQuery("from Product", Product.class).list();
            productList.setAll(products);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleBackToLoginClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/project/projectjava/login.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene newScene = new Scene(root);

            currentStage.setScene(newScene);
            currentStage.show();

            System.out.println("Successfully loaded loginForm.fxml and switched scenes");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading mainForm.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteButton(ActionEvent event) {
        Product selectedProduct = inventory_tableView.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            try (Session session = sessionFactory.openSession()) {
                Transaction transaction = session.beginTransaction();

                session.delete(selectedProduct);

                Query<Product> productsQuery = session.createQuery("FROM Product WHERE productID = :sizeID", Product.class);
                productsQuery.setParameter("sizeID", selectedProduct.getSize().getSizeID());
                List<Product> productsToDelete = productsQuery.list();

                for (Product product : productsToDelete) {
                    session.delete(product);
                }

                transaction.commit();

                inventory_tableView.getItems().remove(selectedProduct);

                showAlert(Alert.AlertType.INFORMATION, "Success", "Product Deleted", "The product and related products have been deleted successfully.");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to Delete Product", "An error occurred while deleting the product and related products from the database.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No Product Selected", "Please select a product to delete.");
        }
    }


    public void populateBrandComboBox() {
        try (Session session = sessionFactory.openSession()) {

            session.beginTransaction();

            Query<String> query = session.createQuery("select b.brandName from Brand b", String.class);
            ObservableList<String> brandNames = FXCollections.observableArrayList(query.list());

            inventory_brand.setItems(brandNames);

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void switchForm(ActionEvent event) {

        if (event.getSource() == dashboard_btn) {
            dashboard_form.setVisible(true);
            inventory_form.setVisible(false);
            menu_form.setVisible(false);
            customers_form.setVisible(false);
            suport_form.setVisible(false);
            updateDashboardUserCount();
            getAllCustomers();
            loadCusData();
        } else if (event.getSource() == inventory_btn) {
            dashboard_form.setVisible(false);
            inventory_form.setVisible(true);
            menu_form.setVisible(false);
            customers_form.setVisible(false);
            suport_form.setVisible(false);
            loadProductData();
            populateBrandComboBox();
            populateTypeComboBox();

        } else if (event.getSource() == menu_btn) {
            dashboard_form.setVisible(false);
            inventory_form.setVisible(false);
            menu_form.setVisible(true);
            customers_form.setVisible(false);
            suport_form.setVisible(false);
            menuDisplayCard();
            loadOrderData();
            menuGetTotal();

        } else if (event.getSource() == customers_btn) {
            dashboard_form.setVisible(false);
            inventory_form.setVisible(false);
            menu_form.setVisible(false);
            customers_form.setVisible(true);
            suport_form.setVisible(false);
            loadCusData();

        }else if(event.getSource()== support){
            dashboard_form.setVisible(false);
            inventory_form.setVisible(false);
            menu_form.setVisible(false);
            customers_form.setVisible(false);
            suport_form.setVisible(true);

        }

    }

    @FXML
    private void handleAddBrandButton() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Brand");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter the name of the new brand:");


        Optional<String> result = dialog.showAndWait();
        
        result.ifPresent(brandName -> {

            if (brandName.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid Brand Name", "Brand name cannot be empty.");
                return;
            }

            addBrand(brandName);
        });
    }

    private void addBrand(String brandName) {
        try (Session session = sessionFactory.openSession()) {

            Transaction transaction = session.beginTransaction();

            Brand brand = new Brand();
            brand.setBrandName(brandName);

            session.save(brand);

            transaction.commit();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Brand Added",
                    "The brand '" + brandName + "' has been added successfully.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to Add Brand",
                    "An error occurred while adding the brand to the database.");
            e.printStackTrace();
        }
    }

    public void populateTypeComboBox() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query<String> query = session.createQuery("select t.typeName from Type t", String.class);
            ObservableList<String> typeNames = FXCollections.observableArrayList(query.list());
            inventory_type.setItems(typeNames);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleAddTypeButton() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Type");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter the name of the new type:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(typeName -> {
            if (typeName.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid Type Name", "Type name cannot be empty.");
                return;
            }
            addType(typeName);
        });
    }
    private void addType(String typeName) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Type type = new Type();
            type.setTypeName(typeName);
            session.save(type);
            transaction.commit();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Type Added",
                    "The type '" + typeName + "' has been added successfully.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to Add Type",
                    "An error occurred while adding the type to the database.");
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
    @FXML
    private ImageView imageView;

    private File selectedFile;

    @FXML
    private void handleSelectImageButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            inventory_imageView.setImage(image);
        }
    }
    @FXML
    private void handleAddButton() {
        String productName = inventory_productName.getText();
        String brand = inventory_brand.getValue();
        String type= inventory_type.getValue();
        String price = inventory_price.getText();

        String quantityst = quantitys.getText().trim();
        String quantitymt = quantitym.getText().trim();
        String quantitylt = quantityl.getText().trim();

        if(productName.isEmpty()||brand==null||type==null||price.isEmpty()||quantityst.isEmpty()||quantitymt.isEmpty()||quantitylt.isEmpty() || selectedFile == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid Product", "cannot be empty.");
            return;
        }

        try {
            byte[] imageBytes = null;
            try {
                imageBytes = Files.readAllBytes(selectedFile.toPath());
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to Read Image", "An error occurred while reading the selected image.");
                e.printStackTrace();
                return;
            }
            int quantity = Integer.parseInt(quantitys.getText()) + Integer.parseInt(quantitym.getText()) + Integer.parseInt(quantityl.getText());
            try (Session session = sessionFactory.openSession()) {
                Transaction transaction = session.beginTransaction();

                Query<Size> sizeQuery = session.createQuery("FROM Size WHERE sizeQuantityS = :quantitys AND sizeQuantityM = :quantitym AND sizeQuantityL = :quantityl", Size.class);

                sizeQuery.setParameter("quantitys", Integer.parseInt(quantitys.getText()));
                sizeQuery.setParameter("quantitym", Integer.parseInt(quantitym.getText()));
                sizeQuery.setParameter("quantityl", Integer.parseInt(quantityl.getText()));
                Size size = sizeQuery.uniqueResult();

                if (size == null) {
                    size = new Size();
                    size.setSizeQuantityS(Integer.parseInt(quantitys.getText()));
                    size.setSizeQuantityM(Integer.parseInt(quantitym.getText()));
                    size.setSizeQuantityL(Integer.parseInt(quantityl.getText()));
                    session.save(size);
                }

                Product product = new Product();
                product.setProductName(productName);
                product.setBrand(brand);
                product.setType(type);
                product.setPrice(Double.parseDouble(price));
                product.setQuantity(quantity);
                product.setSize(size);
                product.setImage(imageBytes);
                session.save(product);

                transaction.commit();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Product Added", "The product has been added successfully.");
                clearInputFields();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to Add Product", "An error occurred while adding the product to the database.");
            }

        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleUpdateButton(ActionEvent event) {
        Product selectedProduct = inventory_tableView.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            inventory_productName.setText(selectedProduct.getProductName());
            inventory_brand.setValue(selectedProduct.getBrand());
            inventory_type.setValue(selectedProduct.getType());
            inventory_price.setText(Double.toString(selectedProduct.getPrice()));

            Size size = selectedProduct.getSize();
            if (size != null) {
                quantitys.setText(Integer.toString(size.getSizeQuantityS()));
                quantitym.setText(Integer.toString(size.getSizeQuantityM()));
                quantityl.setText(Integer.toString(size.getSizeQuantityL()));
            }else {
                quantitys.setText("0");
                quantitym.setText("0");
                quantityl.setText("0");
            }

            inventory_addBtn.setDisable(true);
            inventory_updateBtn.setDisable(false);

            // When update button is clicked again, update the product details in the database
            inventory_updateBtn.setOnAction(event2 -> updateProduct(selectedProduct, size));
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No Product Selected", "Please select a product to update.");
        }
    }

    private void updateProduct(Product product, Size size) {
        String productName = inventory_productName.getText();
        String brand = inventory_brand.getValue();
        String type = inventory_type.getValue();
        String price = inventory_price.getText();
        String quantityst = quantitys.getText().trim();
        String quantitymt = quantitym.getText().trim();
        String quantitylt = quantityl.getText().trim();

        if (productName.isEmpty() || brand == null || type == null || price.isEmpty() || quantityst.isEmpty() || quantitymt.isEmpty() || quantitylt.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid Product", "Fields cannot be empty.");
            return;
        }

        try {
            int quantityS = Integer.parseInt(quantitys.getText());
            int quantityM = Integer.parseInt(quantitym.getText());
            int quantityL = Integer.parseInt(quantityl.getText());
            int totalQuantity = quantityS + quantityM + quantityL;

            try (Session session = sessionFactory.openSession()) {
                Transaction transaction = session.beginTransaction();

                product.setProductName(productName);
                product.setBrand(brand);
                product.setType(type);
                product.setPrice(Double.parseDouble(price));
                product.setQuantity(totalQuantity);

                size.setSizeQuantityS(quantityS);
                size.setSizeQuantityM(quantityM);
                size.setSizeQuantityL(quantityL);

                session.update(product);
                session.update(size);
                transaction.commit();

                showAlert(Alert.AlertType.INFORMATION, "Success", "Product Updated", "The product has been updated successfully.");
                clearInputFields();

                inventory_addBtn.setDisable(false);
                inventory_updateBtn.setDisable(true);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to Update Product", "An error occurred while updating the product.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "Please enter valid numbers for quantities and price.");
        }
    }

    private void clearInputFields() {
        inventory_productName.clear();
        inventory_brand.setValue(null);
        inventory_type.setValue(null);
        inventory_price.clear();
        quantitys.clear();
        quantitym.clear();
        quantityl.clear();
        inventory_imageView.setImage(null);
        selectedFile = null;
    }

//    private void saveProduct(String productName, String brand, String type, String price, int quantity) {
//        try (Session session = sessionFactory.openSession()) {
//            Transaction transaction = session.beginTransaction();
//            Product product = new Product();
//            product.setProductName(productName);
//            product.setBrand(brand);
//            product.setType(type);
//            product.setPrice(Double.parseDouble(price));
//            product.setQuantity(quantity);
//            session.save(product);
//            transaction.commit();
//            session.close();
//        }
//    }

    public ObservableList<Product> menuGetData() {
        ObservableList<Product> listData = FXCollections.observableArrayList();

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Product> criteriaQuery = builder.createQuery(Product.class);
            Root<Product> root = criteriaQuery.from(Product.class);
            criteriaQuery.select(root);

            Query<Product> query = session.createQuery(criteriaQuery);
            List<Product> productList = query.getResultList();

            for (Product prodd : productList) {
                Product prod = new Product(
                        prodd.getProductID(),
                        prodd.getProductName(),
                        prodd.getPrice(),
                        prodd.getImage(),
                        prodd.getSize().getSizeID()
                );
                listData.add(prod);
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listData;
    }

    public void menuDisplayCard() {

        cardListData.clear();
        cardListData.addAll(menuGetData());

        int row = 0;
        int column = 0;

        menu_gridPane.getChildren().clear();
        menu_gridPane.getRowConstraints().clear();
        menu_gridPane.getColumnConstraints().clear();

        for (int q = 0; q < cardListData.size(); q++) {

            try {
                FXMLLoader load = new FXMLLoader();
                load.setLocation(getClass().getResource("/main/resources/project/projectjava/cardProduct.fxml"));
                AnchorPane pane = load.load();
                cardProductController cardC = load.getController();
                cardC.setData(cardListData.get(q));

                if (column == 3) {
                    column = 0;
                    row += 1;
                }

                menu_gridPane.add(pane, column++, row);

                GridPane.setMargin(pane, new Insets(10));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void menuGetTotal() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Query<Double> query = session.createQuery("SELECT SUM(price) FROM OrderItem", Double.class);
            Double totalP = query.uniqueResult();

            session.getTransaction().commit();

            if (totalP != null) {
                menu_total.setText(String.format("%.2f", totalP));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void loadOrderData() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String hql = "FROM OrderItem";
            Query<OrderItem> query = session.createQuery(hql, OrderItem.class);
            List<OrderItem> orderItemList = query.list();

            session.getTransaction().commit();

            ObservableList<OrderItem> orderItems = FXCollections.observableArrayList(orderItemList);
            menu_tableView.setItems(orderItems);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to Load Orders", "An error occurred while loading orders.");
        }
    }

    private String loggedInUsername;

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
    }

    @FXML
    public void menu_buyBtn() {
        ObservableList<OrderItem> orderItems = menu_tableView.getItems();

        if (orderItems.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Empty Order", "Please add items to your order.");
            return;
        }

        loadOrderData();
        menuGetTotal();

        // Calculate the total price locally
        double totalPrice = calculateTotalPrice(orderItems);

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Confirm Purchase");
        confirmation.setContentText("Total Price: $" + totalPrice + "\nAre you sure you want to proceed with the purchase?");
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Send purchase request to the server
                String purchaseRequest = "BUY:" + loggedInUsername + ":" + totalPrice;
                socketHandle.sendMessage(purchaseRequest);

                // Receive response from server
                String response = socketHandle.receiveMessage();
                if ("BUY_SUCCESS".equals(response)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Purchase Completed", "The purchase has been completed successfully.");
                    clearOrderItems();
                    clearOrderItemsInDatabase();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Purchase Failed", "Failed to complete the purchase.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Connection Error", "Failed to connect to server.");
            }

        }

    }

    @FXML
    private void menu_removeBtn(ActionEvent event) {
        OrderItem selectedItem = menu_tableView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            try (Socket socket = new Socket("serverAddress", 12345);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("REMOVE_ITEM:" + selectedItem.getId());

                String response = in.readLine();
                if ("REMOVE_SUCCESS".equals(response)) {
                    menu_tableView.getItems().remove(selectedItem);
                    menu_total.setText("0");

                    showAlert(Alert.AlertType.INFORMATION, "Success", "Item Removed", "The selected item has been removed successfully.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to Remove Item", "An error occurred while removing the selected item.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to Remove Item", "An error occurred while removing the selected item.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No Item Selected", "Please select an item to remove.");
        }
    }
//    @FXML
//    private void handleCreateReceipt() {
//        List<Customers> customersList = getAllCustomers();
//        if (!customersList.isEmpty()) {
//            Customers customer = customersList.get(0); // Lấy khách hàng đầu tiên làm ví dụ
//            double total = customer.getTotal(); // Tổng số tiền của hóa đơn
//
//            createReceipt(customer, total);
//
//            showAlert(Alert.AlertType.INFORMATION, "Receipt Created", null,
//                    "Receipt for customer " + customer.getCusname() + " has been created.");
//        } else {
//            showAlert(Alert.AlertType.WARNING, "No Customers", null,
//                    "No customers found to create receipt.");
//        }
//    }
//
//    private void createReceipt(Customers customer, double total) {
//        Transaction transaction = null;
//        try (Session session = sessionFactory.openSession()) {
//            transaction = session.beginTransaction();
//
//            // Tạo hóa đơn mới
//            Receipt receipt = new Receipt(customer, total, new Date(System.currentTimeMillis()));
//            session.save(receipt);
//
//            transaction.commit();
//
//            // Xuất hóa đơn ra file PDF
//            ReceiptService receiptService = new ReceiptService();
//            receiptService.generatePDFReceipt(receipt);
//        } catch (Exception e) {
//            if (transaction != null) {
//                transaction.rollback();
//            }
//            e.printStackTrace();
//        }
//    }

    //    @FXML
//    private void handleBuyButton() {
//        ObservableList<OrderItem> orderItems = (ObservableList<OrderItem>) menu_tableView.getItems();
//
//        // Kiểm tra xem danh sách đơn hàng có trống không
//        if (orderItems.isEmpty()) {
//            showAlert(Alert.AlertType.WARNING, "Warning", "Empty Order", "Please add items to your order.");
//            return;
//        }
//
//        try (Session session = sessionFactory.openSession()) {
//            Transaction transaction = session.beginTransaction();
//            for (OrderItem orderItem : orderItems) {
//                // Lấy thông tin size từ orderItem
////                Size size ;
////
////
////                // Cập nhật quantityS, quantityM, quantityL
////                size.setQuantityS(size.getQuantityS() - orderItem.getQuantityS());
////                size.setQuantityM(size.getQuantityM() - orderItem.getQuantityM());
////                size.setQuantityL(size.getQuantityL() - orderItem.getQuantityL());
//
//                // Lưu thông tin đơn hàng
//                session.update(size);
//                session.save(orderItem);
//            }
//            transaction.commit();
//            showAlert(Alert.AlertType.INFORMATION, "Success", "Order Placed", "Your order has been successfully placed.");
//        } catch (Exception e) {
//            e.printStackTrace();
//            showAlert(Alert.AlertType.ERROR, "Error", "Failed to Place Order", "An error occurred while processing your order.");
//        }
//
//        // Sau khi mua hàng thành công, xóa danh sách đơn hàng và làm sạch bảng đơn hàng
//        orderItems.clear();
//        menu_tableView.getItems().clear();
//    }
    private double calculateTotalPrice(ObservableList<OrderItem> orderItems) {
        double totalPrice = 0.0;
        for (OrderItem item : orderItems) {
            totalPrice += item.getPrice();
        }
        return totalPrice;
    }

    private void clearOrderItems() {
        menu_tableView.getItems().clear();
        menu_total.setText("0");
    }

    public void loadCusData() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String hql = "FROM Customers";
            Query<Customers> query = session.createQuery(hql, Customers.class);
            List<Customers> customers = query.list();

            session.getTransaction().commit();

            ObservableList<Customers> customers1 = FXCollections.observableArrayList(customers);
            customers_tableView.setItems(customers1);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to Load Customers", "An error occurred while loading customers.");
        }
    }
    private void updateDashboardUserCount() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String hql = "SELECT COUNT(u.username) FROM Users u";
            Query<Long> query = session.createQuery(hql, Long.class);
            Long userCount = query.uniqueResult();

            session.getTransaction().commit();

            dashboard_NC.setText(String.valueOf(userCount));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to Load User Count", "An error occurred while loading user count.");
        }
    }

    private void updateDashboardTotals() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();


            String hqlToday = "SELECT SUM(c.Total) FROM Customers c WHERE c.date = :today";
            Query<Double> queryToday = session.createQuery(hqlToday, Double.class);
            queryToday.setParameter("today", java.sql.Date.valueOf(LocalDate.now()));
            Double todayTotal = queryToday.uniqueResult();


            String hqlOverall = "SELECT SUM(c.Total) FROM Customers c";
            Query<Double> queryOverall = session.createQuery(hqlOverall, Double.class);
            Double overallTotal = queryOverall.uniqueResult();

            session.getTransaction().commit();

            dashboard_TI.setText(todayTotal != null ? String.format("%.2f", todayTotal) : "0.00");
            dashboard_TotalI.setText(overallTotal != null ? String.format("%.2f", overallTotal) : "0.00");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to Load Totals", "An error occurred while loading totals.");
        }
    }


//    public void dashboardIncomeChart() {
//        dashboard_incomeChart.getData().clear();
//
//        String hql = "SELECT date, SUM(Total) FROM Customers GROUP BY date ORDER BY date";
//        try (Session session = sessionFactory.openSession()) {
//            Query<Object[]> query = session.createQuery(hql);
//            List<Object[]> resultList = query.list();
//
//            XYChart.Series<String, Float> chart = new XYChart.Series<>();
//            for (Object[] row : resultList) {
//                String date = (String) row[0];
//                Float total = (Float) row[1];
//                chart.getData().add(new XYChart.Data<>(date, total));
//            }
//
//            dashboard_incomeChart.getData().add(chart);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void dashboardCustomerChart(){
//        dashboard_CustomerChart.getData().clear();
//
//        String hql = "SELECT date, COUNT(Cusid) FROM Customers GROUP BY date ORDER BY date";
//        try (Session session = sessionFactory.openSession()) {
//            Query<Object[]> query = session.createQuery(hql);
//            List<Object[]> resultList = query.list();
//
//            XYChart.Series<String, Integer> chart = new XYChart.Series<>();
//            for (Object[] row : resultList) {
//                String date = (String) row[0];
//                Integer count = (Integer) row[1];
//                chart.getData().add(new XYChart.Data<>(date, count));
//            }
//
//            dashboard_CustomerChart.getData().add(chart);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    public List<Customers> getAllCustomers() {
        Transaction transaction = null;
        List<Customers> customersList = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            customersList = session.createQuery("FROM Customers", Customers.class).list();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return customersList;
    }
    private void clearOrderItemsInDatabase() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();


            Query deleteQuery = session.createQuery("DELETE FROM OrderItem");
            int deletedCount = deleteQuery.executeUpdate();
            System.out.println("Deleted " + deletedCount + " item(s) from OrderItem");

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productList = FXCollections.observableArrayList();
        inventory_tableView.setItems(productList);

        inventory_col_productID.setCellValueFactory(new PropertyValueFactory<>("productID"));
        inventory_col_productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        inventory_col_brand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        inventory_col_type.setCellValueFactory(new PropertyValueFactory<>("type"));
        inventory_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        inventory_col_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        loadProductData();
        populateBrandComboBox();
        populateTypeComboBox();
        menuDisplayCard();
        loadOrderData();
        menuGetTotal();


        inventory_tableView.sortPolicyProperty().set(table -> {
            loadProductData();
            return true;
        });

        menu_col_productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        menu_col_size.setCellValueFactory(new PropertyValueFactory<>("size"));
        menu_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));

        customers_col_customerID.setCellValueFactory(new PropertyValueFactory<>("cusid"));
        customers_col_CusName.setCellValueFactory(new PropertyValueFactory<>("cusname"));
        customers_col_date.setCellValueFactory(new PropertyValueFactory<>("date"));
        customers_col_Total.setCellValueFactory(new PropertyValueFactory<>("total"));

        loadCusData();
        updateDashboardUserCount();
        updateDashboardTotals();
        getAllCustomers();

        List<Customers> customersList = getAllCustomers();



    }

    public void setSocketHandle(SocketHandle socketHandle, String loggedInUsername) {
        this.socketHandle = socketHandle;
        this.loggedInUsername = loggedInUsername;
    }
}
