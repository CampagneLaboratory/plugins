package org.campagnelab.gobyweb.plugins;

import org.campagnelab.gobyweb.plugins.xml.Config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *  The plugin registry
 *  @author manuele
 *
 */
public class PluginRegistry extends ArrayList<Config> {

    /**
     * singleton instance of the registry created at startup time
     */
    private final static PluginRegistry instance = new PluginRegistry();

    private PluginRegistry() {
        super();
    }

    private PluginRegistry(int i) {
        super(i);
    }

    private PluginRegistry(Collection<? extends Config> configs) {
        super(configs);
    }

    /**
     * Gets the registry
     * @return the plugin registry
     */
    public static PluginRegistry getRegistry() {
        return instance;
    }

    /**
     * Gets the list of configurations for a given plugin type
     * @param configClass the type of configurations
     * @param <T> the class to filter for
     * @return the list of configurations for the plugin type loaded from the disk
     */
    public <T extends Config> List<T> filterConfigs(Class<T> configClass) {
        List<T> returnedList = new ArrayList<T>();
        for (Config p : this) {
            if ( (p.getClass().isAssignableFrom(configClass) //same class
                || (configClass.isInstance(p))))             //or a sub-class
                returnedList.add((T) p);
        }
        return returnedList;
    }

    /**
     * Returns the configuration matching id or null if the configuration was not found.
     * @param idToFind
     * @return the configuration that matches or null
     */
    public Config findById(String idToFind) {
        if (idToFind != null) {
            for (Config config: this) {
                if (config.getId().compareTo(idToFind)==0) {
                    return config;
                }
            }
        }
        return null;
    }

    /**
     * Returns all the configurations matching id or an empty list if any configuration was not found.
     * @param idToFind
     * @return the configurations that match or null
     */
    public List<Config> findAllById(String idToFind) {
        List<Config> returnedList = new ArrayList<Config>();
        if (idToFind != null) {
            for (Config config: this) {
                if (config.getId().compareTo(idToFind)==0) {
                    returnedList.add(config);
                }
            }
        }
        return returnedList;
    }

    /**
     * Returns the configuration matching id or null if the configuration was not found.
     * @param idToFind
     * @param configClass the type of configurations
     * @param <T> the class to filter for
     * @return the configuration that matches or null
     */
    public <T extends Config> T findByTypedId(String idToFind, Class<T> configClass ) {
        if (idToFind != null) {
            for (Config config: this) {
                if ((config.getId().compareTo(idToFind)==0)
                    && ((config.getClass().isAssignableFrom(configClass)) //same class
                        ||(configClass.isInstance(config)))){  //or a sub-class
                    return (T)config;
                }
            }
        }
        return null;
    }


    /**
     * Return the configuration with largest version number, such that the resource has the identifier and at least the specified
     * version number.
     * @param resourceId
     * @param versionAtLeast required version
     * @param versionExactly required exact this version
     * @return Most recent resource (by version number) with id and version>v
     */
//    public <T extends SupportAtLeastVersionDependency> T lookup(String resourceId, String versionAtLeast, String versionExactly, Class<T> configClass) {
//        List<T> configList = new ArrayList<T>();
//        for (T resource :  this.filterConfigs(configClass.class)) {
//            if (versionExactly != null) {
//                if (resource.getId().equalsIgnoreCase(resourceId) &&
//                        resource.exactlyVersion(versionExactly))
//                    configList.add(resource);
//            } else if (versionAtLeast != null) {
//                if (resource.getId().equalsIgnoreCase(resourceId) &&
//                        resource.atLeastVersion(versionAtLeast))
//                    configList.add(resource);
//            }
//        }
//        if (configList.size() > 0) {
//            Collections.sort(configList);
//            return configList.get(0);
//        } else
//            return null;
//    }
}
