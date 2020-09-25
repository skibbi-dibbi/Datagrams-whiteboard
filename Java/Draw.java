import java.awt.*;
import java.awt.event.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;
import java.lang.reflect.Field;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Draw {

    static Drawboard db;
    static Udp udp; 

    public static void main(final String[] args) {

        if (args.length != 3) {
            System.out.println("Usage: java draw <my port> <remote host> <remote port>");
            System.exit(1);
        }

        int remotePort = 0;
        int localPort = 0;

        try {
            localPort = Integer.parseInt(args[0]);
            remotePort = Integer.parseInt(args[2]); 
        } catch(NumberFormatException nfe) {
            System.out.println("Ports need to be of integer type!");
            System.exit(1);
        }

        //Invoke new UDP, which is gonna setup sockets for sending and receiving
        udp = new Udp(localPort, args[1], remotePort);

        //Invoke gui from class Drawboard
        db = new Drawboard("Drawing board!");
        db.setVisible(true);

        //Create a new Thread that will handle received packets in a continuous loop
        Thread receiver = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    Point p = udp.receivePacket();
                    db.paint(p);
                }
            }   
        });

        //Start receiver thread
        receiver.start();
        
    }

    public static Udp getUdp() {
        return udp;
    }
}

//UDP class to handle datagram sockets, sending and receiving datagrams
class Udp {
    private static DatagramSocket sendSocket, receiveSocket;
    private static String toSend, remoteHoststr, x, y;
    private static byte[] b;
    private static byte[] buf;
    private static String[] xy;
    private static int targetPort;
    private static Point point;

    public Udp(final int lp, final String rh, final int rp) {
        try{
            sendSocket = new DatagramSocket();
            receiveSocket = new DatagramSocket(lp);
        } catch(Exception e) {e.printStackTrace();}
        remoteHoststr = rh;
        targetPort = rp;
    }

    //Function to send packet containing the point
    public void sendPacket(final Point p) {
        x = Integer.toString(Math.round((int) p.getX()));
        y = Integer.toString(Math.round((int) p.getY()));

        toSend = x + " " + y;
        b = toSend.getBytes();

        try {
            InetAddress remoteHost = InetAddress.getByName(remoteHoststr);
            sendSocket.send(new DatagramPacket(b, b.length, remoteHost, targetPort));
        } catch(Exception e) {}
    }

    //Function that will be constantly called on the other thread, receiving packets and returning a Point
    public Point receivePacket() {
        buf = new byte[10];
        try {
            String s = "";
            DatagramPacket p = new DatagramPacket(buf, buf.length);
            receiveSocket.receive(p);

            s = new String(p.getData()).substring(0, p.getLength());
            xy = s.split(" ");
            point = new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));
        } catch(Exception e) {}

        return point;
    }
}