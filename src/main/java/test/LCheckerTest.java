package test;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import LChecker.Checker;

public class LCheckerTest {
    public static void main(String[] args) throws UnsupportedTagException, InvalidDataException, UnsupportedAudioFileException, IOException{
        //First we normalize our test files:
        Checker.mp3SetLyrics("src\\main\\java\\test\\TestFiles\\RingOfFireLyrics.mp3", "TestLyrics");
        Checker.mp3SetLyrics("src\\main\\java\\test\\TestFiles\\RingOfFireNoLyrics.mp3", null);
        //Boolean checkerTrue = Checker.tyr("Tests\\TestFiles\\RingOfFireLyrics.mp3");
        //Boolean checkerFalse = Checker.tyr("Tests\\TestFiles\\RingOfFireNoLyrics.mp3");
        //Boolean checkerUnsupported = Checker.tyr("Tests\\TestFiles\\RingOfFireUnsupported.gba");
        LChecker.Checker.mp3HasLyrics("src\\main\\java\\test\\TestFiles\\RingOfFireLyrics.mp3");
        LChecker.Checker.mp3HasLyrics("src\\main\\java\\test\\TestFiles\\RingOfFireNoLyrics.mp3");
        LChecker.Checker.mp3HasLyrics("src\\main\\java\\test\\TestFiles\\RingOfFireUnsupported.gba");
    }
}
