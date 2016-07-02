package matching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by josec on 6/27/2016.
 */
public class CustomLogger {
    private final Logger log;

    public CustomLogger(Class<?> baseClass) {
        log  = LoggerFactory.getLogger(baseClass);
    }

    public void info(String stlog){
        log.info(stlog);
    }
}
