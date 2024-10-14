package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.InputStream;

public class YouTubeTrenderFrame extends JFrame {
    Dimension frmWDim = new Dimension(10, 0);
    Dimension frmHDim = new Dimension(0, 10);
    Dimension smlVerticalGap = new Dimension(0, 5);
    Dimension smlHorizontalGap = new Dimension(5, 0);

    private JTextField jTextFieldDataFile;
    private JTextField jTextFieldChannel;
    private JTextField jTextFieldDate;
    private JTextField jTextFieldTitle;
    private JTextArea jTextAreaVideoDescription;
    private JTextField jTextFieldViewCount;

    private JList<String> jListVideo; // To hold video titles
    private DefaultListModel<String> videoModel; // For JList model
    private JsonArray items; // Store the array of items globally

    public YouTubeTrenderFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(new Dimension(650, 300));
        setResizable(false);
        initComponents();
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(YouTubeTrenderFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        EventQueue.invokeLater(() -> new YouTubeTrenderFrame().setVisible(true));
    }

    private void initComponents() {
        JPanel jPanelContainer = new JPanel();
        jPanelContainer.setLayout(new BoxLayout(jPanelContainer, BoxLayout.Y_AXIS));
        jPanelContainer.setBorder(new MatteBorder(10, 10, 10, 10, new Color(0.0f, 0.66f, 0.42f)));
        jPanelContainer.add(Box.createRigidArea(frmHDim));
        jPanelContainer.add(createTopPanel());
        jPanelContainer.add(Box.createRigidArea(frmHDim));
        jPanelContainer.add(createVideoPanel());
        jPanelContainer.add(Box.createRigidArea(frmHDim));
        jPanelContainer.add(createVideoDetailsPanel());

        add(jPanelContainer);
        pack();
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setMaximumSize(new Dimension(550, 25));
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        jTextFieldDataFile = new JTextField();
        jTextFieldDataFile.setPreferredSize(new Dimension(200, 25));
        jTextFieldDataFile.setText("youtubedata_15_50.json"); // Adjust the path accordingly
        JButton jButtonParse = new JButton("Load");
        jButtonParse.addActionListener(this::jButtonParseActionPerformed);

        topPanel.add(Box.createRigidArea(frmWDim));
        topPanel.add(jTextFieldDataFile);
        topPanel.add(Box.createRigidArea(frmWDim));
        topPanel.add(jButtonParse);
        topPanel.add(Box.createRigidArea(frmWDim));

        return topPanel;
    }

    private JPanel createVideoPanel() {
        JPanel videoPanel = new JPanel();
        videoPanel.setPreferredSize(new Dimension(525, 240));
        videoPanel.setBorder(BorderFactory.createTitledBorder("Videos"));

        // Initialize the list model and JList
        videoModel = new DefaultListModel<>();
        jListVideo = new JList<>(videoModel);
        JScrollPane jScrollPaneListVideo = new JScrollPane(jListVideo);
        jScrollPaneListVideo.setPreferredSize(new Dimension(500, 200));

        // Add a listener for the JList selection
        jListVideo.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                int selectedIndex = jListVideo.getSelectedIndex();
                if (selectedIndex != -1) {
                    // Load the details of the selected video
                    loadVideoDetails(selectedIndex);
                }
            }
        });

        videoPanel.setLayout(new BorderLayout());
        videoPanel.add(jScrollPaneListVideo, BorderLayout.CENTER); // Add the list

        return videoPanel;
    }

    private JPanel createVideoDetailsPanel() {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setPreferredSize(new Dimension(525, 300));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Video Details"));

        JLabel jLabelChannel = new JLabel("Channel:");
        jTextFieldChannel = new JTextField();
        jTextFieldChannel.setEditable(false);

        JLabel jLabelDate = new JLabel("Date Posted:");
        jTextFieldDate = new JTextField();
        jTextFieldDate.setEditable(false);

        JLabel jLabelTitle = new JLabel("Title:");
        jTextFieldTitle = new JTextField();
        jTextFieldTitle.setEditable(false);

        JLabel jLabelViewCount = new JLabel("View Count:");
        jTextFieldViewCount = new JTextField();
        jTextFieldViewCount.setEditable(false);

        JLabel jLabelDescription = new JLabel("Description:");
        jTextAreaVideoDescription = new JTextArea();
        jTextAreaVideoDescription.setEditable(false);
        jTextAreaVideoDescription.setLineWrap(true);
        jTextAreaVideoDescription.setWrapStyleWord(true);
        JScrollPane jScrollPaneVideoDescription = new JScrollPane(jTextAreaVideoDescription);

        // Layout for details panel
        detailsPanel.setLayout(new GridLayout(5, 1));
        detailsPanel.add(createHorizontalBox(jLabelChannel, jTextFieldChannel));
        detailsPanel.add(createHorizontalBox(jLabelDate, jTextFieldDate));
        detailsPanel.add(createHorizontalBox(jLabelTitle, jTextFieldTitle));
        detailsPanel.add(createHorizontalBox(jLabelViewCount, jTextFieldViewCount));
        detailsPanel.add(jScrollPaneVideoDescription); // Add text area as scrollable

        return detailsPanel;
    }

    private Box createHorizontalBox(JLabel jLabel, JTextField jTextField) {
        Box b = Box.createHorizontalBox();
        b.setPreferredSize(new Dimension(500, 25));
        jLabel.setPreferredSize(new Dimension(80, 25));
        jLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        b.add(jLabel);
        b.add(Box.createRigidArea(smlHorizontalGap));
        b.add(jTextField);
        return b;
    }

    private void jButtonParseActionPerformed(ActionEvent evt) {
        String dataFile = jTextFieldDataFile.getText();

        // Load JSON file using class loader
        try (InputStream inputStream = YouTubeTrender.class.getClassLoader().getResourceAsStream(dataFile)) {
            if (inputStream == null) {
                JOptionPane.showMessageDialog(this, "File not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JsonReader jsonReader = Json.createReader(inputStream);
            JsonObject jsonObject = jsonReader.readObject();
            items = jsonObject.getJsonArray("items"); // Store the items array globally

            // Clear the previous entries in the list
            videoModel.clear();

            // Populate the JList with video titles
            for (JsonObject item : items.getValuesAs(JsonObject.class)) {
                JsonObject snippet = item.getJsonObject("snippet");
                String title = snippet.getString("title");
                videoModel.addElement(title); // Add the title to the JList model
            }

            // Optionally, load the details of the first video if available
            if (items.size() > 0) {
                loadVideoDetails(0); // Load details of the first item by default
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadVideoDetails(int index) {
        // Load video details based on selected index
        JsonObject selectedItem = items.getJsonObject(index);
        JsonObject snippet = selectedItem.getJsonObject("snippet");
        JsonObject statistics = selectedItem.getJsonObject("statistics");

        // Update the detail fields with the selected video's information
        jTextFieldChannel.setText(snippet.getString("channelTitle"));
        jTextFieldDate.setText(snippet.getString("publishedAt"));
        jTextFieldTitle.setText(snippet.getString("title"));
        jTextAreaVideoDescription.setText(snippet.getString("description"));
        jTextFieldViewCount.setText(statistics.getString("viewCount"));
    }
}
