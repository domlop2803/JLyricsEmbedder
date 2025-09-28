package LFinder;


import Sources.AZLyrics;

import org.jsoup.Jsoup;

import LClasses.Lyrics;

import static LClasses.Lyrics.NO_RESULT;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricFinderUtil {

    public static Lyrics getLyric(String ArtistName, String TrackName){
        return getLyric(ArtistName, TrackName,2);
//
//        try {
//            Lyrics lyrics=new Lyrics(NO_RESULT);
//            lyrics=Genius.Find(ArtistName,TrackName);
//           // System.out.println(lyrics);
//            if(IsFound(lyrics)) {
//                return NormalizeLyric(lyrics);
//            }
//            else {
//                lyrics=Genius.Find2(ArtistName,TrackName);
//                if(IsFound(lyrics)) {
//                    return NormalizeLyric(lyrics);
//                }
//                else
//                return getLyric(ArtistName,TrackName,1);
//            }
//        }
//        catch (Exception e){
//            return new Lyrics(NO_RESULT);
//        }

    }

    private static Lyrics getLyric(String ArtistName, String TrackName, int Helper){
        Lyrics lyrics=new Lyrics(Lyrics.NO_RESULT);

        if(Helper>8){
            return new Lyrics(Lyrics.NO_RESULT);
        }
        else {
            switch (Helper){
                case 2:
                    lyrics= AZLyrics.Find(ArtistName,TrackName);
                    if(IsFound(lyrics)){
                        return NormalizeLyric(lyrics);
                    }
                    else getLyric(ArtistName,TrackName,3);
                    break;
            }
        }
        return lyrics;
    }

    private static Lyrics NormalizeLyric(Lyrics lyrics){
        String ly= RemoveSingersName(Normalizer(LineSeparator(
                lyrics.getText())));
        lyrics.setText(ly);
        return lyrics;
    }

    private static boolean IsFound(Lyrics lyrics){
        switch (lyrics.getFlag()){
            case Lyrics.NO_RESULT:
            case Lyrics.ERROR:
            case Lyrics.NEGATIVE_RESULT:
            case Lyrics.SEARCH_ITEM:
                return false;
            case Lyrics.POSITIVE_RESULT:return true;
            default:return false;
        }
    }


    private static String LineSeparator(String html){
        return html.replaceAll("<[^>]*>","/n");
        //return html.replaceAll("<[^>]*>","\r\n");
        //TODO research proper way to insert line breaks in tag lyrics
    }

    private static List<String> ExtractSingersName(String lyric){
        //[Ariana Grande:] for example
        Matcher matcher = Pattern.compile("\\[([^]]+)").matcher(lyric);

        List<String> tags = new ArrayList<>();

        int pos = -1;
        while (matcher.find(pos+1)){
            pos = matcher.start();
            tags.add(matcher.group(1));
        }

        return tags;
    }

    private static String RemoveSingersName(String lyrics){
        String res= lyrics;

        for (String singer:ExtractSingersName(lyrics)) {
            res=res.replace("["+singer+"]","");
        }
        return res;
    }

    private static String Normalizer(String lyrics) {
        return Jsoup.parse(lyrics).text();
    }
}