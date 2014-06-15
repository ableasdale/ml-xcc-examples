package com.xmlmachines.xcc;

/**
 * <p>All configurable values</p>
 * User: ableasdale
 * Date: 6/15/14
 * Time: 5:32 AM
 */
public class Config {

    protected static final int BATCH_SIZE = 50;
    protected static final int BATCH_TASKS = 20;
    protected static final int TASKS = 1000;
    protected static final int THREAD_POOL_SIZE = 50;

    protected static String XML_STRING = "<test>Test XML Content string</test>";
    protected static String[] COLLECTIONS = {"test-collection", "another-test-collection"};

    /**
     * Content Sources for all available XDBC app servers (nodes) in the cluster
     */
    protected static final String[] URIS = {
            "xcc://[user]:[password]@[host-1-name]:[port]/[database]",
            "xcc://[user]:[password]@[host-2-name]:[port]/[database]",
            "xcc://[user]:[password]@[host-3-name]:[port]/[database]"
    };

}
