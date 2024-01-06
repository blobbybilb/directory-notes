import data.Directory;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;

public class UI extends JFrame {
    private JTree tree;
    private JTextArea textArea;
    private String selectedPath;
    private JLabel pathLabel;
    private JButton saveButton;
    private JPanel textPanel;

    public UI(String rootPath) {
        selectedPath = rootPath;
        makeTree();
        makeTextArea();
        makeSplitPane();
        setTitle("Directory Notes");
        setSize(1200, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch (Exception ignored) {
        }
        updateTextPanel();
    }

    private void makeSplitPane() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tree), new JScrollPane(textPanel));
        splitPane.setResizeWeight(0.3);
        this.add(splitPane);
    }

    private void makeTextArea() {
        pathLabel = new JLabel(selectedPath);
        saveButton = new JButton("Save");

        saveButton.addActionListener(e -> {
            Directory directory = new Directory(selectedPath);
            directory.saveFirstNote(textArea.getText());
        });

        textPanel = new JPanel(new BorderLayout());
        textPanel.add(pathLabel, BorderLayout.NORTH);
        textPanel.add(saveButton, BorderLayout.SOUTH);
        textArea = new JTextArea();
        textPanel.add(textArea, BorderLayout.CENTER);
    }

    private void makeTree() {
        DefaultMutableTreeNode rootNode = buildTree(new Directory(selectedPath));
        tree = new JTree(rootNode);

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = e.getPath();
                StringBuilder fullPath = new StringBuilder();
                for (int i = 0; i < path.getPathCount(); i++) {
                    fullPath.append(path.getPathComponent(i));
                    if (i < path.getPathCount() - 1) {
                        fullPath.append("/");
                    }
                }
                if (areUnsavedChanges()) {
                    int result = JOptionPane.showConfirmDialog(
                            UI.this,
                            "You have unsaved changes. Are you sure you want to continue?",
                            "Unsaved Changes",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (result == JOptionPane.NO_OPTION) {
                        return;
                    }
                }
                selectedPath = fullPath.toString();
                updateTextPanel();
            }
        });
    }

    private void updateTextPanel() {
        textArea.setText(
                new Directory(selectedPath).getFirstNote()
        );
        System.out.println(1);
        System.out.println(new Directory(selectedPath).getFirstNote());
        pathLabel.setText(selectedPath);
    }

    private Boolean areUnsavedChanges() {
        return false; // TODO: implement
    }

    private DefaultMutableTreeNode buildTree(Directory root) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(root);
        for (Directory directory : root.directories) {
            rootNode.add(buildTree(directory));
        }
        return rootNode;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UI ui = new UI(".");
            ui.setVisible(true);
        });
    }
}
