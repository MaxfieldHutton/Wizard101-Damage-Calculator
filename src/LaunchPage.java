import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import org.json.*;   // You need org.json library

public class LaunchPage {

    private JFrame frame;
    private JPanel topPanel, mainPanel, bottomPanel;

    private JSONArray spellsArray;     // holds full JSON spell data
    private JSONArray spellsEnchantsArray;     // holds full JSON enchant data

    private JSplitPane spellPane, enchantPane, parentPane;
    private JLabel damageLabel, accuracyLabel, pipLabel;


    private JSONObject currentSpell = null;
    private JSONObject currentEnchant = null;



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

        /// //////////////////////////normal spells
        // JSON loading
        spellsArray = loadSpellData();

        // Build spell list
        DefaultListModel<String> modelSpell = new DefaultListModel<>();

        for (int i = 0; i < spellsArray.length(); i++) {
            JSONObject o = spellsArray.getJSONObject(i);
            modelSpell.addElement(o.getString("displayName"));
        }

        JList<String> spellList = new JList<>(modelSpell);
        spellList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane listScroll = new JScrollPane(spellList);

        // Right side image panel
        JLabel spellImage = new JLabel("", JLabel.CENTER);
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(spellImage, BorderLayout.CENTER);

        spellPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroll, imagePanel);
        spellPane.setDividerLocation(200);
        /// //////////////////////////

        /// ///////////////////////// enchants
        // JSON loading
        spellsEnchantsArray = loadSpellEnchantData();

        // Build spell list
        DefaultListModel<String> model = new DefaultListModel<>();

        for (int i = 0; i < spellsEnchantsArray.length(); i++) {
            JSONObject o = spellsEnchantsArray.getJSONObject(i);
            model.addElement(o.getString("displayName"));
        }

        JList<String> enchantList = new JList<>(model);
        enchantList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane enchantListScroll = new JScrollPane(enchantList);

        // Right side image panel
        JLabel enchantImage = new JLabel("", JLabel.CENTER);
        JPanel enchantimagePanel = new JPanel(new BorderLayout());
        enchantimagePanel.add(enchantImage, BorderLayout.CENTER);

        enchantPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, enchantListScroll, enchantimagePanel);
        enchantPane.setDividerLocation(200);
        /// /////////////////////////

        /// ///////////////////////// parent pane
        parentPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spellPane, enchantPane);
        parentPane.setDividerLocation(400);

        /// /////////////////////////


        mainPanel.add(parentPane);

        // When user selects a spell
        spellList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = spellList.getSelectedIndex();
                JSONObject spell = spellsArray.getJSONObject(index);
                currentSpell = spell;

                // Load icon
                ImageIcon icon = new ImageIcon("src/resources/assets/spells/damage-spells/" + spell.getString("image"));
                spellImage.setIcon(icon);

                // Update damage label
                JSONObject dmg = spell.getJSONObject("damage");
                int min = dmg.getInt("min");
                int max = dmg.getInt("max");
                //damageLabel.setText("Damage: " + min + " - " + max);

                // Update accuracy label
                int accuracy = spell.getInt("accuracy");
                //accuracyLabel.setText("Accuracy: " + accuracy);


                // Update pip label
                int pipCost = spell.getInt("pipCost");
                pipLabel.setText("Pip Cost: " + pipCost);
                updateFooter();
            }
        });


        JComboBox<String> enchantTypeSelector = new JComboBox<>(new String[] {
                "Damage Enchants",
                "Accuracy Enchants"
        });

        // When user selects an enchant
        enchantList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = enchantList.getSelectedIndex();
                JSONObject enchant = spellsEnchantsArray.getJSONObject(index);
                currentEnchant = enchant;
                // Load icon

                if (Objects.equals(currentEnchant.getString("type"), "damage"))
                {
                    ImageIcon icon = new ImageIcon("src/resources/assets/spells/enchants/damage/" + enchant.getString("image"));
                    enchantImage.setIcon(icon);
                }
                if (Objects.equals(currentEnchant.getString("type"), "accuracy")){
                    ImageIcon icon = new ImageIcon("src/resources/assets/spells/enchants/accuracy/" + enchant.getString("image"));
                    enchantImage.setIcon(icon);
                }

                updateFooter();
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

    private void updateFooter() {


        if (currentSpell == null) {
            damageLabel.setText("Damage: ---"); //no spell selected
            accuracyLabel.setText("Accuracy: ---");
        }
        if (currentSpell != null) {
            JSONObject dmg = currentSpell.getJSONObject("damage");
            int min = dmg.getInt("min");;
            int max = dmg.getInt("max");
            int spellAccuracy = currentSpell.getInt("accuracy");

            if (currentEnchant != null) { //spell and enchant
                int enchantAccuracy = currentEnchant.getInt("accuracy");
                int damage = currentEnchant.getInt("damage");

                int finalMin      = min + damage;
                int finalMax      = max + damage;
                int finalAccuracy = spellAccuracy + enchantAccuracy;

                damageLabel.setText("Damage: " + finalMin + " - " + finalMax);
                accuracyLabel.setText(String.valueOf(finalAccuracy));
            }
            else { //just spell
                damageLabel.setText("Damage: " + min + " - " + max);
                accuracyLabel.setText("Accuracy: " + spellAccuracy);
            }
        }

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

    private JSONArray loadSpellEnchantData() {
        try {
            String json = new String(Files.readAllBytes(Paths.get("src/resources/SpellDataEnchants.json")));
            JSONObject root = new JSONObject(json);
            return root.getJSONArray("spells");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load SpellDataEnchants.json");
            return new JSONArray();
        }
    }
}
