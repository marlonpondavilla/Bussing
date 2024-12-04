package marlon.dev.bussing.ui.location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LocationViewModel extends ViewModel {
    public final MutableLiveData<String> mText;

    public LocationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("See the current location of buses");
    }

    public LiveData<String> getText() {return mText;}
}