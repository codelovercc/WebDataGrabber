import com.cool.config.RootConfig;
import com.cool.grabber.GrabberClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by codelover on 18/3/7.
 */
public class Application {
    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(RootConfig.class);
        GrabberClient client = context.getBean(GrabberClient.class);
        client.startGrabberBack();
    }
}
