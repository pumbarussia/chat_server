package Threads;

import java.net.Socket;
import java.io.*;
import java.lang.*;
import java.util.*;
import com.google.gson.*;
import ObjectExchange.*;
import ObjectExchange.Friend;
/**
 * Created with IntelliJ IDEA.
 * User: shmelev
 * Date: 02.10.12
 * Time: 13:05
 * To change this template use File | Settings | File Templates.
 */
public class ClientThread extends Thread
{
    private static byte USER_CLIENT_STATUS_ONLINE =    0;
    private static byte USER_CLIENT_STATUS_INVISIBLE =    1;
    private static byte USER_CLIENT_STATUS_OFFLINE =    2;
    private static byte SERVER_ID_TREAD              =    -1;
    private Socket socket   =   null;
    protected final static int MESSAGE_RECEIVE                  =   1; //Ответ от клиента что до него дошло сообщение
    protected final static int MESSAGE_FOR_FRIEND_RECEIVE       =   2;
    protected final static int MESSAGE_FOR_ALL_FRIENDS_RECEIVE  =   3;
    protected final static int MESSAGE_FOR_FRIEND_FOR_SEND      =   4;
    protected final static int MESSAGE_FOR_ALL_FRIENDS_FOR_SEND =   5;
    protected final static int CLIENT_CONNECT                   =   11;
    protected final static int SEND_CLIENT_ID                   =   12;
    protected final static int GIVE_FRIENDS_LIST                =   14;
    protected final static int SEND_FRIENDS_LIST                =   15;
    protected final static int SEND_NEW_FRIEND_STATUS           =   16;
    protected final static int CLIENT_CHANGE_STATUS             =   17;
    protected final static int FRIEND_CHANGE_NICK               =   18;
    protected final static int SEND_FRIEND_CHANGE_NICK          =   18;
    protected final static int MESSAGE_CLOSE_SESSION            =   100;
    protected final static int CLIENT_SESSION_CLOSE             =   111;
    ClientThread[] arrayClientSocket   =   null;
    static ArrayList<Friend> chat_user_list = new ArrayList<Friend>();
    private int idThread;
    private Friend friend;
    ObjectInputStream reader;
    ObjectOutputStream writer;

