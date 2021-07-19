import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class sent request and get response
 */
public class Network implements Serializable {
    private HttpURLConnection connection = null;
    private static final long serialVersionUID = 1L;

    /**
     * Sent GET request
     * @param urlToRead request url
     * @param headers request headers
     * @param followRedirect follow redirect boolean
     * @return string of response
     */
    public String httpResponseWithGET(String urlToRead, HashMap<String, String> headers, boolean followRedirect) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urlToRead);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (followRedirect) {
                connection.setInstanceFollowRedirects(false);
                int time = 0;
                while (time < 5) {
                    int status = connection.getResponseCode();
                    if (status == HttpURLConnection.HTTP_OK)
                        break;
                    else {
                        if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
                                || status == HttpURLConnection.HTTP_SEE_OTHER) {
                            String location = connection.getHeaderField("Location");
                            URL newUrl = new URL(location);
                            connection = (HttpURLConnection) newUrl.openConnection();
                        }
                    }
                    time++;
                }
            }
            for (String key : headers.keySet())
                connection.setRequestProperty(key, headers.get(key));
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
                result.append(System.lineSeparator());
            }
            reader.close();
        }
        catch (IOException e) {
            return e.toString();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return result.toString();
    }

    /**
     * Send request with POST, PUT and DELETE method
     * @param urlToRead request url
     * @param method request method
     * @param data request data
     * @param headers request headers
     * @param followRedirect follow redirect boolean
     * @return string of response
     */
    public String httpResponse(String urlToRead,String method,String data,HashMap<String,String> headers,boolean followRedirect) {
        StringBuilder content = new StringBuilder();
        connection = null;
        try {
            URL url = new URL(urlToRead);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            if (followRedirect) {
                connection.setInstanceFollowRedirects(false);
                int time = 0;
                while (time < 5) {
                    int status = connection.getResponseCode();
                    if (status == HttpURLConnection.HTTP_OK)
                        break;
                    else {
                        if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
                                || status == HttpURLConnection.HTTP_SEE_OTHER) {
                            String location = connection.getHeaderField("Location");
                            URL newUrl = new URL(location);
                            connection = (HttpURLConnection) newUrl.openConnection();
                        }
                    }
                    time++;
                }
            }
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(true);
            // if data has a json pattern set a header for it.
            if (data.contains("\""))
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept","application/json");
            for (String key : headers.keySet())
                connection.setRequestProperty(key, headers.get(key));
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
            writer.write(dataBytes);
            writer.flush();
            writer.close();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
                String line;
                while ((line = in.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }
        } catch (IOException ex) {
            return ex.toString();
        }
        finally {
            assert connection != null;
            connection.disconnect();
        }
        return content.toString();
    }

    /**
     * Get response headers
     * @return Map of headers
     */
    // This method should come after httpResponse or httpResponseWithGet methods.
    public Map<String, List<String>> getResponseHeaders () {
        return connection.getHeaderFields();
    }

    /**
     * Get response code
     * @return status code
     */
    public String status() {
        try {
            return connection.getResponseCode() + " " + connection.getResponseMessage();
        } catch (IOException e) {
            return "Error";
        }
    }

    /**
     * get connection content type
     * @return string of content type
     */
    public String getContentType() {
        String str = "";
        try {
            if (connection != null)
                str = connection.getContentType();
        }
        catch (Exception e) {
            str = "";
        }
        return Objects.requireNonNullElse(str, "");
    }
}
