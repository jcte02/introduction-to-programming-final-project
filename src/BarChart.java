import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JPanel;

public class BarChart extends JPanel {
	private String title;
	private Map<String, Double> data;
	private boolean showLegend, truncate, hideChart;

	public BarChart() {		
		title = "";
		data = new LinkedHashMap<String, Double>();
		
		addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				repaintComponent();
			}
			@Override
			public void componentShown(ComponentEvent e)  { }
			@Override
			public void componentMoved(ComponentEvent e)  { }
			@Override
			public void componentHidden(ComponentEvent e) { }
		});
	}

	public BarChart setTitle(String title) {
		if(title != null) {
			this.title = title;			
		}
		return repaintComponent();
	}

	public BarChart setData(Map<String, Double> data) {
		if(data != null) {
			this.data = data;
		}
		return repaintComponent();
	}
	
	public BarChart showLegend() {
		return setLegendVisibility(true);
	}
	
	public BarChart hideLegend() {
		return setLegendVisibility(false);
	}

	public BarChart setLegendVisibility(boolean visible) {
		showLegend = visible;
		return repaintComponent();
	}
	
	public BarChart truncateIntegers() {
		return setTruncateIntegers(true);
	}
	
	public BarChart hideIntegers() {
		return setTruncateIntegers(false);
	}
	
	public BarChart setTruncateIntegers(boolean truncate) {
		this.truncate = truncate;
		return repaintComponent();
	}	
	
	public BarChart showChart() {
		return setChartVisibility(true);
	}
	
	public BarChart hideChart() {
		return setChartVisibility(false);
	}
	
	public BarChart setChartVisibility(boolean visible) {
		hideChart = !visible;
		return repaintComponent();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(showLegend) {
			drawLegend(g);
		}
		if(!hideChart) {
			drawBottomLine(g);
			drawBars(g);
		}
	}
	
	private BarChart repaintComponent() {
		recalculateMeasures();
		repaint();
		return this;
	}
	
	private final static int LINE_THICKNESS = 5, BAR_MIN_HEIGHT = 10;
	private final static int MARGIN_WIDTH = 50, MARGIN_HEIGHT = 50;
	private final static int TOOLTIP_SPACING = 15, TEXT_SPACING = TOOLTIP_SPACING + 5, BAR_SPACING = 50;
	private final static int LEGEND_MARGIN = 15, LEGEND_BOX_WIDTH = 15, LEGEND_BOX_HEIGHT = (int)(LEGEND_BOX_WIDTH / 1.5);
	
	private int panelW, panelH;
	private int lineX, lineY, lineW, lineH;
	private int legendAnchorX, legendAnchorY;
	private int barAnchorX, barAnchorY, barMaxW, barMaxH, widthPerBar;
	private double pixelPerUnit;
	
	private void recalculateMeasures() {
		panelW = getWidth();
		panelH = getHeight();
	
		lineX = MARGIN_WIDTH;
		lineY = panelH - (MARGIN_HEIGHT + LINE_THICKNESS);
		lineW = panelW - (MARGIN_WIDTH * 2);
		lineH = LINE_THICKNESS;
		
		barAnchorX = lineX + BAR_SPACING;
		barAnchorY = lineY;
		barMaxH = lineY - (MARGIN_HEIGHT + TEXT_SPACING + BAR_MIN_HEIGHT);
		barMaxW = lineW - (BAR_SPACING * 2);
		
		if(showLegend) {
			// clip height under the legend
			barMaxH -= (TEXT_SPACING) * (data.size() - 1);
			legendAnchorX = hideChart ? MARGIN_WIDTH + 5: LEGEND_MARGIN;
			legendAnchorY = LEGEND_MARGIN;
		}
		
		pixelPerUnit = barMaxH / getMaximumValue();
		
		if(data.size() > 0) {
			widthPerBar = barMaxW / data.size();
		}
	}

	private void drawBottomLine(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(lineX, lineY, lineW, lineH);
		if(!title.isEmpty()) {
			drawStringCentered(g, title, lineX, lineY + lineH + TEXT_SPACING, lineW, new Font("default", Font.BOLD, 16));
		}
	}
	
	private void drawBars(Graphics g) {
		int index = 0;
		for(double v : data.values()) {
			drawBar(g, index, v);
			index++;
		}
	}
	
	private void drawLegend(Graphics g) {
		int index = 0, maxW = 0;
		for(String s : data.keySet()) {
			drawLegendEntry(g, index, s);
			
			// keep track of longest text width for bounding rect later
			int strW = g.getFontMetrics().stringWidth(s);
			if(strW > maxW) {
				maxW = strW;
			}
			
			index++;
		}
		
		int boundingRectW = 5 + TEXT_SPACING + maxW + 5;
		
		// draw bounding rect
		g.setColor(Color.BLACK);
		g.drawRect(legendAnchorX - 5, legendAnchorY - 5, boundingRectW, TEXT_SPACING * index);
	}
	
	private static final Color[] COLORS = new Color[] {
			Color.MAGENTA, Color.GREEN, Color.CYAN,
			Color.YELLOW, Color.ORANGE, Color.GRAY,
			Color.PINK, Color.WHITE, Color.BLACK
	};
	
	private int getBarHeight(double value) {
		return (int)(value * pixelPerUnit) + BAR_MIN_HEIGHT;
	}
			
	private void drawBar(Graphics g, int index, double value) {
		int barH = getBarHeight(value);
		int barX = barAnchorX + (widthPerBar * index);
		int barY = barAnchorY - barH;
		
		g.setColor(COLORS[index % COLORS.length]);
		g.fillRect(barX, barY, widthPerBar, barH);
		
		g.setColor(Color.BLACK);
		g.drawRect(barX, barY, widthPerBar, barH);
		drawStringCentered(g, getBarTooltip(value), barX, barY - TOOLTIP_SPACING, widthPerBar, new Font("default", Font.BOLD, 13));
	}

	private void drawLegendEntry(Graphics g, int index, String str) {
		int rowY = LEGEND_MARGIN + (TEXT_SPACING * index);
		
		g.setColor(COLORS[index % COLORS.length]);
		g.fillRect(legendAnchorX, rowY, LEGEND_BOX_WIDTH, LEGEND_BOX_HEIGHT);
		
		g.setColor(Color.BLACK);
		g.drawRect(legendAnchorX, rowY, LEGEND_BOX_WIDTH, LEGEND_BOX_HEIGHT);
		g.drawString(str, legendAnchorX + TEXT_SPACING, rowY + LEGEND_BOX_HEIGHT);
	}
	

	private void drawStringCentered(Graphics g, String str, int x, int y, int w, Font font) {
		Font f = g.getFont();
		
		g.setFont(font);
		int strPixelW = g.getFontMetrics().stringWidth(str);
		int strideX = (w - strPixelW) / 2;

		g.setColor(Color.BLACK);
		g.drawString(str, x + strideX, y);
		
		// restore previous font
		g.setFont(f);
	}
	
	private String getBarTooltip(double value) {
		String str;
		if(truncate && isInteger(value)) {
			str = Integer.toString((int)value);
		} else {
			str = Double.toString(value);
		}
		return str;
	}
	
	private boolean isInteger(double d) {
		return Double.isFinite(d) && (Math.ceil(d) == Math.floor(d));
	}

	private double getMaximumValue() {
		double max = 0;
		for(double v : data.values()) {
			if(v > max) {
				max = v;
			}
		}
		return max;
	}
}
