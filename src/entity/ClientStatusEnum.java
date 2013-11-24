package entity;

public enum ClientStatusEnum {

    OFFLINE     (0),
    INVISIBLE   (1),
    ONLINE      (2);

    private int id;

    ClientStatusEnum(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String toString(){
        return name();

    }


}
