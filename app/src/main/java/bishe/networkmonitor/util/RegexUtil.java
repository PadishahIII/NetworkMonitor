package bishe.networkmonitor.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.callback.Callback;

import bishe.networkmonitor.pojo.Packet;
import bishe.networkmonitor.pojo.RegexMsg;
import bishe.networkmonitor.pojo.RegexPattern;

public class RegexUtil {
    private final List<RegexPattern> patterns;
    private static volatile RegexUtil INSTANCE;
    private final int NUMBER_OF_THREADS = 4;
    private ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    CompletionService<RegexMsg> completionService = new ExecutorCompletionService<>(executor);

    public static RegexUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RegexUtil();
        }
        return INSTANCE;

    }

    private RegexUtil() {
        patterns = new ArrayList<>();

        /*telephone number*/
        patterns.add(new RegexPattern(Pattern.compile("\\b1(3\\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\\d|9[0-35-9])\\d{8}\\b"), "telephone number", 2));

        /*account number*/
        patterns.add(new RegexPattern(Pattern.compile("(([1-6][1-9]|50)\\d{4}(18|19|20)\\d{2}((0[1-9])|10|11|12)(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx])|(([1-6][1-9]|50)\\d{4}\\d{2}((0[1-9])|10|11|12)(([0-2][1-9])|10|20|30|31)\\d{3})\\b"), "bank account number", 2));

        /*id card number*/
        patterns.add(new RegexPattern(Pattern.compile("\\b[1-9]\\d{5}(19|20)\\d{2}(0[1-9]|1[012])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]\\b"), "id card", 1));

        /*email*/
        patterns.add(new RegexPattern(Pattern.compile("\\b[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]{2,4}\\b"), "email", 2));

        /*car number*/
        patterns.add(new RegexPattern(Pattern.compile("[\\u4e00-\\u9fa5]{1}[A-Za-z]{1}[A-HJ-NP-Za-hj-np-z]?[A-HJ-NP-Za-hj-np-z0-9]{4,5}\\b"),"car number",2));

        /*bank card number*/
        patterns.add(new RegexPattern(Pattern.compile("\\b([1-9]{1})(\\d{15}|\\d{18})\\b"),"bank card number",2));

        /*medicare number*/
        patterns.add(new RegexPattern(Pattern.compile("\\b[1-9]\\d{5}(19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]\\b"),"medicare number",2));

        /*GPS*/
        patterns.add(new RegexPattern(Pattern.compile("-?\\d+(.\\d+)?,\\s*-?\\d+(.\\d+)?"),"GPS",1));

        /*passport number*/
        patterns.add(new RegexPattern(Pattern.compile("1[45][0-9]{7}\\b"),"passport number",2));
    }

    public RegexMsg findSingle(String data, RegexPattern pattern) {
        Matcher matcher = pattern.pattern.matcher(data);
        if (matcher.find()) {
            RegexMsg regexMsg = RegexMsg.getDefault();
            regexMsg.tarStr = matcher.group();
            regexMsg.from = matcher.start();
            regexMsg.to = matcher.end();
            regexMsg.level = pattern.level;
            regexMsg.type = pattern.type;
            return regexMsg;
        } else {
            return null;
        }
    }

    public List<RegexMsg> find(String data) {
        List<Callable<RegexMsg>> taskList = new ArrayList<>();
        List<RegexMsg> resList = new ArrayList<>();
        for (RegexPattern p : patterns
        ) {
            taskList.add(new Callable<RegexMsg>() {
                @Override
                public RegexMsg call() throws Exception {
                    return findSingle(data, p);
                }
            });
        }
        for (Callable<RegexMsg> callable : taskList) {
            completionService.submit(callable);
        }
        for (int i = 0; i < taskList.size(); i++) {
            try {
                RegexMsg regexMsg = completionService.take().get();
                if (regexMsg != null)
                    resList.add(regexMsg);
            } catch (ExecutionException e) {
                Log.d("regexUtil", e.toString());
            } catch (InterruptedException e) {

            }
        }
        return resList;
    }

}

