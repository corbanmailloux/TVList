import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * TVListGui.java
 *
 * File:
 *  $Id$
 *
 * Revisions:
 *  $Log$
 * 
 */

/**
 * A GUI for the TVList
 * 
 * @author Corban Mailloux <corb@corb.co>
 */
public class TVListGui extends JFrame implements Observer, ActionListener,
    ListSelectionListener {

  /**
   * The "Please wait" window
   */
  private JDialog waitDialog;

  /**
   * The "Add Folders" window
   */
  // private JFrame addFolder;

  /**
   * The folder selection window.
   */
  private JFileChooser folderSelect;

  /**
   * The add folder button.
   */
  private JButton browseForFolder;

  /**
   * The list of folder locations to search.
   */
  private List<File> folderList;

  /**
   * Default, to avoid a warning
   */
  private static final long serialVersionUID = 1L;

  /**
   * The model that handles the TV series
   */
  private TVList TVModel;

  /**
   * The current list of TVs
   */
  private List<TV> TVList;

  /**
   * The top search box
   */
  private JTextField searchBox;

  /**
   * The JList for the main window
   */
  private JList<String> list;

  /**
   * The ListModel for the JList of TV titles
   */
  private DefaultListModel<String> listModel;

  /**
   * The button that opens the current selection in explorer
   */
  private JButton openExplorer;

  /**
   * The button that shows duplicates
   */
  private JButton showDupes;

  /**
   * The label that says the number of TVs
   */
  private JLabel statusLabel;

  /**
   * The button that resets the search
   */
  private JButton resetButton;

  /**
   * The TV Details box
   */
  private JTextArea detailsBox;

  /**
   * The constructor for a GUI
   */
  public TVListGui() {

    // Set up the folderList and the properties file
    folderList = new ArrayList<File>();
    Properties prop = new Properties();
    try {
      prop.load(new FileInputStream("TVList.properties"));
    } catch (FileNotFoundException e1) {
      try {
        prop.load(new FileInputStream("src/TVList.properties"));
      } catch (FileNotFoundException e2) {
        JOptionPane.showMessageDialog(this,
            "\"TVList.properties\" file not found.");
        System.exit(1);
      } catch (IOException e2) {
        e2.printStackTrace();
        System.exit(1);
      }
    } catch (IOException e1) {
      e1.printStackTrace();
      System.exit(1);
    }

    if (prop.containsKey("FOLDERS")) {
      String folders = prop.getProperty("FOLDERS");
      String[] folderArray = folders.split(";");
      for (String folder : folderArray) {
        folderList.add(new File(folder));
      }
    }
    // System.out.println(folderList);

    /*
     * if (folderList.isEmpty()) { // Build the folder select dialog
     * folderSelect = new JFileChooser();
     * folderSelect.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
     * addFolder = new JFrame();
     * addFolder.setTitle("Add new location(s) for TVs.");
     * addFolder.setSize(100, 500);
     * addFolder.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
     * browseForFolder = new JButton("Add a folder");
     * browseForFolder.addActionListener(this); addFolder.add(browseForFolder);
     * addFolder.setVisible(true);
     * 
     * 
     * //folderSelect.showOpenDialog(this); }
     */

    // Build the main GUI
    setTitle("TVList");
    setLayout(new BorderLayout());

    // Top search box
    searchBox = new JTextField("Enter search term...");
    searchBox.addFocusListener(new FocusListener() {

      @Override
      public void focusGained(FocusEvent arg0) {
        if (searchBox.getText().equals("Enter search term...")) {
          searchBox.setText("");
        }
      }

      @Override
      public void focusLost(FocusEvent arg0) {
        if (searchBox.getText().equals("")) {
          searchBox.setText("Enter search term...");
        }
      }

    });

    searchBox.addKeyListener(new KeyAdapter() {
      // Allows the user to press ENTER to open a TV in Explorer
      @Override
      public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
          searchBox.setText("");
        }
      }
    });

    // Listen for ENTER
    searchBox.addActionListener(this);

    // Real-time searching
    searchBox.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void changedUpdate(DocumentEvent e) {
        if (!searchBox.getText().equals("Enter search term...")) {
          TVModel.search(searchBox.getText());
        }
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        if (!searchBox.getText().equals("Enter search term...")) {
          TVModel.search(searchBox.getText());
        }
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        if (!searchBox.getText().equals("Enter search term...")) {
          TVModel.search(searchBox.getText());
        }
      }
    });

    // Middle selection box
    listModel = new DefaultListModel<String>();
    // Add a blank element (required by JList)
    listModel.addElement(" ");

    // Create the list and put it in a scroll pane.
    list = new JList<String>(listModel);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setSelectedIndex(0);
    list.setVisibleRowCount(20);
    list.addListSelectionListener(this);
    list.addKeyListener(new KeyAdapter() {
      // Allows the user to press ENTER to open a TV in Explorer
      @Override
      public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
          int tempSelectionIndex = list.getSelectedIndex();
          if (tempSelectionIndex >= 0) {
            TVModel.openExplorer(TVList.get(tempSelectionIndex));
          }
        }
      }
    });

    list.addMouseListener(new MouseAdapter() {
      // Allows the user to double-click to open a TV in Explorer
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          int tempSelectionIndex = list.getSelectedIndex();
          if (tempSelectionIndex >= 0) {
            TVModel.openExplorer(TVList.get(tempSelectionIndex));
          }
        }
      }
    });

    // Get the color preferences, and try to set them.
    String backColor = prop.getProperty("Color.Background");
    String foreColor = prop.getProperty("Color.Foreground");
    try {
      if (backColor != null && foreColor != null) {
        list.setBackground((Color) (Class.forName("java.awt.Color")
            .getField(backColor)).get(null));
        list.setForeground((Color) (Class.forName("java.awt.Color")
            .getField(foreColor)).get(null));
      }
    } catch (IllegalArgumentException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    } catch (IllegalAccessException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    } catch (NoSuchFieldException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    } catch (SecurityException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    } catch (ClassNotFoundException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    // Bottom section (Details and buttons)
    JPanel bottomPanel = new JPanel(new BorderLayout());

    detailsBox = new JTextArea("Select a TV Series.", 3, 0);
    detailsBox.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
    detailsBox.setEditable(false);

    JPanel bottomButtonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
    openExplorer = new JButton("Open in Explorer");
    openExplorer.addActionListener(this);
    showDupes = new JButton("Show Duplicates");
    showDupes.addActionListener(this);
    statusLabel = new JLabel();
    resetButton = new JButton("Reset");
    resetButton.addActionListener(this);
    bottomButtonPanel.add(openExplorer);
    bottomButtonPanel.add(showDupes);
    bottomButtonPanel.add(statusLabel);
    bottomButtonPanel.add(resetButton);

    // Add to the bottomPanel
    bottomPanel.add(detailsBox, BorderLayout.CENTER);
    bottomPanel.add(bottomButtonPanel, BorderLayout.SOUTH);

    // Add everything the the main frame
    add(searchBox, BorderLayout.NORTH);
    add(new JScrollPane(list), BorderLayout.CENTER);
    add(bottomPanel, BorderLayout.SOUTH);
    setSize(700, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // pack();

    // Build the "Please wait" dialog
    waitDialog = new JDialog();
    JLabel label = new JLabel("Please wait while the files are indexed.");
    label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
    waitDialog.setTitle("Please Wait...");
    waitDialog.add(label);
    waitDialog.pack();

    // Show the "Please wait" dialog
    waitDialog.setVisible(true);
    // Actually create the TVList and scan the folders.
    TVModel = new TVList(folderList);
    // Register the view with the model.
    TVModel.addObserver(this);
    // waitDialog.dispose();
    // waitDialog.setVisible(false);

    // Show the main window
    // setVisible(true);
    TVModel.update();

  }

  public static void main(String[] args) {
    new TVListGui();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
   */
  @Override
  public void update(Observable obs1, Object obj1) {
    TVList = TVModel.getTVList();
    int numTV = TVList.size();
    statusLabel.setText("Number of TV Series: " + numTV);

    listModel.clear();

    if (numTV > 0) {
      for (TV m1 : TVList) {
        listModel.addElement(m1.getName());
      }
    }
    validate();

    waitDialog.setVisible(false);
    setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // If it's a search:
    if (e.getSource() == searchBox) {
      TVModel.search(searchBox.getText());
    } else if (e.getSource() == openExplorer) {
      // Open Explorer
      int tempSelectionIndex = list.getSelectedIndex();
      if (tempSelectionIndex >= 0) {
        TVModel.openExplorer(TVList.get(tempSelectionIndex));
      }
    } else if (e.getSource() == showDupes) {
      TVModel.dupes();
    } else if (e.getSource() == resetButton) {
      searchBox.setText("");
    } else if (e.getSource() == browseForFolder) {
      if (folderSelect.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        folderList.add(folderSelect.getSelectedFile());
      }
    }

  }

  /**
   * Updates the details box when a TV is selected.
   */
  @Override
  public void valueChanged(ListSelectionEvent e) {
    int tempSelectionIndex = list.getSelectedIndex();
    if (tempSelectionIndex >= 0) {
      detailsBox.setText(TVList.get(tempSelectionIndex).toString());
    }
  }
}
