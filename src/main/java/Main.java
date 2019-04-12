
import datamanager.ItemManager;
import datamanager.UserLogManager;
import datastruct.Item;
import datastruct.Pair;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.ServerConnector;
import recommender.BasicRecommender;
import recommender.ContentBasedRecommender;
import recommender.RecommendAbstract;
import utils.MyParser;
import io.sql.DBConnection;
import nlp.DictionaryManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import datamanager.helper.ContentSimilarManager;
import config.Resources;

import java.sql.SQLException;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j2.properties");
        LOGGER.info("Start");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    DBConnection.getInstance().getConnection().close();
                    LOGGER.info("close sql connection in shutdown hook");
                } catch (SQLException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        });

        Integer port = MyParser.parseInteger(Resources.getInstance().getProperty("api.port"));
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        if (port == null) {
            LOGGER.error("Port service is null");
            return;
        }
        DictionaryManager.init();
        Integer numDateKeepItem = MyParser.parseInteger(Resources.getInstance().getProperty("item.num_date_keep"));
        if (numDateKeepItem != null)
            ItemManager.init(numDateKeepItem);
        ItemManager itemManager = ItemManager.getInstance();
        itemManager.startAutoReload();
        UserLogManager logManager = UserLogManager.getInstance();
        logManager.startAutoReload();
        ContentSimilarManager.init(itemManager);

        // init both recommender for testing
        BasicRecommender.init(itemManager, logManager);
        ContentBasedRecommender.init(itemManager, logManager);

        // init resources to start server
        ResourceConfig config = new ResourceConfig();
        config.packages("api.handle");
        ServletHolder servlet = new ServletHolder(new ServletContainer(config));

        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(server, "/*");
        context.addServlet(servlet, "/*");

//        HttpConfiguration http_config = new HttpConfiguration();
////        http_config.setOutputBufferSize(1000);
//        ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config));
//        http.setIdleTimeout(3000);
//        http.setAcceptQueueSize(100);
//        http.setPort(1233);
//        http.
//        server.addConnector(http);

        try {
            server.start();
            LOGGER.info("Started server on port: " + port);
            server.join();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        } finally {
            server.destroy();
            try {
                DBConnection.getInstance().getConnection().close();
                LOGGER.info("close sql connection");
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }
}
