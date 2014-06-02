

package app.servidor;

/**
 *
 * @author felipe
 */

import java.net.ServerSocket;
import java.net.Socket;
 
import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.util.StringTokenizer;

public class AppServidor {

    private static int port = 1001; /* port to listen on */
 
    public static void main (String[] args) throws IOException {
        try{
        
            String basePath = new File("").getAbsolutePath();
            File archivo = new File(basePath+ "/mensajes.txt");
            if(archivo.exists()){
                //no hacer nada
            
            }
            else{
                //Si no existe, se crea uno nuevo
                FileWriter archivoMensajes ;
                PrintWriter pw ;

                archivoMensajes = new FileWriter("mensajes.txt");
                pw = new PrintWriter(archivoMensajes);
                pw.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try{
        
            String basePath = new File("").getAbsolutePath();
            File archivo = new File(basePath+ "/nicks.txt");
            if(archivo.exists()){
                //no hacer nada
            
            }
            else{
                //Si no existe, se crea uno nuevo
                FileWriter archivoMensajes ;
                PrintWriter pw ;

                archivoMensajes = new FileWriter("nicks.txt");
                pw = new PrintWriter(archivoMensajes);
                pw.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        
        ServerSocket server = null;
        try {
            server = new ServerSocket(port); /* start listening on the port */
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.err.println(e);
            System.exit(1);
        }
 
        Socket client = null;
        while(true) {
            try {
                client = server.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.err.println(e);
                System.exit(1);
            }
            /* start a new thread to handle this client */
            Thread t = new Thread(new ClientConn(client));
            t.start();
        }
    }
}

class ChatServerProtocol {
    private String nick;
    private ClientConn conn;
 
    
 
    private static final String msg_OK = "OK";
    private static final String msg_NICK_IN_USE = "NICK IN USE";
    private static final String msg_SPECIFY_NICK = "SPECIFY NICK";
    private static final String msg_INVALID = "MSG INVALIDO";
    private static final String msg_SEND_FAILED = "FALLO ENVIO";
 
 
    
 
    public ChatServerProtocol(ClientConn c) {
    conn=c;
    }
    private void ClienteSolicitaMensajes(String IP_enviar,String Nick) throws IOException  {
        try{
            if(NickesIP(IP_enviar,Nick)){
                FileReader fr = new FileReader ("mensajes.txt");
                BufferedReader br = new BufferedReader(fr);
                String linea;
                //Variables utilizadas para la separación del String
                StringTokenizer aux1;
                String aux2;
                //Variables utilizadas para guardar campos.
                String IP_d;
                String IP_f;
                String mensaje;
                String Destinatario;


                while ((linea=br.readLine())!=null){
                    aux1=new StringTokenizer(linea,"&&");
                    aux2= aux1.nextToken();
                    aux2=aux1.nextToken();
                    if (aux2.equals(IP_enviar)){
                        IP_d=aux2;
                        IP_f=aux1.nextToken();
                        mensaje=aux1.nextToken();
                        if(aux1.nextToken().equals(Nick))
                            EnviarACliente(IP_d,IP_f,mensaje,aux1.nextToken());    
                        }


                }
                br.close();
                fr.close();
                
            }
            conn.sendMsg("END");
        }
        catch (FileNotFoundException ex) {
            System.out.println("No se ha escrito ningun mensaje aún");
            
        }
        
        
    }
    
    public void EnviarACliente(String IP_destino,String IP_fuente,String Mensaje,String NickFuente){
        conn.sendMsg("- "+NickFuente+"("+IP_fuente+"): "+ Mensaje);
        
    }
    
    public boolean NickesIP(String IP,String Nick) throws IOException{
        try{
            FileReader fr = new FileReader ("nicks.txt");
            BufferedReader br = new BufferedReader(fr);
            String linea;
            //Variables utilizadas para la separación del String
            StringTokenizer aux1;
            String aux2;
            //Variables utilizadas para guardar campos.
           
            
            while ((linea=br.readLine())!=null){
                aux1=new StringTokenizer(linea,":");
                aux2=aux1.nextToken();
                if (aux2.equals(Nick) && aux1.nextToken().equals(IP)){
                    return true;    
                }   
            }
            br.close();
            fr.close();
            return false;
           
        }
        catch (FileNotFoundException ex) {
            System.out.println("No se ha escrito ningun mensaje aún");
            
        }
        
        return false;
        
        
    }
    public boolean RevisarNick(String Nick) throws IOException{
        try{
            FileReader fr = new FileReader ("nicks.txt");
            BufferedReader br = new BufferedReader(fr);
            String linea;
            //Variables utilizadas para la separación del String
            StringTokenizer aux1;
            String aux2;
            //Variables utilizadas para guardar campos.
           
            
            while ((linea=br.readLine())!=null){
                aux1=new StringTokenizer(linea,":");
                aux2=aux1.nextToken();
                if (aux2.equals(Nick)){
                    return false;    
                }   
            }
            br.close();
            fr.close();
            return true;
           
        }
        catch (FileNotFoundException ex) {
            System.out.println("No se ha escrito ningun mensaje aún");
            
        }
        
        return true;
    }
 
    /**
     * Process a message coming from the client
     */
    public String ProcesarMensaje(String msg)throws IOException  {
 
        String[] msg_parts = msg.split(" ", 6);
        String msg_type = msg_parts[0];
 
        if(msg_type.equals("MSG")) {
            if(msg_parts.length < 6) return msg_INVALID;
            if(msg_parts.length==6){
                FileWriter archivoMensajes;
                PrintWriter pw ;
                archivoMensajes = new FileWriter("mensajes.txt",true);
                pw = new PrintWriter(archivoMensajes);
                pw.println(msg_parts[0]+"&&"+msg_parts[1]+"&&"+msg_parts[2]+"&&"+msg_parts[3]+"&&"+msg_parts[4]+"&&"+msg_parts[5]);
                pw.close();
                return msg_OK;
            }
            else return msg_SEND_FAILED;
        } 
        else if(msg_type.equals("RETORNAR")){
            ClienteSolicitaMensajes(msg_parts[1],msg_parts[2]);
            return msg_OK;
        }
        else if(msg_type.equals("NICK")){
                FileWriter archivoMensajes;
                PrintWriter pw ;
                archivoMensajes = new FileWriter("nicks.txt",true);
                pw = new PrintWriter(archivoMensajes);
                if(RevisarNick(msg_parts[1])){
                    pw.println(msg_parts[1]+":"+msg_parts[2]);
                    pw.close();
                    conn.sendMsg("OK");
                    conn.sendMsg("NICKEND");
                    return msg_OK;
                    
                }
                else{
                    conn.sendMsg("ENUSO");
                    pw.close();
                    conn.sendMsg("NICKEND");
                    return msg_NICK_IN_USE;
                }
                
                       
        }
        else {
            return msg_INVALID;
        }
    }
}
 
class ClientConn implements Runnable {
    private Socket client;
    private BufferedReader in = null;
    private PrintWriter out = null;
 
    ClientConn(Socket client) {
        this.client = client;
        try {
            /* obtain an input stream to this client ... */
            in = new BufferedReader(new InputStreamReader(
                        client.getInputStream()));
            /* ... and an output stream to the same client */
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println(e);
            return;
        }
    }
 
    public void run() {
        String msg, response;
        ChatServerProtocol protocol = new ChatServerProtocol(this);
        try {
            
            while ((msg = in.readLine()) != null) {
                
                System.out.println(msg);
                response = protocol.ProcesarMensaje(msg);
                out.println("SERVER: " + response);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
 
    public void sendMsg(String msg) {
        out.println(msg);
    }
}
    
