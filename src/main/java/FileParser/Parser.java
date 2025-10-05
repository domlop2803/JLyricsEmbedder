package FileParser;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

import LChecker.Checker;
import LClasses.Lyrics;
import LClasses.Track;
import LFinder.LyricsHandler;
import LFinder.LyricsHandler.LyricFinderListener;

public class Parser {
    public static List<File> parse(File folder){
        List<File> res = new ArrayList<File>();
        if(folder.isFile()) return res;
        for (final File file:folder.listFiles()){
            if(file.isDirectory()){ 
                for (File fileInFolder:parse(file)) res.add(fileInFolder);
            }
            else if(isSupported(file)){
                res.add(file);
                //processFile(file);
                //System.out.println("Supported file found: "+file.getName());
            } //else System.out.println("Unsupported file: " + file.getName());
        }
        return res;
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
    public static void processFiles(List<File> files, Semaphore sem){
        for(File file:files){
            Track track = getTrack(file);
            try{
                sem.acquire();
                LyricsHandler.Find(track.getArtistNames(), track.getTrackName(), new LyricFinderListener() {

                    @Override
                    public void OnFound(Lyrics lyrics) {
                        Checker.setLyrics(file, lyrics);
                        sem.release();
                    }

                    @Override
                    public void OnNotFound(Lyrics track) {
                        sem.release();
                    }
                });
            } catch(InterruptedException e){
                System.out.println(e);
            }
        }
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
    public static Track getTrack(File file){
        if(getFileExtension(file).equals("mp3")){
           return Checker.mp3getTrack(file);
        } else return null;
    }
}
