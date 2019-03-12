package hippo.com.powerts.module;

public class PowerPercent {
    public int _id;
    public String curtime;
    public String powerpct;
    public String addform;

    public PowerPercent(String curtime, String powerpct, String addform) {
        this.addform = addform;
        this.curtime = curtime;
        this.powerpct = powerpct;
    }

    public PowerPercent(){

    }
}
