package com.xmlmachines.xcc;

import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.ContentSourceFactory;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.XccConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <p>XCC Content Source Helper Class</p>
 * User: ableasdale
 * Date: 6/15/14
 * Time: 6:07 AM
 */
public class XCCHelper {

    private static Logger LOG = LoggerFactory.getLogger(XCCHelper.class.getCanonicalName());
    private static Random random = new Random();
    private List<ContentSource> contentSources;

    private XCCHelper() {
        contentSources = getContentSources();
    }

    private static class LazyHolder {
        private static final XCCHelper INSTANCE = new XCCHelper();
    }

    private List<ContentSource> getContentSources() {
        try {
            contentSources = new ArrayList<ContentSource>();
            for (String s : Config.URIS) {
                LOG.info(String.format("Adding XCC URI for node: %s", s));
                contentSources.add(ContentSourceFactory.newContentSource(new URI(s)));
            }
        } catch (XccConfigException e) {
            LOG.error("Exception: ", e);
        } catch (URISyntaxException e) {
            LOG.error("Exception: ", e);
        }
        return contentSources;
    }

    public static XCCHelper getInstance() {
        return LazyHolder.INSTANCE;
    }

    public Session getSession() {
        return contentSources.get(random.nextInt(contentSources.size())).newSession();
    }

}
