import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.*;   // You need org.json library

public class LaunchPage {

    private JFrame frame;
    private JPanel topPanel, mainPanel, bottomPanel;

    private JSONArray spellsArray;     // holds full JSON spell data

    private JLabel damageLabel, accuracyLabel, pipLabel;



    public LaunchPage() {

        frame = new JFrame("Wizard101 Damage Calculator");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);

        buildTopPanel();
        buildMainPanel();
        buildBottomPanel();

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // ------------------------------
    //  Top Panel
    // ------------------------------
    private void buildTopPanel() {
        topPanel = new JPanel();
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(Color.DARK_GRAY);

        ImageIcon wizIcon = new ImageIcon("src/resources/assets/wiz.png");

        JLabel label = new JLabel("W101 Damage Calculator", wizIcon, JLabel.LEFT);
        label.setForeground(Color.BLACK);

        label.setHorizontalTextPosition(SwingConstants.CENTER);
        label.setVerticalTextPosition(SwingConstants.BOTTOM);

        topPanel.add(label);
    }


    // ------------------------------
    //  Main Panel (JList + Image)
    // ------------------------------
    private void buildMainPanel() {

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.GRAY);

        // JSON loading
        spellsArray = loadSpellData();

        // Build spell list
        DefaultListModel<String> model = new DefaultListModel<>();

        for (int i = 0; i < spellsArray.length(); i++) {
            JSONObject o = spellsArray.getJSONObject(i);
            model.addElement(o.getString("displayName"));
        }

        JList<String> spellList = new JList<>(model);
        spellList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane listScroll = new JScrollPane(spellList);

        // Right side image panel
        JLabel spellImage = new JLabel("", JLabel.CENTER);
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(spellImage, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                listScroll,
                imagePanel
        );

        splitPane.setDividerLocation(200);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // When user selects a spell
        spellList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = spellList.getSelectedIndex();
                JSONObject spell = spellsArray.getJSONObject(index);

                // Load icon
                ImageIcon icon = new ImageIcon("src/resources/assets/spells/" + spell.getString("image"));
                spellImage.setIcon(icon);

                // Update damage label
                JSONObject dmg = spell.getJSONObject("damage");
                int min = dmg.getInt("min");
                int max = dmg.getInt("max");
                damageLabel.setText("Damage: " + min + " - " + max);

                // Update accuracy label
                int accuracy = spell.getInt("accuracy");
                accuracyLabel.setText("Accuracy: " + accuracy);

                // Update pip label
                int pipCost = spell.getInt("pipCost");
                pipLabel.setText("Pip Cost: " + pipCost);
            }
        });
    }


    // ------------------------------
    //  Bottom Panel
    // ------------------------------
    private void buildBottomPanel() {
        bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.setBackground(Color.WHITE);

        ImageIcon damageIcon = new ImageIcon("src/resources/assets/spell-icons/32x32/Damage.png");
        ImageIcon accuracyIcon = new ImageIcon("src/resources/assets/spell-icons/32x32/Accuracy.png");
        ImageIcon pipIcon = new ImageIcon("src/resources/assets/spell-icons/32x32/Power_Pip.png");

        damageLabel = new JLabel("Damage: ---", damageIcon, JLabel.LEFT);
        damageLabel.setForeground(Color.BLACK);

        accuracyLabel = new JLabel("Accuracy: ---", accuracyIcon, JLabel.LEFT);
        accuracyLabel.setForeground(Color.BLACK);

        pipLabel = new JLabel("Pip Cost: ---", pipIcon, JLabel.LEFT);
        pipLabel.setForeground(Color.BLACK);

        bottomPanel.add(damageLabel);
        bottomPanel.add(accuracyLabel);
        bottomPanel.add(pipLabel);
    }



    // ------------------------------
    //  JSON Loader
    // ------------------------------
    private JSONArray loadSpellData() {
        try {
            String json = new String(Files.readAllBytes(Paths.get("src/resources/SpellData.json")));
            JSONObject root = new JSONObject(json);
            return root.getJSONArray("spells");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load SpellData.json");
            return new JSONArray();
        }
    }
}
