package cn.alpha2j.bean;

/**
 * Created by cn.alpha2j on 2016/11/22.
 */
public class Customer {

    private String id;
    private String username;
    private String password;
    private boolean isManager;

    public Customer() {

    }

    //默认注册的都是非管理员
    public Customer(String id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        isManager = false;
    }

    public Customer(String id, String username, String password, boolean isManager) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isManager = isManager;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isManager() {
        return isManager;
    }

    public void setManager(boolean manager) {
        isManager = manager;
    }
}
