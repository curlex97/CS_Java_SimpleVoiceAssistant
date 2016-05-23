using System;
using System.Collections.Generic;
using System.Windows;
using System.Windows.Resources;
using System.Xml.Linq;

// ReSharper disable once CheckNamespace
namespace VitaVoice
{

    internal class Vita
    {
        public List<Category> Categories = new List<Category>();
        Dictionary<int, Line> _bufDictionary;

        public Dictionary<int, Line> BufDictionary
        {
            get { return _bufDictionary; }
        }

        public string[] GetAllCommands()
        {
            _bufDictionary = new Dictionary<int, Line>();
            List<string> ret = new List<string>();
            foreach (Category category in Categories)
            {
                foreach (Line line in category.Lines)
                {
                    _bufDictionary.Add(ret.Count, line);
                    foreach (string call in line.Calls)
                    {
                        ret.Add(call);
                    }

                }

            }
            return ret.ToArray();
        }

        public void Execute(string command)
        {
            foreach (Category category in Categories)
            {
                foreach (Line line in category.Lines)
                {
                    foreach (string call in line.Calls)
                    {
                        if (call == command) line.Execute();
                    }

                }

            }
        }

        public Vita()
        {
            ReadXml();
        }

        
        public void ReadXml()
        {
            try
            {
                StreamResourceInfo sri =
                    Application.GetResourceStream(new Uri("default.xml", UriKind.Relative));

                XDocument xdoc = XDocument.Load(sri.Stream);

                foreach (var xCategory in xdoc.Root.Nodes())
                {
                    string catName = "";
                    string catDescription = "";
                    string catId = "";
                    try
                    {
                        catName = (xCategory as XElement).Attribute("name").Value;
                        catDescription = (xCategory as XElement).Attribute("description").Value;
                        catId = (xCategory as XElement).Attribute("id").Value;
                    }
                    catch
                    {
                        /*Ignored*/
                    }

                    Category category = new Category(catName, catDescription, catId);
                    ReadLines(category, xCategory);
                    Categories.Add(category);
                }
            }
            catch { /*Ignored*/}
        }

        void ReadLines(Category category, XNode xCategory)
        {
            foreach (var xLine in (xCategory as XElement).Nodes())
            {

                if ((xLine as XElement).Name == "call")
                {
                    try
                    {
                        category.Calls.Add((xLine as XElement).Value);
                    }
                    catch
                    {
                        /*Ignored*/
                    }
                }
                else if ((xLine as XElement).Name == "line")
                {
                    string name = "";
                    try { name = (xLine as XElement).Attribute("name").Value; }
                    catch { /*Ignored*/ }
                    Line line = new Line(name);
                    ReadExeces(category, line, xLine);
                    category.Lines.Add(line);
                }



            }
        }

        // ReSharper disable once FunctionComplexityOverflow
        void ReadExeces(Category category, Line line, XNode xLine)
        {
            foreach (var xLineCall in (xLine as XElement).Nodes())
            {

                if ((xLineCall as XElement).Name == "linecall")
                {
                    try
                    {
                        if (category.Calls.Count > 0)
                        {
                            foreach (string catCall in category.Calls)
                            {
                                line.Calls.Add(catCall + " " + (xLineCall as XElement).Value);
                            }
                        }
                        else line.Calls.Add((xLineCall as XElement).Value);


                    }
                    catch
                    {
                        /*Ignored*/
                    }
                }
                else if ((xLineCall as XElement).Name == "exec")
                {
                    string interpreter = "";
                    string script = "";
                    try
                    {
                        script = (xLineCall as XElement).Value;
                        interpreter = (xLineCall as XElement).Attribute("interpreter").Value;
                    }
                    catch { /*Ignored*/}
                    Executor executor = new Executor(interpreter, script);
                    line.Executors.Add(executor);
                }
            }
        }

    }

    /// <summary>
    /// Исполнитель. Класс, 
    /// который превращает PS (PigaScript) в набор команд 
    /// и исполняет их
    /// </summary>
    internal class Executor
    {
        // это сам скрипт
        private string _script;
        // это название либы, которая будет этот скрипт компилить
        // не исключено, что разные либы будут использовать свои языки,
        // но это уже в будущем. Я думаю, что Пига будет единым.
        private string _interpreter;

        // Конструктор нехитрый ;)
        public Executor(string interpreter, string script)
        {
            _script = script;
            _interpreter = interpreter;
        }

        public string Interpreter
        {
            get { return _interpreter; }
        }

        public void Execute()
        {
            // тут подключаем либы и фигачим всё. 

            //var assembly = Assembly.Load(new AssemblyName("DefaultVitaExecutor.dll"));
            
            //    foreach (var type in assembly.GetTypes())
            //    {

            //        if (type != null)
            //        {
            //            MethodInfo methodInfo = type.GetMethod("Execute");
            //            if (methodInfo != null)
            //            {
            //                object result = null;
            //                ParameterInfo[] parameters = methodInfo.GetParameters();
            //                object classInstance = Activator.CreateInstance(type, null);
            //                if (parameters.Length == 0)
            //                {
            //                    //This works fine
            //                    result = methodInfo.Invoke(classInstance, null);
            //                }
            //                else
            //                {
            //                    object[] parametersArray =  { _script };            
            //                    methodInfo.Invoke(classInstance, parametersArray);
            //                }
            //            }
            //        }
            //    }
            

            PigaScriptExecutor pigaScriptExecutor = new PigaScriptExecutor();
            pigaScriptExecutor.Execute(_script);


        }
    }

    /// <summary>
    /// Реплика. Команда, которая выполняет действия по вызову
    /// </summary>
    internal class Line
    {
        private readonly string _name;
        // список слов, по которым эта реплика будет вызываться
        List<string> _calls = new List<string>();
        // исполнители команд
        List<Executor> _executors = new List<Executor>();

        public List<Executor> Executors
        {
            get { return _executors; }
        }

        public List<string> Calls
        {
            get { return _calls; }
        }

        public string Name
        {
            get { return _name; }
        }

        public Line(string name)
        {
            _name = name;
        }

        /// <summary>
        /// Выполнить все исполнители.
        /// </summary>
        public void Execute()
        {
            foreach (Executor t in _executors)
            {
                t.Execute();
            }
        }
    }

    /// <summary>
    /// Это категория вызовов команды. Тематическая
    /// </summary>
    internal class Category
    {
        private string _name, _description, _id;
        public List<Line> Lines = new List<Line>();
        public List<string> Calls = new List<string>();

        public string Name
        {
            get { return _name; }
        }

        public string Description
        {
            get { return _description; }
        }

        public string Id
        {
            get { return _id; }
        }

        public Category(string name, string description, string id)
        {
            _name = name;
            _description = description;
            _id = id;
        }


    }


}
