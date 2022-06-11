package com.example.diarycloud;
public class Memory {
    private int memID;
    private String memTitle;
    private String memFeeling;
    private String memMain;
    private String memDate;
    private String memLocation;
    private String memAddress;
    private String memMedia;

    public Memory(int memID,
                  String memTitle,
                  String memFeeling,
                  String memMain,
                  String memDate,
                  String memLocation,
                  String memAddress,
                  String memMedia) {
        this.memID = memID;
        this.memTitle = memTitle;
        this.memFeeling = memFeeling;
        this.memMain = memMain;
        this.memDate = memDate;
        this.memLocation = memLocation;
        this.memAddress = memAddress;
        this.memMedia = memMedia;
    }

    public int getMemID() {
        return memID;
    }

    public void setMemID(int memID) {
        this.memID = memID;
    }

    public String getMemTitle() {
        return memTitle;
    }

    public void setMemTitle(String memTitle) {
        this.memTitle = memTitle;
    }

    public String getMemFeeling() {
        return memFeeling;
    }

    public void setMemFeeling(String memFeeling) {
        this.memFeeling = memFeeling;
    }

    public String getMemMain() {
        return memMain;
    }

    public void setMemMain(String memMain) {
        this.memMain = memMain;
    }

    public String getMemDate() {
        return memDate;
    }

    public void setMemDate(String memDate) {
        this.memDate = memDate;
    }

    public String getMemLocation() {
        return memLocation;
    }

    public void setMemLocation(String memLocation) {
        this.memLocation = memLocation;
    }

    public String getMemAddress() {
        return memAddress;
    }

    public void setMemAddress(String memAddress) {
        this.memAddress = memAddress;
    }

    public String getMemMedia() {
        return memMedia;
    }

    public void setMemMedia(String memMedia) {
        this.memMedia = memMedia;
    }
}
