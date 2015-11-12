
import java.awt.BorderLayout;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javafx.application.Platform;
import javax.swing.JTextField;
import org.tweetwallfx.embeddable.EmbeddedTweetwall;

public class Main {

    private static EmbeddedTweetwall tweetwall;
    private static JTextField queryTextfield;

    public static void main(String[] args) {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        queryTextfield = new JTextField("#javaone");
        tweetwall = new EmbeddedTweetwall();

        final JFXPanel p = new JFXPanel();
        p.setScene(new Scene(new BorderPane(tweetwall)));

        queryTextfield.addActionListener(e -> {
            stop();
            start(queryTextfield.getText());
        });

        panel.add(queryTextfield, BorderLayout.NORTH);
        panel.add(p, BorderLayout.CENTER);

        JFrame jFrame = new JFrame("Hi there");
        jFrame.setContentPane(panel);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(1024, 768);
        jFrame.setVisible(true);
    }

    private static void start(final String query) {
        System.out.println(query);
        Platform.runLater(() -> tweetwall.start(query));
    }

    private static void stop() {
        tweetwall.stop();
    }
}
