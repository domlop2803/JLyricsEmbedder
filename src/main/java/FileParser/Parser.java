package FileParser;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import LChecker.Checker;

public class Parser {
    public static void parse(File folder){
        if(folder.isFile()) return;
        for (final File file:folder.listFiles()){
            if(file.isDirectory()){ 
                System.out.println("Parsing folder: "+file.getName());
                parse(file);}
            else if(isSupported(file)){
                processFile(file);
                System.out.println("Supported file found: "+file.getName());
            } else System.out.println("Unsupported file: " + file.getName());
        }
    }

    public static void processFile(File file){
        if(getFileExtension(file).equals("mp3")){
            if(Checker.mp3HasLyrics(file)) return;
            //Search lyrics and assign to str variable
            String lyrics = "SearchLyrics";
            Checker.mp3SetLyrics(file.getAbsolutePath(), lyrics);
        }
        return;
    }

    public static Boolean isSupported(File file){
        List<String> supported = Arrays.asList("mp3"); 
        return supported.contains(getFileExtension(file));
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf + 1).toLowerCase();
    }
    public static String getFileExtension(String file){
        return getFileExtension(new File(file));
    }
}
