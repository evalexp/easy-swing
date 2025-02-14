import io.github.evalexp.Launcher;
import io.github.evalexp.annotations.*;
import io.github.evalexp.annotations.Container;
import io.github.evalexp.annotations.Frame;

import javax.swing.*;
import java.awt.*;

@Frame
public class TestFrame extends JFrame {
    public TestFrame() {}

    @I18N(key = "title")
    JLabel label;

    @Container
    io.github.evalexp.Container container;

    @Initializer
    public void setup() {
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(label, BorderLayout.CENTER);
        System.out.println(this.container);
        JLabel test = this.container.newComponent(JLabel.class, "title", "Hello");
        this.getContentPane().add(test, BorderLayout.SOUTH);
        this.setSize(800, 600);
        this.container.packComponent(this, "title");
    }

    public static void main(String[] args) {
        Launcher.launch(TestFrame.class);
    }

    @I18NMethod
    public void setText(String text) {
        this.setTitle(text);
    }
}
