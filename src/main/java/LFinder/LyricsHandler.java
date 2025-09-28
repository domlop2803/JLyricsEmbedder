package LFinder;

import LClasses.Lyrics;
import LClasses.Track;

public class LyricsHandler {

    public static void Find(Track track, LyricFinderListener lyricFinderListener){

        Thread thread=new Thread(new LyricFinderAsync(track,lyricFinderListener));
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }
    public static void Find(String artist, String song, LyricFinderListener lyricFinderListener){
        Find(new Track(song, artist), lyricFinderListener);
    }
    private static Lyrics SetTrack(Lyrics lyrics, Track track){
        lyrics.setArtist(track.getArtistNames());
        lyrics.setTitle(track.getTrackName());
        return lyrics;
    }

    //Async function for searching the lyrics and differentitating between finding them or not
    private static class LyricFinderAsync implements Runnable{

        private Track track;
        private LyricFinderListener lyricFinderListener;

        public LyricFinderAsync(Track track,LyricFinderListener lyricFinderListener){
            this.track=track;
            this.lyricFinderListener=lyricFinderListener;
        }

        @Override
        //semaforo mas de x procesos a la vez
        public void run() {
                Lyrics lyrics = LyricFinderUtil.getLyric(
                        track.getArtistNames().split(",")[0]
                        , track.getTrackName());

                if (lyrics.getFlag() == Lyrics.POSITIVE_RESULT) {
                    lyricFinderListener.OnFound(SetTrack(lyrics, track));}
                else{
                    lyricFinderListener.OnNotFound(track);}
        }
    }

    //Interface for managing the possibilities after lyrics search finishes
    public interface LyricFinderListener {
        void OnFound(Lyrics foundLyrics);
        void OnNotFound(Track track);
    }
}