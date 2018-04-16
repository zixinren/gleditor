package animation.editor;

import static animation.editor.MainFrame.useIcon;
import static animation.editor.MainFrame.useToolTipText;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;

import animation.world.Sprite;

/**
 * 精灵面板
 * 
 * @author 段链
 * @time 2012-9-15
 */
public class SpritePane extends JPanel implements Localizable {
	private static final long serialVersionUID = -6585324793529562748L;

	JToolBar toolBar = new JToolBar();
	JButton moveUpButton = new JButton();
	JButton moveDownButton = new JButton();
	JButton removeButton = new JButton();

	JPanel contentPanel = new JPanel();

	JScrollPane tableScrollPane = new JScrollPane();
	JTable spriteTable = new JTable();

	JPanel titledPanel = new JPanel();
	JLabel titledLabel = new JLabel();

	public SpritePane() {
		try {
			jbInit();
			updateLocalization();
			listenerInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化精灵面板组件
	 */
	private void jbInit() {
		moveUpButton.setRequestFocusEnabled(false);
		moveDownButton.setRequestFocusEnabled(false);
		removeButton.setRequestFocusEnabled(false);

		toolBar.setOrientation(JToolBar.HORIZONTAL);
		toolBar.setFloatable(false);
		toolBar.add(moveUpButton);
		toolBar.add(moveDownButton);
		toolBar.add(removeButton);

		titledLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		titledPanel.setBackground(SystemColor.info);
		titledPanel.setLayout(new BorderLayout());
		titledPanel.add(titledLabel, BorderLayout.NORTH);

		tableScrollPane.getViewport().add(spriteTable);
		Sprite.setTable(spriteTable);

		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(toolBar, BorderLayout.NORTH);
		contentPanel.add(tableScrollPane, BorderLayout.CENTER);
		contentPanel.setDebugGraphicsOptions(0);

		setLayout(new BorderLayout());
		add(titledPanel, BorderLayout.NORTH);
		add(contentPanel, BorderLayout.CENTER);
	}

	@Override
	public void updateLocalization() {
		titledLabel.setText("Sprite");

		useToolTipText(moveUpButton, "moveUpButton_ToolTip");
		useToolTipText(moveDownButton, "moveDownButton_ToolTip");
		useToolTipText(removeButton, "removeButton_ToolTip");

		useIcon(moveUpButton, "moveUpButton_Icon");
		useIcon(moveDownButton, "moveDownButton_Icon");
		useIcon(removeButton, "removeButton_Icon");
	}

	/**
	 * 初始化监听器
	 */
	private void listenerInit() {
		moveUpButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Sprite.getModel().moveUp();
			}

		});
		moveDownButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Sprite.getModel().moveDown();
			}

		});
		removeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Sprite.getModel().remove();
			}

		});
		contentPanel.setTransferHandler(spriteTable.getTransferHandler());
	}

}
