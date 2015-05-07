package org.tweetwallfx.twitter;

import java.util.Date;
import twitter4j.MediaEntity;
import twitter4j.Status;

/**
 * TweetWallFX - Devoxx 2014 {@literal @}johanvos {@literal @}SvenNB
 * {@literal @}SeanMiPhillips {@literal @}jdub1581 {@literal @}JPeredaDnr
 *
 * @author jpereda
 */
public class TweetInfo {

    private final Status status;

    public TweetInfo(Status status) {
        this.status = status;
    }

    public String getName() {
        return status.getUser().getName();
    }

    public String getText() {
        return status.getText();
    }

    public String getImageURL() {
        return status.getUser().getProfileImageURL();
    }

    public String getHandle() {
        return status.getUser().getScreenName();
    }

    public Date getDate() {
        return status.getCreatedAt();
    }

    public MediaEntity[] getMediaEntities() {
        return status.getMediaEntities();
    }

    @Override
    public String toString() {
        return "TweetInfo{" + "status=" + status + '}';
    }

}
