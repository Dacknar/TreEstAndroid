package com.uni.treest.models;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Date;

public class Post {
    private int delay;
    private int status;
    private String comment;
    private boolean followingAuthor;
    private Date postDate;
    private String authorName;
    private String pVersion;
    private String authorID;
    private Bitmap authorImage;

    public Post(int delay, int status, String comment, boolean followingAuthor, Date postDate, String authorName, String pVersion, String authorID) {
        this.delay = delay;
        this.status = status;
        this.comment = comment;
        this.followingAuthor = followingAuthor;
        this.postDate = postDate;
        this.authorName = authorName;
        this.pVersion = pVersion;
        this.authorID = authorID;
    }

    public void setAuthorImage(Bitmap authorImage) {
        this.authorImage = authorImage;
    }

    public Bitmap getAuthorImage() {
        return authorImage;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getDelay() {
        switch (delay){
            case 0:
                return "In orario";
            case 1:
                return "Ritardo di pochi minuti";
            case 2:
                return "Ritardo oltre i 15 minuti";
            case 3:
                return "Treni soppressi";
            default:
                return "Non specificato";
        }
    }

    public String getStatus() {
        switch (status){
            case 0:
                return "Situazione ideale";
            case 1:
                return "Accettabile";
            case 2:
                return "Gravi problemi";
            default:
                return "Non specificato";
        }
    }

    public void setFollowingAuthor(boolean followingAuthor) {
        this.followingAuthor = followingAuthor;
    }

    public String getComment() {
        return comment;
    }

    public boolean isFollowing(){
        return followingAuthor;
    }

    public String isFollowingAuthor() {
        if(followingAuthor){
            return "Non seguire";
        }
        else return "Segui";
    }

    public String getPostDate() {
        String updatedMinute = postDate.getMinutes() < 10 ? "0" + postDate.getMinutes() : "" + postDate.getMinutes();
        return "pubblicato il: " + (postDate.getDate()) + "/" +(postDate.getMonth() +1 ) + "/" +(postDate.getYear() - 100) + " alle ore: "+postDate.getHours()+":"+updatedMinute;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getpVersion() {
        return pVersion;
    }

    public String getAuthorID() {
        return authorID;
    }
}
