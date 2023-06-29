import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class DistanceCalculatorServer {
    public static void main(String[] args) throws IOException {
        // Set up HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new HomePageHandler());
        server.start();
        System.out.println("Server started on port 8080");
    }

    // Handler for the home page
    static class HomePageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
        }
    }
}
