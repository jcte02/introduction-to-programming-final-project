import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TestPanel extends JPanel {
	//#region Data
	private final static String INCIPIT = "I see myself ";
	private final static String[] QUESTIONS = { "extraverted, enthusiastic.", "critical, quarrelsome.",
			"dependable, self-disciplined.", "anxious, easily upset.", "open to new experiences, complex.",
			"reserved, quiet.", "sympathetic, warm", "disorganized, careless.", "calm, emotionally stable.",
			"conventional, uncreative." };
	private final static String[] ANSWERS = { "Disagree<br> strongly", "Disagree<br> moderately",
			"Disagree<br> a little", "Neither agree<br> nor disagree", "Agree<br> a little", "Agree<br> moderately",
			"Agree<br> strongly" };
	//#endregion
	private StateMachine sm;
	public TestPanel() {
		setLayout(new BorderLayout());

		add(getContentPanel(), BorderLayout.CENTER);
		add(getNavigationPanel(), BorderLayout.SOUTH);

		sm = new StateMachine(new Component[][] { 
			{ welcomePanel, startButton }, 
			{ testPanel, backButton, resultButton },
			{ barChart, restartButton } });
		sm.initState(0);
	}

	//#region Content Panel
	private JPanel getContentPanel() {
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.add(getWelcomePanel());
		content.add(getTestPanel());
		content.add(getBarChart());
		return content;
	}

	//#region Welcome Panel
	private JScrollPane welcomePanel;
	private final static Path WELCOME_FILE_PATH = Paths.get("Resources/Welcome");

	private JScrollPane getWelcomePanel() {
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setOpaque(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		String text;
		try {
			text = new String(Files.readAllBytes(WELCOME_FILE_PATH));
		} catch (IOException e) {
			text = "Cannot open " + e.getMessage();
		}
		textArea.setText(text);
		welcomePanel = new JScrollPane(textArea);
		welcomePanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		welcomePanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		return welcomePanel;
	}
	//#endregion

	//#region Test Panel
	private JScrollPane testPanel;
	private ButtonGroup[] answerButtons;

	private final static int SPACING_STRUT = 30;
	private final static int PADDING_STRUT = 5;

	private JScrollPane getTestPanel() {
		initAnswerButtons();
		JPanel wrapPanel = new JPanel();
		wrapPanel.setLayout(new BoxLayout(wrapPanel, BoxLayout.Y_AXIS));
		wrapPanel.add(Box.createVerticalStrut(SPACING_STRUT));
		for (int i = 0; i < QUESTIONS.length; i++) {
			// horizontal line
			wrapPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
			wrapPanel.add(Box.createVerticalStrut(PADDING_STRUT));
			// question
			wrapPanel.add(getQuestionPanel(i));
			// horizontal line
			wrapPanel.add(Box.createVerticalStrut(PADDING_STRUT));
			wrapPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
			// answers
			wrapPanel.add(getAnswerPanel(i));
			// double horizontal line
			wrapPanel.add(Box.createVerticalStrut(PADDING_STRUT));
			wrapPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
			wrapPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
			// spacing
			wrapPanel.add(Box.createVerticalStrut(SPACING_STRUT));
		}
		testPanel = new JScrollPane(wrapPanel);
		return testPanel;
	}

	private JLabel errorLabel;
	private JTextField nickname;
	
	private void showError(String err) {
		errorLabel.setText(err);
		nickname.setBorder(BorderFactory.createLineBorder(Color.RED));
	}
	
	private boolean checkNicknameValid() {
		String n = nickname.getText();
		if(n.isEmpty()) {
			showError("Nickname cannot be empty");
		} else if(!Personality.isNicknameValid(n)) {
			showError("Invalid nickname");
		} else if(!Database.getInstance().isNicknameAvaiable(n)) {
			showError("Nickname already taken");
		} else {
			errorLabel.setText("");
			nickname.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			return true;
		}
		return false;
	}
	
	private String getNickname() {
		errorLabel = new JLabel();
		nickname = new JTextField();
		PlaceholderManager.attachTo(nickname, "Enter a nickname");
		nickname.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				checkNicknameValid();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				checkNicknameValid();
			}

			@Override
			public void changedUpdate(DocumentEvent e) { }
		});
		JPanel wrapPanel = new JPanel();
		wrapPanel.setLayout(new BoxLayout(wrapPanel, BoxLayout.Y_AXIS));
		wrapPanel.add(errorLabel);
		wrapPanel.add(nickname);
		do {
			errorLabel.requestFocus();
			JOptionPane.showMessageDialog(this, wrapPanel, "Enter a nickname", JOptionPane.PLAIN_MESSAGE);
		} while(!checkNicknameValid());
		return nickname.getText();
	}
	
	//#region Radio buttons
	private void initAnswerButtons() {
		answerButtons = new ButtonGroup[QUESTIONS.length];
		for (int i = 0; i < answerButtons.length; i++) {
			answerButtons[i] = new ButtonGroup();
		}
	}

	private void resetTestPanel() {
		for (ButtonGroup g : answerButtons) {
			g.clearSelection();
		}
		nickname.setText("");
		testPanel.getVerticalScrollBar().setValue(0);
		resultButton.setEnabled(false);
	}

	private boolean allQuestionsAnswered() {
		for (ButtonGroup g : answerButtons) {
			if (g.getSelection() == null) {
				return false;
			}
		}
		return true;
	}
	
	private int[] getAnswers() {
		int[] answers = new int[QUESTIONS.length];
		for (int i = 0; i < answerButtons.length; i++) {
			try {
				answers[i] = Integer.parseInt(answerButtons[i].getSelection().getActionCommand());
			}
			catch (Exception ex) {
				answers[i] = -1;
			}
		}
		return answers;
	}
	//#endregion
	//#endregion
	private BarChart barChart;
	
	private BarChart getBarChart() {
		barChart = new BarChart().showLegend();
		return barChart;
	}
	//#endregion

	//#region Navigation Panel
	private JPanel getNavigationPanel() {
		JPanel eastPanel = new JPanel();
		eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.X_AXIS));
		eastPanel.add(getStartButton());
		eastPanel.add(getResultButton());

		JPanel westPanel = new JPanel();
		westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.X_AXIS));
		westPanel.add(getBackButton());
		westPanel.add(getRestartButton());

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(eastPanel, BorderLayout.EAST);
		panel.add(westPanel, BorderLayout.WEST);
		return panel;
	}

	//#region Navigation Buttons
	private JButton startButton, backButton, resultButton, restartButton;

	private JButton getStartButton() {
		startButton = getButton("Take the test");
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sm.nextState();
			}
		});
		return startButton;
	}

	private JButton getBackButton() {
		backButton = getButton("Back");
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetTestPanel();
				sm.previousState();
			}
		});
		return backButton;
	}

	private JButton getResultButton() {
		resultButton = getButton("View result");
		resultButton.setEnabled(false);
		resultButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Personality p = Personality.fromTestAnswers(getAnswers()).setNickname(getNickname());
				Database.getInstance().addUser(p);
				barChart.setTitle(p.getNickname()).setData(p.getScoresAnnotated());
				sm.nextState();
			}
		});
		return resultButton;
	}

	private JButton getRestartButton() {
		restartButton = getButton("Restart");
		restartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetTestPanel();
				sm.jumpToState(0);
			}
		});
		return restartButton;
	}
	//#endregion
	//#endregion

	//#region Generators
	private JButton getButton(String text) {
		JButton button = new JButton(text);
		// border-only button
		button.setOpaque(false);
		button.setContentAreaFilled(false);
		// don't focus text after click
		button.setFocusPainted(false);
		return button;
	}

	private JButton getButton(String text, Icon icon, int hAlignment) {
		JButton button = getButton(text);
		button.setIcon(icon);
		button.setHorizontalAlignment(hAlignment);
		return button;
	}

	private JPanel getQuestionPanel(int index) {
		JPanel questionPanel = new JPanel();
		questionPanel.setLayout(new BorderLayout());
		JLabel question = new JLabel();
		if (index >= 0 && index < QUESTIONS.length) {
			question.setText(String.format("<html>Q%d. <i>%s</i></html>", index + 1, INCIPIT + QUESTIONS[index]));
		}
		questionPanel.add(question, BorderLayout.WEST);
		return questionPanel;
	}

	private JPanel getAnswerPanel(int index) {
		JPanel answerPanel = new JPanel();
		// TODO: GridBagLayout
		answerPanel.setLayout(new GridLayout(2, 7));
		for (int i = 0; i < ANSWERS.length; i++) {
			answerPanel.add(getLabel(i));
		}
		for (int i = 0; i < ANSWERS.length; i++) {
			JRadioButton button = getRadioButton(i);
			if (index >= 0 && index <= answerButtons.length) {
				answerButtons[index].add(button);
			}
			answerPanel.add(button);
		}
		return answerPanel;
	}

	private final static String HTML_TEMPLATE = "<html><div style='text-align: center;'>%s</div></html>";

	private JLabel getLabel(int index) {
		JLabel label = new JLabel();
		if (index >= 0 && index <= ANSWERS.length) {
			label.setText(String.format(HTML_TEMPLATE, ANSWERS[index]));
		}
		label.setHorizontalAlignment(SwingConstants.CENTER);
		return label;
	}

	private JRadioButton getRadioButton(int index) {
		JRadioButton button = new JRadioButton(Integer.toString(index + 1));
		button.setActionCommand(button.getText());
		// don't select text
		button.setFocusPainted(false);
		// center text
		button.setHorizontalTextPosition(SwingConstants.CENTER);
		button.setVerticalTextPosition(SwingConstants.BOTTOM);
		// center button
		button.setHorizontalAlignment(SwingConstants.CENTER);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (allQuestionsAnswered()) {
					resultButton.setEnabled(true);
				}
			}
		});
		return button;
	}
	//#endregion
}
