import java.io.*;

public class FileUtils {
    public static File[] getFilesInDirectory(String path) {
        return new File(path).listFiles();
    }
    public static String fileReader(File file) {
        StringBuilder str = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new FileReader(file))){
            char[] buffer = new char[2048];
            int count;
            while ((count = in.read(buffer)) != -1) {
                str.append(buffer, 0, count);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }
    public static void fileWriter(String content, String fileName) {
        try (FileOutputStream out = new FileOutputStream(fileName)) {
            byte[] bytes = content.getBytes();
            out.write(bytes);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Request readFromFile(File file) {
        Request request = null;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))){
            request = (Request) in.readObject();
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        assert request != null;
        return request;
    }
    public static void writeToFile(Request request, String fileName) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(request);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
