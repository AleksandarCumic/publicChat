import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
                poruka = cenzurisiPoruku(poruka);
                poruka = obradiPoruku(poruka);
                System.out.println(poruka);
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

        String ime = in.readLine();
        while(!Main.proveraUsername(ime)){
            out.println("Username koji ste zeleli je trenutno zauzet, probajte opet: ");
            ime = in.readLine();
        }
        username = ime;
        Main.dodajUsername(username);
    }

    private void posaljiDobrodoslicu(){
        System.out.println("Prikljucio se " + username + "!");
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
            if(poruka.contains(l)){
                return false;
            }
        }
        return true;
    }

    private String cenzurisiRec(String rec){
        char[] cenzurisano = new char[rec.length() - 2];
        Arrays.fill(cenzurisano, '*');
        return rec.charAt(0) + new String(cenzurisano) + rec.charAt(rec.length() - 1);
    }

    private String cenzurisiPoruku(String poruka){
        for(String cenzurisanaRec : Main.getZabranjenePoruke()){
            poruka = poruka.replaceAll("\\b" + cenzurisanaRec + "\\b", cenzurisiRec(cenzurisanaRec));
        }
        return poruka;
    }

    private String obradiPoruku(String poruka){
        String obradjenaPoruka = formirajPoruku(poruka);
        if(validnaPoruka(obradjenaPoruka)) {
            Main.dodajPoruku(obradjenaPoruka);
            podeliPoruku(obradjenaPoruka);
            return obradjenaPoruka;
        }
        //obradjenaPoruka = formirajPoruku("*****");
        Main.dodajPoruku(obradjenaPoruka);
        podeliPoruku(obradjenaPoruka);

        return obradjenaPoruka;
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
