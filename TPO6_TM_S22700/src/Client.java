import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;

public class Client {

    private static String username;
    private static String message;
    private static GUI gui;
    private static Topic topic;
    private static TopicConnection con;

    public Client(String username, GUI gui) {
        this.gui = gui;
        Client.username = username;
        initialize();
    }

    private static void subscribe(TopicConnection con, Topic topic) throws JMSException {
        TopicSession ses = con.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        TopicSubscriber sub = ses.createSubscriber(topic);
        sub.setMessageListener(message -> {
            try {
                TextMessage msg = (TextMessage) message;
                System.out.println(msg.getText());
                gui.getMessage(msg.getText());
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void publish(TopicConnection con, Topic topic) throws JMSException {
        TopicSession ses = con.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        TopicPublisher pub = ses.createPublisher(topic);
        con.start();
        TextMessage msg = ses.createTextMessage();
        msg.setText(message);
        msg.setStringProperty("username", username);
        pub.publish(msg);
    }

    public void sendMessage(String text) {
        String firstWord = text.split(" ")[0];
        if (firstWord.equals("1")) {
            message = "A new user has connected: " + username;
        } else if (firstWord.equals("2")) {
            message = username + " has reconnected";
        } else {
            Client.message = username + ": " + text;
        }
        try {
            publish(con, topic);
        } catch (JMSException e) {
            gui.getMessage("Server has disconnected");
            gui.setButtonSendEnabled(false);
            gui.setButtonEnabled(true);
        } catch (NullPointerException e) {
            gui.getMessage("Unable to connect to server");
            gui.setButtonSendEnabled(false);
            gui.setButtonEnabled(true);
        }
    }

    public void initialize() {
        Hashtable env = new Hashtable(11);
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory");
        env.put(Context.PROVIDER_URL, "tcp://localhost:3035");

        try {
            Context ctx = new InitialContext(env);
            topic = (Topic) ctx.lookup("topic1");
            TopicConnectionFactory fact = (TopicConnectionFactory) ctx.lookup("ConnectionFactory");
            con = fact.createTopicConnection();
            subscribe(con, topic);
        } catch (JMSException | NamingException | NullPointerException e) {
            gui.setButtonEnabled(true);
            gui.setButtonSendEnabled(false);
        }
    }

}
