package animation.editor;

import static animation.editor.MainFrame.useIcon;
import static animation.editor.MainFrame.useToolTipText;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

/**
 * 模板角色面板
 * 
 * @author 段链
 * @time 2012-9-15
 */
public class TemplateActorPane extends JPanel implements Localizable {
	private static final long serialVersionUID = -1785190386015045163L;

	JToolBar toolBar = new JToolBar();
	JButton zoomButton = new JButton();
	JButton gridButton = new JButton();
	JButton showBoxButton = new JButton();

	JScrollPane scrollPane = new JScrollPane();
	TemplateActorViewer viewer = new TemplateActorViewer();

	public TemplateActorPane() {
		try {
			jbInit();
			updateLocalization();
			listenerInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化模板角色面板组件
	 */
	private void jbInit() {
		gridButton.setSelected(true);

		zoomButton.setRequestFocusEnabled(false);
		gridButton.setRequestFocusEnabled(false);
		showBoxButton.setRequestFocusEnabled(false);

		gridButton.setMnemonic(KeyEvent.VK_G);

		toolBar.setOrientation(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		toolBar.addSeparator();
		toolBar.add(zoomButton);
		toolBar.addSeparator();
		toolBar.add(showBoxButton);
		toolBar.add(gridButton);

		scrollPane.getViewport().add(viewer);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		setDebugGraphicsOptions(0);
		setLayout(new BorderLayout());
		add(toolBar, BorderLayout.WEST);
		add(scrollPane, BorderLayout.CENTER);
	}

	@Override
	public void updateLocalization() {
		useToolTipText(zoomButton, "zoomButton_ToolTip");
		useToolTipText(gridButton, "gridButton_ToolTip");
		useToolTipText(showBoxButton, "showBoxButton_ToolTip");

		useIcon(zoomButton, "zoomButton_Icon");
		useIcon(gridButton, "gridButton_Icon");
		useIcon(showBoxButton, "showBoxButton_Icon");
	}

	/**
	 * 初始化监听器
	 */
	private void listenerInit() {
		showBoxButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				viewer.setShowBox(showBoxButton.isSelected());
			}

		});
		gridButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				viewer.setEnableGird(gridButton.isSelected());
			}

		});
		zoomButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					viewer.zoomIn();
				} else {
					viewer.zoomOut();
				}
			}

		});
	}

}
