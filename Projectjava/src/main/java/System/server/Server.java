package System.server;

import System.View.ServerApp;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 12345;
    private static SessionFactory sessionFactory;

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                sessionFactory = new Configuration().configure().buildSessionFactory();
                ServerSocket serverSocket = new ServerSocket(PORT);
                System.out.println("Server is running on port " + PORT);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected");

                    ServerThread serverThread = new ServerThread(clientSocket, sessionFactory);
                    serverThread.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (sessionFactory != null) {
                    sessionFactory.close();
                }
            }
        }).start();

        ServerApp.launch(ServerApp.class, args);
    }
}