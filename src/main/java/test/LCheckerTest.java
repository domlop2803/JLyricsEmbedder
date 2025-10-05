package test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
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
            public void OnNotFound(Lyrics track) {
                System.out.println("NotFound -> "+track.getTitle() + " - "+track.getArtist());
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
            public void OnNotFound(Lyrics track) {
                System.out.println("NotFound -> "+track.getTitle()+" - " +track.getArtist());
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
            public void OnNotFound(Lyrics track) {
                System.out.println("NotFound -> "+track.getTitle()+" - "+track.getArtist());
            }
        });
    }
    public static void ClassifyInstrumentalTracks(String uri){
        Semaphore sem = new Semaphore(1);
        File folder = new File(uri);
        List<File> compatible = Parser.parse(folder);
        System.out.println(compatible.size()+" compatible files found.");
        compatible.removeIf(e->Checker.mp3HasLyrics(e));
        System.out.println(compatible.size()+" compatible files found without lyrics.");
        
        for(File file:compatible){
            Track track = Parser.getTrack(file);
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
                if(ans.equals("s")) Checker.mp3SetLyrics(file.getAbsolutePath(), "[Instrumental]");
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
            Track track = Parser.getTrack(file);
            if(tracks.contains(track)) System.out.println("Duplicated track: "+track);
            else tracks.add(track);
        }
    }
    public static void FolderProcesserTest(String uri, Integer maxSem){
        Semaphore sem = new Semaphore(maxSem);
        File folder = new File(uri);
        List<File> compatible = Parser.parse(folder);
        System.out.println(compatible.size()+" compatible files found.");
        compatible.removeIf(e->Checker.mp3HasLyrics(e));
        System.out.println(compatible.size()+" compatible files found without lyrics.");
        System.out.println("Parsing "+compatible.size()+" files. Start time: "+LocalDateTime.now());
        
        for(File file:compatible){
            Track track = Parser.getTrack(file);
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
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                });
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
        compatible.removeIf(e->Checker.mp3HasLyrics(e));
        System.out.println(compatible.size()+" compatible files found without lyrics.");
        System.out.println("Parsing "+compatible.size()+" files. Start time: "+LocalDateTime.now());
        
        for(File file:compatible){
            Track track = Parser.getTrack(file);
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
                            System.out.println("Lyrics not found. Would you like to retry with manual input? y/n/i(Instrumental)/abort");
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
                                FindAndWriteTest(ans, ans2, file.getAbsolutePath());
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
                });
            } catch(InterruptedException e){
                System.out.println(e);
            }
        }
        System.out.println("Finish at: "+LocalDateTime.now());
    }
    public static void main(String[] args) throws UnsupportedTagException, InvalidDataException, UnsupportedAudioFileException, IOException{
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
        //ManualFolderProcesserTest("H:\\Music\\Sounds good to me",1);
    }
}
