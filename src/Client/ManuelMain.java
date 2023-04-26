package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ManuelMain {

    private static PrintWriter out;
    private static BufferedReader in;


    public static void main(String[] args) throws IOException, InterruptedException {

        Scanner scanner = new Scanner(System.in);

        Socket s = new Socket("192.168.1.102",6666);

        System.err.println("Wating on server");
        out = new PrintWriter(s.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        String greeting;
        String respons = "NA";
        do {
            greeting = in.readLine();
            if (greeting != null)
                System.out.println("in: " + greeting);
            if ("Got it".equals(greeting)) {
                Thread.sleep(1000);
                respons = scanner.nextLine();
                out.println(respons);
                System.err.println("sendt : \"" + respons + "\" end.");
            } else {
                out.println("unrecognised greeting");
            }
        } while (!respons.equals("exit"));
    }
}
