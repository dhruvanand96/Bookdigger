package com.example.hp.bookseller.fcm;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.hp.bookseller.BookSellerUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;




public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService
{
    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh()
    {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(final String token)
    {

        SharedPreferences preferences = getSharedPreferences(BookSellerUtil.SHAREDPREFS_FILENAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(BookSellerUtil.ARG_FIREBASE_TOKEN,token);
        editor.commit();

        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(BookSellerUtil.JSON_USER)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(BookSellerUtil.ARG_FIREBASE_TOKEN)
                    .setValue(token);
        }
    }
}