package edu.cogswell.morningvoice;

/**
 * Created by Christian on 4/9/2017.
 */

public class SaveState {
    private boolean completed;
    private int postNum;
    private MainActivity.itemReadType saveType;

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted() {
        completed = true;
    }

    public int getPostNum() {
        return postNum;
    }

    public void setPostNum(int newPostNum) {
        postNum = newPostNum;
        completed = false;
    }

    public MainActivity.itemReadType getSaveType() {
        return saveType;
    }

    public void setSaveType(MainActivity.itemReadType newSaveType) {
        saveType = newSaveType;
    }

    public SaveState(){
        completed = true;
        postNum = 0;
    }
}
