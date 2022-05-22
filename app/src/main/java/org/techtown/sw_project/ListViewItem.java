package org.techtown.sw_project;

public class ListViewItem {
    private String nameStr;
    private String amountStr;

    public void setName(String name){
        nameStr = name;
    }
    public void setAmount(String amount){
        amountStr = amount;
    }
    public String getName(){
        return this.nameStr;
    }
    public String getAmount(){
        return this.amountStr;
    }
}
