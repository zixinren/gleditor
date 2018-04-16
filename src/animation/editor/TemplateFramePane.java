package animation.editor;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.SystemColor;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import animation.world.TemplateFrame;

/**
 * 模板帧面板
 * 
 * @author 段链
 * @time 2012-9-15
 */
public class TemplateFramePane extends JPanel implements Localizable {
	private static final long serialVersionUID = -8595039057514302178L;

	JScrollPane tableScrollPane = new JScrollPane();
	JTable frameTable = new JTable();

	JPanel contentPanel = new JPanel();

	JLabel titledLabel = new JLabel();
	JPanel titledPanel = new JPanel();

	public TemplateFramePane() {
		try {
			jbInit();
			updateLocalization();
			listenerInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化模板帧面板组件
	 */
	private void jbInit() {
		titledLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		titledPanel.setBackground(SystemColor.info);
		titledPanel.setLayout(new BorderLayout());
		titledPanel.add(titledLabel, BorderLayout.NORTH);

		tableScrollPane.getViewport().add(frameTable);
		TemplateFrame.setTable(frameTable);

		contentPanel.setDebugGraphicsOptions(0);
		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(tableScrollPane, BorderLayout.CENTER);

		setEnabled(true);
		setLayout(new BorderLayout());
		add(titledPanel, BorderLayout.NORTH);
		add(contentPanel, BorderLayout.CENTER);
	}

	@Override
	public void updateLocalization() {
		titledLabel.setText("Template Frame");
	}

	private void listenerInit() {
	}
}
