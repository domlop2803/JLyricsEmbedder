package test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.sound.sampled.UnsupportedAudioFileException;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import FileParser.Parser;
import LChecker.Checker;
import LClasses.Lyrics;
import LClasses.Track;
import LFinder.LyricsHandler;
import LFinder.LyricsHandler.LyricFinderListener;

public class LCheckerTest {
    private static void CheckerEmbedderTest(){
        System.out.print("-------------------------------\nChecker and embedder test:\n");
        //First we normalize our test files:
        //Checker.mp3SetLyrics("src\\main\\java\\test\\TestFiles\\RingOfFireNoLyrics.mp3", null);
        //Checker.mp3SetLyrics("src\\main\\java\\test\\TestFiles\\RingOfFireLyrics.mp3", "TestLyrics");
        Boolean setLyricsCheck = false;
        try {
            setLyricsCheck = new Mp3File("src\\main\\java\\test\\TestFiles\\RingOfFireLyrics.mp3").getId3v2Tag().getLyrics().equals("TestLyrics");
        } catch (UnsupportedTagException | InvalidDataException | IOException e) {
            e.printStackTrace();
        }
        Boolean lyrics = Checker.mp3HasLyrics("src\\main\\java\\test\\TestFiles\\RingOfFireLyrics.mp3");
        Boolean noLyrics = !Checker.mp3HasLyrics("src\\main\\java\\test\\TestFiles\\RingOfFireNoLyrics.mp3");
        Boolean unsupported = Checker.mp3HasLyrics("src\\main\\java\\test\\TestFiles\\RingOfFireUnsupported.gba")==null;
        System.out.println("Lyrics checker test (set and read lyrics): "+setLyricsCheck);
        System.out.println("Recognize file has lyrics: "+lyrics);
        System.out.println("Recognize file doesn't have lyrics: "+noLyrics);
        System.out.println("Recognize file unsupported extension: "+unsupported);
    }


    private static void FileParserTest(){
        System.out.print("-------------------------------\nFile parser test:\n");
        File folder = new File("src\\main\\java\\test");
        Parser.parse(folder);
    }

    private static void FinderTest(String artist, String songName){
        LyricsHandler.Find(artist, songName, new LyricFinderListener() {

            @Override
            public void OnFound(Lyrics lyrics) {
                System.out.println("Found -> "+lyrics);
            }
            @Override
            public void OnNotFound(Track track) {
                System.out.println("NotFound -> "+track);
            }
        });
    }

    //Works, maybe implement a method in Parser that receives a list of files and a semaphore and parses them to find their lyrics
    private static void SemaphoreTest(){
        List<String> files = List.of("ring of fire","cry,cry,cry","heart of gold", "folsom prison blues");
        Semaphore sem = new Semaphore(1);
        for(String file:files){
            try{
                sem.acquire();
                System.out.println("File "+files.indexOf(file)+ " got permit at "+LocalDateTime.now());
                LyricsHandler.Find("johnny cash", file, new LyricFinderListener() {

            @Override
            public void OnFound(Lyrics lyrics) {
                System.out.println("Found -> "+lyrics.getTitle());
            System.out.println("File "+files.indexOf(file)+ " released permit at "+LocalDateTime.now());
            sem.release();
            }
            @Override
            public void OnNotFound(Track track) {
                System.out.println("NotFound -> "+track);
            System.out.println("File "+files.indexOf(file)+ " released permit at "+LocalDateTime.now());
            sem.release();
            }
        });
            } catch(InterruptedException e){
                System.out.println(e);
            }
        }
    }

    private static void FindAndWriteTest(String artist, String songName, String filename){
        LyricsHandler.Find(artist, songName, new LyricFinderListener() {
            @Override
            public void OnFound(Lyrics lyrics) {
                Checker.mp3SetLyrics(filename, lyrics.getText());
            }
            @Override
            public void OnNotFound(Track track) {
                System.out.println("NotFound -> "+track);
            }
        });
    }
    
    public static void main(String[] args) throws UnsupportedTagException, InvalidDataException, UnsupportedAudioFileException, IOException{
        //CheckerEmbedderTest();
        //FileParserTest();
        //FinderTest("johnny cash", "ring of fire");
        SemaphoreTest();
    }
}
