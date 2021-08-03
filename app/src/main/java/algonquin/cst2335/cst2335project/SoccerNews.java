package algonquin.cst2335.cst2335project;

public class SoccerNews {
    private String soccerTitle;
    private String soccerDate;
    private String soccerImg;
    private String soccerLink;
    private String soccerDescription;
    private long soccerId;

    /**
     * get soccer Id
     * @return soccerId
     */
    public long getSoccerId() {
        return soccerId;
    }

    /**
     * set SoccerId
     * @param soccerId
     */
    public void setSoccerId(long soccerId) {
        this.soccerId = soccerId;
    }

    /**
     *this method is to get the title
     * @return soccerTitle String
     */
    public String getSoccerTitle() {
        return soccerTitle;
    }

    /**
     *this method is to get the date
     * @return soccerDate String
     */
    public String getSoccerDate() {
        return soccerDate;
    }

    /**
     *this method is to get Img
     * @return soccerImg String
     */
    public String getSoccerImg() {
        return soccerImg;
    }

    /**
     *this method is to get the link
     * @return soccerLink String
     */
    public String getSoccerLink() {
        return soccerLink;
    }

    /**
     *this method is to get the description
     * @return soccerDescription String
     */
    public String getSoccerDescription() {
        return soccerDescription;
    }

    /**
     *this method is to set up the title
     * @param soccerTitle String
     */
    public void setSoccerTitle(String soccerTitle) {
        this.soccerTitle = soccerTitle;
    }

    /**
     *this method is to set up the Date
     * @param soccerDate String
     */
    public void setSoccerDate(String soccerDate) {
        this.soccerDate = soccerDate;
    }

    /**
     *this method is to set up the Img
     * @param soccerImg String
     */
    public void setSoccerImg(String soccerImg) {
        this.soccerImg = soccerImg;
    }

    /**
     *this method is to set up the link
     * @param soccerLink String
     */
    public void setSoccerLink(String soccerLink) {
        this.soccerLink = soccerLink;
    }

    /**
     *this method is to set up the description
     * @param soccerDescription String
     */
    public void setSoccerDescription(String soccerDescription) {
        this.soccerDescription = soccerDescription;
    }
}
