import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class ResponsePanel extends JPanel {
    private JPanel preview;
    private static JTextArea response;
    private static JLabel status;
    private static JLabel time;
    private static JLabel responseBytes;
    private static JPanel header;
    private static Map<String, List<String>> responseHeaders;
    private static JLabel responsePic;
    /**
     * create a new panel
     */
    public ResponsePanel() {
        preview = new JPanel();
        response = new JTextArea();
        status = new JLabel();
        time = new JLabel();
        responseBytes = new JLabel();
        header = new JPanel(new BorderLayout());
        responsePic = new JLabel();
        this.init();
    }

    /**
     * Initialize response panel
     */
    public void init() {
        setLayout(new BorderLayout());
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBorder(new EmptyBorder(8, 12, 8, 8));
        JPanel p = new JPanel(new GridBagLayout());
        status.setForeground(Color.white);
        status.setOpaque(true);
        status.setFont(new Font("Arial", Font.PLAIN, 14));
        time.setBackground(new Color(140,140,140));
        time.setForeground(Color.white);
        time.setOpaque(true);
        time.setFont(new Font("Arial", Font.PLAIN, 14));
        responseBytes.setBackground(new Color(140,140,140));
        responseBytes.setForeground(Color.white);
        responseBytes.setOpaque(true);
        responseBytes.setFont(new Font("Arial", Font.PLAIN, 14));
        p.add(status);
        p.add(time);
        p.add(responseBytes);
        northPanel.add(p, BorderLayout.WEST);
        add(northPanel, BorderLayout.NORTH);
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel body = new JPanel(new BorderLayout());
        tabbedPane.addTab("Body", body);
        tabbedPane.addTab("Header", new JScrollPane(header));
        tabDisplay(tabbedPane);
        tabbedPane.setBackground(Color.lightGray);
        add(tabbedPane, BorderLayout.CENTER);
        JTabbedPane bodyTab = new JTabbedPane();
        JPanel raw = new JPanel(new BorderLayout());
        bodyTab.addTab("Raw", raw);
        bodyTab.addTab("Preview", preview);
        tabDisplay(bodyTab);
        body.add(bodyTab);
        header.setBorder(new EmptyBorder(0, 5, 15, 5));
        JPanel panel = new JPanel(new GridLayout(1, 2, 3, 5));
        JLabel label1 = new JLabel("Key");
        JLabel label2 = new JLabel("Value");
        label1.setFont(new Font("Arial",Font.PLAIN,12));
        label2.setFont(new Font("Arial",Font.PLAIN,12));
        label1.setForeground(Color.DARK_GRAY);
        label2.setForeground(Color.DARK_GRAY);
        panel.add(label1);
        panel.add(label2);
        header.add(panel, BorderLayout.NORTH);
        JButton copy = new JButton("Copy to Clipboard");
        JPanel tmp = new JPanel(new BorderLayout());
        tmp.add(copy, BorderLayout.EAST);
        header.add(tmp, BorderLayout.SOUTH);
        copy.addActionListener(e -> {
            StringBuilder res = new StringBuilder();
            for (Map.Entry<String, List<String>> entry : responseHeaders.entrySet()) {
                res.append(entry.getKey()).append("\t").append(entry.getValue()).append("\n");
            }
            StringSelection stringSelection = new StringSelection(res.toString());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        });
        response.setLineWrap(true);
        response.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(response);
        scrollPane.setVerticalScrollBarPolicy(20);
        raw.add(scrollPane, "Center");
        preview.add(responsePic);
    }

    /**
     * method to remove tab margin
     * @param tab chosen JTabbedPane
     */
    private void tabDisplay(JTabbedPane tab) {
        tab.setUI(new BasicTabbedPaneUI() {
            private final Insets borderInsets = new Insets(0, 0, 0, 0);

            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            }

            protected Insets getContentBorderInsets(int tabPlacement) {
                return this.borderInsets;
            }
        });
        tab.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    }

    /**
     * Show response and status code in response panel
     * @param request request to send
     */
    public static void sendRequest(Request request) {
        SwingWorker<String,Void> swingWorker = new SwingWorker<>() {
            long connectionTime;
            long bytes;

            /**
             * Send request
             * @return response
             */
            @Override
            protected String doInBackground() {
                long start = new Date().getTime();
                String result = request.sendAndGetResponse();
                long end = new Date().getTime();
                connectionTime = end - start;
                if (request.contentType().equals("image/png")) {
                    try {
                        BufferedImage image = ImageIO.read(new URL(request.getUrl()));
                        ImageIO.write(image,"png",new File("pic.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return result;
            }

            /**
             * Show response massage in textarea and set label's text
             */
            @Override
            protected void done() {
                try {
                    String str = get();
                    response.setText(str);
                    status.setText(" " + request.detectStatus() + "  ");
                    if (request.getStatus().equals("Error")) {
                        status.setBackground(new Color(200,50,50));
                        time.setText(" 0 ms ");
                        responseBytes.setText(" 0 B ");
                    }
                    else {
                        String temp = request.getStatus();
                        temp = temp.substring(0, temp.indexOf(" "));
                        // set status label color
                        if (Integer.parseInt(temp) / 100 == 2)
                            status.setBackground(new Color(85,180,50));
                        else if (Integer.parseInt(temp) / 100 == 3)
                            status.setBackground(new Color(110,80,250));
                        else if (Integer.parseInt(temp) / 100 == 4)
                            status.setBackground(new Color(230,100,75));
                        else if (Integer.parseInt(temp) / 100 == 5)
                            status.setBackground(new Color(200,50,50));
                        // set time
                        if (connectionTime > 1000) {
                            double tmp = (double) connectionTime / 1000;
                            String runningTime = String.format("%.2f", tmp);
                            runningTime = " " + runningTime.concat(" s ");
                            time.setText(runningTime);
                        }
                        else if (connectionTime == 1000) {
                            time.setText(" 1 s ");
                        }
                        else {
                            time.setText(" " + connectionTime + " ms ");
                        }
                        byte[] b = str.getBytes(StandardCharsets.UTF_8);
                        bytes = b.length;
                        //set bytes
                        if (bytes > 1000) {
                            double tmp = (double) bytes / 1000;
                            String resBytes = String.format("%.2f", tmp);
                            resBytes = " " + resBytes.concat(" KB ");
                            responseBytes.setText(resBytes);
                        }
                        else if (bytes == 1000) {
                            responseBytes.setText(" 1 KB ");
                        }
                        else {
                            responseBytes.setText("" + bytes + " B ");
                        }
                    }
//
                    //set response headers in header tab.
                    responseHeaders = request.responseHeaders();
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
                    for (Map.Entry<String, List<String>> entry : responseHeaders.entrySet()) {
                        JPanel jPanel = new JPanel(new GridLayout(1,2,1,1));
                        jPanel.setBorder(new EmptyBorder(1,0,1,0));
                        JTextArea key = new JTextArea(entry.getKey());
                        key.setLineWrap(true);
                        key.setEditable(false);
                        JTextArea value = new JTextArea();
                        value.setText(String.valueOf(entry.getValue()));
                        value.setLineWrap(true);
                        value.setEditable(false);
                        jPanel.add(key);
                        jPanel.add(value);
                        panel.add(jPanel);
                    }
                    header.add(panel, BorderLayout.CENTER);
                    if (request.contentType().equals("image/png")) {
                        Icon icon = new ImageIcon("pic.png");
                        responsePic.setIcon(icon);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        swingWorker.execute();
    }

    /**
     * reset response panel when saved request open
     */
    public void openRequest() {
        status.setText("");
        time.setText("");
        responseBytes.setText("");
        response.setText("");
        preview = new JPanel();
        updateUI();
    }
}