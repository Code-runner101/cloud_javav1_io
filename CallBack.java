import lombok.extern.slf4j.Slf4j;
import model.Message;

@FunctionalInterface
public interface CallBack {
    void call(String arg);

}
