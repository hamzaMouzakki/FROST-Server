/*
 * Copyright (C) 2016 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fraunhofer.iosb.ilt.sta.settings;

import de.fraunhofer.iosb.ilt.sta.util.LiquibaseUser;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author scf
 */
public class CoreSettings {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreSettings.class);

    /**
     * Tags
     */
    public static final String TAG_API_VERSION = "ApiVersion";
    public static final String TAG_DEFAULT_COUNT = "defaultCount";
    public static final String TAG_DEFAULT_TOP = "defaultTop";
    public static final String TAG_MAX_TOP = "maxTop";
    public static final String TAG_MAX_DATASIZE = "maxDataSize";
    public static final String TAG_SERVICE_ROOT_URL = "serviceRootUrl";
    public static final String TAG_USE_ABSOLUTE_NAVIGATION_LINKS = "useAbsoluteNavigationLinks";
    public static final String TAG_TEMP_PATH = "tempPath";
    /**
     * Used when passing CoreSettings in a map.
     */
    public static final String TAG_CORE_SETTINGS = "CoreSettings";

    // HTTP Tags
    public static final String TAG_CORS_ENABLE = "cors.enable";
    public static final String TAG_CORS_ALLOWED_ORIGINS = "cors.allowed.origins";
    public static final String TAG_CORS_ALLOWED_METHODS = "cors.allowed.methods";
    public static final String TAG_CORS_EXPOSED_HEADERS = "cors.exposed.headers";
    public static final String TAG_CORS_ALLOWED_HEADERS = "cors.allowed.headers";
    public static final String TAG_CORS_SUPPORT_CREDENTIALS = "cors.support.credentials";
    public static final String TAG_CORS_PREFLIGHT_MAXAGE = "cors.preflight.maxage";
    public static final String TAG_CORS_REQUEST_DECORATE = "cors.request.decorate";

    // Auth Tags
    public static final String TAG_AUTH_PROVIDER = "provider";
    public static final String TAG_AUTH_ALLOW_ANON_READ = "allowAnonymousRead";
    public static final String TAG_AUTH_ROLE_READ = "role.read";
    public static final String TAG_AUTH_ROLE_CREATE = "role.create";
    public static final String TAG_AUTH_ROLE_UPDATE = "role.update";
    public static final String TAG_AUTH_ROLE_DELETE = "role.delete";
    public static final String TAG_AUTH_ROLE_ADMIN = "role.admin";

    /**
     * Defaults
     */
    public static final String DEFAULT_API_VERSION = "v1.0";
    public static final int DEFAULT_MAX_TOP = 100;
    public static final long DEFAULT_MAX_DATASIZE = 25000000;
    public static final boolean DEFAULT_COUNT = true;
    public static final boolean DEFAULT_USE_ABSOLUTE_NAV_LINKS = true;
    // HTTP Defaults
    public static final boolean DEFAULT_CORS_ENABLE = false;
    public static final String DEFAULT_CORS_ALLOWED_ORIGINS = "*";
    public static final String DEFAULT_CORS_ALLOWED_METHODS = "GET,HEAD,OPTIONS";
    public static final String DEFAULT_CORS_EXPOSED_HEADERS = "Location";
    public static final String DEFAULT_CORS_ALLOWED_HEADERS = "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization";
    public static final String DEFAULT_CORS_SUPPORT_CREDENTIALS = "false";
    public static final String DEFAULT_CORS_PREFLIGHT_MAXAGE = "1800";
    public static final String DEFAULT_CORS_REQUEST_DECORATE = "true";
    // Auth Defaults
    public static final boolean DEF_AUTH_ALLOW_ANON_READ = false;
    public static final String DEF_AUTH_ROLE_READ = "read";
    public static final String DEF_AUTH_ROLE_CREATE = "create";
    public static final String DEF_AUTH_ROLE_UPDATE = "update";
    public static final String DEF_AUTH_ROLE_DELETE = "delete";
    public static final String DEF_AUTH_ROLE_ADMIN = "admin";

    /**
     * Prefixes
     */
    public static final String PREFIX_BUS = "bus.";
    public static final String PREFIX_MQTT = "mqtt.";
    public static final String PREFIX_HTTP = "http.";
    public static final String PREFIX_AUTH = "auth.";
    public static final String PREFIX_PERSISTENCE = "persistence.";

    /**
     * Root URL of the service to run.
     */
    private String serviceRootUrl;

    /**
     * API Version.
     */
    private String apiVersion = DEFAULT_API_VERSION;

    /**
     * Root URL of the service to run.
     */
    private boolean useAbsoluteNavigationLinks = DEFAULT_USE_ABSOLUTE_NAV_LINKS;

    /**
     * The default top to use when no specific top is set.
     */
    private int topDefault = DEFAULT_MAX_TOP;
    /**
     * The maximum allowed top.
     */
    private int topMax = DEFAULT_MAX_TOP;
    /**
     * The maximum data size.
     */
    private long dataSizeMax = DEFAULT_MAX_DATASIZE;
    /**
     * The default count to use when no specific count is set.
     */
    private boolean countDefault = DEFAULT_COUNT;
    /**
     * Path to temp folder.
     */
    private String tempPath;
    /**
     * The MQTT settings to use.
     */
    private MqttSettings mqttSettings;
    /**
     * The message bus settings to use.
     */
    private BusSettings busSettings;
    /**
     * The Persistence settings to use.
     */
    private PersistenceSettings persistenceSettings;
    /**
     * The HTTP settings to use.
     */
    private Settings httpSettings;
    /**
     * The HTTP settings to use.
     */
    private Settings authSettings;

    private Set<Class<? extends LiquibaseUser>> liquibaseUsers = new LinkedHashSet<>();

    /**
     * Creates an empty, uninitialised CoreSettings.
     */
    public CoreSettings() {
        // Nothing here
    }

    /**
     * Creates a new CoreSettings and initialises it with the given properties,
     * and environment variables.
     *
     * @param properties The properties to use. Environment variables can
     * override these.
     */
    public CoreSettings(Properties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("properties must be non-null");
        }
        init(properties);
    }

    private void init(Properties properties) {
        Settings settings = new Settings(properties);
        if (!settings.containsName(TAG_SERVICE_ROOT_URL)) {
            throw new IllegalArgumentException(getClass().getName() + " must contain property '" + TAG_SERVICE_ROOT_URL + "'");
        }
        if (!settings.containsName(TAG_TEMP_PATH)) {
            throw new IllegalArgumentException(getClass().getName() + " must contain property '" + TAG_TEMP_PATH + "'");
        }
        tempPath = settings.get(TAG_TEMP_PATH);
        if (tempPath == null || tempPath.isEmpty()) {
            throw new IllegalArgumentException("tempPath must be non-empty");
        }
        try {
            if (!Paths.get(tempPath).toRealPath(LinkOption.NOFOLLOW_LINKS).toFile().exists()) {
                throw new IllegalArgumentException("tempPath '" + tempPath + "' does not exist");
            }
        } catch (IOException exc) {
            LOGGER.error("Failed to find tempPath: {}.", tempPath);
            throw new IllegalArgumentException("tempPath '" + tempPath + "' does not exist", exc);
        }
        apiVersion = settings.get(TAG_API_VERSION, DEFAULT_API_VERSION);
        serviceRootUrl = URI.create(settings.get(CoreSettings.TAG_SERVICE_ROOT_URL) + "/" + apiVersion).normalize().toString();
        useAbsoluteNavigationLinks = settings.getBoolean(TAG_USE_ABSOLUTE_NAVIGATION_LINKS, DEFAULT_USE_ABSOLUTE_NAV_LINKS);
        countDefault = settings.getBoolean(TAG_DEFAULT_COUNT, DEFAULT_COUNT);
        topDefault = settings.getInt(TAG_DEFAULT_TOP, DEFAULT_MAX_TOP);
        topMax = settings.getInt(TAG_MAX_TOP, DEFAULT_MAX_TOP);
        dataSizeMax = settings.getLong(TAG_MAX_DATASIZE, DEFAULT_MAX_DATASIZE);

        mqttSettings = new MqttSettings(new Settings(settings.getProperties(), PREFIX_MQTT, false));
        persistenceSettings = new PersistenceSettings(new Settings(settings.getProperties(), PREFIX_PERSISTENCE, false));
        busSettings = new BusSettings(new Settings(settings.getProperties(), PREFIX_BUS, false));
        httpSettings = new Settings(settings.getProperties(), PREFIX_HTTP, false);
        authSettings = new Settings(settings.getProperties(), PREFIX_AUTH, false);
        if (mqttSettings.getTopicPrefix() == null || mqttSettings.getTopicPrefix().isEmpty()) {
            mqttSettings.setTopicPrefix(apiVersion + "/");
        }
    }

    public static CoreSettings load(String file) {
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(Paths.get(file, (String) null))) {
            properties.load(reader);
        } catch (IOException ex) {
            LOGGER.error("error loading properties file, using defaults", ex);
        }
        return load(properties);
    }

    public static CoreSettings load(Properties properties) {
        return new CoreSettings(properties);
    }

    public BusSettings getBusSettings() {
        return busSettings;
    }

    public MqttSettings getMqttSettings() {
        return mqttSettings;
    }

    public Settings getHttpSettings() {
        return httpSettings;
    }

    public Settings getAuthSettings() {
        return authSettings;
    }

    public PersistenceSettings getPersistenceSettings() {
        return persistenceSettings;
    }

    public String getServiceRootUrl() {
        return serviceRootUrl;
    }

    public boolean isUseAbsoluteNavigationLinks() {
        return useAbsoluteNavigationLinks;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getTempPath() {
        return tempPath;
    }

    /**
     * The default top to use when no specific top is set.
     *
     * @return the topDefault
     */
    public int getTopDefault() {
        return topDefault;
    }

    /**
     * The default top to use when no specific top is set.
     *
     * @param topDefault the topDefault to set
     */
    public void setTopDefault(int topDefault) {
        this.topDefault = topDefault;
    }

    /**
     * The maximum allowed top.
     *
     * @return the topMax
     */
    public int getTopMax() {
        return topMax;
    }

    /**
     * The maximum allowed top.
     *
     * @param topMax the topMax to set
     */
    public void setTopMax(int topMax) {
        this.topMax = topMax;
    }

    /**
     * The maximum allowed data size to return in a single query. This uses a
     * very coarse estimation of the size, using only the fields with an
     * unbounded size, such as Observation.result and Thing.properties.
     *
     * @return The maximum result data size.
     */
    public long getDataSizeMax() {
        return dataSizeMax;
    }

    /**
     * The maximum allowed data size to return in a single query. This uses a
     * very coarse estimation of the size, using only the fields with an
     * unbounded size, such as Observation.result and Thing.properties.
     *
     * @param dataSizeMax The maximum result data size.
     */
    public void setDataSizeMax(long dataSizeMax) {
        this.dataSizeMax = dataSizeMax;
    }

    /**
     * The default count to use when no specific count is set.
     *
     * @return the countDefault
     */
    public boolean isCountDefault() {
        return countDefault;
    }

    /**
     * The default count to use when no specific count is set.
     *
     * @param countDefault the countDefault to set
     */
    public void setCountDefault(boolean countDefault) {
        this.countDefault = countDefault;
    }

    /**
     * Get the unmodifiable list of liquibase users.
     *
     * @return the unmodifiable list of liquibase users.
     */
    public Set<Class<? extends LiquibaseUser>> getLiquibaseUsers() {
        return Collections.unmodifiableSet(liquibaseUsers);
    }

    /**
     * Register a new liquibaseUser that wants to be called when it is time to
     * upgrade the database.
     *
     * @param liquibaseUser
     */
    public void addLiquibaseUser(Class<? extends LiquibaseUser> liquibaseUser) {
        liquibaseUsers.add(liquibaseUser);
    }
}
