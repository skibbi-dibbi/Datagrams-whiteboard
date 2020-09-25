import java.awt.*;
import java.awt.event.*;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.event.MouseAdapter;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.awt.Graphics;


@SuppressWarnings("serial")
public class Drawboard extends JFrame {

    private static JPanel jp;

    public Drawboard(String name) {
        //Set up gui
        this.setTitle(name);
        jp = new JPanel();
        setSize(800, 600);
        jp.setBackground(Color.WHITE);
        setLocation(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        add(jp);

        //Add mouse listener to listen to clicks as well as mousedrag, and call on paint as well as "send packet" function once clicked
        jp.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                paint(e.getPoint());
                Draw.getUdp().sendPacket(e.getPoint());
            }
        });

        jp.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                paint(e.getPoint());
                Draw.getUdp().sendPacket(e.getPoint());
            }
        });
    }

    //Paint function that will use panel Graphics object to draw to the panel once called upon with the Point
    public void paint(Point x) {
        if (x != null) {
            Graphics g = jp.getGraphics();
            if (jp.contains(x)) {
                g.drawLine((int) x.getX(), (int) x.getY(), (int) x.getX(), (int) x.getY());
                g.dispose();
            }
        }
    }
}