import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;


public class LaunchPage{

    private JFrame frame;
    private JPanel panel;
    private JPanel topPanel;
    private JPanel mainPanel;
    private JLabel label;
    private JSplitPane SpellComboBox;

    int windowWidth = 800;
    int windowHeight = 500;

    LaunchPage(){
        //set up the main window
        frame = new JFrame();
        frame.setTitle("Wizard101 Damage Calculator");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(windowWidth, windowHeight);
        frame.setLocationRelativeTo(null);

        //set up the container that holds the panels
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JPanel headerPanel = new JPanel();
        JPanel mainPanel = new JPanel();

        container.add(headerPanel);
        container.add(mainPanel);


        //top panel used as the header
        topPanel = new JPanel();
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(Color.DARK_GRAY);

        ImageIcon wizIcon = new ImageIcon("assets/wiz.png");
        label = new JLabel("W101 Damage Calculator", wizIcon, JLabel.LEFT);
        label.setForeground(Color.BLACK);
        topPanel.add(label);

        label.setHorizontalTextPosition(SwingConstants.CENTER);
        label.setVerticalTextPosition(SwingConstants.BOTTOM);

        //main panel is where everything will happen
        mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.GRAY);



        //im pretty sure this code has to go last????
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

}
