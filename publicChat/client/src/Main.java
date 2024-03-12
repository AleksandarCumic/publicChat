import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    public static final int PORT = 9000;

    public static void main(String[] args) {

        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;

        try{
            socket = new Socket("127.0.0.1", PORT);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            Scanner scanner = new Scanner(System.in);

            while(true){
                // logika
            }

        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if(in != null){
                try{
                    in.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
            if(out != null){
                out.close();
            }

            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

    }
}