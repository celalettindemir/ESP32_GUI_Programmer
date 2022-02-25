package org.example;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Random;

public class App 
{
    private static String OTAstatus;

    private static byte[] buf = new byte[256];
    private static char[] ret = new char[10];
    public static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }
    public static void main( String[] args )
    {
        try{
            int ServerPort= (new Random().nextInt(50000)+10000);
            ServerSocket serverSocket = new ServerSocket(ServerPort);
            OTAstatus = "Starting";
            System.out.println(OTAstatus);
            System.out.println("Starting on 20000 port open server");
            //Socket socket=new Socket("localhost",20000);
            OTAstatus = "Binding";
            System.out.println(OTAstatus);

            String fileName = "src/main/resources/yakmalisondurmeli.bin";
            File f = new File(fileName);
            long content_size = f.length();
            byte[] content = Files.readAllBytes(Paths.get(fileName));
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(Files.readAllBytes(Paths.get(fileName)));
            byte[] digest = md.digest();
            String file_md5 = toHexString(digest);
            String message="0 "+ServerPort+" "+content_size+" "+file_md5+"\n";

            System.out.print(message);

            int inv_trys=0;
            String data="";


            while(inv_trys<10)
            {
                inv_trys+=1;
                DatagramSocket socket1 = new DatagramSocket();
                socket1.send(new DatagramPacket(message.getBytes(StandardCharsets.UTF_8),message.length(),InetAddress.getByName("192.168.2.199"),3232));
                socket1.setSoTimeout(10);
                while(true) {
                    try {
                        socket1.receive(new DatagramPacket(buf, 37));
                        data = (new String(buf)).substring(0,2);
                        if (data.equals("OK"))
                            break;
                    } catch (Exception e) {
                        socket1.close();
                        break;
                    }
                }
                if (data.equals("OK")){
                    socket1.close();
                    break;
                }
            }

            OTAstatus = "Waiting";
            System.out.println(OTAstatus);
            //serverSocket.setSoTimeout(1000);

            Socket connection = serverSocket.accept();
            connection.setReceiveBufferSize(10);

            DataInputStream input= new DataInputStream(new BufferedInputStream(connection.getInputStream()));
            //InputStream input = connection.getInputStream();
            OutputStream output = connection.getOutputStream();
            int count=0;

            FileInputStream fis = new FileInputStream(new File(fileName));
            InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.US_ASCII);
            //BufferedInputStream reader = new BufferedInputStream(fis, 1024);

            DataInputStream reader1=new DataInputStream(
                    new BufferedInputStream(
                            new FileInputStream(new File(fileName))));


            byte[] readfile=new byte[1024];
            //readfile

            byte[] Okreceive=new byte[10];
            int totalSize=0;
            while(true)
            {

                readfile=new byte[1024];
                int len=reader1.read(readfile);
                if(len==-1)
                    break;
                totalSize+=len;
                output.write(readfile,0,len);
                input.read(Okreceive);
                //System.out.println(Okreceive);
                data = new String(Okreceive).substring(0,2);
            }

            System.out.println(totalSize);
            if (data.equals("OK")){
                System.out.println("Başarılı");
                connection.close();
            }
            connection.setSoTimeout(60);
            connection.setReceiveBufferSize(32);
            while(true)
            {
                try {
                    if (data.equals("OK")){
                        System.out.println("Başarılı");
                        connection.close();
                        break;
                    }
                    input.read(Okreceive);

                    data = (new String(Okreceive,StandardCharsets.UTF_8)).substring(0,2);

                } catch (Exception e) {
                    connection.close();
                    break;
                }
            }

            reader.close();
            connection.close();
            serverSocket.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}
