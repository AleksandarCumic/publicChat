import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Main {

    public static final int PORT = 9000;
    private static final int MAX_MESSAGES = 100;
    private static Set<String> usernames = new HashSet<>();
    private static List<String> messages = new ArrayList<>();
    private static List<String> zabranjenePoruke = new ArrayList<>();
    private static List<Server> aktivniKlijenti = new ArrayList<>();

    public static void main(String[] args) {
        try{
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server je uspostavio konekciju. Ocekuje klijente...");
            while(true){
                Socket socket = serverSocket.accept();
                Server s = new Server(socket);
                Thread server = new Thread(s);
                aktivniKlijenti.add(s);
                server.start();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    static synchronized boolean proveraUsername(String username){
        return !usernames.contains(username);
    }

    static synchronized void dodajUsername(String username){
        usernames.add(username);
    }

    static synchronized void izbaciUsername(String username){
        usernames.remove(username);
    }

    static synchronized void dodajPoruku(String message){
        if(messages.size() >= MAX_MESSAGES){
            messages.remove(0);
        }
        messages.add(message);
    }

    public static List<String> getZabranjenePoruke() {
        zabranjenePoruke.add("ruzan");
        zabranjenePoruke.add("ruzne");
        zabranjenePoruke.add("ruzni");
        zabranjenePoruke.add("ruznu");
        zabranjenePoruke.add("ruznim");
        zabranjenePoruke.add("ruznom");
        return zabranjenePoruke;
    }

    static synchronized List<String> getMessages() {
        return new ArrayList<>(messages);
    }

    public static List<Server> getAktivniKlijenti() {
        return aktivniKlijenti;
    }
}