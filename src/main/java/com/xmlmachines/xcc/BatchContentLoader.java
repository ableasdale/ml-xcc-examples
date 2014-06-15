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
 * <p>XCC "Batch" Content Loader - uses XCC/J to insert multiple documents per session - configure with com.xmlmachines.xcc.Config.BATCH_SIZE</p>
 * User: ableasdale
 * Date: 6/14/14
 * Time: 2:28 PM
 */
public class BatchContentLoader {

    private static Logger LOG = LoggerFactory.getLogger(BatchContentLoader.class.getCanonicalName());

    public static void main(String[] args) {
        LOG.info("Application started.");
        BatchContentLoader bcl = new BatchContentLoader();

        // Create Thread Pool and CompletionService
        ExecutorService es = Executors.newFixedThreadPool(Config.THREAD_POOL_SIZE);
        ExecutorCompletionService<Integer> completionService = new ExecutorCompletionService<Integer>(es);

        // Queue up 1000 Docs (20 batches of 50 inserts) using CompletionService
        for (int i = 0; i < Config.BATCH_TASKS; i++) {
            completionService.submit(bcl.new XCCProcess());
        }

        // Wait on CompletionService
        for (int i = 0; i < Config.BATCH_TASKS; ++i) {
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
     * <p>The "Process" Thread</p>
     */
    public class XCCProcess implements Callable {

        public int saveXml() {

            Session session = XCCHelper.getInstance().getSession();
            // LOG.info(String.format("Connection URI: %s", session.getConnectionUri().toString()));

            ContentCreateOptions createOptions = new ContentCreateOptions();
            createOptions.setFormatXml();
            createOptions.setCollections(Config.COLLECTIONS);
            Content[] contentArray = new Content[Config.BATCH_SIZE];

            for (int i = 0; i < Config.BATCH_SIZE; i++) {
                contentArray[i] = ContentFactory.newContent(String.format("/%s.xml", UUID.randomUUID()), Config.XML_STRING, createOptions);
            }

            try {
                session.insertContent(contentArray);
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