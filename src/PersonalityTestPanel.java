import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class PersonalityTestPanel extends JPanel {

	private final static String[] tabNames = {
			"Take the test", 
			"Statistics", 
			"Search"
	};
	
	public PersonalityTestPanel() {
		setLayout(new BorderLayout());
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JPanel testPanel = new TestPanel();
		tabbedPane.add(tabNames[0], testPanel);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_T);
		
		JPanel statisticsPanel = new StatisticsPanel();
		tabbedPane.add(tabNames[1], statisticsPanel);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_A);
		
		JPanel searchPanel = new SearchPanel();
		tabbedPane.add(tabNames[2], searchPanel);
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_S);
		
		add(tabbedPane, BorderLayout.CENTER);
	}
}
