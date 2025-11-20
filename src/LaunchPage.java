import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;


public class LaunchPage{

    private JFrame frame;
    private JPanel panel;
    private JPanel topPanel;
    private JPanel mainPanel;
    private JPanel bottomPanel;
    private JLabel label;
    private JSplitPane SpellSplitPane;

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
        JPanel bottomPanel = new JPanel();

        container.add(headerPanel);
        container.add(mainPanel);
        container.add(bottomPanel);


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

        // Spell Selector (simple for now)
        String[] spells = {
                "Fire Cat", "Thunder Snake", "Frost Beetle", "Scarab", "Dark Sprite", "Imp", "Blood Bat"
        };

        JList<String> spellList = new JList<>(spells);
        spellList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane listScroll = new JScrollPane(spellList);

        //setup the spell image
        JLabel spellImage = new JLabel();
        spellImage.setHorizontalAlignment(JLabel.CENTER);
        spellImage.setVerticalAlignment(JLabel.CENTER);

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(spellImage, BorderLayout.CENTER);

        //split the pane in half with test on the left and image on the right
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroll, imagePanel);

        //default size of the left panel
        splitPane.setDividerLocation(200);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(splitPane, BorderLayout.NORTH);

        spellList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {

                String selected = spellList.getSelectedValue();
                ImageIcon icon = new ImageIcon("assets/spells/" + selected + ".png");
                spellImage.setIcon(icon);

                if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {

                    int imgH = icon.getIconHeight();

                    // Resize ONLY the split pane
                    Dimension newSize = new Dimension(splitPane.getWidth(), imgH + 40);
                    splitPane.setPreferredSize(newSize);

                    // Refresh ONLY the split pane
                    splitPane.revalidate();
                    splitPane.repaint();
                }
            }
        });




        //bottom panel is the output where you see your damage
        bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.setBackground(Color.WHITE);

        ImageIcon damageIcon = new ImageIcon("assets/spell-icons/Damage_32x32.png");
        label = new JLabel("Your Damage: ", damageIcon, JLabel.LEFT);
        label.setForeground(Color.BLACK);
        bottomPanel.add(label);




        //im pretty sure this code has to go last????
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }


}
