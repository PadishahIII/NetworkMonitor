package bishe.networkmonitor.pojo;

/**
 * Created by Dell on 4/27/2023.
 */

public class NetworkConnection {
    public String localAddress;
    public int localPort;
    public String remoteAddress;
    public int remotePort;
    public int state;
    public String stateStr;
    public int uid;
    public String appName;

    public NetworkConnection(String localAddress, int localPort, String remoteAddress, int remotePort, int state, int uid, String appName) {
        this.localAddress = localAddress;
        this.localPort = localPort;
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
        this.state = state;
        this.stateStr = getState(state);
        this.uid = uid;
        this.appName = appName;
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("laddr=" + this.localAddress + ";lport=" + Integer.toString(this.localPort));
        stringBuffer.append(";raddr=" + this.remoteAddress + ";rport=" + Integer.toString(this.remotePort) + ";state="+NetworkConnection.getState(this.state)+";uid="+Integer.toString(this.uid));
        stringBuffer.append(";appName="+this.appName);
        return stringBuffer.toString();
    }

    public static String getState(int state) {
        String s;
        switch (state) {
            case 1:
                s = "TCP_ESTABLISHED";
                break;
            case 2:
                s = "TCP_SYN_SENT";
                break;
            case 3:
                s = "TCP_SYN_RECV";
                break;
            case 4:
                s = "TCP_FIN_WAIT1";
                break;
            case 5:
                s = "TCP_FIN_WAIT2";
                break;
            case 6:
                s = "TCP_TIME_WAIT";
                break;
            case 7:
                s = "TCP_CLOSE";
                break;
            case 8:
                s = "TCP_CLOSE_WAIT";
                break;
            case 9:
                s = "TCP_LAST_ACK";
                break;
            case 10:
                s = "TCP_LISTEN";
                break;
            case 11:
                s = "TCP_CLOSING";
                break;
            default:
                s = "UNKNOWN";
                break;

        }
        return s;
    }
}
