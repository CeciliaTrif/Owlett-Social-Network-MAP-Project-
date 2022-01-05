package map.socialnetwork.controller;

import java.util.Map;

public interface InitializableController {

    /**
     *
     * @param parameters is a map representing the object we want to send over to the view controllers.
     * For example, we have the MainController that needs an userId then when create a map having and entry
     * with the key "userId" and the value the user's id as a Long. (Map<KEY, VALUE> parameters)
     */
    void initializeData(Map<String, Object> parameters);
}
