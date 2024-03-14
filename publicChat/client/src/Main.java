import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    public static final int PORT = 9000;
    public static final String SERVER = "127.0.0.1";
    public static void main(String[] args) {

        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;

        try{
            socket = new Socket(SERVER, PORT);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            Scanner scanner = new Scanner(System.in);

            System.out.println("Prikljucili ste se serveru. Izabrite vas username: ");
            String username = scanner.nextLine();
            out.println(username);

            BufferedReader finalIn = in;
            Thread citanjeThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        String poruka;
                        while((poruka = finalIn.readLine()) != null){
                            System.out.println(poruka);
                        }
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });

            citanjeThread.start();

            String tekst;

            while(true){
                tekst = scanner.nextLine();
                if(tekst.equalsIgnoreCase("izlaz")){
                    break;
                }
                out.println(tekst);
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