

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

    private static int port = 1001; /* puerto para escuchar peticiones*/
 
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
            server = new ServerSocket(port); /* Comienza a escuchar peticiones */
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.err.println(e);
            System.exit(1);
        }
        /* Esperando peticiones del cliente*/
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

/*Protocolo de lectura para mensajes del cliente*/
class ChatServerProtocol {
    private String nick;
    private ClientConn conn;
 
   public ChatServerProtocol(ClientConn c) {
    conn=c;
    }
   
   /*Metodo que devuelve mensajes del cliente si es que los tiene*/
    private String  ClienteSolicitaMensajes(String IP_enviar,String Nick) {
        String mismensajes="MISMSG ";
        try{
            if(NickesIP(IP_enviar,Nick)){
                
                FileReader fr = new FileReader ("mensajes.txt");
                BufferedReader br = new BufferedReader(fr);
                FileWriter archivoMensajes;
                PrintWriter pw ;
                archivoMensajes = new FileWriter("temp.txt",true);
                pw = new PrintWriter(archivoMensajes);
                String linea;
                //Variables utilizadas para la separación del String
                StringTokenizer aux1;
                String aux2;
                //Variables utilizadas para guardar campos.
                String msg;
                String IP_d;
                String IP_f;
                String mensaje;
                String nickf;
                String nickd;
                String estado;

                while ((linea=br.readLine())!=null){
                    aux1=new StringTokenizer(linea,"&&");
                    msg= aux1.nextToken();
                    aux2=aux1.nextToken();
                    if (aux2.equals(IP_enviar)){
                        IP_d=aux2;
                        IP_f=aux1.nextToken();
                        nickf=aux1.nextToken();
                        nickd=aux1.nextToken();
                        mensaje=aux1.nextToken();
                        estado=aux1.nextToken();
                        if(nickd.equals(Nick)){
                            if(estado.equals("NO")){
                                System.out.println("entre");
                                pw.println(msg+"&&"+IP_d+"&&"+IP_f+"&&"+nickf+"&&"+nickd+"&&"+mensaje+"&&"+"SI");
                                
                                mismensajes+=nickf+"("+IP_f+"): "+ mensaje+"&&";
                            }
                            else{
                               pw.println(msg+"&&"+IP_d+"&&"+IP_f+"&&"+nickf+"&&"+nickd+"&&"+mensaje+"&&"+estado); 
                            }
                        }
                        else{
                            pw.println(msg+"&&"+IP_d+"&&"+IP_f+"&&"+nickf+"&&"+nickd+"&&"+mensaje+"&&"+estado);
                        }

                    }
                      
                }
                
                br.close();
                fr.close();
                pw.close();
                File fichero1 = new File("mensajes.txt");
                fichero1.delete();
                
                File fichero2 = new File("temp.txt");
                File fichero3 = new File("mensajes.txt");
                fichero2.renameTo(fichero3);
            }
            
            
        }
        catch (FileNotFoundException ex) {
            System.out.println("No se ha escrito ningun mensaje aún");
            
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return mismensajes;
        
    }
    
    /*comprueba ip y puerto asociado al nick*/
    public boolean ComprobarDatos(String Nick,String IP,String puerto)  throws IOException{
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
                aux1.nextToken();
                if (aux2.equals(Nick) && aux1.nextToken().equals(IP) && aux1.nextToken().equals(puerto)){
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
    
    /*Obtiene ip de un determinado nick*/
    public String obtenerIP(String Nick)throws IOException{
        String ip="";
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
                    aux1.nextToken();
                    br.close();
                    fr.close();
                    return aux1.nextToken();
                }   
            }
       
        return ip;
        
    }
    /* retorna contactos del nick asociado al mensaje*/
    public String ClienteSolicitaContactos(String Nick){
        String contactos="FALLOCONTACTOS";
        try{
                
                contactos="MISCONTACTOS ";
                FileReader fr = new FileReader (Nick+".txt");
                BufferedReader br = new BufferedReader(fr);
                String linea;
                
                String ip="";
                while ((linea=br.readLine())!=null){
                    ip=obtenerIP(linea);
                    contactos+=linea+":"+ip+"&&";
                      
                }
                br.close();
                fr.close(); 
            
            
            
        }
        catch (FileNotFoundException ex) {
            System.out.println("No se ha escrito ningun mensaje aún");
            
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
        return contactos;
    }
    
    /* retorna true si el nick asociado a la ip es correcto*/
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
                aux1.nextToken();
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
    
    /*Comprueba que el nick existe*/
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
    
    
    public boolean Login(String Nick,String Pass)throws IOException{
        try{
            FileReader fr = new FileReader ("nicks.txt");
            BufferedReader br = new BufferedReader(fr);
            String linea;
            //Variables utilizadas para la separación del String
            StringTokenizer aux1;
            String aux2;
            String aux3;
            //Variables utilizadas para guardar campos.
           
            
            while ((linea=br.readLine())!=null){
                aux1=new StringTokenizer(linea,":");
                aux2=aux1.nextToken();
                aux3=aux1.nextToken();
                if (aux2.equals(Nick) && aux3.equals(Pass) ){
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
    
    /*Verifica que el contacto no se duplique para el ciente*/
    public boolean YaExisteContacto(String archivo,String Nick) throws IOException{
        try{
            FileReader fr = new FileReader (archivo+".txt");
            BufferedReader br = new BufferedReader(fr);
            String linea;
            //Variables utilizadas para la separación del String
            
            //Variables utilizadas para guardar campos.
           while ((linea=br.readLine())!=null){
                
                if (linea.equals(Nick) ){
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
 
    /**
     *Metodo principla para procesar mensajes, recibe instrucciones y parametros
     */
    public String ProcesarMensaje(String msg)throws IOException  {
 
        String[] msg_parts = msg.split(" ", 6);
        String msg_type = msg_parts[0];
 
        if(msg_type.equals("MSG")) {
            if(msg_parts.length < 6) return "INVALIDO";
            if(msg_parts.length==6){
                FileWriter archivoMensajes;
                PrintWriter pw ;
                archivoMensajes = new FileWriter("mensajes.txt",true);
                pw = new PrintWriter(archivoMensajes);
                pw.println(msg_parts[0]+"&&"+msg_parts[1]+"&&"+msg_parts[2]+"&&"+msg_parts[3]+"&&"+msg_parts[4]+"&&"+msg_parts[5]+"&&"+"NO");
                pw.close();
                return "OKMSG";
            }
            else return "FALLOENVIO";
        } 
        else if(msg_type.equals("RETORNAR")){
            return ClienteSolicitaMensajes(msg_parts[1],msg_parts[2]);
            
        }
        else if(msg_type.equals("NICK")){
                FileWriter archivoMensajes;
                PrintWriter pw ;
                archivoMensajes = new FileWriter("nicks.txt",true);
                pw = new PrintWriter(archivoMensajes);
                if(RevisarNick(msg_parts[1])){
                    pw.println(msg_parts[1]+":"+msg_parts[2] +":" +msg_parts[3]+ ":" + msg_parts[4] );
                    pw.close();
                    return "NICKOK";
                    
                }
                else{
                    pw.close();
                    return "NICKFALLO";
                }
        }
        else if(msg_type.equals("LOGIN")){
                
            if(Login(msg_parts[1],msg_parts[2])){
                return "LOGINOK";
                    
            }
            else{
                return "LOGINFALLO";
            }
        }
        else if(msg_type.equals("AGREGAR")){
            if(msg_parts.length < 5) return "INVALIDO";
            if(msg_parts.length==5){
                try{
        
                        String basePath = new File("").getAbsolutePath();
                        File archivo = new File(basePath+ "/"+msg_parts[1]+".txt");
                        if(archivo.exists()){
                            //no hacer nada

                        }
                        else{
                            //Si no existe, se crea uno nuevo
                            FileWriter archivoMensajes ;
                            PrintWriter pw ;

                            archivoMensajes = new FileWriter(msg_parts[1]+".txt");
                            pw = new PrintWriter(archivoMensajes);
                            pw.close();
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                FileWriter archivoMensajes;
                PrintWriter pw ;
                archivoMensajes = new FileWriter(msg_parts[1]+".txt",true);
                
                pw = new PrintWriter(archivoMensajes);
                if(!YaExisteContacto(msg_parts[1],msg_parts[2])){
                    if(ComprobarDatos(msg_parts[2],msg_parts[3],msg_parts[4])){
                        pw.println(msg_parts[2]);
                        pw.close();
                        return "OKAGREGAR";
                    }
                    else{
                        pw.close();
                        return "NOEXISTE"; 
                    }
                }
                else{
                    pw.close();
                    return "YAEXISTE";
                }
            }
            else return "FALLOAGREGAR";
        }
        else if(msg_type.equals("MISCONTACTOS")){
            return ClienteSolicitaContactos(msg_parts[1]);
        }
        
        else {
            return "DESCONOCIDO";
        }
    }
}
 
/*Clase para manipular al cliente*/
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
            
        }
    }
 
    public void run() {
        String msg, response;
        ChatServerProtocol protocol = new ChatServerProtocol(this);
        try {
            /*Lee mensajes del servidor y responde*/
            while ((msg = in.readLine()) != null) {
                
                System.out.println(msg);
                response = protocol.ProcesarMensaje(msg);
                System.out.println(response);
                out.println(response);
                
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
 
    public void sendMsg(String msg) {
        out.println(msg);
    }
}
    
