package org.qiuhua.UnrealGUIPro.trmenu;




public class TrPlayer {

    private boolean first = true;

    private boolean trMenu = false;



    public void setTrMenu(Boolean b){
        this.trMenu = b;
    }

    public Boolean getTrMenu(){
        return trMenu;
    }
    public void setFirst(Boolean b){
        this.first = b;
    }

    public Boolean getFirst(){
        return first;
    }

}
