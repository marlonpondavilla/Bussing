package marlon.dev.bussing.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private String displayText = "";

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(displayText);
    }

    public void setHello(String text){
        displayText = text;
    }

    public String getHello(){
        return this.displayText;
    }

    public LiveData<String> getText() {
        return mText;
    }
}