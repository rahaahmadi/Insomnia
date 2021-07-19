import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckInput {
    private String request;
    Scanner scanner = new Scanner(System.in);

    /**
     * Create a class to get request from console
     */
    public CheckInput() {

    }

    /**
     * This method calls for creating new request from console
     */
    public void start() {
        System.out.println("\033[1;36m" + "Welcome to jurl " +"\u001B[0m"+ "\uD83D\uDE0E" + "\n"+
                "\u001B[35m"+  "For get more information use help (--help)" + "\u001B[0m");
        while (true) {
            request = scanner.nextLine();
            if (request.equals("exit"))
                break;
            else {
                checkRequest();
            }
        }
    }

    /**
     * find url from input
     * @return string of url
     */
    public String detectUrl() {
        String url = "";
        if (request.contains("--url")) {
            try {
                String str = request.substring(request.indexOf("--url") + 6);
                str = str.trim();
                for (int i = 0; i < str.length(); i++) {
                    if (str.charAt(i) == ' ')
                        break;
                    else
                        url = url.concat("" + str.charAt(i));
                }
            }
            catch (StringIndexOutOfBoundsException e) {
                url = "";
            }
        }
        return url;
    }

    /**
     * find method form input
     * @return request method
     */
    public String detectMethod () {
        String method = "";
        if (request.contains("--method") || request.contains("-M")) {
            try {
                String str;
                if (request.contains("--method") && !request.contains("-M"))
                    str = request.substring(request.indexOf("--method") + 9);
                else
                    str = request.substring(request.indexOf("-M") + 3);
                str = str.trim();
                for (int i = 0; i < str.length(); i++) {
                    if (str.charAt(i) == ' ')
                        break;
                    else
                        method = method.concat("" + str.charAt(i));
                }
            }
            catch (StringIndexOutOfBoundsException e) {
                method = "";
            }
        }
        else
            return "GET";
        return method;
    }

    /**
     * detect request headers form input
     * @return HashMap of headers
     */
    public HashMap<String, String> detectHeaders () {
        HashMap<String, String> headersList = new HashMap<>();
        String headers = "";
        String[] headersArray;
        String str ;
        if (request.contains("--headers") || request.contains("-H")) {
            try {
                // check if request contains either --headers or -H
                if (request.contains("--headers"))
                    // find a string after --headers
                    str = request.substring(request.indexOf("--headers") + 10);
                else
                    // find a String after -H
                    str = request.substring(request.indexOf("-H") + 3);
                str = str.trim();
                // find a string in quotations
                if (str.charAt(0) == '\"') {
                    if (str.indexOf('\"',1) > 0) {
                        for (int i = 1; i < str.length() && str.charAt(i) != '\"'; i++)
                            headers = headers.concat("" + str.charAt(i));
                    }
                }
                headersArray = headers.split(";");
                for (String header : headersArray) {
                    Pattern pattern = Pattern.compile(".*" + ":" + ".*");
                    Matcher matcher = pattern.matcher(header);
                    if (matcher.matches()) {
                        String[] keyValue = header.split(":");
                        headersList.put(keyValue[0],keyValue[1]);
                    }
                }
            }
            catch (StringIndexOutOfBoundsException ignored) {
            }
        }
        return headersList;
    }

    /**
     * Detect data from input
     * @return string of data
     */
    public String detectData() {
        String str ;
        String data = "";
        if (request.contains("--data") || request.contains("-d")) {
            try {
                if (request.contains("--data"))
                    str = request.substring(request.indexOf("--data") + 7);
                else
                    str = request.substring(request.indexOf("-d") + 3);
                str = str.trim();
                // find a string in quotations
                if (str.charAt(0) == '\"') {
                    if (str.indexOf('\"',1) > 0) {
                        for (int i = 1; i < str.length(); i++) {
                            if (str.charAt(i) == '\"')
                                break;
                            else
                                data = data.concat("" + str.charAt(i));
                        }
                    }
                }
                // check if input is matches to appropriate format
                String[] dataArray = data.split("&");
                for (String s : dataArray) {
                    Pattern pattern = Pattern.compile(".*" + "=" + ".*");
                    Matcher matcher = pattern.matcher(s);
                    if (matcher.matches()) {
                        return data;
                    }
                }
            }
            catch (StringIndexOutOfBoundsException e) {
                return  "";
            }
        }
        return "";
    }

    /**
     * Detect json data
     * @return string of json
     */
    public String detectJson() {
        if (request.contains("--json") || request.contains("-j")) {
            try {
                String str ;
                String data = "";
                if (request.contains("--json"))
                    str = request.substring(request.indexOf("--json") + 7);
                else
                    str = request.substring(request.indexOf("-j") + 3);
                str = str.trim();
                // find a string in quotations
                if (str.charAt(0) == '\"' && str.charAt(1) == '{') {
                    if (str.contains("}")) {
                        for (int i = 2; i < str.length(); i++) {
                            if (str.charAt(i) == '}' && str.charAt(i+1) == '\"')
                                break;
                            else
                                data = data.concat("" + str.charAt(i));
                        }
                    }
                }
                // check if input is matches to appropriate format
                String[] dataArray = data.split(",");
                for (String s : dataArray) {
                    Pattern pattern = Pattern.compile("\"" + ".*" + "\"" + ":" + "\"" + ".*" + "\"");
                    Matcher matcher = pattern.matcher(s);
                    if (matcher.matches()) {
                        data = "{" + data + "}";
                        return data;
                    }
                }
            }
            catch (StringIndexOutOfBoundsException e) {
                return  "";
            }
        }
        return "";
    }

    /**
     * check if input has url
     * @return true if input has url
     */
    private boolean hasValidUrl () {
        return !detectUrl().equals("");
    }

    /**
     * check the validity of headers
     * @return boolean
     */
    private boolean hasValidHeaders () {
        if (!(request.contains("--headers") || request.contains("-H")))
            return true;
        return detectHeaders().size() > 0;
    }
    /**
     * check the validity of method
     * @return boolean
     */
    private boolean hasValidMethod () {
        String method = detectMethod();
        return method.equals("GET") || method.equals("PUT") || method.equals("POST") || method.equals("DELETE");
    }
    /**
     * check the validity of form data
     * @return boolean
     */
    private boolean hasValidData() {
        if (!(request.contains("--data") || request.contains("-d")))
            return true;
        return !detectData().equals("");
    }
    /**
     * check the validity of json data
     * @return boolean
     */
    private boolean hasValidJson() {
        if (!(request.contains("--json") || request.contains("-j")))
            return true;
        return !detectJson().equals("");
    }

    /**
     * Check if we should show response headers
     * @return boolean
     */
    private boolean showHeaders() {
        return request.contains("-i");
    }

    /**
     * Check if request is follow redirect
     * @return boolean
     */
    private boolean followRedirect() {
        return request.contains("-f");
    }

    /**
     * Check if we should save response
     * @return boolean
     */
    private boolean saveResponse() {
        return request.contains("-O") || request.contains("--output");
    }
    /**
     * Check if we should save request
     * @return boolean
     */
    private boolean saveRequest() {
        return request.contains("-S") || request.contains("--save");
    }

    /**
     * Request should not have both form data and json
     * @return boolean
     */
    private boolean haveBothJsonAndFormData() {
        return (request.contains("--data") || request.contains("-d")) && (request.contains("--json") || request.contains("-j"));
    }

    /**
     * Create a request if the input is valid.
     */
    public void checkRequest() {
        String url, method , data;
        HashMap<String, String> headers;

        if (request.trim().equals("--help") || request.trim().equals("-h"))
            help();
        else if (request.trim().equals("list")) {
            File[] requestList = FileUtils.getFilesInDirectory("./Requests/");
            for (int i = 0; i < requestList.length; i++) {
                System.out.println(i+1 + " . " + FileUtils.readFromFile(requestList[i]).toString());
            }
        }
        else if (request.contains("fire")) {
            String str = request.substring(request.indexOf("fire")+4);
            str = str.trim();
            String[] requests = str.split(" ");
            Request request;
            for (String s : requests) {
                try {
                    int i = Integer.parseInt(s);
                    File[] temp = FileUtils.getFilesInDirectory("./Requests/");
                    System.out.println("Request " + i + ":") ;
                    if (i > 0 && i <= temp.length) {
                        request = FileUtils.readFromFile(temp[i-1]);
                        System.out.println(request.sendAndGetResponse());
                    }
                    else
                        System.out.println("Invalid request number!");
                } catch(NumberFormatException | NullPointerException e) {
                    System.out.println("Invalid request number!");
                }
            }
        }
        else {
            if (hasValidUrl()) {
                if (hasValidMethod()) {
                    if (hasValidHeaders()) {
                        if (hasValidData()) {
                            if (hasValidJson()) {
                                while (true) {
                                    if (haveBothJsonAndFormData())
                                        System.out.println("Request can't have both json and form data");
                                    else
                                        break;
                                }
                                url = detectUrl();
                                method = detectMethod();
                                if (request.contains("--data") || request.contains("-d"))
                                    data = detectData();
                                else if (request.contains("--json") || request.contains("-j"))
                                    data = detectJson();
                                else
                                    data = "";
                                headers = detectHeaders();
                                newRequest(url, method, data, headers);
                            }
                            else
                                System.out.println("Invalid Json data");
                        }
                        else
                            System.out.println("Invalid form data");
                    }
                    else
                        System.out.println("Invalid headers");
                }
                else
                    System.out.println("Wrong method!");
            }
            else
                System.out.println("Enter URL or use --help");
        }
    }

    /**
     * Create a new Request
     * @param url request url
     * @param method request method
     * @param data request data
     * @param headers request headers
     */
    public void newRequest(String url,String method, String data, HashMap<String,String> headers) {
        Request request = new Request(url, method, data, headers,followRedirect());
        String response = "";
        if (saveRequest()) {
            String fileName = "./Requests/" + "request"+ (FileUtils.getFilesInDirectory("./Requests/").length+1) + ".ser";
            FileUtils.writeToFile(request, fileName);
        }
        if (saveResponse()) {
            System.out.print("Write file name or enter  ");
            String name = scanner.nextLine();
            if (name.equals("")) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                Date date = new Date();
                name = "output_[" + formatter.format(date) + "].txt";
            }
            response = request.sendAndGetResponse();
            if (request.contentType().equals("image/png")) {
                if (name.contains("."))
                    name = name.substring(0,name.indexOf('.'));
                name = name.concat(".png");
                try {
                    BufferedImage image = ImageIO.read(new URL(request.getUrl()));
                    ImageIO.write(image,"png",new File("./Responses/" +name));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
                FileUtils.fileWriter(request.sendAndGetResponse(),"./Responses/" +name);
        }
        if (response.equals(""))
            response = request.sendAndGetResponse();
        if (showHeaders()) {
            Map<String, List<String>> headerFields = request.responseHeaders();
            for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
                System.out.println(entry.getKey() + "\t" + entry.getValue());
            }
        }
        System.out.println(response);
    }

    /**
     * help method
     */
    public void help() {
        String help = "--url  Request URL\n--method or -M  Request method\n" +
                "--headers or -H  Request headers with this format : \"key1:value1;key2=value2\"\n"
                + "--data or -d  Request form data with this format : \"key1=value1&key2=value2\"\n"
                + "--json or -j  Request json data with this format : {\"key1\":\"value1\",\"key2\":\"value2\""
                + "-i  Show response headers\n" + "--output or -O  Save response body in a file\n"
                + "--save or -S  Save a request in a file\n" + "list  Show list of requests\n"
                + "fire  choose a request from request list to send";
        System.out.println(help);
    }

}