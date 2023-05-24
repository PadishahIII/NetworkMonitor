package bishe.networkmonitor.pojo;

public class Packet {
    public String srcHost;
    public int srcPort;
    public String desHost;
    public int desPort;
    public String content;

    public Packet(String srcHost, int srcPort, String desHost, int desPort, String content) {
        this.srcHost = srcHost;
        this.srcPort = srcPort;
        this.desHost = desHost;
        this.desPort = desPort;
        this.content = content;
    }
}
