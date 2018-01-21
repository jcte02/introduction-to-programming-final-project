import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class StatisticsPanel extends JPanel {
	private BarChart[] charts;
	
	public StatisticsPanel() {	
		setLayout(new BorderLayout());
		add(getChartsPanel(), BorderLayout.CENTER);
		add(getClearButton(), BorderLayout.SOUTH);
	}
	
	private final static String clearButtonMessage = "Clear stored data";
	private final static String clearDialogMessage = "Delete stored data?", clearDialogTitle = "WARNING";
	
	private JButton getClearButton() {
		JButton button = new JButton(clearButtonMessage);
		// don't focus text after click
		button.setFocusPainted(false);
		JPanel dis = this;
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(dis, clearDialogMessage, clearDialogTitle, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if(result == JOptionPane.YES_OPTION) {
					Database.getInstance().clear();
					updateCharts();
				}
			}
		});
		return button;
	}
	
	
	private JPanel getChartsPanel() {
		JPanel wrapPanel = new JPanel();
		wrapPanel.setLayout(new GridLayout(3, 3));
		initializeCharts(wrapPanel);
		return wrapPanel;
	}
	
	private void initializeCharts(JPanel p) {
		charts = new BarChart[Personality.getTraits().length];
		for(int i = 0; i < charts.length; i++) {
			charts[i] = new BarChart().truncateIntegers();
			p.add(charts[i]);
		}
		p.add(getLegendPanel());
		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
				updateCharts();
			}
			@Override
			public void componentResized(ComponentEvent e) { }
			@Override
			public void componentMoved(ComponentEvent e) { }
			@Override
			public void componentHidden(ComponentEvent e) { }
		});
	}
	
	// legend-only chart
	private JPanel getLegendPanel() {
		return new BarChart().showLegend().hideChart().setData(
				new TraitStatistic("").getData());
	}
	
	private void updateCharts() {
		int index = 0;
		for(TraitStatistic ts : Database.getInstance().getPopulationStatistic().getTraitsStatistics().values()) {
			charts[index].setTitle(ts.getTraitName()).setData(ts.getData());
			index++;
		}
	}
}
