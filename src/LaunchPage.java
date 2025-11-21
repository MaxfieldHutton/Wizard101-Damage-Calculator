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
    private JSONArray enemyArray;     // holds full JSON enemy data

    private JSplitPane spellPane, enchantPane, enemyPane, parentPane;
    private JLabel damageLabel, accuracyLabel, pipLabel;


    private JSONObject currentSpell = null;
    private JSONObject currentEnchant = null;
    private JSONObject currentEnemy = null;



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
        DefaultListModel<String> modelEnchants = new DefaultListModel<>();

        for (int i = 0; i < spellsEnchantsArray.length(); i++) {
            JSONObject o = spellsEnchantsArray.getJSONObject(i);
            modelEnchants.addElement(o.getString("displayName"));
        }

        JList<String> enchantList = new JList<>(modelEnchants);
        enchantList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane enchantListScroll = new JScrollPane(enchantList);

        // Right side image panel
        JLabel enchantImage = new JLabel("", JLabel.CENTER);
        JPanel enchantimagePanel = new JPanel(new BorderLayout());
        enchantimagePanel.add(enchantImage, BorderLayout.CENTER);

        enchantPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, enchantListScroll, enchantimagePanel);
        enchantPane.setDividerLocation(200);
        /// /////////////////////////

        /// ///////////////////////// enemies
        // JSON loading
        enemyArray = loadEnemyData();

        // Build spell list
        DefaultListModel<String> modelEnemy = new DefaultListModel<>();

        for (int i = 0; i < enemyArray.length(); i++) {
            JSONObject o = enemyArray.getJSONObject(i);
            modelEnemy.addElement(o.getString("displayName"));
        }

        JList<String> enemyList = new JList<>(modelEnemy);
        enemyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane enemyListScroll = new JScrollPane(enemyList);

        // Right side image panel
        JLabel enemyImage = new JLabel("", JLabel.CENTER);
        JPanel enemyImagePanel = new JPanel(new BorderLayout());
        enemyImagePanel.add(enemyImage, BorderLayout.CENTER);

        enemyPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, enemyListScroll, enemyImagePanel);
        enemyPane.setDividerLocation(200);
        /// /////////////////////////

        /// ///////////////////////// parent pane
        parentPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spellPane, enchantPane);
        parentPane.setDividerLocation(400);

        /// /////////////////////////


        mainPanel.add(parentPane, BorderLayout.CENTER);
        mainPanel.add(enemyPane, BorderLayout.EAST);
        enemyPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));


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


        // When user selects an enemy
        enemyList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = enemyList.getSelectedIndex();
                JSONObject enemy = enemyArray.getJSONObject(index);
                currentEnemy = enemy;
                // Load icon
                ImageIcon icon = new ImageIcon("src/resources/assets/creatures/" + enemy.getString("image"));

                // Get panel size (or fallback if not yet laid out)
                int maxW = enemyImage.getWidth() > 0 ? enemyImage.getWidth() : 300;
                int maxH = enemyImage.getHeight() > 0 ? enemyImage.getHeight() : 300;
                // Scale image to fit
                enemyImage.setIcon(scaleToFit(icon, maxW, maxH));

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

                if (finalMin == finalMax) {
                    damageLabel.setText("Damage: " + finalMin);
                } else {
                    damageLabel.setText("Damage: " + finalMin + " - " + finalMax);
                }
                accuracyLabel.setText(String.valueOf(finalAccuracy));
            }
            else { //just spell
                if (min == max) {
                    damageLabel.setText("Damage: " + min);
                } else {
                    damageLabel.setText("Damage: " + min + " - " + max);
                }
                accuracyLabel.setText(String.valueOf(spellAccuracy));
            }
        }

    }

    private ImageIcon scaleToFit(ImageIcon original, int maxW, int maxH) {
        int width = original.getIconWidth();
        int height = original.getIconHeight();

        double scale = Math.min((double) maxW / width, (double) maxH / height);

        int newW = (int) (width * scale);
        int newH = (int) (height * scale);

        Image resized = original.getImage().getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        return new ImageIcon(resized);
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
            return root.getJSONArray("enchants");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load SpellDataEnchants.json");
            return new JSONArray();
        }
    }

    private JSONArray loadEnemyData() {
        try {
            String json = new String(Files.readAllBytes(Paths.get("src/resources/creatures.json")));
            JSONObject root = new JSONObject(json);
            return root.getJSONArray("creatures");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load creatures.json");
            return new JSONArray();
        }
    }
}
