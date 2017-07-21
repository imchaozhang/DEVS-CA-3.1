package view.CAView;


import java.io.IOException;
import java.io.OutputStream;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class JXConsoleComponent extends OutputStream
{
    private TextArea    output;

    public JXConsoleComponent(TextArea ta)
    {
        this.output = ta;
    }

    @Override
    public void write(final int i) throws IOException {
        Platform.runLater(new Runnable() {
            public void run() {
                output.appendText(String.valueOf((char) i));
            }
        });
    }

}