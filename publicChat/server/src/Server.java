import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Server implements Runnable{

    private final Socket socket;
    private String username;
    private BufferedReader in = null;
    private PrintWriter out = null;
    public Server(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {


        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            traziUsername();
            posaljiDobrodoslicu();
            posaljiIstorijuCeta();

            while(true){
                String poruka = in.readLine();
                if(poruka == null){
                    break;
                }
                obradiPoruku(poruka);
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

            if(username != null){
                Main.izbaciUsername(username);
                System.out.println(username + " se iskljucio.");
            }

            if(this.socket != null){
                try{
                    socket.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void traziUsername() throws IOException{
        out.println("Izaberite svoj username: ");
        String ime = in.readLine();
        while(!Main.proveraUsername(ime)){
            out.println("Username koji ste zeleli je trenutno zauzet, probajte opet: ");
            ime = in.readLine();
        }
        username = ime;
        Main.dodajUsername(username);
    }

    private void posaljiDobrodoslicu(){
        out.println("Dobrodosao " + username + "!");
    }

    private void posaljiIstorijuCeta(){
        List<String> istorija = Main.getMessages();
        for (String poruka : istorija){
            out.println(poruka);
        }
    }

    private boolean validnaPoruka(String poruka){
        List<String> lista = Main.getZabranjenePoruke();
        for(String l : lista){
            if(l.equals(poruka)){
                return false;
            }
        }
        return true;
    }

    private void obradiPoruku(String poruka){
        String obradjenaPoruka = formirajPoruku(poruka);
        if(validnaPoruka(poruka)) {
            Main.dodajPoruku(obradjenaPoruka);
            podeliPoruku(poruka);
            return;
        }
        poruka = "*****";
        Main.dodajPoruku(poruka);
        podeliPoruku(poruka);
    }

    private String formirajPoruku(String poruka){
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String vreme = dateFormat.format(now);
        return vreme + " - " + username + ": " + poruka;
    }

    private void podeliPoruku(String poruka){
        for(Server s : Main.getAktivniKlijenti()){
            if(s != this){
                s.out.println(poruka);
            }
        }
    }
}
