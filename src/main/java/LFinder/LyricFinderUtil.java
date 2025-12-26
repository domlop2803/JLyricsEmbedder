package LFinder;


import Sources.AZLyrics;
import Sources.Genius;

import org.jsoup.Jsoup;

import LClasses.Lyrics;

import static LClasses.Lyrics.NO_RESULT;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class LyricFinderUtil {

    public static Lyrics getLyric(String ArtistName, String TrackName, Integer helper){
        return getLyric(ArtistName, TrackName,helper, false);
    }

    public static Lyrics getLyric(String ArtistName, String TrackName, Integer Helper, Boolean retry){
        Lyrics lyrics=new Lyrics(Lyrics.NO_RESULT);

        if(Helper>8){
            return new Lyrics(Lyrics.NO_RESULT, TrackName, ArtistName);
        }
        else {
            switch (Helper){
                case 1:
                    lyrics= AZLyrics.Find(ArtistName,TrackName);
                    if(IsFound(lyrics)){
                        return NormalizeLyric(lyrics);
                    }
                    else return getLyric(ArtistName,TrackName,retry? 2:9, retry);
                case 2:
                    lyrics = Genius.find(ArtistName, TrackName);
                    if(IsFound(lyrics))
                        return NormalizeLyric(lyrics);
                    else return getLyric(ArtistName, TrackName, retry? 9:9, retry);
            }
        }
        return lyrics;
    }

    private static Lyrics NormalizeLyric(Lyrics lyrics){
        String ly= RemoveSingersName(Normalizer(LineSeparator(lyrics.getText())));
        lyrics.setText(ly);
        if(ly.equals("INSTRUMENTAL"))lyrics.setText("[Instrumental]");
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
        //return html.replaceAll("<[^>]*>","/n");
        return html.replaceAll("<[^>]*>","//LINEBREAK//");
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
        String res = Jsoup.parse(lyrics).text();
        return res.replace("//LINEBREAK//", "\r\n");
    }

    public static String normalizeToHtmlNoSpaces(String toSearch){
        toSearch = java.text.Normalizer.normalize(toSearch, java.text.Normalizer.Form.NFKD);
        toSearch = toSearch.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return toSearch.trim().replaceAll("[\\s'\"-]", "")
                .replaceAll("&", "and").replaceAll("[^A-Za-z0-9]", "");
    }
    public static String normalizeToHtml(String toSearch){
        toSearch = java.text.Normalizer.normalize(toSearch, java.text.Normalizer.Form.NFKD);
        toSearch = toSearch.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return toSearch.trim().replaceAll("['\"-]", "")
                .replaceAll("&", "and").replaceAll("[^A-Za-z0-9\s]", "");
    }
}