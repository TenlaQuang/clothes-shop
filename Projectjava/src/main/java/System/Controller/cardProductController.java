package System.Controller;

import System.Model.OrderItem;
import System.Model.Product;
import System.Model.Size;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.query.Query;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class cardProductController implements Initializable {
    @FXML
    private Button c_add;

    @FXML
    private ImageView c_image;

    @FXML
    private Label c_money;

    @FXML
    private Label c_name;

    @FXML
    private ComboBox<?> c_size;


    private String[] sizelist={"Small","Medium","Large"};
    private Product prodData;
    private static SessionFactory sessionFactory;
    public cardProductController() {
        if (sessionFactory == null) {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        }
    }


    public void setData(Product prodData) {
        this.prodData = prodData;
        c_name.setText(prodData.getProductName());
        c_money.setText(String.valueOf(prodData.getPrice()));

        Image image = new Image(new ByteArrayInputStream(prodData.getImage()));
        c_image.setImage(image);
    }
    public void sizelist() {
        List<String> list=new ArrayList<String>();
        for(int i=0;i<sizelist.length;i++) {
            list.add(sizelist[i]);
        }
        ObservableList list1 = FXCollections.observableArrayList(list);
        c_size.setItems(list1);
    }



    @FXML
    public void addOrder() {
        String productName = c_name.getText();
        String sizeName = c_size.getSelectionModel().getSelectedItem() != null ? c_size.getSelectionModel().getSelectedItem().toString() : "";
        double price = Double.parseDouble(c_money.getText());

        if (productName.isEmpty() || sizeName.isEmpty() || price < 0) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid Product", "Please choose a size.");
            return;
        }

        try (Session session = sessionFactory.openSession()) {
            // Begin transaction
            session.beginTransaction();

            // Fetch the Product entity based on the product name
            String hqlProduct = "FROM Product p WHERE p.productName = :productName";
            Query<Product> queryProduct = session.createQuery(hqlProduct, Product.class);
            queryProduct.setParameter("productName", productName);
            Product product = queryProduct.uniqueResult();

            // Check if product exists
            if (product == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid Product", "The product does not exist.");
                session.getTransaction().rollback();
                return;
            }

            // Fetch the Size entity
            Size size = product.getSize();

            // Check if size exists and has enough quantity
            int sizeQuantity = 0;
            if (sizeName.equalsIgnoreCase("Small")) {
                sizeQuantity = size.getSizeQuantityS();
            } else if (sizeName.equalsIgnoreCase("Medium")) {
                sizeQuantity = size.getSizeQuantityM();
            } else if (sizeName.equalsIgnoreCase("Large")) {
                sizeQuantity = size.getSizeQuantityL();
            }

            if (sizeQuantity <= 0) {
                showAlert(Alert.AlertType.ERROR, "Error", "Out of Stock", "The selected size is out of stock.");
                session.getTransaction().rollback();
                return;
            }

            // Create new OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setProductName(productName);
            orderItem.setSize(sizeName);
            orderItem.setPrice(price);

            session.save(orderItem);

            if (sizeName.equalsIgnoreCase("Small")) {
                size.setSizeQuantityS(size.getSizeQuantityS() - 1);
            } else if (sizeName.equalsIgnoreCase("Medium")) {
                size.setSizeQuantityM(size.getSizeQuantityM() - 1);
            } else if (sizeName.equalsIgnoreCase("Large")) {
                size.setSizeQuantityL(size.getSizeQuantityL() - 1);
            }

            // Save the updated Size entity
            session.update(size);

            // Commit transaction
            session.getTransaction().commit();


            showAlert(Alert.AlertType.INFORMATION, "Success", "Updated", "The product has been added successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed", "An error occurred while adding the product.");
        }
    }
    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){

        sizelist();
    }

}
