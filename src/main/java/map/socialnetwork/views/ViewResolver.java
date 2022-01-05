package map.socialnetwork.views;

import java.net.URL;

public interface ViewResolver {

    static URL getView(String name) {
        return ViewResolver.class.getResource(name);
    }
}
