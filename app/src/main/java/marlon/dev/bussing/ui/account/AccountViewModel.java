package marlon.dev.bussing.ui.account;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<Boolean> isSignedIn;  // Track sign-in state

    public AccountViewModel() {
        mText = new MutableLiveData<>();

        // Initialize the sign-in state
        isSignedIn = new MutableLiveData<>();
        checkIfSignedIn();
    }

    // Get the live data that holds the greeting message
    public LiveData<String> getText() {
        return mText;
    }

    // Get the live data that holds the sign-in state
    public LiveData<Boolean> isSignedIn() {
        return isSignedIn;
    }

    // Check if the user is signed in
    private void checkIfSignedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        isSignedIn.setValue(user != null); // Update sign-in state
    }

    // Method to handle sign-out
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        checkIfSignedIn(); // Update sign-in state after signing out
    }
}
