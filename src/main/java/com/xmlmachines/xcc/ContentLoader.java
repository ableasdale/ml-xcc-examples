package com.xmlmachines.xcc;

import com.marklogic.xcc.Content;
import com.marklogic.xcc.ContentCreateOptions;
import com.marklogic.xcc.ContentFactory;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.*;

/**
 * <p>XCC Content Loader - uses XCC/J to insert one document per session</p>
 * User: ableasdale
 * Date: 6/14/14
 * Time: 1:00 PM
 */
public class ContentLoader {

    private static Logger LOG = LoggerFactory.getLogger(ContentLoader.class.getCanonicalName());

    public static void main(String[] args) {

        LOG.info("Application started.");
        ContentLoader cl = new ContentLoader();

        // Create Thread Pool and CompletionService
        ExecutorService es = Executors.newFixedThreadPool(Config.THREAD_POOL_SIZE);
        ExecutorCompletionService<Integer> completionService = new ExecutorCompletionService<Integer>(es);

        // Queue up 1000 Docs using CompletionService
        for (int i = 0; i < Config.TASKS; i++) {
            completionService.submit(cl.new XCCProcess());
        }

        // Wait on CompletionService
        for (int i = 0; i < Config.TASKS; ++i) {
            final Future<Integer> future;
            try {
                future = completionService.take();
                future.get();
            } catch (InterruptedException e) {
                LOG.error("Exception: ", e);
            } catch (ExecutionException e) {
                LOG.error("Exception: ", e);
            }
        }

        LOG.info("All queued tasks have completed.");
    }

    /**
     * <p>The XCC "Process" Thread</p>
     */
    public class XCCProcess implements Callable {

        public int saveXml() {

            Session session = XCCHelper.getInstance().getSession();
            ContentCreateOptions createOptions = new ContentCreateOptions();
            createOptions.setFormatXml();
            createOptions.setCollections(Config.COLLECTIONS);

            Content content = ContentFactory.newContent(String.format("/%s.xml", UUID.randomUUID()),
                    Config.XML_STRING, createOptions);
            try {
                session.insertContent(content);
            } catch (RequestException e) {
                LOG.error("Exception: ", e);
                return 0;
            }
            session.close();
            return 1;
        }

        public Object call() throws Exception {
            return saveXml();
        }
    }
}
