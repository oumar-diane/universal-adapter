package org.zenithblox.container;

public class ContainerEvent {

    public static final String PROJECT_SAVED = "PROJECT_SAVED";
    public static final String PROJECT_DELETED = "PROJECT_DELETED";
    public static final String PROJECT_FILE_SAVED = "PROJECT_FILE_SAVED";
    public static final String PROJECT_FILE_DELETED = "PROJECT_FILE_DELETED";

    public static final String CMD_PUSH_PROJECT = "CMD_PUSH_PROJECT";

    public static final String NOTIFICATION_PROJECTS_STARTED = "NOTIFICATION_PROJECTS_STARTED";
    public static final String COMMIT_HAPPENED = "COMMIT_HAPPENED";
    public static final String NOTIFICATION_IMAGES_LOADED = "NOTIFICATION_IMAGES_LOADED";

    public static final String CMD_SHARE_CONFIGURATION = "CMD_SHARE_CONFIGURATION";
    public static final String NOTIFICATION_CONFIG_SHARED = "NOTIFICATION_CONFIG_SHARED";

    public static final String NOTIFICATION_ERROR = "NOTIFICATION_ERROR";

    public static final String CMD_COLLECT_CAMEL_STATUS = "CMD_COLLECT_CAMEL_STATUS";
    public static final String CMD_COLLECT_CONTAINER_STATISTIC = "CMD_COLLECT_CONTAINER_STATISTIC";
    public static final String CMD_CLEAN_STATUSES = "CMD_CLEAN_STATUSES";
    public static final String CMD_PULL_IMAGES = "CMD_PULL_IMAGES";

    public static final String CMD_RELOAD_PROJECT_CODE = "CMD_RELOAD_PROJECT_CODE";
    public static final String CMD_DELETE_CONTAINER = "CMD_DELETE_CONTAINER";

    public static final String CMD_RESTART_INFORMERS = "CMD_RESTART_INFORMERS";

    public static final String POD_CONTAINER_UPDATED = "POD_CONTAINER_UPDATED";
    public static final String POD_CONTAINER_DELETED = "POD_CONTAINER_DELETED";

    public static final String DEPLOYMENT_UPDATED = "DEPLOYMENT_UPDATED";
    public static final String DEPLOYMENT_DELETED = "DEPLOYMENT_DELETED";

    public static final String SERVICE_UPDATED = "SERVICE_UPDATED";
    public static final String SERVICE_DELETED = "SERVICE_DELETED";
}
