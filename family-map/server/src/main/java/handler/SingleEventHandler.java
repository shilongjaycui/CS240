package handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import service.SingleEventService;
import request.SingleEventRequest;
import response.SingleEventResponse;
import response.SinglePersonResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

public class SingleEventHandler extends GETHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().toUpperCase().equals("GET")) {
                Headers headers = exchange.getRequestHeaders();
                if (headers.containsKey("Authorization")) {
                    URI uri = exchange.getRequestURI();
                    String path = uri.getPath();
                    String[] parts = path.split("/");
                    String eventID = parts[2];

                    String authToken = headers.getFirst("Authorization");

                    SingleEventRequest request = new SingleEventRequest(eventID, authToken);
                    SingleEventResponse response = SingleEventService.getEvent(request);

                    if (response.isSuccess()) {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    }
                    else {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                    }
                    sendResponse(response, exchange.getResponseBody());
                }
                else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_UNAUTHORIZED, 0);
                    sendResponse(new SinglePersonResponse("Error: Unauthorized"), exchange.getResponseBody());
                }
            }
            else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                sendResponse(new SinglePersonResponse("Error: Http Bad Request"), exchange.getResponseBody());
            }
        }
        catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            e.printStackTrace();
        }
        finally {
            exchange.getResponseBody().close();
        }
    }
}
