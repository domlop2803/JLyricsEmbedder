package LChecker;

import javax.sound.midi.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v1Tag;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

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
    public static Boolean mp3HasLyrics(String filename) throws UnsupportedAudioFileException {
        //We use ID3v2 because ID3v1 doesnt support lyrics
        //Returning true means the file won't be processed any more,
        //So if we try to process an unsupported file the error won't cascade 
        //Consider returning null in case of exception
        if(!filename.endsWith(".mp3")){
            System.out.println("Tried to process a " + filename.substring(filename.length()-4)+ " file with an mp3 oriented function.");
            return true;
        }
        Boolean res = true;
        Mp3File mp3file;
        try {
            mp3file = new Mp3File(filename);  
            if(mp3file.hasId3v2Tag()){
                String lyrics = mp3file.getId3v2Tag().getLyrics();
                if(lyrics!=null) {
                    res = !(lyrics.isBlank()||lyrics.isEmpty());
                    System.out.println(lyrics);
                }else{
                    res = false;
                    System.out.println("Null lyrics");
                }
            } else{
            // mp3 does not have an ID3v2..
            // consider checking if it has an ID3v1 and updating it to an ID3v2
            }
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
    public static void mp3SetLyrics(String filename, String lyrics){
        try {
            Mp3File mp3 = new Mp3File(filename);
            if(mp3.hasId3v2Tag()){
                ID3v2 tag = mp3.getId3v2Tag();
                tag.setLyrics(lyrics);
                File oldFile = new File(filename);
                String newFileName = filename.substring(0,filename.length()-4)+"C.mp3";
                mp3.save(newFileName);
                cleanFiles(oldFile, new File(newFileName));
            } else return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void cleanFiles(File oldFile, File newFile){
        try {
            Files.deleteIfExists(oldFile.toPath());
            Files.move(newFile.toPath(),oldFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
