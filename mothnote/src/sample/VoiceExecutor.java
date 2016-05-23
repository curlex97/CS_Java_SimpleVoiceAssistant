package sample;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import jdk.internal.org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by arthur on 08.01.2016.
 */
public class VoiceExecutor {
    List<Category> categories;
    List<Interpreter> interpreters;
    Script currentScript = null;

    public VoiceExecutor() {

        categories = new ArrayList<Category>();
        interpreters = new ArrayList<Interpreter>();
        interpreters.add(new SpeakInterpreter());
        interpreters.add(new ProgramLauncherInterpreter());
        interpreters.add(new KeyboardInputInterpreter());
        Document doc = null;
        try {
            doc = createXMLDocument();
            createTreeDocument(doc);
            int t = 0;

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.xml.sax.SAXException e) {
            e.printStackTrace();
        }

    }


    protected Document createXMLDocument() throws ParserConfigurationException,
            SAXException, IOException, org.xml.sax.SAXException {
        // фабрика для парсера DOM
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // Создание парсера DocumentBuilder
        DocumentBuilder parser = factory.newDocumentBuilder();
        // парсинг XML-документа и его представление в виде DOM
        Document document = parser.parse(new File("default.xml"));
        return document;
    }

    protected void createTreeDocument(Document xmlDocument) {
        // получим корневой элемент
        Element xmlRoot = xmlDocument.getDocumentElement();
        // создадим на основе корневого XML-элемента корневой узел дерева
        DefaultMutableTreeNode treeRoot = new
                DefaultMutableTreeNode(xmlRoot.getTagName());
        // формируем список дочерних элементов для корневого узла
        NodeList cdList = xmlRoot.getChildNodes();
        for (int i = 0; i < cdList.getLength(); ++i) {
            Node cdXMLNode = cdList.item(i);
            if (cdXMLNode.getNodeType() == Node.ELEMENT_NODE) {
                Category category = new Category();
                category.Id = cdXMLNode.getAttributes().getNamedItem("id").getNodeValue();
                category.Name = cdXMLNode.getAttributes().getNamedItem("name").getNodeValue();
                category.Description = cdXMLNode.getAttributes().getNamedItem("description").getNodeValue();

                NodeList xcdList = cdXMLNode.getChildNodes();
                for (int j = 0; j < xcdList.getLength(); ++j) {
                    Node xcdXMLNode = xcdList.item(j);
                    if (xcdXMLNode.getNodeName() == "call")
                        category.Calls.add(xcdXMLNode.getAttributes().getNamedItem("name").getNodeValue());
                    else if (xcdXMLNode.getNodeName() == "line") {
                        Line line = new Line();
                        line.Name = xcdXMLNode.getAttributes().getNamedItem("name").getNodeValue();

                        NodeList xxcdList = xcdXMLNode.getChildNodes();
                        for (int k = 0; k < xxcdList.getLength(); ++k) {
                            Node xxcdXMLNode = xxcdList.item(k);
                            String bb = xxcdXMLNode.getNodeName();
                            if (xxcdXMLNode.getNodeName() == "linecall")
                                line.Calls.add(xxcdXMLNode.getAttributes().getNamedItem("name").getNodeValue());
                            else if (xxcdXMLNode.getNodeName() == "exec") {
                                Executor exec = new Executor();
                                exec.Interpreter = xxcdXMLNode.getAttributes().getNamedItem("interpreter").getNodeValue();
                                exec.Script = new Script(xxcdXMLNode.getAttributes().getNamedItem("script").getNodeValue());
                                line.Executors.add(exec);
                            }

                        }

                        category.Lines.add(line);
                    }
                }

                categories.add(category);
            }
        }

    }


    public void Execute(String speech) {

        if (currentScript == null) {
            Line line = null;
            for (int i = 0; i < categories.size(); i++) {
                line = categories.get(i).getLine(speech);
                if (line != null) break;
            }
            if (line != null) {
                for (int i = 0; i < line.Executors.size(); i++) {
                    currentScript = line.Executors.get(i).Script;

                    for (int j = 0; j < currentScript.Commands.size(); j++) {
                        currentScript.CurrentLine = j;
                        for (int k = 0; k < interpreters.size(); k++) {
                            if (interpreters.get(k).interpret(currentScript.Commands.get(currentScript.CurrentLine)) == 0)
                                break;
                        }
                    }
                    currentScript = null;
                }
            }
        }

    }

}

interface Interpreter {
    int interpret(String cmd);
}


