package LClasses;

import java.io.Serializable;
import java.util.Objects;

public class Track implements Serializable {

    private String TrackName ;
    private String ArtistNames ;

    public Track(String TrackName,String ArtistNames){
        this.ArtistNames=ArtistNames;
        this.TrackName=TrackName;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Track)) return false;
        Track track = (Track) o;
        return Objects.equals(ArtistNames, track.ArtistNames) &&
                Objects.equals(TrackName, track.TrackName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(TrackName, ArtistNames);
    }

    @Override
    public String toString() {
        return "Track{" +
                ", TrackName='" + TrackName + '\'' +
                ", ArtistNames='" + ArtistNames + '\'' +
                '}';
    }


    public String getTrackName() {
        return TrackName;
    }

    public void setTrackName(String trackName) {
        TrackName = trackName;
    }

    public String getArtistNames() {
        return ArtistNames;
    }

    public void setArtistNames(String artistNames) {
        ArtistNames = artistNames;
    }
    
}