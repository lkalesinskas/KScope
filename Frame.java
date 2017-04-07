import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class Frame extends JFrame {

	ControlPanel cpanel = new ControlPanel();
	ControlPanel cpanel2 = new ControlPanel();
	/**
	 *   THIS IS THE FRAME THAT WILL TAKE IN THE USER TABLE AND ROWS
	 */
	public Frame() {
		super("Disco Test Frame");
		this.setBounds(0, 0, 1000, 1000);
		///  MAX KELLY CLOSING OPERATION
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {

				JFrame frame = (JFrame) e.getSource();

				int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit the application?",
						"Exit Application", JOptionPane.YES_NO_OPTION);

				if (result == JOptionPane.YES_OPTION) {
					frame.dispose();
					System.gc();
					System.out.println("X has been clicked");
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}
			}
		});
		Container content = getContentPane();
		content.setLayout(new BorderLayout());
		//  SET THE LABELS IN EACH OF THE PANELS
		cpanel2.label.setText("what would you like to name the table?");
		cpanel2.In.setText("Please set the name of the Table");
		content.add(cpanel, BorderLayout.WEST);
		content.add(cpanel2, BorderLayout.NORTH);

		this.setVisible(true);
	}

	/**
	 *   THE PANEL THAT WILL BE DISPLAYED UPON EXECUTING THE PROGRAM
	 * @author MAX KELLY
	 *
	 */
	class ControlPanel extends JPanel {
		InputField In = new InputField();
		AddButton add = new AddButton();
		JLabel label = new JLabel();

		@SuppressWarnings("deprecation")
		public ControlPanel() {

			TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(),
					"Control Panel");
			border.setTitleJustification(TitledBorder.LEFT);
			this.setBorder(border);
			this.setLayout(new FlowLayout());
			this.setSize(1000, 1000);

			this.In.setText("Number of Planes");

			label.setText("How many kmer planes?");
			this.add(label);
			this.add(this.In);
			this.add(this.add);
		}

		// the jinputfield. Will work for button or if enter is pressed
		class InputField extends JTextField {
			public InputField() {
				this.setColumns(80);
				this.selectAll();
				
			}
		}

		// addbutton for adding rows and tables to the DB
		class AddButton extends JButton {
			public AddButton() {
				super("Add");
				
			}

		}

	}
	
}
