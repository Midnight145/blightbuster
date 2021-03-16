package Zeno410Utils;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Zeno410Logger
{
    private static FileHandler fileTxt;
    private static SimpleFormatter formatterTxt;
    public static final boolean suppress = true;
    private Logger logger;

    public static void crashIfRecording(RuntimeException toThrow)
    {
    }

    public static Logger globalLogger()
    {
        Logger logger = Logger.getLogger("global");

        logger.setLevel(Level.ALL);

        logger.info("Starting");
        return logger;
    }
    public Logger logger() {
        return this.logger;
    }
    public Zeno410Logger(String name) {
        this.logger = Logger.getLogger(name);
    }
}