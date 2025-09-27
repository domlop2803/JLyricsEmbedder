package test;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import FileParser.Parser;
import LChecker.Checker;

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
        Boolean lyrics = LChecker.Checker.mp3HasLyrics("src\\main\\java\\test\\TestFiles\\RingOfFireLyrics.mp3");
        Boolean noLyrics = !LChecker.Checker.mp3HasLyrics("src\\main\\java\\test\\TestFiles\\RingOfFireNoLyrics.mp3");
        Boolean unsupported = LChecker.Checker.mp3HasLyrics("src\\main\\java\\test\\TestFiles\\RingOfFireUnsupported.gba")==null;
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
    public static void main(String[] args) throws UnsupportedTagException, InvalidDataException, UnsupportedAudioFileException, IOException{
        CheckerEmbedderTest();
        FileParserTest();
    }
}
