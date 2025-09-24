package FileParser;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Parser {
    public static void parse(File folder){
        if(folder.isFile()) return;
        for (final File file:folder.listFiles()){
            if(file.isDirectory()) parse(file);
            else if(isSupported(file)){
                //TODO
                System.out.println(file.getName());
            }
        }
    }

    private static Boolean isSupported(File file){
        List<String> supported = Arrays.asList("mp3"); 
        return supported.contains(getFileExtension(file));
    }

    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf + 1);
    }
}
