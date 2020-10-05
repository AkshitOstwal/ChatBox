package chatbox;
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 *
 * @author Akshit Ostwal
 */
public class SimpleChatClient {
    JTextArea incoming;
    JTextField outgoing;
    BufferedReader reader;
    PrintWriter writer;
    Socket sock;
    
    public static void main(String[] args){
        SimpleChatClient client =new SimpleChatClient();
        client.go();
        
    }
    public void go(){
        JFrame frame =new JFrame("Chat Messenger");
        JPanel panel =new JPanel();
        incoming =new JTextArea(15,40);
        incoming.setWrapStyleWord(true);
        incoming.setLineWrap(true);
        incoming.setEditable(false);
        JScrollPane scroller =new JScrollPane(incoming);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        outgoing =new JTextField(20);
        JButton sendButton =new JButton("send");
        sendButton.addActionListener(new SendButtonListener());//adding listener
        panel.add(scroller);
        panel.add(outgoing);
        panel.add(sendButton);
        frame.getContentPane().add(BorderLayout.CENTER,panel);
        setUpNetworking();
        
        Thread readerThread =new Thread(new IncomingReader());//starting another thread to read from server
        readerThread.start();
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,500);
        frame.setVisible(true);
    }

    private void setUpNetworking() {
        try{
            sock =new Socket("127.0.0.1",5000);
            InputStreamReader streamReader=new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("network established");
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public class IncomingReader implements Runnable {

        public IncomingReader() {
        }

        @Override
        public void run() {
            String message;
            try{
                while((message=reader.readLine())!=null){
                    System.out.println("read "+message);
                   // incoming.setText(message);
                    incoming.append(message + "\n");
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public class SendButtonListener implements ActionListener {

        public SendButtonListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
         try{
             writer.println(outgoing.getText());
             writer.flush();
         }   catch(Exception ex){
             ex.printStackTrace();
         }
         outgoing.setText("");
         outgoing.requestFocus();
        }
    }
}
