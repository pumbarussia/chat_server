/**
 * Created with IntelliJ IDEA.
 * User: shmelev
 * Date: 05.10.12
 * Time: 13:46
 * To change this template use File | Settings | File Templates.
 */
import Transport.*;
public class ImServer
{
    public static void main(String[] args)
    {

        SocketTransport transport   =   new SocketTransport(4444);
        transport.waitConnection();
    }
}

