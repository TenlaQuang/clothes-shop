package System.server;

import System.Model.Customers;
import System.Model.OrderItem;
import System.Model.Product;
import System.Model.Users;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Date;

public class ServerThread extends Thread {
    private Socket clientSocket;
    private SessionFactory sessionFactory;
    private static final Logger logger = LoggerFactory.getLogger(ServerThread.class);

    public ServerThread(Socket clientSocket, SessionFactory sessionFactory) {
        this.clientSocket = clientSocket;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received: " + message);

                String[] parts = message.split(":");
                String command = parts[0];

                switch (command) {
                    case "LOGIN":
                        handleLogin(out, parts);
                        break;
                    case "REGISTER":
                        handleRegister(out, parts);
                        break;
                    case "RESET_PASSWORD":
                        handleResetPassword(out, parts);
                        break;
                    case "CHECK_USER":
                        handleCheckUser(out, parts);
                        break;
                    case "VALIDATE_SECURITY":
                        handleValidateSecurity(out, parts);
                        break;
                    case "BUY":
                        handleBuyCommand(out, parts);
                        break;
                    case "REMOVE_ITEM":
                        handleRemoveItem(out, parts);
                        break;
                    default:
                        out.println("INVALID_COMMAND");
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleLogin(PrintWriter out, String[] parts) {
        if (parts.length == 3 && "LOGIN".equals(parts[0])) {
            String username = parts[1];
            String password = parts[2];

            if (validateUser(username, password)) {
                out.println("LOGIN_SUCCESS");
            } else {
                out.println("LOGIN_FAILED");
            }
        } else {
            out.println("INVALID_COMMAND");
        }
    }

    private void handleRegister(PrintWriter out, String[] parts) {
        if (parts.length == 5 && "REGISTER".equals(parts[0])) {
            String registerResponse = register(parts[1], parts[2], parts[3], parts[4]);
            out.println(registerResponse);
        } else {
            out.println("INVALID_COMMAND");
        }
    }

    private void handleResetPassword(PrintWriter out, String[] parts) {
        if (parts.length == 3 && "RESET_PASSWORD".equals(parts[0])) {
            String username = parts[1];
            String newPassword = parts[2];
            String resetPasswordResponse = resetPassword(username, newPassword);
            out.println(resetPasswordResponse);
        } else {
            out.println("INVALID_COMMAND");
        }
    }

    private void handleCheckUser(PrintWriter out, String[] parts) {
        if (parts.length == 2 && "CHECK_USER".equals(parts[0])) {
            String username = parts[1];
            if (isUserExist(username)) {
                out.println("USER_FOUND");
            } else {
                out.println("USER_NOT_FOUND");
            }
        } else {
            out.println("INVALID_COMMAND");
        }
    }

    private void handleValidateSecurity(PrintWriter out, String[] parts) {
        if (parts.length == 4 && "VALIDATE_SECURITY".equals(parts[0])) {
            String username = parts[1];
            String question = parts[2];
            String answer = parts[3];
            if (validateSecurityAnswer(username, question, answer)) {
                out.println("VALIDATION_SUCCESS");
            } else {
                out.println("VALIDATION_FAILED");
            }
        } else {
            out.println("INVALID_COMMAND");
        }
    }

    private boolean validateUser(String username, String password) {
        try (Session session = sessionFactory.openSession()) {
            Users user = session.get(Users.class, username);

            if (user != null) {

                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String a=passwordEncoder.encode(password);
                System.out.println(a);
                boolean isPasswordMatch = passwordEncoder.matches(password, user.getPassword());
                String storedPassword = user.getPassword();
                System.out.println(storedPassword);
                System.out.println("Password match result: " + isPasswordMatch);
                return isPasswordMatch;
            } else {
                return false; // User not found
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Error occurred
        }
    }

    private String register(String username, String password, String question, String answer) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            Users user = new Users();
            user.setUsername(username);
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            user.setPassword(passwordEncoder.encode(password));
            user.setQuestion(question);
            user.setAnswer(answer);

            session.save(user);
            transaction.commit();
            return "REGISTER_SUCCESS";
        } catch (Exception e) {
            e.printStackTrace();
            return "REGISTER_FAILED";
        }
    }

    private String resetPassword(String username, String newPassword) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Users user = session.get(Users.class, username);
            if (user != null) {
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                user.setPassword(passwordEncoder.encode(newPassword));
                session.update(user);
                transaction.commit();
                return "RESET_SUCCESS";
            } else {
                return "RESET_FAILED"; // User not found
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "RESET_FAILED"; // Error occurred
        }
    }

    private boolean isUserExist(String username) {
        try (Session session = sessionFactory.openSession()) {
            Users user = session.get(Users.class, username);
            return user != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Error occurred
        }
    }

    private boolean validateSecurityAnswer(String username, String question, String answer) {
        try (Session session = sessionFactory.openSession()) {
            Users user = session.get(Users.class, username);
            return user != null && user.getQuestion().equals(question) && user.getAnswer().equals(answer);
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Error occurred
        }
    }

    private void handleBuyCommand(PrintWriter out, String[] parts) {
        if (parts.length >= 3 && "BUY".equals(parts[0])) {
            String username = parts[1];
            double totalPrice = Double.parseDouble(parts[2]);

            try (Session session = sessionFactory.openSession()) {
                Transaction transaction = session.beginTransaction();

                for (int i = 3; i < parts.length; i++) {
                    String productName = parts[i];
                    Query<Product> productQuery = session.createQuery("FROM Product WHERE productName = :productName", Product.class);
                    productQuery.setParameter("productName", productName);
                    Product product = productQuery.uniqueResult();

                    if (product != null) {
                        // Assume 1 quantity per product
                        product.setQuantity(product.getQuantity() - 1);
                        session.update(product);
                    } else {
                        out.println("INVALID_PRODUCT");
                        transaction.rollback();
                        return;
                    }

                    session.createQuery("DELETE FROM OrderItem WHERE productName = :productName")
                            .setParameter("productName", productName)
                            .executeUpdate();
                }

                Customers customers = new Customers();
                customers.setCusname(username);
                customers.setTotal(totalPrice);
                customers.setDate(new Date(System.currentTimeMillis()));
                session.save(customers);


                transaction.commit();
                out.println("BUY_SUCCESS");
            } catch (Exception e) {
                e.printStackTrace();
                out.println("BUY_FAILED");
            }
        } else {
            out.println("INVALID_COMMAND");
        }
    }
    private void handleRemoveItem(PrintWriter out, String[] parts) {
        if (parts.length == 2) {
            int itemId = Integer.parseInt(parts[1]);

            try (Session session = sessionFactory.openSession()) {
                Transaction transaction = session.beginTransaction();
                OrderItem orderItem = session.get(OrderItem.class, itemId);

                if (orderItem != null) {
                    session.delete(orderItem);
                    transaction.commit();
                    out.println("REMOVE_SUCCESS");
                } else {
                    out.println("REMOVE_FAILED");
                }
            } catch (Exception e) {
                e.printStackTrace();
                out.println("REMOVE_FAILED");
            }
        } else {
            out.println("INVALID_COMMAND");
        }
    }
}