import java.awt.*;
import javax.swing.*;

public class DisplayWindow extends JFrame {
   // default rendering values
    private static int width = 500;
    private static int height = 500;
    private final static String defaultDisplay_ = "Display";

    private Container c;

    public DisplayWindow() {
    	super(defaultDisplay_);
        c = this.getContentPane();
    }

    public DisplayWindow(String title) {
        super(title);
        c = this.getContentPane();
    }

    public void addPanel(JPanel p) {
        c.add(p);
    }

    public void addPanel(JPanel p, boolean useDefault) {
        if(useDefault)
          p.setPreferredSize(new Dimension(width, height));
        c.add(p);
    }

    public void showFrame() {
        this.pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}