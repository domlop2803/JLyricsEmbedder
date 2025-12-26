package LChecker;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;

import FileParser.Parser;
import LClasses.Lyrics;
import LClasses.Track;

import java.io.*;
public class Checker {
    //returns true if the file has a tag and lyrics in it
    public static Boolean hasLyrics(File file){
        try {
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();
            if(tag!=null){
                String lyrics = tag.getFirst(FieldKey.LYRICS);
                if(lyrics.isBlank()||lyrics.isEmpty()) return false;
                else return true;
            }
            return false;
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException
                | InvalidAudioFrameException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean hasLyrics(String file){
        return hasLyrics(new File(file));
    }

    public static Boolean hasTag(File file){
        try {
            AudioFile audioFile = AudioFileIO.read(file);
            if(audioFile.getTag()==null || audioFile.getTag().isEmpty()) return false;
            else return true;
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException
                | InvalidAudioFrameException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean mp3HasLyrics(File file) {
        //We use ID3v2 because ID3v1 doesnt support lyrics
        //Returning true means the file won't be processed any more,
        //So if we try to process an unsupported file the error won't cascade 
        //Consider returning null in case of exception
        if(!Parser.getFileExtension(file).equals("mp3")){
            System.out.println("Tried to process a " + Parser.getFileExtension(file) + " file with an mp3 oriented function.");
            return null;
        }
        Boolean res = true;
        MP3File mp3file;
        try {
            mp3file = new MP3File(file);  
            if(mp3file.hasID3v2Tag()){
                String lyrics = mp3file.getID3v2Tag().getFirst(FieldKey.LYRICS);
                if(lyrics!=null) res = !(lyrics.isBlank()||lyrics.isEmpty());
                else res = false;
            } else if (mp3file.hasID3v1Tag()){
                //If the mp3 doesnt have an id3v2 tag it cant support lyrics, so we return false
                res = false;
                //We update the mp3 id tag to support embedded lyrics
                ID3v1Tag oldTag = mp3file.getID3v1Tag();
                ID3v24Tag newTag = new ID3v24Tag();
                newTag.setField(FieldKey.ALBUM,oldTag.getFirstAlbum());
                newTag.setField(FieldKey.ARTIST,oldTag.getFirstArtist());
                newTag.setField(FieldKey.COMMENT,oldTag.getFirstComment());
                newTag.setField(FieldKey.TITLE,oldTag.getFirstTitle());
                newTag.setField(FieldKey.GENRE,oldTag.getFirstGenre());
                newTag.setField(FieldKey.TRACK,oldTag.getFirstTrack());
                newTag.setField(FieldKey.YEAR,oldTag.getFirstYear());
                mp3file.setID3v2Tag(newTag);
                
                mp3file.commit();
            } else {
                //If the file doesn't have any id tag, we don't add lyrics to it
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } 
        return res;
    }

    public static Track getTrack(File file){
        try {
            AudioFile audioFile = AudioFileIO.read(file);
            if(audioFile.getTag()==null) return null;
            return new Track(audioFile.getTag().getFirst(FieldKey.TITLE), audioFile.getTag().getFirst(FieldKey.ARTIST));
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException
                | InvalidAudioFrameException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getLyrics(File file){
        try {
            AudioFile f = AudioFileIO.read(file);
            return f.getTag().getFirst(FieldKey.LYRICS);
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException
                | InvalidAudioFrameException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setLyrics(File file, String lyrics){
        try {
            AudioFile audioFile = AudioFileIO.read(file);
            if(audioFile.getTag()==null) return;
            audioFile.getTag().setField(FieldKey.LYRICS, lyrics);
            audioFile.commit();
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException
                | InvalidAudioFrameException |CannotWriteException e) {
            e.printStackTrace();
        }
    }

    public static void setLyrics(File file, Lyrics lyrics){
        setLyrics(file, lyrics.getText());
    }
}