    public ClientThread(Socket socket, int idThread, ClientThread[] arrayClientSocket)
    {
        this.socket     =   socket;
        this.idThread   =   idThread;
        this.friend =   FriendWrap.getInstance(this.idThread,"default name #"+idThread, USER_CLIENT_STATUS_ONLINE);
        chat_user_list.add(this.friend);
        this.arrayClientSocket  =  arrayClientSocket;

        System.out.println("ClientThread constructed success");
    }
    public void sendMessage(ObjectExchange messageObject)  throws IOException
    {
        writer.writeObject(messageObject);
        writer.flush();
    }
    public void run()
    {
        System.out.println("New connection"+socket.getRemoteSocketAddress());
        boolean end_session =   false;
        try
        {
            arrayClientSocket[idThread] =   this;
            writer = new ObjectOutputStream ((socket.getOutputStream()));
            writer.flush();
            reader = new ObjectInputStream (new BufferedInputStream(socket.getInputStream()));
            // Если не Нужный тип пришел, вылетит исключение
            ObjectExchange data;
            System.out.println(chat_user_list.toString());
            Gson gson   =   new Gson();
            String client;
            while (!end_session && (data = (ObjectExchange) reader.readObject())!= null){
                    switch (data.message_code)
                    {
                        case MESSAGE_RECEIVE:
                            break;

                        case CLIENT_CONNECT:

                            synchronized (this)
                            {
                                sendMessage(new ObjectExchangeWrap(SEND_CLIENT_ID, null, idThread).getObjectExchange());
                                friend.status   =  USER_CLIENT_STATUS_ONLINE;
                                client= gson.toJson(friend);
                                for(int i=0;i< arrayClientSocket.length; i++)
                                {
                                    if ((arrayClientSocket[i]!=null)&&(i!=idThread))
                                    {
                                        ClientThread  friendTread =   arrayClientSocket[i];
                                        friendTread.sendMessage(new ObjectExchangeWrap(SEND_NEW_FRIEND_STATUS, client, idThread).getObjectExchange());

                                    }
                                }
                                System.out.println("Client connected :"+idThread);
                            }
                        break;

                        case MESSAGE_FOR_FRIEND_RECEIVE:
                            synchronized (this)
                            {
                                if (arrayClientSocket[data.friend_id]!=null)
                                {
                                    ClientThread friendTread =   arrayClientSocket[data.friend_id];
                                    friendTread.sendMessage(new ObjectExchangeWrap(MESSAGE_FOR_FRIEND_FOR_SEND,  data.message, idThread).getObjectExchange());
                                    System.out.println("Private message"+data.message);
                                }
                                sendMessage(new ObjectExchangeWrap(MESSAGE_RECEIVE,  null, idThread).getObjectExchange());
                            }
                        break;

                        case MESSAGE_FOR_ALL_FRIENDS_RECEIVE:

                            synchronized (this)
                            {
                                for(int i=0;i< arrayClientSocket.length; i++)
                                {
                                    if ((arrayClientSocket[i]!=null)&&(i!=idThread))
                                    {
                                        ClientThread  friendTread =   arrayClientSocket[i];
                                        friendTread.sendMessage(new ObjectExchangeWrap(MESSAGE_FOR_ALL_FRIENDS_FOR_SEND,  data.message, idThread).getObjectExchange());
                                    }
                                }
                                sendMessage(new ObjectExchangeWrap(MESSAGE_RECEIVE,  null, idThread).getObjectExchange());
                                System.out.println("Multicast message"+data.message);
                            }
                            break;

                        case GIVE_FRIENDS_LIST:

                            String json= gson.toJson(chat_user_list);
                            sendMessage(new ObjectExchangeWrap(SEND_FRIENDS_LIST,  json, SERVER_ID_TREAD).getObjectExchange());
                            System.out.println("Multicast message"+data.message);
                            break;

                        case CLIENT_CHANGE_STATUS:
                            friend.status = gson.fromJson(data.message, byte.class);
                            client= gson.toJson(friend);
                            for(int i=0;i< arrayClientSocket.length; i++)
                            {
                                if ((arrayClientSocket[i]!=null)&&(i!=idThread))
                                {
                                    ClientThread  friendTread =   arrayClientSocket[i];
                                    friendTread.sendMessage(new ObjectExchangeWrap(SEND_NEW_FRIEND_STATUS,client, idThread).getObjectExchange());
                                }
                            }
                            break;
                        case FRIEND_CHANGE_NICK:
                            friend.nick_name = gson.fromJson(data.message, String.class);
                            client= gson.toJson(friend);
                            for(int i=0;i< arrayClientSocket.length; i++)
                            {
                                if ((arrayClientSocket[i]!=null)&&(i!=idThread))
                                {
                                    ClientThread  friendTread =   arrayClientSocket[i];
                                    friendTread.sendMessage(new ObjectExchangeWrap(SEND_FRIEND_CHANGE_NICK,client, idThread).getObjectExchange());
                                }
                            }
                            break;
                        case MESSAGE_CLOSE_SESSION:
                            end_session =   true;
                            break;
                        case CLIENT_SESSION_CLOSE:
                            friend.status   =  USER_CLIENT_STATUS_OFFLINE;
                            client = gson.toJson(friend);
                            for(int i=0;i< arrayClientSocket.length; i++)
                            {
                                if ((arrayClientSocket[i]!=null)&&(i!=idThread))
                                {
                                    ClientThread  friendTread =   arrayClientSocket[i];
                                    friendTread.sendMessage(new ObjectExchangeWrap(SEND_NEW_FRIEND_STATUS, client, idThread).getObjectExchange());
                                }
                            }
                            end_session =   true;
                            break;

                        default:
                            System.out.println("default Multicast message"+data.message);
                            System.out.println("default Multicast message"+data.message_code);
                    }

                System.out.println("Received message " +idThread + ":" + data.message);
                System.out.println("Received code " +idThread + ":" + data.message_code);

            }
            System.out.println("Client socket close success");
        }
        catch (Exception e)
        {
            System.out.println("Server error");
        }
        finally {
            try{
                synchronized (this)
                {
                    chat_user_list.remove(this.friend);
                    arrayClientSocket[idThread] =   null;
                    System.out.println("Disconnected " +idThread );
                }
                reader.close();
                writer.close();
                socket.close();

            }
            catch (IOException e)
            {
                System.out.println("finally error");
                e.printStackTrace();
            }
        }
    }
}
