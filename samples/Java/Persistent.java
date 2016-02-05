import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Persistent
{
    public static void main(String[] args)
    {
        int port = 12345;

        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getLoopbackAddress());
        } catch (IOException ex) {
            System.err.println("Could not listen on port 12345: " + ex.getMessage());
            System.exit(1); return;
        }

        System.out.println("Listening on port " + port + ".");

        JSONParser parser = new JSONParser();

        while (true) {
            Socket clientSocket;

            try {
                clientSocket = serverSocket.accept();
            }
            catch (IOException ex) {
                System.out.println("Error accepting connection: " + ex.getMessage());
                continue;
            }

            System.out.println("Got new connection.");

            try {
                handleRequest(parser, clientSocket.getInputStream(), clientSocket.getOutputStream());
                clientSocket.close();
                System.out.println("Done.");
            }
            catch (IOException ex) {
                System.out.println("- I/O error handling request: " + ex.getMessage());
            }
        }
    }

    public static void handleRequest(JSONParser parser, InputStream in, OutputStream out)
    {
        Object requestJsonValue;

        try {
            BufferedReader bin = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            parser.reset();
            try {
                requestJsonValue = parser.parse(bin);
            }
            catch (ParseException ex) {
                System.out.println("- Invalid JSON in request: " + ex);
                return;
            }

            int c = in.read();
            // Skip over trailing whitespace.
            while (c == ' ' || c == '\n' || c == '\t' || c == '\r') {
                c = in.read();
            }

            // Make sure there's no garbage at the end.
            if (c != -1) {
                System.out.println("- Got junk after JSON.");
                return;
            }
        }
        catch (IOException ex) {
            System.err.println("- I/O error reading request: " + ex.getMessage());
            return;
        }

        if (!(requestJsonValue instanceof JSONObject)) {
            System.out.println("- Expecting JSON object, but got some other kind of JSON value.");
            return;
        }
        JSONObject requestJsonObj = (JSONObject) requestJsonValue;

        PrintStream pout = new PrintStream(out);
        AIClient.run(requestJsonObj, pout);
        if (pout.checkError()) {
            System.out.println("- I/O error sending response");
        }
    }
}
