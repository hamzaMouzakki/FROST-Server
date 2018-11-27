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

import static de.fraunhofer.iosb.ilt.sta.settings.MqttSettings.Options.CREATE_MESSAGE_QUEUE_SIZE;
import static de.fraunhofer.iosb.ilt.sta.settings.MqttSettings.Options.CREATE_THREAD_POOL_SIZE;
import static de.fraunhofer.iosb.ilt.sta.settings.MqttSettings.Options.ENABLE_MQTT;
import static de.fraunhofer.iosb.ilt.sta.settings.MqttSettings.Options.HOST;
import static de.fraunhofer.iosb.ilt.sta.settings.MqttSettings.Options.HOST_INTERNAL;
import static de.fraunhofer.iosb.ilt.sta.settings.MqttSettings.Options.IMPLEMENTATION_CLASS;
import static de.fraunhofer.iosb.ilt.sta.settings.MqttSettings.Options.PORT;
import static de.fraunhofer.iosb.ilt.sta.settings.MqttSettings.Options.QOS_LEVEL;
import static de.fraunhofer.iosb.ilt.sta.settings.MqttSettings.Options.SUBSCRIBE_MESSAGE_QUEUE_SIZE;
import static de.fraunhofer.iosb.ilt.sta.settings.MqttSettings.Options.SUBSCRIBE_THREAD_POOL_SIZE;

/**
 *
 * @author jab
 */
public class MqttSettings {

    /**
     * Configuration options.
     */
    public static enum Options implements Setting {
        IMPLEMENTATION_CLASS("mqttServerImplementationClass", "de.fraunhofer.iosb.ilt.sensorthingsserver.mqtt.moquette.MoquetteMqttServer"),
        ENABLE_MQTT("Enabled", true),
        QOS_LEVEL("QoS", 2),
        PORT("Port", 1883),
        HOST("Host", "0.0.0.0"),
        HOST_INTERNAL("internalHost", "localhost"),
        SUBSCRIBE_MESSAGE_QUEUE_SIZE("SubscribeMessageQueueSize", 10),
        SUBSCRIBE_THREAD_POOL_SIZE("SubscribeThreadPoolSize", 10),
        CREATE_MESSAGE_QUEUE_SIZE("CreateMessageQueueSize", 10),
        CREATE_THREAD_POOL_SIZE("CreateThreadPoolSize", 5);

        public final String key;
        private final String defltString;
        private final Integer defltInt;
        private final Boolean defltBool;

        private Options(String key) {
            this.key = key;
            this.defltString = null;
            this.defltInt = null;
            this.defltBool = null;
        }

        private Options(String key, String defltString) {
            this.key = key;
            this.defltString = defltString;
            this.defltInt = null;
            this.defltBool = null;
        }

        private Options(String key, int defltInt) {
            this.key = key;
            this.defltString = null;
            this.defltInt = defltInt;
            this.defltBool = null;
        }

        private Options(String key, boolean defltBool) {
            this.key = key;
            this.defltString = null;
            this.defltInt = null;
            this.defltBool = defltBool;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getDefault() {
            if (defltString != null) {
                return defltString;
            } else if (defltInt != null) {
                return defltInt.toString();
            }
            throw new UnsupportedOperationException("This Setting has no default value.");
        }

        @Override
        public int getDefaultInt() {
            if (defltInt != null) {
                return defltInt;
            }
            return Setting.super.getDefaultInt();
        }

        @Override
        public boolean getDefaultBool() {
            if (defltBool != null) {
                return defltBool;
            }
            return Setting.super.getDefaultBool();
        }

    }

    /**
     * Constraints
     */
    public static final int MIN_PORT = 1025;
    public static final int MAX_PORT = 65535;
    public static final int MIN_QOS_LEVEL = 0;
    public static final int MAX_QOS_LEVEL = 2;

    private static final String MUST_BE_POSITIVE = " must be > 0";

    /**
     * Fully-qualified class name of the MqttServer implementation class
     */
    private String mqttServerImplementationClass;

    /**
     * Defines if MQTT should be enabled or not
     */
    private boolean enableMqtt;

    /**
     * The external IP address or host name the MQTT server should listen on.
     * Set to 0.0.0.0 to listen on all interfaces.
     */
    private String host;

    /**
     * The internal host name of the MQTT server.
     */
    private String internalHost;

    /**
     * The port used to run the MQTT server.
     */
    private int port;

    /**
     * A prefix used for all topics. By default, this we be the version number
     * of the service.
     */
    private String topicPrefix;

    /**
     * Quality of Service Level used to deliver MQTT messages
     */
    private int qosLevel;

    /**
     * Queue size for subscribe messages passed between PersistenceManager and
     * MqttManager
     */
    private int subscribeMessageQueueSize;
    /**
     * Number of threads used to process EntityChangeEvents
     */
    private int subscribeThreadPoolSize;
    /**
     * Queue size for create messages passed between PersistenceManager and
     * MqttManager
     */
    private int createMessageQueueSize;
    /**
     * Number of threads used to process ObservationCreateEvents
     */
    private int createThreadPoolSize;
    /**
     * Extension point for implementation specific settings
     */
    private Settings customSettings;

    public MqttSettings(Settings settings) {
        if (settings == null) {
            throw new IllegalArgumentException("settings most be non-null");
        }
        init(settings);
    }

