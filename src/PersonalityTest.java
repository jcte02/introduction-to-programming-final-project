import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

//@author Fabio Endrizzi
public class PersonalityTest {
	private final static Path ABOUT_FILE_PATH = Paths.get("Resources/About");
	public static void main(String[] args) {
		JFrame frame = new JFrame("Personality Test");		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setPreferredSize(new Dimension(800, 600));
		frame.setMinimumSize(new Dimension(800, 600));
	
		attachMenu(frame);
	
		frame.getContentPane().add(new PersonalityTestPanel());
		frame.pack();
		
		// center to the screen
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private static void attachMenu(JFrame frame) {		
		JMenuItem exit = new JMenuItem("Exit");
		exit.setMnemonic(KeyEvent.VK_E);
		exit.setAccelerator(KeyStroke.getKeyStroke("control alt E"));
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);	
			}
		});
		
		JMenuItem about = new JMenuItem("About");
		about.setMnemonic(KeyEvent.VK_A);
		about.setAccelerator(KeyStroke.getKeyStroke("control alt A"));
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg;
				int msgType = JOptionPane.INFORMATION_MESSAGE;
				try {
					byte[] about = Files.readAllBytes(ABOUT_FILE_PATH);
					msg = new String(about);
				} catch (IOException ex) {
					msg = "Cannot open " + ex.getMessage();
					msgType = JOptionPane.ERROR_MESSAGE;
				}
				JOptionPane.showMessageDialog(frame, msg, "About", msgType);
			}
		});
		
		JMenu menu = new JMenu("Menu");
		menu.setMnemonic(KeyEvent.VK_M);
		menu.add(exit);
		menu.add(about);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menu);
		frame.setJMenuBar(menuBar);
	}

}
