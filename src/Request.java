import java.io.Serializable;
import java.util.*;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    private String url;
    private String method;
    private String data;
    private HashMap<String, String> headers;
    private Network network;
    private boolean followRedirect;
    private String stringOfResponse;
    private String status;

    /**
     * Create a new request
     * @param url url to connect
     * @param method request method
     * @param data request data
     * @param headers request headers
     * @param followRedirect follow redirect boolean
     */
    public Request (String url, String method, String data, HashMap<String, String> headers, boolean followRedirect) {
        this.url = url;
        this.method = method;
        this.data = data;
        this.headers = headers;
        network = new Network();
        this.followRedirect = followRedirect;
        stringOfResponse = "";
        status = "";
    }

    /**
     * get request url
     * @return string of url
     */
    public String getUrl() {
        return url;
    }

    /**
     * get request method
     * @return method
     */
    public String getMethod() {
        return method;
    }

    /**
     * get request data
     * @return data
     */
    public String getData() {
        return data;
    }

    /**
     * get request headers
     * @return headers
     */
    public HashMap<String, String> getHeaders() {
        return headers;
    }

    /**
     * get response status code
     * @return response code
     */
    public String getStatus() {
        return status;
    }

    /**
     * get connect content type
     * @return content type
     */
    public String contentType() {
        return network.getContentType();
    }

    /**
     * get request response
     * @return response
     */
    public String sendAndGetResponse() {
        if (!(url.startsWith("http://") || url.startsWith("https://")))
            url = "http://" + url;
        if (method.equals("GET"))
            stringOfResponse = network.httpResponseWithGET(url,headers,followRedirect);
        else
            stringOfResponse = network.httpResponse(url, method, data, headers, followRedirect);
        return stringOfResponse;

    }

    /**
     * get response headers
     * @return response headers
     */
    public Map<String, List<String>> responseHeaders() {
        return network.getResponseHeaders();
    }

    /**
     * detect response code
     * @return response code
     */
    public String detectStatus() {
        status = network.status();
        return status;
    }

    /**
     * convert HashMap of request headers to string
     * @return string of headers
     */
    public String stringOfHeaders() {
        StringBuilder res = new StringBuilder();
        int tmp = headers.size();
        if (tmp > 0) {
            for (String key : headers.keySet()) {
                res.append(key).append(": ").append(headers.get(key));
                tmp--;
                if (tmp > 0)
                    res.append(";");
            }
        }
        return res.toString();
    }

    /**
     * override toString method
     * @return string of request
     */
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("url: ").append(url).append("\t").append("method: ").append(method).append("\t");
        if (!stringOfHeaders().equals(""))
            res.append("Headers: ").append(stringOfHeaders()).append("\t");
        if (!data.equals(""))
            res.append("data: ").append(data).append("\t");
        return res.toString();
    }
}
