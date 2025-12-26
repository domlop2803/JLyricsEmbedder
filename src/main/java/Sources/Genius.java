package Sources;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.Pattern;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.safety.Safelist;

import LClasses.Lyrics;
import LFinder.LyricFinderUtil;

public class Genius {
    public static final String domain = "www.genius.com/";

    public static Lyrics find(String artist, String song){
        String htmlArtist = LyricFinderUtil.normalizeToHtml(artist.replaceAll("-"," ")).replaceAll(" ", "-");
        String htmlSong = LyricFinderUtil.normalizeToHtml(song.replaceAll("-"," ")).replaceAll(" ","-");
        String urlString = String.format(
            "https://www.genius.com/%s", htmlArtist+"-"+htmlSong+"-lyrics");
        return fromURL(urlString,artist, song);
    }

    public static Lyrics fromURL(String url, String artist, String song){
        String html;
        Document document;
        try {
            document = Jsoup.connect(url).
                    userAgent(HttpConnection.DEFAULT_UA).get();
            if (document.location().contains("genius")) html = document.html();
            else throw new IOException("Redirected to wrong domain " + document.location());
        } catch (HttpStatusException e) {
            e.printStackTrace();
            return new Lyrics(Lyrics.NO_RESULT, song, artist);
        } catch (IOException e) {
            return new Lyrics(Lyrics.ERROR, song, artist);
        }
        Pattern e = Pattern.compile("Verify you are human", Pattern.DOTALL);
        if(e.matcher(html).find()) return new Lyrics(Lyrics.ERROR, song, artist);

        Elements lyricsDiv = document.getElementsByAttribute("data-lyrics-container");
        if(lyricsDiv.isEmpty()){ 
            if(document.getElementById("lyrics-root").toString().contains("This song is an instrumental")){
                Lyrics result = new Lyrics(Lyrics.POSITIVE_RESULT);  
                result.setArtist(artist);
                result.setTitle(song);
                result.setText("INSTRUMENTAL");
                result.setURL(url);
                result.setSource("Genius");
                return result;  
            } else return new Lyrics(Lyrics.NO_RESULT, song, artist);
        }
        String text = Jsoup.clean(lyricsDiv.html(), Safelist.none().addTags("br")).trim();
        Pattern pattern = Pattern.compile("\\[.+]");
        Lyrics result = new Lyrics(Lyrics.POSITIVE_RESULT);    
        StringBuilder builder = new StringBuilder();
            for (String line : text.split("<br> ")) {
                String strippedLine = line.replaceAll("\\s", "");
                if (!pattern.matcher(strippedLine).matches() && !(strippedLine.isEmpty() && builder.length() == 0))
                    builder.append(line.replaceAll("\\P{Print}", "")).append("<br/>");
            }
            if (builder.length() > 5)
                builder.delete(builder.length() - 5, builder.length());
            result.setArtist(artist);
            result.setTitle(song);
            result.setText(text.substring(text.indexOf("\n")));
            result.setURL(url);
            result.setSource("Genius");
            return result;
    }
}
