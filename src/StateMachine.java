import java.awt.Component;

public class StateMachine {
	private int stateId;
	private Component[][] state;	
	
	public StateMachine(Component[][] state) {
		this.state = (state != null) ? state : new Component[][] {{}};
	}
	
	public void initState(int index) {
		for (int i = 0; i < state.length; i++) {
			hideState(i);
		}
		showState(index);
	}

	public void nextState() {
		stateTransition(stateId + 1);
	}

	public void previousState() {
		stateTransition(stateId - 1);
	}

	public void jumpToState(int index) {
		stateTransition(index);
	}

	private void stateTransition(int index) {
		if (index >= 0 && index < state.length) {
			hideState(stateId);
			stateId = index;
			showState(stateId);
		}
	}

	private void showState(int index) {
		updateState(index, true);
	}

	private void hideState(int index) {
		updateState(index, false);
	}

	private void updateState(int index, boolean newState) {
		if (index >= 0 && index < state.length) {
			for (Component c : state[index]) {
				if(c != null) {
					c.setVisible(newState);
				}
			}
		}
	}
}
