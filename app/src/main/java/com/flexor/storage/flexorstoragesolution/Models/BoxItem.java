package com.flexor.storage.flexorstoragesolution.Models;

public class BoxItem {
    private String boxItemIdentifier;
    private String boxItemAmmount;
    private Boolean boxItemVerivied;
    private String boxItemID;

    public BoxItem() {
    }

    public BoxItem(String boxItemIdentifier, String boxItemAmmount, Boolean boxItemVerivied, String boxItemID) {
        this.boxItemIdentifier = boxItemIdentifier;
        this.boxItemAmmount = boxItemAmmount;
        this.boxItemVerivied = boxItemVerivied;
        this.boxItemID = boxItemID;
    }

    @Override
    public String toString() {
        return "BoxItem{" +
                "boxItemIdentifier='" + boxItemIdentifier + '\'' +
                ", boxItemAmmount='" + boxItemAmmount + '\'' +
                ", boxItemVerivied=" + boxItemVerivied +
                ", boxItemID='" + boxItemID + '\'' +
                '}';
    }

    public String getBoxItemIdentifier() {
        return boxItemIdentifier;
    }

    public void setBoxItemIdentifier(String boxItemIdentifier) {
        this.boxItemIdentifier = boxItemIdentifier;
    }

    public String getBoxItemAmmount() {
        return boxItemAmmount;
    }

    public void setBoxItemAmmount(String boxItemAmmount) {
        this.boxItemAmmount = boxItemAmmount;
    }

    public Boolean getBoxItemVerivied() {
        return boxItemVerivied;
    }

    public void setBoxItemVerivied(Boolean boxItemVerivied) {
        this.boxItemVerivied = boxItemVerivied;
    }

    public String getBoxItemID() {
        return boxItemID;
    }

    public void setBoxItemID(String boxItemID) {
        this.boxItemID = boxItemID;
    }
}
