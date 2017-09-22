package KScope.Code;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class KSCOPEGUI {
	
	public static void main(String[] args){
		//  make the components
		JFrame frame = new JFrame("KSCOPE frame");
		JPanel panel = new JPanel();
		JPanel northPanel = new JPanel();
		JPanel northEastPanel = new JPanel();
		JPanel northWestPanel = new JPanel();
		JPanel southPanel = new JPanel();
		JLabel kmerLabel = new JLabel("Kmer Count");
		JLabel threadLabel = new JLabel("Thread Count");
		JCheckBox fastaToFeatureCheckBox = new JCheckBox();
		JTextField kmerCount = new JTextField(20);
		JTextField trainIn = new JTextField(20);
		JTextField testIn = new JTextField(20);
		JTextField pcaIn = new JTextField(20);
		JTextField threadCount = new JTextField(20);
		JTextField outFileName = new JTextField(20);
		JButton trainButton = new JButton("Train File");
		JButton testButton = new JButton("Test File");
		JButton pcaButton = new JButton("PCA File");
		JButton goButton = new JButton("GO");
		Console console = new Console();
		JScrollPane scroll = new JScrollPane(console);
		PrintStream out = new PrintStream(new TextAreaOutputStream(console));
		
		trainButton.setEnabled(true);
//		trainButton.setSize(150, 25);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		//  button action listeners
		trainButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser choosed = new JFileChooser(); 
                File workingDirectory = new File(System.getProperty("user.dir"));
                choosed.setCurrentDirectory(workingDirectory);
                int returnValue = choosed.showOpenDialog(null); 
                if (returnValue == JFileChooser.APPROVE_OPTION) { 
                     trainIn.setText(choosed.getSelectedFile().getPath().replace("\\", "\\\\")); 
                } 
			}
			
		});
		
		testButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser choosed = new JFileChooser(); 
                File workingDirectory = new File(System.getProperty("user.dir"));
                choosed.setCurrentDirectory(workingDirectory);
                int returnValue = choosed.showOpenDialog(null); 
                if (returnValue == JFileChooser.APPROVE_OPTION) { 
                     testIn.setText(choosed.getSelectedFile().getPath().replace("\\", "\\\\")); 
                } 
			}
			
		});
		
		pcaButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser choosed = new JFileChooser(); 
                File workingDirectory = new File(System.getProperty("user.dir"));
                choosed.setCurrentDirectory(workingDirectory);
                int returnValue = choosed.showOpenDialog(null); 
                if (returnValue == JFileChooser.APPROVE_OPTION) { 
                     pcaIn.setText(choosed.getSelectedFile().getPath().replace("\\", "\\\\")); 
                } 
			}
			
		});
		
		goButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				goButton.setEnabled(false);
				console.setText("");
//				System.out.println("testing\nt\nt\nt\nt\nt\nt\nt\nt");
				String pca = pcaIn.getText();
				String testin = testIn.getText();
				String trainin = trainIn.getText();
				String out = outFileName.getText();
				int numthread = Integer.parseInt(threadCount.getText());
				int kmer = Integer.parseInt(kmerCount.getText());
				boolean fastatofeature = fastaToFeatureCheckBox.isSelected();
				Thread t = new Thread(new Runnable(){

					@Override
					public void run() {
						KDTOnlyMain.execute(
								pca,
								testin,
								trainin,
								out,
								numthread,
								kmer,
								fastatofeature
						);
					}
					
				});
				t.start();
				
				goButton.setEnabled(true);
			}
			
		});
		
		panel.setLayout(new BorderLayout());
		northPanel.setLayout(new BorderLayout());
		southPanel.setLayout(new BorderLayout());
		northEastPanel.setLayout(new BoxLayout(northEastPanel, BoxLayout.PAGE_AXIS));
		northWestPanel.setLayout(new BoxLayout(northWestPanel, BoxLayout.PAGE_AXIS));
		
		
		//  center the frame on the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setSize(500, 500);
		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
		
		//  add the components
		panel.add(northPanel, BorderLayout.NORTH);
		panel.add(southPanel, BorderLayout.SOUTH);
		southPanel.add(goButton, BorderLayout.CENTER);
		southPanel.add(scroll, BorderLayout.SOUTH);
		northPanel.add(northWestPanel, BorderLayout.WEST);
		northPanel.add(northEastPanel, BorderLayout.EAST);
		
		northWestPanel.add(trainButton);
		northWestPanel.add(Box.createRigidArea(new Dimension(0,15)));
		northEastPanel.add(trainIn);
		northEastPanel.add(Box.createRigidArea(new Dimension(0,15)));
		northWestPanel.add(testButton);
		northWestPanel.add(Box.createRigidArea(new Dimension(0,15)));
		northEastPanel.add(testIn);
		northEastPanel.add(Box.createRigidArea(new Dimension(0,15)));
		northWestPanel.add(pcaButton);
		northWestPanel.add(Box.createRigidArea(new Dimension(0,15)));
		northEastPanel.add(pcaIn);
		northEastPanel.add(Box.createRigidArea(new Dimension(0,15)));
		northWestPanel.add(kmerLabel);
		northWestPanel.add(Box.createRigidArea(new Dimension(0,15)));
		northEastPanel.add(kmerCount);
		northEastPanel.add(Box.createRigidArea(new Dimension(0,15)));
		northWestPanel.add(threadLabel);
		northWestPanel.add(Box.createRigidArea(new Dimension(0,15)));
		northEastPanel.add(threadCount);
		northEastPanel.add(Box.createRigidArea(new Dimension(0,15)));
		northEastPanel.add(outFileName);
		
		JLabel outfile = new JLabel("Name of Output Fasta File");
		northWestPanel.add(outfile);
		northWestPanel.add(Box.createRigidArea(new Dimension(0,15)));
		JLabel fastaToFeatureLabel = new JLabel("Convert to Feature File");
		northWestPanel.add(fastaToFeatureLabel);
		northEastPanel.add(fastaToFeatureCheckBox);
		frame.getContentPane().add(panel);
		
		
		System.setOut(out);
		System.setErr(out);
		//  jframe close ops
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	
		

}

//creates the console at the bottom of the frame
class Console extends JTextArea {
	public Console() {
		this.setEditable(false);
		TitledBorder border = BorderFactory.createTitledBorder(
				BorderFactory.createLoweredBevelBorder(), "Console");
		border.setTitleJustification(TitledBorder.LEFT);
		this.setBorder(border);
		this.setPreferredSize(new Dimension(145, 100));
		

	}
}


//  redirects console output
class TextAreaOutputStream extends OutputStream{
	private JTextArea textControl;
	
	public TextAreaOutputStream(JTextArea control){
		textControl = control;
	}

	@Override
	public void write(int b) throws IOException {
		textControl.append(String.valueOf((char) b));
		
	}
	
}
