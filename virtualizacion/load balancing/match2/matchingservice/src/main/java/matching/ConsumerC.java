package matching;

/**
 * Created by josec on 6/27/2016.
 */
public class ConsumerC {

    CustomLogger customLogger = new CustomLogger(this.getClass());

    public ConsumerC() {
    }

    public void logear(String stlog){
        customLogger.info(stlog);
    }
}
