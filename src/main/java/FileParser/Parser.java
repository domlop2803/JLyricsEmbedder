package FileParser;

import java.io.File;
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
            }
        }
        return res;
    }

    public static void processFiles(List<File> files, Semaphore sem){
        for(File file:files){
            Track track = Checker.getTrack(file);
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
                }, 1, false);
            } catch(InterruptedException e){
                System.out.println(e);
            }
        }
    }

    public static Boolean isSupported(File file){
        List<String> supported = Arrays.asList("mp3");
        return supported.contains(getFileExtension(file));

        /*
        try {
       day = SupportedFileFormat.valueOf(getFileExtension(file));
       //yes
    } catch (IllegalArgumentException ex) {  
        //nope
  } */
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
