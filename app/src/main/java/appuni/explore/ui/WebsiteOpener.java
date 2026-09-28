package appuni.explore.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;

import appuni.explore.domain.DetailsPolicy;
import appuni.explore.domain.WebsiteAction;

public final class WebsiteOpener {
    private final Context context;

    public WebsiteOpener(Context context) {
        this.context = context;
    }

    public boolean hasValidatedNetwork() {
        ConnectivityManager manager = context.getSystemService(ConnectivityManager.class);
        if (manager == null) {
            return false;
        }
        android.net.Network network = manager.getActiveNetwork();
        if (network == null) {
            return false;
        }
        NetworkCapabilities capabilities = manager.getNetworkCapabilities(network);
        if (capabilities == null) {
            return false;
        }
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }

    public WebsiteAction open(String website) {
        WebsiteAction action = DetailsPolicy.websiteAction(website, hasValidatedNetwork());
        if (action instanceof WebsiteAction.OpenWebsite) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(((WebsiteAction.OpenWebsite) action).url));
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException ignored) {
                return action;
            }
        }
        return action;
    }
}
