package LClasses;


import java.io.*;
import java.util.Objects;

public class Lyrics implements Serializable {

    public static final int NO_RESULT = -2;
    public static final int NEGATIVE_RESULT = -1;
    public static final int POSITIVE_RESULT = 1;
    public static final int ERROR = -3;
    public static final int SEARCH_ITEM = 2;
    private final int mFlag;
    private String mTitle;
    private String mArtist;
    private String mLyrics;
    private String mSource;
    private String mSourceUrl;

    public Lyrics(int flag) {
        this.mFlag = flag;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }


    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        this.mArtist = artist;
    }

    public String getText() {
        return mLyrics;
    }

    public void setText(String lyrics) {
        this.mLyrics = lyrics;
    }

    public String getSource() {
        return mSource;
    }

    public void setSource(String mSource) {
        this.mSource = mSource;
    }
    
    public String getURL() {
        return this.mSourceUrl;
    }
    
    public void setURL(String mSourceUrl){
        this.mSourceUrl = mSourceUrl;
    }

    public int getFlag() {
        return mFlag;
    }

    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.close();
        } finally {
            bos.close();
        }
        return bos.toByteArray();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lyrics)) return false;
        Lyrics lyrics = (Lyrics) o;
        return mFlag == lyrics.mFlag &&
                Objects.equals(mTitle, lyrics.mTitle) &&
                Objects.equals(mArtist, lyrics.mArtist) &&
                Objects.equals(mSourceUrl, lyrics.mSourceUrl) &&
                Objects.equals(mLyrics, lyrics.mLyrics) &&
                Objects.equals(mSource, lyrics.mSource);
    }

    @Override
    public int hashCode() {
        return this.getURL() != null ? this.getURL().hashCode() :
                (""+this.getArtist()+this.getTitle()+this.getSource()).hashCode();
    }

    @Override
    public String toString() {
        return "Lyrics{" +
                "mTitle='" + mTitle + '\'' +
                ", mArtist='" + mArtist + '\'' +
                ", mSourceUrl='" + mSourceUrl + '\'' +
                ", mLyrics='" + mLyrics + '\'' +
                ", mSource='" + mSource + '\'' +
                ", mFlag=" + mFlag +
                '}';
    }
}