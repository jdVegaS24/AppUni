package appuni.explore.domain;

public abstract class WebsiteAction {
    private WebsiteAction() {
    }

    public static final class OpenWebsite extends WebsiteAction {
        public final String url;

        public OpenWebsite(String url) {
            this.url = url;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof OpenWebsite && ((OpenWebsite) other).url.equals(url);
        }

        @Override
        public int hashCode() {
            return url.hashCode();
        }
    }

    public static final class ConnectionRequired extends WebsiteAction {
        public static final ConnectionRequired INSTANCE = new ConnectionRequired();

        private ConnectionRequired() {
        }
    }

    public static final class Unavailable extends WebsiteAction {
        public static final Unavailable INSTANCE = new Unavailable();

        private Unavailable() {
        }
    }
}
