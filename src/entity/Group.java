package entity;

import java.util.Collection;
/**
 * Пользователи всегда помещаются в группу, если что, то это группа по умолчанию
 *
 * */
public interface Group {
    String getGuid();
    String getName();
    Collection<Group> getSubGroup();
}
