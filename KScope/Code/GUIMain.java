package KScope;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GUIMain {
	
	public static void main(String[] args){
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		
		panel.setLayout(new BorderLayout());
		
		frame.add(panel);
		frame.setSize(500, 500);
		frame.setVisible(true);
		
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener( new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
            	
            	
                JFrame frame = (JFrame)e.getSource();
         
                int result = JOptionPane.showConfirmDialog(
                    frame,
                    "Are you sure you want to exit the application?",
                    "Exit Application",
                    JOptionPane.YES_NO_OPTION);
         
                if (result == JOptionPane.YES_OPTION){
                	frame.dispose();
                	System.gc();
                	System.out.println("X has been clicked");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
            }
        });
		
	}

}
