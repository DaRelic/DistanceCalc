import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DistanceCalculatorServer {
    public static void main(String[] args) throws IOException {
        // Set up HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(3000), 0);

        server.createContext("/", new HomePageHandler());
        server.createContext("/calculateDistance", new CalculateDistanceHandler());
        server.start();

        System.out.println("Server started on port 3000");
    }

    // Handler for the home page
    static class HomePageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String filePath = "website elements/index.html";
            String response = readFile(filePath);

            sendResponse(exchange, response);
        }

         private String readFile(String filePath) throws IOException {
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            return new String(fileBytes, StandardCharsets.UTF_8);
        }
    }
    
    //Define handler for distance calculation/conversion request

    static class CalculateDistanceHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                //Reads the request body and extract form data
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                String[] formData = requestBody.split("&");
                
                //Extracts distance and unit values from form data
                double distance1 = Double.parseDouble(formData[0].split("=")[1]);
                String unit1 = formData[1].split("=")[1];
                double distance2 = Double.parseDouble(formData[2].split("=")[1]);
                String unit2 = formData[3].split("=")[1];
                String outputUnit = formData[4].split("=")[1];
                
               // Calculates the total distance based on input values
                double totalDistance = calculateTotalDistance(distance1, unit1, distance2, unit2, outputUnit);
                
                String response = "<html><body><h1>Total Distance: " + totalDistance + "</h1></body></html>";
                
                sendResponse(exchange, response);
            } else {
            
                exchange.sendResponseHeaders(405, -1);
            }
        }

      // Calculates the total distance based on input values
        private double calculateTotalDistance(double distance1, String unit1, double distance2, String unit2, String outputUnit) {
            //Converts input distances to meters
            double convertedDistance1 = convertToMeters(distance1, unit1);
            double convertedDistance2 = convertToMeters(distance2, unit2);
            
            //Calculates the total distance in meters
            double totalDistanceInMeters = convertedDistance1 + convertedDistance2;
            
            //Converts the total distance to the desired output unit
            return convertFromMeters(totalDistanceInMeters, outputUnit);
        }
        
        //Converts a distance to meters
        private double convertToMeters(double distance, String unit) {
            //Checks the input unit and perform the conversion
            if (unit.equalsIgnoreCase("yards")) {
                return distance * 0.9144; // 1 yard = 0.9144 meters
            }
            return distance; // Assume meters if no conversion needed
        }
        
        //Converts a distance from meters to the desired output unit
        private double convertFromMeters(double distance, String unit) {
            //Checks the output unit and perform the conversion
            if (unit.equalsIgnoreCase("yards")) {
                return distance / 0.9144; // 1 yard = 0.9144 meters
            }
            return distance; // Assume meters if no conversion needed
        }
    }
    
    //Sends the HTTP response to the client
    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, response.length());
        
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }
}