class SpeakInterpreter implements Interpreter {
    public int interpret(String cmd) {
        if(cmd.length() < 5) return -1;
        String str = cmd.substring(0, 4);
        if (str.equals("tell")) {
            Voice voice;
            VoiceManager vm = VoiceManager.getInstance();
            voice = vm.getVoice("kevin16");
            voice.allocate();
            String time = new java.util.Date().getHours() + " " + new java.util.Date().getMinutes();
            if (cmd.contains("$time"))
                voice.speak(time);

            else if (cmd.contains("$weather")) {
                String page = getInternetPage("https://sinoptik.ua/%D0%BF%D0%BE%D0%B3%D0%BE%D0%B4%D0%B0-%D0%BE%D0%B4%D0%B5%D1%81%D1%81%D0%B0");
                String cur = page.split("today-temp")[1].substring(2);
                cur = cur.substring(0, cur.indexOf('&'));
                voice.speak(cur);
            } else voice.speak(cmd.substring(4));


            return 0;
        }
        return -1;
    }

    public String getInternetPage(String url) {
        String content = null;
        URLConnection connection = null;
        try {
            connection = new URL(url).openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            content = scanner.next();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return content;
    }
}


class ProgramLauncherInterpreter implements Interpreter {
    public int interpret(String cmd) {
        if(cmd.length() < 10) return -1;
        String str1 = cmd.substring(0, 9);
        String str2 = cmd.substring(0, 7);
        if (str1.equals("taskstart")) {
            try {
                Runtime.getRuntime().exec(cmd.substring(10));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        } else if (str2.equals("taskend")) {
            try {
                Runtime.getRuntime().exec("taskkill /F /IM " + cmd.substring(7));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }
        return -1;
    }
}


class KeyboardInputInterpreter implements Interpreter {
    public int interpret(String cmd) {
        if(cmd.length() < 7) return -1;
        String str1 = cmd.substring(0, 6);
        if (str1.equals("kpress")) {


                GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
                GraphicsDevice screen=env.getDefaultScreenDevice();
                Robot robot = null;
                try {
                    robot = new Robot(screen);
                } catch (AWTException e) {
                    return -1;
                }
                char sym = cmd.substring(6).trim().charAt(0);
                getButton(robot, sym);
                return 0;
        }

        return -1;
    }


    public void getButton(Robot robot, char c){

        if(c == '^'){ robot.keyPress(KeyEvent.VK_BACK_SPACE); robot.delay(100); robot.keyRelease(KeyEvent.VK_BACK_SPACE);}
        if(c == '='){ robot.keyPress(KeyEvent.VK_EQUALS); robot.delay(100); robot.keyRelease(KeyEvent.VK_EQUALS);}
        if(c == '1'){ robot.keyPress(KeyEvent.VK_1); robot.delay(100); robot.keyRelease(KeyEvent.VK_1);}
        if(c == '2'){ robot.keyPress(KeyEvent.VK_2); robot.delay(100); robot.keyRelease(KeyEvent.VK_2);}
        if(c == '3'){ robot.keyPress(KeyEvent.VK_3); robot.delay(100); robot.keyRelease(KeyEvent.VK_3);}
        if(c == '4'){ robot.keyPress(KeyEvent.VK_4); robot.delay(100); robot.keyRelease(KeyEvent.VK_4);}
        if(c == '5'){ robot.keyPress(KeyEvent.VK_5); robot.delay(100); robot.keyRelease(KeyEvent.VK_5);}
        if(c == '6'){ robot.keyPress(KeyEvent.VK_6); robot.delay(100); robot.keyRelease(KeyEvent.VK_6);}
        if(c == '7'){ robot.keyPress(KeyEvent.VK_7); robot.delay(100); robot.keyRelease(KeyEvent.VK_7);}
        if(c == '8'){ robot.keyPress(KeyEvent.VK_8); robot.delay(100); robot.keyRelease(KeyEvent.VK_8);}
        if(c == '9'){ robot.keyPress(KeyEvent.VK_9); robot.delay(100); robot.keyRelease(KeyEvent.VK_9);}
        if(c == '0'){ robot.keyPress(KeyEvent.VK_0); robot.delay(100); robot.keyRelease(KeyEvent.VK_0);}
        if(c == '/'){ robot.keyPress(KeyEvent.VK_DIVIDE);robot.delay(100);robot.keyRelease(KeyEvent.VK_DIVIDE);}
        if(c == '-'){ robot.keyPress(KeyEvent.VK_MINUS);robot.delay(100);robot.keyRelease(KeyEvent.VK_MINUS);}
        if(c == '+')
        {

            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.delay(100);
            robot.keyPress(KeyEvent.VK_EQUALS);
            robot.delay(100);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.delay(100);
            robot.keyRelease(KeyEvent.VK_EQUALS);
            robot.delay(100);
        }

        if(c == '*')
        {
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.delay(100);
            robot.keyPress(KeyEvent.VK_8);
            robot.delay(100);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.delay(100);
            robot.keyRelease(KeyEvent.VK_8);
            robot.delay(100);
        }

    }

}

