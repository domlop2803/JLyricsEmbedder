import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import LChecker.Checker;
import LClasses.Track;

public class CheckerTest {
    
    @Test
    public void mp3hasLyricsTest(){
        assertEquals(Checker.hasLyrics("src\\test\\java\\TestFiles\\RingOfFireLyrics.mp3"), true);
    }

    @Test
    public void mp3hasNoLyricsTest(){
        assertEquals(Checker.hasLyrics("src\\test\\java\\TestFiles\\RingOfFireNoLyrics.mp3"), false);
    }
    
    @Test
    public void mp3ReaderUnsupportedFileTest(){
        assertEquals(Checker.hasLyrics("src\\test\\java\\TestFiles\\RingOfFireUnsupported.gba"), null);
    }

    @Test
    public void mp3GetLyricsTest(){
        assertEquals(Checker.getLyrics(new File("src\\test\\java\\TestFiles\\RingOfFireLyrics.mp3")), "Ring of fire");
    }
    
    @Test
    public void mp3GetTrackTest(){
        assertEquals(Checker.getTrack(new File("src\\test\\java\\TestFiles\\RingOfFire.mp3")), new Track("Ring Of Fire", "Johnny Cash"));
    }

    @Test
    public void mp3SetLyricsTest(){
        String pool = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder rand = new StringBuilder();
        for(Integer i = 0; i<16; i++) rand.append(pool.charAt((int)(Math.random()*pool.length())));
        Checker.setLyrics(new File("src\\test\\java\\TestFiles\\RingOfFire.mp3"), rand.toString());
        System.out.println(rand.toString());
        assertEquals(Checker.getLyrics(new File("src\\test\\java\\TestFiles\\RingOfFire.mp3")), rand.toString());
    }
}
