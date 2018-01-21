import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SearchPanel extends JPanel {	
	private StateMachine sm;
	private JTextField nickname;
	private JList<String> nickList;
	private BarChart barChart;
	private DefaultListModel<String> model;
	private JButton viewButton, backButton;
	private JScrollPane scrollPane;
	public SearchPanel() {
		setLayout(new BorderLayout());

		add(getNicknameField(), BorderLayout.NORTH);
		add(getContentPanel(), BorderLayout.CENTER);
		add(getNavigationPanel(), BorderLayout.SOUTH);
		
		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
				performSearch();
			}
			
			@Override
			public void componentResized(ComponentEvent e) { }
			@Override
			public void componentMoved(ComponentEvent e) { }
			@Override
			public void componentHidden(ComponentEvent e) { }
		});
		
		sm = new StateMachine(new Component[][] {
			{nickname, scrollPane, viewButton},
			{barChart, backButton}
		});
		sm.initState(0);
	}
	
	private final static String placeholderText =  "Enter a nickname";
	private JTextField getNicknameField() {
		nickname = new JTextField();
		PlaceholderManager.attachTo(nickname,placeholderText);
		nickname.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				performSearch();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) { 
				performSearch();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) { }
		});
		nickname.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode()) {
				case KeyEvent.VK_ENTER:
					performSelection();
					break;
				case KeyEvent.VK_UP:
					if(result.size() > 1 && !result.hasPerfectMatch()) {
						int index = nickList.getSelectedIndex();
						nickList.setSelectedIndex((--index < 0) ? result.size() - 1 : index);
					}
					break;
				case KeyEvent.VK_DOWN:
					if(result.size() > 1 && !result.hasPerfectMatch()) {
						nickList.setSelectedIndex((nickList.getSelectedIndex() + 1) % result.size());
					}
					break;
				}
			}
			@Override
			public void keyTyped(KeyEvent e) { }
			@Override
			public void keyReleased(KeyEvent e) { }
		});
		return nickname;
	}
	
	private JPanel getContentPanel() {
		JPanel wrapPanel = new JPanel();
		wrapPanel.setLayout(new BoxLayout(wrapPanel, BoxLayout.Y_AXIS));
		wrapPanel.add(getListPanel());
		wrapPanel.add(getBarChart());
		return wrapPanel;
	}
	
	private JPanel getNavigationPanel() {
		JPanel navigationPanel = new JPanel();
		navigationPanel.setLayout(new BorderLayout());
		navigationPanel.add(getBackButton(), BorderLayout.WEST);
		navigationPanel.add(getViewButton(), BorderLayout.EAST);
		return navigationPanel;
	}
	
	private JScrollPane getListPanel() {
		nickList = new JList<String>();
		model = new DefaultListModel<String>();
		nickList.setModel(model);
		nickList.setLayoutOrientation(JList.VERTICAL);
		nickList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		nickList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				viewButton.setEnabled(isSelectionValid());
			}
		});
		nickList.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// double-click
				if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
					performSelection();
				}
			}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {} 
			@Override
			public void mouseReleased(MouseEvent e) {}
		});
		scrollPane = new JScrollPane(nickList);
		return scrollPane;
	}
	
	private BarChart getBarChart() {
		barChart = new BarChart().showLegend();
		return barChart;
	}
	
	private JButton getViewButton() {
		viewButton = getButton("View");
		viewButton.setEnabled(false);
		viewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				performSelection();
			}
		});
		return viewButton;
	}
	
	private JButton getBackButton() {
		backButton = getButton("Back");
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sm.previousState();
			}
		});
		return backButton;
	}
	
	private JButton getButton(String text) {
		JButton button = new JButton(text);
		// border-only button
		button.setOpaque(false);
		button.setContentAreaFilled(false);
		// don't focus text after click
		button.setFocusPainted(false);
		return button;
	}
		
	private SearchResult result;
	private void performSearch() {
		String prevSelection = "";
		if (isSelectionValid()) {
			prevSelection = result.getNicknames().get(nickList.getSelectedIndex());
		}
		model.clear();
		result = Database.getInstance().searchUser(getNickname());
		if(result.isEmpty()) {
			model.addElement("No user found");
			nickList.setSelectedIndex(0);
		} else {
			for(String s : result.getNicknames()) {
				model.addElement(s);
			}
			int index = result.getNicknames().indexOf(prevSelection);
			if(result.hasPerfectMatch()) {
				index = result.getPerfectMatchIndex();
			} else if(result.hasSingleResult()) {
				index = 0;
			}
			nickList.setSelectedIndex(index);
		}
	}
	
	private String getNickname() {
		return nickname.getText().equals(placeholderText) ? "" : nickname.getText();
	}
	
	private void performSelection() {
		if(isSelectionValid()) {
			Personality p = result.getMatches().get(nickList.getSelectedIndex());
			barChart.setTitle(p.getNickname()).setData(p.getScoresAnnotated());
			sm.nextState();
		}
	}

	private boolean isSelectionValid() {
		// if index != 1 && result == null this will crash
		// when this panel is first shown, index will be equal to -1 since the list will be empty, thus this will not crash
		// when this panel is first shown the list is then initialized and hence result will be not null
		return (nickList.getSelectedIndex() != -1) && !result.isEmpty();
	}
}
