package com.whereismytransport.resthook.client;
import java.util.List;

/**
 * Created by Nick Cuthbert on 25/04/2016.
 */
public interface RestHookRepository {
    /**
     * @return whether the repository was successfully initialized
     */
    boolean initialize(List<String> logs, List<String> messageBodies);
    boolean addOrReplaceRestHook(RestHook hook);
    List<RestHook> getRestHooks();
}