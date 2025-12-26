

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;

import LClasses.Lyrics;
import LFinder.LyricsHandler;
import LFinder.LyricsHandler.LyricFinderListener;

public class LyricFinderTest {
    

    @Test
    public void findLyricsAZLyricsTest() throws InterruptedException, ExecutionException{
        CompletableFuture<Lyrics> res = new CompletableFuture<>();
        LyricsHandler.Find("Johnny Cash", "Ring of Fire", new LyricFinderListener() {
            @Override
            public void OnFound(Lyrics lyrics) {
                res.complete(lyrics);
            }
            @Override
            public void OnNotFound(Lyrics track) {
                res.complete(track);
            }
        }, 1, false);
        assertEquals(res.get().getFlag(), LClasses.Lyrics.POSITIVE_RESULT);
    }

    @Test
    public void failFindLyricsAZLyricsTest() throws InterruptedException, ExecutionException{
        CompletableFuture<Lyrics> res = new CompletableFuture<>();
        LyricsHandler.Find("wwwwwwwwwwwwwww", "wwwwwwwwwwwww", new LyricFinderListener() {
            @Override
            public void OnFound(Lyrics lyrics) {
                res.complete(lyrics);
            }
            @Override
            public void OnNotFound(Lyrics track) {
                res.complete(track);
            }
        }, 1, false);
        assertEquals(res.get().getFlag(), LClasses.Lyrics.NO_RESULT);
    }

    @Test
    public void findLyricsGeniusTest() throws InterruptedException, ExecutionException{
        CompletableFuture<Lyrics> res = new CompletableFuture<>();
        LyricsHandler.Find("masayoshi takanaka", "brasilian skies", new LyricFinderListener() {
            @Override
            public void OnFound(Lyrics lyrics) {
                res.complete(lyrics);
            }
            @Override
            public void OnNotFound(Lyrics track) {
                res.complete(track);
            }
        }, 2, false);
        System.out.println(res.get().toString());
        assertEquals(res.get().getFlag(), LClasses.Lyrics.POSITIVE_RESULT);
    }

    @Test
    public void failFindLyricsGeniusTest() throws InterruptedException, ExecutionException{
        CompletableFuture<Lyrics> res = new CompletableFuture<>();
        LyricsHandler.Find("wwwwwwwwwww", "wwwwwwwwwwwww", new LyricFinderListener() {
            @Override
            public void OnFound(Lyrics lyrics) {
                res.complete(lyrics);
            }
            @Override
            public void OnNotFound(Lyrics track) {
                res.complete(track);
            }
        }, 2, false);
        System.out.println(res.get().toString());
        assertEquals(res.get().getFlag(), LClasses.Lyrics.NO_RESULT);
    }

    @Test
    public void findLyricsRetryTest() throws InterruptedException, ExecutionException{
        CompletableFuture<Lyrics> res = new CompletableFuture<>();
        LyricsHandler.Find("masayoshi takanaka", "brasilian skies", new LyricFinderListener() {
            @Override
            public void OnFound(Lyrics lyrics) {
                res.complete(lyrics);
            }
            @Override
            public void OnNotFound(Lyrics track) {
                res.complete(track);
            }
        }, 1, true);
        System.out.println(res.get().toString());
        assertEquals(res.get().getFlag(), LClasses.Lyrics.POSITIVE_RESULT);
    }
    
}
