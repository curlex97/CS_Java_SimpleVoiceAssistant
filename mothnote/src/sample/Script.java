package sample;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arthur on 08.01.2016.
 */
public class Script {
    public List<String> Commands = new ArrayList<String>();
    public int CurrentLine;

    public Script(String script) {
        String[] strs = script.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').split(";");
        for (int i = 0; i < strs.length; i++) {
            Commands.add(strs[i]);
        }
        CurrentLine = 0;
    }

}
