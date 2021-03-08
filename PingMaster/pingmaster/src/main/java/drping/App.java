package drping;

/**
 * Hello world!
 *
 */
public class App  {
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }

    public static boolean pingHost(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }
}
