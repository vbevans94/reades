package ua.org.cofriends.reades.utils;

import android.app.Application;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;
import javax.inject.Singleton;

import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.ui.basic.tools.CircleTransform;

@Singleton
public class GoogleApi implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int RC_SIGN_IN = 0;
    public static final String KEY_CANCELLED = "key_google_api_cancelled";

    private final Picasso picasso;

    private GoogleApiClient googleApiClient;

    @Inject
    public GoogleApi(Application context, Picasso picasso) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        this.picasso = picasso;
    }

    @Override
    public void onConnected(Bundle bundle) {
        BusUtils.post(new ConnectedEvent(googleApiClient));
    }

    @Override
    public void onConnectionSuspended(int i) {
        connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            BusUtils.post(new ConnectionFailedEvent(connectionResult));
        }
    }

    /**
     * Connects to Google APIs if not cancelled by user.
     */
    public void connect() {
        if (!LocalStorage.INSTANCE.contains(KEY_CANCELLED)) {
            manualConnect();
        }
    }

    /**
     * Connects if possible.
     */
    public void manualConnect() {
        if (googleApiClient != null && !googleApiClient.isConnecting()) {
            if (!googleApiClient.isConnected()) {
                LocalStorage.INSTANCE.remove(KEY_CANCELLED);

                googleApiClient.connect();
            } else {
                onConnected(null);
            }
        }
    }

    public void disconnect() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    public void cancel() {
        LocalStorage.INSTANCE.setBoolean(KEY_CANCELLED, true);
    }

    public void loadImage(ImageView imageView) {
        Person currentPerson = Plus.PeopleApi.getCurrentPerson(googleApiClient);
        if (currentPerson != null) {
            Person.Image profilePicture = currentPerson.getImage();
            if (profilePicture != null && profilePicture.hasUrl()) {
                int size = (int) imageView.getContext().getResources().getDimension(R.dimen.item_icon_size);
                picasso.load(profilePicture.getUrl() + "&size=" + size)
                        .transform(new CircleTransform())
                        .resize(size, size)
                        .centerCrop()
                        .into(imageView);
            }
        }
    }

    public static class ConnectedEvent extends BusUtils.Event<GoogleApiClient> {

        public ConnectedEvent(GoogleApiClient object) {
            super(object);
        }
    }

    public static class ConnectionFailedEvent extends BusUtils.Event<ConnectionResult> {

        public ConnectionFailedEvent(ConnectionResult object) {
            super(object);
        }
    }
}
