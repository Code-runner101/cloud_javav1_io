import model.Message;

import java.io.DataInputStream;

public class ReadHandler implements Runnable{

    private final DataInputStream is;
    private final CallBack callBack;

    public ReadHandler(DataInputStream is,
                       CallBack callBack) {
        this.is = is;
        this.callBack = callBack;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = is.readUTF();
                 callBack.call(message);
            }
        } catch (Exception e) {
            System.err.println("Exception while read");
        }
    }

}
