package LChecker;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;

import FileParser.Parser;

import java.io.*;
import java.nio.file.Files;
public class Checker {
    //Assuming the files are supported audio files
    public static Boolean hasLyrics(String file){
        Boolean res = true;
        File audioFile = new File(file);
        AudioInputStream in;
        try {
            in = AudioSystem.getAudioInputStream(audioFile);
            System.out.println(in.getFormat());
            res = false;
            System.out.println(audioFile.getName() + " is of an supported type.");
        } catch (UnsupportedAudioFileException e) {
            System.out.println(audioFile.getName() + " is of an unsupported type.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(audioFile.getName() + " resulted in an unexpected error.");
        }
        return res;
    }
    public static Boolean mp3HasLyrics(File file){
        return mp3HasLyrics(file.getAbsolutePath());
    }
    public static Boolean mp3HasLyrics(String filename) {
        //We use ID3v2 because ID3v1 doesnt support lyrics
        //Returning true means the file won't be processed any more,
        //So if we try to process an unsupported file the error won't cascade 
        //Consider returning null in case of exception
        if(!Parser.getFileExtension(filename).equals("mp3")){
            System.out.println("Tried to process a " + Parser.getFileExtension(filename) + " file with an mp3 oriented function.");
            return null;
        }
        Boolean res = true;
        Mp3File mp3file;
        try {
            mp3file = new Mp3File(filename);  
            if(mp3file.hasId3v2Tag()){
                String lyrics = mp3file.getId3v2Tag().getLyrics();
                if(lyrics!=null) res = !(lyrics.isBlank()||lyrics.isEmpty());
                else res = false;
            } else if (mp3file.hasId3v1Tag()){
                //If the mp3 doesnt have an id3v2 tag it cant support lyrics, so we return false
                res = false;
                //We update the mp3 id tag to support embedded lyrics
                ID3v1 oldTag = mp3file.getId3v1Tag();
                ID3v2 newTag = new ID3v24Tag();
                newTag.setAlbum(oldTag.getAlbum());
                newTag.setArtist(oldTag.getArtist());
                newTag.setComment(oldTag.getComment());
                newTag.setTitle(oldTag.getTitle());
                newTag.setGenreDescription(oldTag.getGenreDescription());
                newTag.setTrack(oldTag.getTrack());
                newTag.setYear(oldTag.getYear());
                newTag.setGenre(oldTag.getGenre());
                mp3file.setId3v2Tag(newTag);
                
                String newFileName = filename.substring(0,filename.length()-4)+"C.mp3";
                mp3file.save(newFileName);
                cleanFiles(new File(filename), new File(newFileName));
            } else {
                //If the file doesn't have any id tag, we don't add lyrics to it
            }
        } catch (UnsupportedTagException | InvalidDataException | IOException | NotSupportedException e) {
            e.printStackTrace();
            return null;
        } 
        return res;
    }

    public static void mp3SetLyrics(String filename, String lyrics){
        try {
            Mp3File mp3 = new Mp3File(filename);
            if(mp3.hasId3v2Tag()){
                ID3v2 tag = mp3.getId3v2Tag();
                tag.setLyrics(lyrics);
                String newFileName = filename.substring(0,filename.length()-4)+"C.mp3";
                mp3.save(newFileName);
                cleanFiles(new File(filename), new File(newFileName));
            } else return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void cleanFiles(File oldFile, File newFile){
        //Method needed for deleting the original file and renaming the updated file with the old one's name.
        try {
            Files.deleteIfExists(oldFile.toPath());
            Files.move(newFile.toPath(),oldFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
