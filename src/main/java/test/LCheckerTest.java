package test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.UnsupportedAudioFileException;


import FileParser.Parser;
import LChecker.Checker;
import LClasses.Lyrics;
import LClasses.Track;
import LFinder.LyricsHandler;
import LFinder.LyricsHandler.LyricFinderListener;

public class LCheckerTest {

    private static void FindAndWriteTest(String artist, String songName, String filename, Integer helper, Boolean retry){
        LyricsHandler.Find(artist, songName, new LyricFinderListener() {
            @Override
            public void OnFound(Lyrics lyrics) {
                Checker.setLyrics(new File(filename), lyrics.getText());
            }
            @Override
            public void OnNotFound(Lyrics track) {
                System.out.println("NotFound -> "+track.getTitle()+" - "+track.getArtist());
            }
        }, helper, retry);
    }
    
    public static void ClassifyInstrumentalTracks(String uri){
        Semaphore sem = new Semaphore(1);
        File folder = new File(uri);
        List<File> compatible = Parser.parse(folder);
        System.out.println(compatible.size()+" compatible files found.");
        compatible.removeIf(e->Checker.hasLyrics(e)||!Checker.hasTag(e));
        System.out.println(compatible.size()+" compatible files found with tags and without lyrics.");
        
        for(File file:compatible){
            Track track = Checker.getTrack(file);
            System.out.println("Next track:");
            try{
                sem.acquire();
                System.out.println("Processing " + track.toString()+"\nIs it an instrumental track? s/n");
                String ans = "";
                Scanner input = new Scanner(System.in);
                ans = input.nextLine();
                while(!ans.equals("s")&&!ans.equals("n")){
                    System.out.println("Unrecognized input: "+ans+", please answer s/n");
                    ans = input.nextLine();
                }
                if(ans.equals("s")) Checker.setLyrics(file, "[Instrumental]");
                input.close();
                sem.release();
            } catch(InterruptedException e){
                System.out.println(e);
            }
        }
    }
    public static void CheckForDuplicates(String uri){
        File folder = new File(uri);
        List<File> compatible = Parser.parse(folder);
        System.out.println(compatible.size()+" compatible files found.");
        List<Track> tracks = new ArrayList<Track>();
        
        for(File file:compatible){
            Track track = Checker.getTrack(file);
            if(tracks.contains(track)) System.out.println("Duplicated track: "+track);
            else tracks.add(track);
        }
    }
    public static void FolderProcesserTest(String uri, Integer maxSem){
        Semaphore sem = new Semaphore(maxSem);
        File folder = new File(uri);
        List<File> compatible = Parser.parse(folder);
        System.out.println(compatible.size()+" compatible files found.");
        compatible.removeIf(e->Checker.hasLyrics(e)||!Checker.hasTag(e));
        System.out.println(compatible.size()+" compatible files found with tags and without lyrics.");
        System.out.println("Parsing "+compatible.size()+" files. Start time: "+LocalDateTime.now());
        
        for(File file:compatible){
            Track track = Checker.getTrack(file);
            try{
                sem.acquire();
                System.out.println("Searching " + track.toString());
                LyricsHandler.Find(track.getArtistNames(), track.getTrackName(), new LyricFinderListener() {

                    @Override
                    public void OnFound(Lyrics lyrics) {
                        System.out.println("Lyrics found, embedding");
                        Checker.setLyrics(file, lyrics);
                        try {
                            Random rand = new Random();
                            System.out.println("Lyrics set. Waiting 2-4 seconds.");
                            Thread.sleep(2500 + rand.nextInt(1000));
                            sem.release();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void OnNotFound(Lyrics track) {    
                        String ans = "";
                        if(track.getFlag()==(Lyrics.ERROR)){
                            Scanner input = new Scanner(System.in);
                            System.out.println("BOT CHECKED. Would you like to abort? y/n");
                            ans = input.nextLine();
                            while(!ans.equals("y")&&!ans.equals("n")){
                                System.out.println("Unrecognized input: "+ans+", please answer y/n");
                                ans = input.nextLine();
                            }
                            if(ans.equals("y")) {
                                input.close();
                                Thread.currentThread().interrupt();}
                            else sem.release();
                        } else{
                            System.out.println("Lyrics not found. Waiting 2-4 seconds");
                            Random rand = new Random();
                            try {
                                Thread.sleep(2500 + rand.nextInt(1000));
                                sem.release();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, 1, true);
            } catch(InterruptedException e){
                System.out.println(e);
            }
        }
        System.out.println("Finish at: "+LocalDateTime.now());
    }

    public static void ManualFolderProcesserTest(String uri, Integer maxSem){
        Semaphore sem = new Semaphore(maxSem);
        File folder = new File(uri);
        List<File> compatible = Parser.parse(folder);
        System.out.println(compatible.size()+" compatible files found.");
        compatible.removeIf(e->Checker.hasLyrics(e)||!Checker.hasTag(e));
        System.out.println(compatible.size()+" compatible files found with tags and without lyrics.");
        System.out.println("Parsing "+compatible.size()+" files. Start time: "+LocalDateTime.now());
        
        for(File file:compatible){
            Track track = Checker.getTrack(file);
            try{
                sem.acquire();
                System.out.println("Searching " + track.toString());
                LyricsHandler.Find(track.getArtistNames(), track.getTrackName(), new LyricFinderListener() {

                    @Override
                    public void OnFound(Lyrics lyrics) {
                        System.out.println("Lyrics found, embedding");
                        Checker.setLyrics(file, lyrics);
                        try {
                            Random rand = new Random();
                            System.out.println("Lyrics set. Waiting 2-4 seconds.");
                            Thread.sleep(2500 + rand.nextInt(1000));
                            sem.release();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void OnNotFound(Lyrics track) {    
                        String ans = "";
                        Scanner input = new Scanner(System.in);
                        if(track.getFlag()==(Lyrics.ERROR)){
                            System.out.println("BOT CHECKED. Would you like to abort? y/n");
                            ans = input.nextLine();
                            while(!ans.equals("y")&&!ans.equals("n")){
                                System.out.println("Unrecognized input: "+ans+", please answer y/n");
                                ans = input.nextLine();
                            }
                            if(ans.equals("y")) Thread.currentThread().interrupt();
                            else sem.release();
                        } else{
                            System.out.println("Lyrics not found for song: "+track.getTitle()+" by "+track.getArtist()+". Would you like to retry with manual input? y/n/i(Instrumental)/abort");
                            ans = input.nextLine();
                            while(!ans.equals("y")&&!ans.equals("n")&&!ans.equals("abort")&&!ans.equals("i")){
                                System.out.println("Unrecognized input: "+ans+", please answer y/n/i/abort");
                                ans = input.nextLine();
                            }
                            if(ans.equals("y")){
                                System.out.println("Input the artist");
                                ans= input.nextLine();
                                System.out.println("Input the song title");
                                String ans2= input.nextLine();
                                FindAndWriteTest(ans, ans2, file.getAbsolutePath(), 1, true);
                                try {
                                    Thread.sleep(4500);
                                    sem.release();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                
                            }
                            else if(ans.equals("abort")){
                                input.close();
                                Thread.currentThread().interrupt();
                            }
                            else if(ans.equals("i")){
                                System.out.println("Setting file as instrumental.");
                                Checker.setLyrics(file, "[Instrumental]");
                                sem.release();
                            }
                            else {
                                sem.release();
                            }
                        }
                    }
                }, 1, true);
            } catch(InterruptedException e){
                System.out.println(e);
            }
        }
        System.out.println("Finish at: "+LocalDateTime.now());
    }
    public static void main(String[] args) throws Exception, UnsupportedAudioFileException, IOException{
        //CheckerEmbedderTest();
        //FileParserTest();
        //FindAndWriteTest("johnny cash", "ring of fire", "src\\main\\java\\test\\TestFiles\\RingOfFireLyrics.mp3");
        //SemaphoreTest();
        //FolderProcesserTest("H:\\Music\\Sounds good to me",1);
        //turns out u get 1: bot checked and 2: ip banned if u try to do 250 html petitions in 2 seconds
        //maybe try with a sem size 1 next time? when u get unbanned?
        //ClassifyInstrumentalTracks("H:\\Music");
        //CheckForDuplicates("H:\\Music");
        //FindAndWriteTest("daft punk", "within", "H:\\Music\\Daft Punk\\Random Access Memories\\04 - Within.mp3");
        //System.out.println(Checker.mp3HasLyrics("H:\\Music\\Daft Punk\\Random Access Memories\\01 - Give Life Back to Music.mp3"));
        //FinderTest("daft punk", "give life back to music");
        //Checker.mp3SetLyrics("H:\\Music\\Daft Punk\\Random Access Memories\\01 - Give Life Back to Music.mp3", "Just turn on the music");
        Logger[] pin = new Logger[]{ Logger.getLogger("org.jaudiotagger") };
        for (Logger l : pin) l.setLevel(Level.OFF);
        ManualFolderProcesserTest("H:\\Music\\Sounds good to me",1);
    }
}
