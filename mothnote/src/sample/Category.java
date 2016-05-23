package sample;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arthur on 08.01.2016.
 */
public class Category {
    public String Id, Name, Description;
    public List<Line> Lines = new ArrayList<Line>();
    public List<String> Calls = new ArrayList<String>();

    public Line getLine(String command) {
        for (int i = 0; i < Lines.size(); i++) {
            Line curLine = Lines.get(i);

            for (int j = 0; j < curLine.Calls.size(); j++) {
                String lineCall = curLine.Calls.get(j);

                if (Calls.size() > 0) {
                    for (int k = 0; k < Calls.size(); k++) {
                        String catCall = Calls.get(k) + " " + lineCall;
                        if (catCall.equals(command))
                            return Lines.get(i);
                    }
                } else {
                    if (lineCall.equals(command))
                        return Lines.get(i);
                }

            }


        }
        return null;
    }

}

class Line {
    public String Name;
    public List<Executor> Executors = new ArrayList<Executor>();
    public List<String> Calls = new ArrayList<String>();

}

class Executor {
    public Script Script;
    public String Interpreter;
}