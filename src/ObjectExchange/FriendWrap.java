package ObjectExchange;

/**
 * Created with IntelliJ IDEA.
 * User: shmelev
 * Date: 08.01.13
 * Time: 14:31
 * To change this template use File | Settings | File Templates.
 */
public class FriendWrap {


    public static Friend getInstance(int id, String nick, byte status) {
        Friend friend = new Friend();
        friend.uid = id;
        friend.nickName = nick;
        friend.status = status;
        return friend;
    }
}
