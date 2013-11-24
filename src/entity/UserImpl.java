package entity;

import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;

import java.util.Collection;
import java.util.Date;

public class UserImpl implements User {
    private String uid;
    private String name;
    private String nick;
    private String email;
    private Date registrationDate;
    private short gender;
    private Date birthday;

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public Collection<User> getContactList() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<Group> getGroup() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Boolean addInGroup(Group group, User user) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Boolean changeGroupForMan(Group group, User user) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Boolean removeManFromGroup(Group group, User user) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Boolean removeManFromAllGroup(Group group, User user) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
