import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;

public class Server {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(5687), 0);
        server.createContext("/ishouldhavelockedmycomputer", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {

        private String currentUser = System.getProperty("user.name");

        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "";
            int httpStatus = 200;
            if (!"POST".equals(t.getRequestMethod())) {
                response = "Use POST http method";
                httpStatus = 400;
            } else {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(t.getRequestBody()))) {
                    showMessage(reader);
                    response = "Message sent!";
                } catch (Exception e){
                    response = "Error during message processing. " + e.getClass().getSimpleName() + ": " + e.getMessage();
                    httpStatus = 500;
                }
            }


            t.sendResponseHeaders(httpStatus, response.length());

            try (OutputStream os = t.getResponseBody()){
                os.write(response.getBytes());
            }
        }

        private void showMessage(BufferedReader requestReader) throws IOException {
            Process process = new ProcessBuilder("C:\\Windows\\system32\\msg.exe", currentUser).start();

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                requestReader.lines().map(x -> x + "\n\r").forEach(x -> { try { writer.write(x); } catch (IOException e) { /*Ignore exception*/ } });
            } finally {
                process.getInputStream().close();
            }
        }
    }
}