    private void init(Settings settings) {
        mqttServerImplementationClass = settings.get(IMPLEMENTATION_CLASS.key, IMPLEMENTATION_CLASS.getDefault());
        enableMqtt = settings.getBoolean(ENABLE_MQTT.key, ENABLE_MQTT.getDefaultBool());
        port = settings.getInt(PORT.key, PORT.getDefaultInt());
        setHost(settings.get(HOST.key, HOST.getDefault()));
        setInternalHost(settings.get(HOST_INTERNAL.key, HOST_INTERNAL.getDefault()));
        setSubscribeMessageQueueSize(settings.getInt(SUBSCRIBE_MESSAGE_QUEUE_SIZE.key, SUBSCRIBE_MESSAGE_QUEUE_SIZE.getDefaultInt()));
        setSubscribeThreadPoolSize(settings.getInt(SUBSCRIBE_THREAD_POOL_SIZE.key, SUBSCRIBE_THREAD_POOL_SIZE.getDefaultInt()));
        setCreateMessageQueueSize(settings.getInt(CREATE_MESSAGE_QUEUE_SIZE.key, CREATE_MESSAGE_QUEUE_SIZE.getDefaultInt()));
        setCreateThreadPoolSize(settings.getInt(CREATE_THREAD_POOL_SIZE.key, CREATE_THREAD_POOL_SIZE.getDefaultInt()));
        setQosLevel(settings.getInt(QOS_LEVEL.key, QOS_LEVEL.getDefaultInt()));
        customSettings = settings;
    }

    public boolean isEnableMqtt() {
        return enableMqtt;
    }

    public int getPort() {
        return port;
    }

    public int getQosLevel() {
        return qosLevel;
    }

    public void setEnableMqtt(boolean enableMqtt) {
        this.enableMqtt = enableMqtt;
    }

    public void setQosLevel(int qosLevel) {
        if (qosLevel < MIN_QOS_LEVEL || qosLevel > MAX_QOS_LEVEL) {
            throw new IllegalArgumentException(QOS_LEVEL + " must be between " + MIN_QOS_LEVEL + " and " + MAX_QOS_LEVEL);
        }
        this.qosLevel = qosLevel;
    }

    /**
     * The external IP address or host name the MQTT server should listen on.
     * Set to 0.0.0.0 to listen on all interfaces.
     *
     * @return The external IP address or host name the MQTT server should
     * listen on.
     */
    public String getHost() {
        return host;
    }

    /**
     * The internal host name of the MQTT server.
     *
     * @return The internal host name of the MQTT server.
     */
    public String getInternalHost() {
        return internalHost;
    }

    public String getTopicPrefix() {
        return topicPrefix;
    }

    /**
     * The external IP address or host name the MQTT server should listen on.
     * Set to 0.0.0.0 to listen on all interfaces.
     *
     * @param host The external IP address or host name the MQTT server should
     * listen on.
     */
    public void setHost(String host) {
        if (host == null || host.isEmpty()) {
            throw new IllegalArgumentException(HOST + " must be non-empty");
        }
        this.host = host;
    }

    /**
     * The internal host name of the MQTT server.
     *
     * @param internalHost The internal host name of the MQTT server.
     */
    public void setInternalHost(String internalHost) {
        this.internalHost = internalHost;
    }

    public void setTopicPrefix(String topicPrefix) {
        this.topicPrefix = topicPrefix;
    }

    public int getSubscribeMessageQueueSize() {
        return subscribeMessageQueueSize;
    }

    public int getSubscribeThreadPoolSize() {
        return subscribeThreadPoolSize;
    }

    public void setSubscribeMessageQueueSize(int subscribeMessageQueueSize) {
        if (subscribeMessageQueueSize < 1) {
            throw new IllegalArgumentException(SUBSCRIBE_MESSAGE_QUEUE_SIZE + MUST_BE_POSITIVE);
        }
        this.subscribeMessageQueueSize = subscribeMessageQueueSize;
    }

    public void setSubscribeThreadPoolSize(int subscribeThreadPoolSize) {
        if (subscribeThreadPoolSize < 1) {
            throw new IllegalArgumentException(SUBSCRIBE_THREAD_POOL_SIZE + MUST_BE_POSITIVE);
        }
        this.subscribeThreadPoolSize = subscribeThreadPoolSize;
    }

    public String getMqttServerImplementationClass() {
        return mqttServerImplementationClass;
    }

    public void setMqttServerImplementationClass(String mqttServerImplementationClass) {
        if (mqttServerImplementationClass == null || mqttServerImplementationClass.isEmpty()) {
            throw new IllegalArgumentException(IMPLEMENTATION_CLASS + " must be non-empty");
        }
        try {
            Class.forName(mqttServerImplementationClass, false, this.getClass().getClassLoader());
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(IMPLEMENTATION_CLASS + " '" + mqttServerImplementationClass + "' could not be found", ex);
        }
        this.mqttServerImplementationClass = mqttServerImplementationClass;
    }

    public Settings getCustomSettings() {
        return customSettings;
    }

    public int getCreateMessageQueueSize() {
        return createMessageQueueSize;
    }

    public int getCreateThreadPoolSize() {
        return createThreadPoolSize;
    }

    public void setCreateMessageQueueSize(int createMessageQueueSize) {
        if (createMessageQueueSize < 1) {
            throw new IllegalArgumentException(CREATE_MESSAGE_QUEUE_SIZE + MUST_BE_POSITIVE);
        }
        this.createMessageQueueSize = createMessageQueueSize;
    }

    public void setCreateThreadPoolSize(int createThreadPoolSize) {
        if (createThreadPoolSize < 1) {
            throw new IllegalArgumentException(CREATE_THREAD_POOL_SIZE + MUST_BE_POSITIVE);
        }
        this.createThreadPoolSize = createThreadPoolSize;
    }

}
