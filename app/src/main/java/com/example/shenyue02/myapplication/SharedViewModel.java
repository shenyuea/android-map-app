package com.example.shenyue02.myapplication;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.ClipData;

public class SharedViewModel extends ViewModel {

    private  MutableLiveData<Long> selected = new MutableLiveData<>();
    private MutableLiveData<Long>  terminalId = new MutableLiveData<>();

    public void select(Long item) {
        selected.setValue(item);
    }
    public  void setTerminalId(Long id){
        terminalId.setValue(id);
    }
    public LiveData<Long> getSelected() {
        return selected;
    }
    public LiveData<Long> getTerminalId() {
        return terminalId;
    }
}
