package bishe.networkmonitor.pojo;

public class RegexMsg {
    public String type;
    public int level = 1;
    public int from = 0;
    public int to = 0;
    public String tarStr;

    public RegexMsg() {
    }

    public RegexMsg(String type, int level, int from, int to,String tarStr) {
        this.type = type;
        this.level = level;
        this.from = from;
        this.to = to;
        this.tarStr = tarStr;
    }

    public static RegexMsg getDefault() {
        return new RegexMsg("<null>", 1, 0, 0,"<null>");
    }
}
