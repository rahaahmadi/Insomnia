import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * This class represent main frame
 * @author Raha Ahmadi
 * @version 0.0
 */

public class MainFrame extends JFrame {
    private  static JList<File> requestList;
    private static final String directoryPath = "./guiRequests/";
    private JPanel requestListPanel;
    private RequestPanel requestPanel;
    private ResponsePanel responsePanel;
    private JMenu application, view, help;
    private JMenuItem options, exit, toggleFullScreen, toggleSidebar, about, insomniaHelp;
    private boolean isFullScreen = false;
    private boolean hasSidebar = true;
    private Rectangle previousSize;
    private JPanel optionPanel;
    private JCheckBox systemTray;
    private static JCheckBox followRedirectCheckBox;

    /**
     * Create a new frame, Change look and feel, set frame size and location
     */
    public MainFrame() {
        super("Insomnia");
        // change look and feel to system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        setSize(880, 650);
        setLocation(200, 28);
        setMinimumSize(new Dimension(750, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        previousSize = new Rectangle(getX(), getY(), getWidth(), getHeight());
        requestListPanel = new JPanel();
        requestPanel = new RequestPanel();
        responsePanel = new ResponsePanel();
        application = new JMenu("Application");
        options = new JMenuItem("Options");
        exit = new JMenuItem("Exit");
        view = new JMenu("View");
        toggleFullScreen = new JMenuItem("Toggle Full Screen");
        toggleSidebar = new JMenuItem("Toggle Sidebar");
        help = new JMenu("Help");
        about = new JMenuItem("About");
        insomniaHelp = new JMenuItem("Help");
        optionPanel = new JPanel();
        systemTray = new JCheckBox("System tray");
        followRedirectCheckBox = new JCheckBox("Follow redirect");
        init();
    }

    /**
     * Initial frame with menubar and panels
     */
    public void init() {
        initMenubar();
        initRequestList();
        JPanel jPanel = new JPanel(new BorderLayout());
        JPanel panel = new JPanel(new GridLayout(1, 2));
        jPanel.add(requestListPanel, BorderLayout.WEST);
        panel.add(requestPanel);
        panel.add(responsePanel);
        jPanel.add(panel, BorderLayout.CENTER);
        add(jPanel);

    }

    /**
     * Initialize menubar with menu items
     */
    public void initMenubar() {
        JMenuBar menuBar = new JMenuBar();
        Handler keyHandler = new Handler();
        setFocusable(true);
        addKeyListener(keyHandler);
        // set mnemonic and accelerator
        application.setMnemonic(KeyEvent.VK_A);
        view.setMnemonic(KeyEvent.VK_V);
        help.setMnemonic(KeyEvent.VK_H);
        options.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        toggleFullScreen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, InputEvent.ALT_DOWN_MASK));
        toggleSidebar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, InputEvent.ALT_DOWN_MASK));
        about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
        insomniaHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, InputEvent.ALT_DOWN_MASK));
        // add action listener for menu items
        MenuItemHandler handler = new MenuItemHandler();
        options.addActionListener(handler);
        exit.addActionListener(handler);
        toggleFullScreen.addActionListener(handler);
        toggleSidebar.addActionListener(handler);
        about.addActionListener(handler);
        insomniaHelp.addActionListener(handler);
        application.add(options);
        application.add(exit);
        view.add(toggleFullScreen);
        view.add(toggleSidebar);
        help.add(about);
        help.add(insomniaHelp);
        menuBar.add(application);
        menuBar.add(view);
        menuBar.add(help);
        menuBar.setBackground(new Color(230, 230, 230));
        optionPanel.setLayout(new GridLayout(3, 1, 2, 2));
        optionPanel.add(systemTray);
        optionPanel.add(followRedirectCheckBox);
        // Check state of checkbox selection
        String tmp = FileUtils.fileReader(new File("follow redirect"));
        if (tmp.equals("true"))
            followRedirectCheckBox.setSelected(true);
        else
            followRedirectCheckBox.setSelected(false);
        followRedirectCheckBox.addChangeListener(e -> {
            String content;
            if (followRedirectCheckBox.isSelected())
                content = "true";
            else
                content = "false";
            FileUtils.fileWriter(content, "follow redirect");
        });
        String temp = FileUtils.fileReader(new File("system tray"));
        if (temp.equals("true"))
            systemTray.setSelected(true);
        else
            systemTray.setSelected(false);
        systemTray.addChangeListener(e -> {
            String content;
            if (systemTray.isSelected())
                content = "true";
            else
                content = "false";
            FileUtils.fileWriter(content, "system tray");
        });
        add(menuBar, BorderLayout.NORTH);
    }

    /**
     * Initialize request list panel
     * Restore saved request from guiRequests directory
     */
    public void initRequestList() {
        requestListPanel.setLayout(new BorderLayout());
        requestListPanel.setPreferredSize(new Dimension(getWidth()-650, getHeight()));
        JLabel label = new JLabel("  Insomnia");
        label.setBackground(new Color(110, 75, 200));
        label.setOpaque(true);
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        label.setForeground(Color.white);
        label.setPreferredSize(new Dimension(label.getWidth(), label.getHeight() + 50));
        requestListPanel.add(label, BorderLayout.NORTH);
        File[] files = FileUtils.getFilesInDirectory(directoryPath);
        requestList = new JList<>(files);
        requestList.setCellRenderer(new MyCellRenderer());
        JScrollPane scrollPane = new JScrollPane(requestList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        requestList.setCellRenderer(new MyCellRenderer());
        requestList.addMouseListener(new MyMouseAdapter());
        requestListPanel.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Update list of requests
     * This method calls when a request saved
     */
    private static void updateList() {
        File[] newFiles = FileUtils.getFilesInDirectory(directoryPath);
        requestList.setListData(newFiles);
    }

    /**
     * Save request in file
     * @param request request to save
     * @param fileName file name
     */
    public static void save(Request request, String fileName) {
        FileUtils.writeToFile(request,directoryPath + fileName);
        updateList();
    }
    private class MyMouseAdapter extends MouseAdapter {
        /**
         * Mouse clicked action
         * @param e MouseEvent
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            // Double-click detected
            if (e.getClickCount() == 2) {
                int index = requestList.locationToIndex(e.getPoint());
                File[] curr = FileUtils.getFilesInDirectory(directoryPath);
                Request request = FileUtils.readFromFile(curr[index]);
                requestPanel.openExistingRequest(request);
                responsePanel.openRequest();
            }
        }
    }
    private static class MyCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object object, int index, boolean isSelected, boolean cellHasFocus) {
            if (object instanceof File) {
                File file = (File) object;
                setText(file.getName());
                if (isSelected) {
                    setBackground(list.getSelectionBackground());
                    setForeground(list.getSelectionForeground());
                } else {
                    setBackground(list.getBackground());
                    setForeground(list.getForeground());
                }
                setEnabled(list.isEnabled());
            }
            return this;
        }
    }

    /**
     * Show frame
     */
    public void showFrame() {
        setVisible(true);
    }
    /**
     * Toggle full screen method
     * if frame is full screen it returns to previous size
     * if frame is not full screen if changes to full screen
     */
    public void toggleFullScreen() {
        GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = graphics.getDefaultScreenDevice();
        if (isFullScreen) {
            dispose();
            setUndecorated(false);
            setResizable(true);
            setBounds(previousSize);
            device.setFullScreenWindow(null);
            setVisible(true);
        } else {
            dispose();
            previousSize = this.getBounds();
            setUndecorated(true);
            setResizable(false);
            device.setFullScreenWindow(this);
//            setBounds(0,0,Toolkit.getDefaultToolkit().getScreenSize().width,Toolkit.getDefaultToolkit().getScreenSize().height);
            setVisible(true);
        }
        isFullScreen = !isFullScreen;
    }
    /**
     * If there frame has sidebar remove it
     * else add sidebar
     */
    public void toggleSidebar() {
        if (hasSidebar)
            requestListPanel.setVisible(false);
        else
            requestListPanel.setVisible(true);
        hasSidebar = !hasSidebar;
    }

    /**
     * Add app to system tray
     */
    public void setSystemTray() {
        if (SystemTray.isSupported()) {
            SystemTray systemTray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage("InsomniaPic.png");
            PopupMenu trayPopupMenu = new PopupMenu();
            MenuItem action = new MenuItem("Show");
            action.addActionListener(e -> setVisible(true));
            trayPopupMenu.add(action);
            MenuItem close = new MenuItem("Close");
            close.addActionListener(e -> System.exit(0));
            trayPopupMenu.add(close);
            TrayIcon trayIcon = new TrayIcon(image, "SystemTray Demo", trayPopupMenu);
            trayIcon.setImageAutoSize(true);
            try {
                systemTray.add(trayIcon);
            } catch (AWTException awtException) {
                awtException.printStackTrace();
            }
        } else
            System.out.println("System tray is not supported!");
    }

    /**
     * @return true if follow redirect checkbox is selected
     */
    public static boolean followRedirect() {
        return followRedirectCheckBox.isSelected();
    }

    /**
     * Action listener class
     */
    private class MenuItemHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(exit)) {
                if (systemTray.isSelected()) {
                    setSystemTray();
                    setVisible(false);
                } else
                    System.exit(0);
            }
            if (e.getSource().equals(toggleFullScreen))
                toggleFullScreen();
            if (e.getSource().equals(toggleSidebar))
                toggleSidebar();
            if (e.getSource().equals(about)) {
                String msg = "Name: Raha Ahmadi\nID: 9831108\nEmail: r4ha.ahmadi@gmail.com";
                JOptionPane.showMessageDialog(null, msg, "Information", JOptionPane.INFORMATION_MESSAGE);
            }
            if (e.getSource().equals(insomniaHelp)) {
                String msg = "Each API request uses an HTTP method. The most common methods are GET, POST, PATCH, PUT, and DELETE.\n"
                        + "GET methods retrieve data from an API.\n" +
                        "POST sends new data to an API.\n" +
                        "PATCH and PUT methods update existing data.\n" +
                        "DELETE removes existing data.\n"
                        + "When you create a request and click Send, the API response appears.\n" +
                        "With all methods you can pass parameters to the server using \"Query String Parameters\". For example, in the following request,\n"
                        + "http://example.com/hi/there?hand=wave\nThe parameter \"hand\" has the value \"wave\".\n" +
                        "Except GET request you can sent data (Either form data or json data).\n" +
                        "Headers:\n"+
                        "HTTP header fields provide required information about the request or response, or about the object sent in the message body.\n" +
                        "Response Status Code:\n" +
                        "Every response is accompanied by a status code.\nThe status code provides a summary of the nature of response sent by the server.\n" +
                        "For example, a status code of 200 means everything is okay with the response\nand a code of 404 implies that the requested URL does not exist on server.";
                JOptionPane.showMessageDialog(null, msg, "Help", JOptionPane.INFORMATION_MESSAGE);
            }
            if (e.getSource().equals(options))
                JOptionPane.showMessageDialog(getContentPane(), optionPanel);
        }
    }

    /**
     * key listener class
     */
    private class Handler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_A)
            application.doClick();
            if (e.getKeyCode() == KeyEvent.VK_V)
                view.doClick();
            if (e.getKeyCode() == KeyEvent.VK_H)
                help.doClick();
        }
    }
}