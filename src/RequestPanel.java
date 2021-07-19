import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class RequestPanel extends JPanel {
    private JTextArea addressBar;
    private JButton send;
    private JButton save;
    private JComboBox<String> methods;
    private JTabbedPane bodyTabs;
    private JTabbedPane tabbedPane;
    private HashMap<JTextArea,JTextArea> headers;
    private HashMap<JTextArea, JTextArea> formData;
    private HashMap<JTextArea, JTextArea> json;
    private HashMap<JTextArea, JTextArea> query;
    private MyHandler handler;
    private JButton create;

    /**
     * Create a new panel
     */
    public RequestPanel() {
        setLayout(new BorderLayout());
        addressBar = new JTextArea("type a URL");
        send = new JButton("send");
        save = new JButton("save");
        bodyTabs = new JTabbedPane();
        tabbedPane = new JTabbedPane();
        headers = new HashMap<>();
        formData = new HashMap<>();
        json = new HashMap<>();
        query = new HashMap<>();
        handler = new MyHandler();
        init();
    }

    /**
     * Initialize request panel
     */
    public void init() {
        JPanel northPanel = new JPanel(new BorderLayout(1, 1));
        northPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        addressBar.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(addressBar);
        scrollPane.setPreferredSize(new Dimension(130, 37));
        JPanel panel = new JPanel(new GridLayout(1,2));
        send.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 5));
        send.addActionListener(handler);
        save.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 5));
        save.addActionListener(handler);
        panel.add(send);
        panel.add(save);
        String[] items = {"GET", "POST", "PUT", "DELETE"};
        methods = new JComboBox<>(items);
        methods.setPreferredSize(new Dimension(methods.getWidth()+65, methods.getHeight()));
        methods.setFont(new Font("Arial", Font.PLAIN, 10));
        northPanel.add(methods, BorderLayout.WEST);
        northPanel.add(scrollPane, BorderLayout.CENTER);
        northPanel.add(panel, BorderLayout.EAST);
        add(northPanel, BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout());
        initBodyTabs();
        displayTab(bodyTabs);
        body.add(bodyTabs);
        JPanel headerPanel = new JPanel();
        keyValue(headerPanel,"header");
        JPanel query = new JPanel();
        keyValue(query,"query");
        tabbedPane.addTab("Body", body);
        tabbedPane.addTab("Header", new JScrollPane(headerPanel));
        tabbedPane.addTab("Query", new JScrollPane(query));
        displayTab(tabbedPane);
        tabbedPane.setBackground(Color.lightGray);
        add(new JScrollPane(tabbedPane), BorderLayout.CENTER);
    }

    /**
     * remove JTabbedPane margin
     * @param tab chosen tab
     */
    private void displayTab(JTabbedPane tab) {
        tab.setUI(new BasicTabbedPaneUI() {
            private final Insets borderInsets = new Insets(0, 0, 0, 0);
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            }
            protected Insets getContentBorderInsets(int tabPlacement) {
                return this.borderInsets;
            }
        });
        tab.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,1));
    }

    /**
     * Initialize body tab
     */
    private void initBodyTabs() {
        JPanel noBodyPanel = new JPanel();
        noBodyPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        Icon icon = new ImageIcon("lightBackground.png");
        JLabel label = new JLabel(icon);
        JLabel label1 = new JLabel("Select a body from above");
        label1.setFont(new Font("Arial", Font.PLAIN, 18));
        noBodyPanel.add(label, gbc);
        gbc.gridy = 1;
        noBodyPanel.add(label1, gbc);
        bodyTabs.addTab("None",new JScrollPane(noBodyPanel));
        JPanel formDataPanel = new JPanel();
        keyValue(formDataPanel,"formData");
        bodyTabs.addTab("Form Data", new JScrollPane(formDataPanel));
        JPanel jsonPanel = new JPanel();
        keyValue(jsonPanel,"json");
        bodyTabs.addTab("JSON", new JScrollPane(jsonPanel));
        bodyTabs.setBackground(Color.LIGHT_GRAY);
    }

    /**
     * Key and value for header
     * @param panel chosen panel
     */
    private void keyValue(JPanel panel, String type) {
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        jPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
        panel.add(jPanel, BorderLayout.NORTH);
        create = new JButton("New");
        create.addActionListener(e -> {
            jPanel.add(addKeyValue(type));
            updateUI();
        });
        panel.add(create, BorderLayout.SOUTH);
    }

    /**
     * Add a new header by clicking create button
     * @return panel
     */
    private JPanel addKeyValue(String type) {
        JPanel jPanel = new JPanel(new BorderLayout());
        JPanel panel1 = new JPanel(new GridLayout(1, 2, 5, 5));
        JTextArea key = new JTextArea();
        key.setLineWrap(true);
        JScrollPane scrollPane1 = new JScrollPane(key);
        scrollPane1.setPreferredSize(new Dimension(key.getWidth() + 85, key.getHeight() + 25));
        scrollPane1.setVerticalScrollBarPolicy(20);
        JTextArea value = new JTextArea();
        value.setLineWrap(true);
        JScrollPane scrollPane2 = new JScrollPane(value);
        scrollPane2.setPreferredSize(new Dimension(key.getWidth() + 85, key.getHeight() + 25));
        scrollPane2.setVerticalScrollBarPolicy(20);
        panel1.add(scrollPane1);
        panel1.add(scrollPane2);
        if (type.equals("header"))
            headers.put(key,value);
        if (type.equals("query"))
            query.put(key, value);
        if (type.equals("formData"))
            formData.put(key, value);
        if (type.equals("json"))
            json.put(key, value);
        JPanel panel2 = new JPanel(new GridLayout(1, 2, 0, 0));
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(true);
        checkBox.addChangeListener(e -> {
            if (checkBox.isSelected()) {
                if (type.equals("header"))
                    headers.put(key,value);
                if (type.equals("query"))
                    query.put(key, value);
                if (type.equals("formData"))
                    formData.put(key, value);
                if (type.equals("json"))
                    json.put(key, value);
            }
            else {
                if (type.equals("header"))
                    headers.remove(key,value);
                if (type.equals("query"))
                    query.remove(key, value);
                if (type.equals("formData"))
                    formData.remove(key, value);
                if (type.equals("json"))
                    json.remove(key, value);
            }
        });
        panel2.add(checkBox);
        JButton delete = new JButton("\ud83d\uddd1");
        delete.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 2));
        delete.addActionListener(e -> {
            if (type.equals("header"))
                headers.remove(key, value);
            if (type.equals("query"))
                query.remove(key, value);
            if (type.equals("formData"))
                formData.remove(key, value);
            if (type.equals("json"))
                json.remove(key, value);
            jPanel.getParent().remove(jPanel);
            updateUI();
        });
        panel2.add(delete);
        jPanel.add(panel1, BorderLayout.CENTER);
        jPanel.add(panel2, BorderLayout.EAST);
        jPanel.setBorder(new EmptyBorder(3,3,3,3));
        return jPanel;
    }

    /**
     * Convert query hashMap into string
     * @return string of query
     */
    public String querySting() {
        StringBuilder data = new StringBuilder();
        int count = query.size();
        for (JTextArea t : query.keySet()) {
            data.append(t.getText());
            if (!query.get(t).getText().equals(""))
                data.append("=");
            data.append(query.get(t).getText());
            if (count > 1)
                data.append("&");
            count--;
        }
        return  data.toString();
    }
    /**
     * Convert data hashMap into string
     * @return string of data
     */
    public String formDataSting() {
        StringBuilder data = new StringBuilder();
        int count = formData.size();
        for (JTextArea t : formData.keySet()) {
            data.append(t.getText()).append("=").append(formData.get(t).getText());
            if (count > 1)
                data.append("&");
            count--;
        }
        return  data.toString();
    }
    /**
     * Convert json hashMap into string
     * @return string of json
     */
    public String jsonSting() {
        StringBuilder data = new StringBuilder();
        int count = json.size();
        if (count > 0) {
            data.append("{");
            for (JTextArea t : json.keySet()) {
                data.append("\"").append(t.getText()).append("\"").append(":");
                data.append("\"").append(json.get(t).getText()).append("\"");
                if (count > 1)
                    data.append(",");
                count--;
            }
            data.append("}");
        }

        return  data.toString();
    }

    public void openExistingRequest(Request request) {
        methods.setSelectedItem(request.getMethod());
        addressBar.setText(request.getUrl());
        headerMap(request.getHeaders());
        queryMap(request.getUrl());
        String data = request.getData();
        if (!data.equals("")) {
            if (data.startsWith("{") && data.endsWith("}")) {
                jsonMap(data);
                bodyTabs.remove(1);
                JPanel formDataPanel = new JPanel();
                formData.clear();
                keyValue(formDataPanel,"formData");
                bodyTabs.add(formDataPanel,1);
                bodyTabs.setTitleAt(1,"Form Data");
            }
            else {
                formDataMap(data);
                bodyTabs.remove(2);
                JPanel jsonPanel = new JPanel();
                json.clear();
                keyValue(jsonPanel,"json");
                bodyTabs.add(jsonPanel,2);
                bodyTabs.setTitleAt(2,"JSON");
            }
        }
        else {
            bodyTabs.remove(1);
            JPanel formDataPanel = new JPanel();
            formData.clear();
            keyValue(formDataPanel,"formData");
            bodyTabs.add(formDataPanel,1);
            bodyTabs.setTitleAt(1,"Form Data");
            bodyTabs.remove(2);
            JPanel jsonPanel = new JPanel();
            json.clear();
            keyValue(jsonPanel,"json");
            bodyTabs.add(jsonPanel,2);
            bodyTabs.setTitleAt(2,"JSON");
        }
        updateUI();
    }
    public void headerMap(HashMap<String,String> requestHeaders) {
        tabbedPane.remove(1);
        JPanel headerPanel = new JPanel();
        headers.clear();
        keyValue(headerPanel, "header");
        tabbedPane.add(headerPanel, 1);
        tabbedPane.setTitleAt(1, "Header");
        if (requestHeaders.size() > 0) {
            for (int i = 0; i < requestHeaders.size(); i++)
                create.doClick();
            Iterator<Map.Entry<JTextArea, JTextArea>> it1 = headers.entrySet().iterator();
            Iterator<Map.Entry<String, String>> it2 = requestHeaders.entrySet().iterator();
            while (it1.hasNext() && it2.hasNext()) {
                Map.Entry<JTextArea, JTextArea> e1 = it1.next();
                Map.Entry<String, String> e2 = it2.next();
                e1.getKey().setText(e2.getKey());
                e1.getValue().setText(e2.getValue());
            }
        }
    }
    public void formDataMap(String data) {
        HashMap<String,String> map = new HashMap<>();
        String[] strings = data.split("&");
        for (String s : strings) {
            String[] keyVal = s.split("=");
            map.put(keyVal[0],keyVal[1]);
        }
        bodyTabs.remove(1);
        JPanel formDataPanel = new JPanel();
        formData.clear();
        keyValue(formDataPanel,"formData");
        for (int i = 0; i < map.size(); i++)
            create.doClick();
        bodyTabs.add(formDataPanel,1);
        bodyTabs.setTitleAt(1,"Form Data");
        Iterator<Map.Entry<JTextArea,JTextArea>> it1 = formData.entrySet().iterator();
        Iterator<Map.Entry<String,String>> it2 = map.entrySet().iterator();
        while(it1.hasNext() && it2.hasNext()) {
            Map.Entry<JTextArea, JTextArea> e1 = it1.next();
            Map.Entry<String, String> e2 = it2.next();
            e1.getKey().setText(e2.getKey());
            e1.getValue().setText(e2.getValue());
        }
        bodyTabs.setSelectedIndex(1);
    }
    public void jsonMap(String data) {
        HashMap<String,String> map = new HashMap<>();
        data = data.replace("{","");
        data = data.replace("}","");
        String[] strings = data.split(",");
        for (String s : strings) {
            String[] keyVal = s.split(":");
            keyVal[0] = keyVal[0].replace("\"","");
            keyVal[1] = keyVal[1].replace("\"","");
            map.put(keyVal[0],keyVal[1]);
        }
        bodyTabs.remove(2);
        JPanel jsonPanel = new JPanel();
        json.clear();
        keyValue(jsonPanel,"json");
        for (int i = 0; i < map.size(); i++)
            create.doClick();
        bodyTabs.add(jsonPanel,2);
        bodyTabs.setTitleAt(2,"JSON");
        Iterator<Map.Entry<JTextArea,JTextArea>> it1 = json.entrySet().iterator();
        Iterator<Map.Entry<String,String>> it2 = map.entrySet().iterator();
        while(it1.hasNext() && it2.hasNext()) {
            Map.Entry<JTextArea, JTextArea> e1 = it1.next();
            Map.Entry<String, String> e2 = it2.next();
            e1.getKey().setText(e2.getKey());
            e1.getValue().setText(e2.getValue());
        }
        bodyTabs.setSelectedIndex(2);
    }
    public void queryMap(String url) {
        if (url.contains("?")) {
            String q = url.substring(url.indexOf("?"));
            HashMap<String,String> map = new HashMap<>();
            String[] strings = q.split("&");
            for (String s : strings) {
                String[] keyVal = s.split("=");
                if (keyVal.length > 1)
                    map.put(keyVal[0],keyVal[1]);
                else
                    map.put("", keyVal[0]);
            }
            tabbedPane.remove(2);
            JPanel queryPanel = new JPanel();
            formData.clear();
            keyValue(queryPanel,"query");
            for (int i = 0; i < map.size(); i++)
                create.doClick();
            tabbedPane.add(queryPanel,2);
            tabbedPane.setTitleAt(2,"Query");
            Iterator<Map.Entry<JTextArea,JTextArea>> it1 = query.entrySet().iterator();
            Iterator<Map.Entry<String,String>> it2 = map.entrySet().iterator();
            while(it1.hasNext() && it2.hasNext()) {
                Map.Entry<JTextArea, JTextArea> e1 = it1.next();
                Map.Entry<String, String> e2 = it2.next();
                e1.getKey().setText(e2.getKey());
                e1.getValue().setText(e2.getValue());
            }
        }
    }
    public Request createRequest() {
        String query = querySting();
//        if (!query.equals("")) {
//        addressBar.append("?");
//        addressBar.append(query);
//         }
        String url = addressBar.getText();
        url = url.concat("?").concat(query);
        String method = methods.getItemAt(methods.getSelectedIndex());
        String data;
        if (bodyTabs.getSelectedIndex() == 0)
            data = "";
        else if (bodyTabs.getSelectedIndex() == 1)
             data = formDataSting();
        else
            data = jsonSting();
         HashMap<String, String> header = new HashMap<>();
        for (JTextArea t : headers.keySet()) {
        header.put(t.getText(),headers.get(t).getText());
         }
        return new Request(url,method,data,header,MainFrame.followRedirect());
    }
    private class MyHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Request request;
            if (e.getSource().equals(send)) {
                request = createRequest();
                ResponsePanel.sendRequest(request);
            }
            if (e.getSource().equals(save)) {
//                if (request == null)
                request = createRequest();
                Date date =  new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                String name = JOptionPane.showInputDialog(null,"Request name:",formatter.format(date));
                MainFrame.save(request, name);
            }
        }
    }
}
