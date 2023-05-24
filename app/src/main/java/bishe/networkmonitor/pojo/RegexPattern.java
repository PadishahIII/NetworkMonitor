package bishe.networkmonitor.pojo;

import java.util.regex.Pattern;

public class RegexPattern {
    public Pattern pattern;
    public String type;
    public int level;

    public RegexPattern(Pattern pattern, String type, int level) {
        this.pattern = pattern;
        this.type = type;
        this.level = level;
    }
}
