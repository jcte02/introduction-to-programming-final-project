import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

public class PlaceholderManager {
	private JTextField field;
	private String placeholderText;
	
	public PlaceholderManager(JTextField field, String placeholderText) {
		this.field = field;
		this.placeholderText = placeholderText;		
		field.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if(field.getText().isEmpty()) {
					showPlaceholder();
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if(field.getText().equals(placeholderText)) {
					hidePlaceholder();
				}
			}
		});
		showPlaceholder();
	}
	
	public static void attachTo(JTextField field, String placeholderText) {
		new PlaceholderManager(field, placeholderText);
	}
	
	private void showPlaceholder() {
		stateTransition(Color.GRAY, placeholderText);
	}
	
	private void hidePlaceholder() {
		stateTransition(Color.BLACK, "");
	}
	
	private void stateTransition(Color c, String s) {
		field.setForeground(c);
		field.setText(s);
	}
}
