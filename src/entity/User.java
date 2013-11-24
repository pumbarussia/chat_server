package entity;

import java.util.Collection;

public interface User {
    String getUid();
    String getName();
    Collection<User> getContactList();
    Collection<Group> getGroup();
    Boolean addInGroup(Group group, User user );
    Boolean changeGroupForMan(Group group, User user );
    Boolean removeManFromGroup(Group group, User user );
    Boolean removeManFromAllGroup(Group group, User user );
}
