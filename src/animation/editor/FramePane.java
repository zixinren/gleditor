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
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import animation.world.AniFrame;
import animation.world.Sequence;

/**
 * 帧面板
 * 
 * @author 段链
 * @time 2012-9-15
 */
public class FramePane extends JPanel implements Localizable {
	private static final long serialVersionUID = -3562181270131726396L;

	JToolBar toolBar = new JToolBar();
	JButton addNewButton = new JButton();
	JButton moveUpButton = new JButton();
	JButton moveDownButton = new JButton();
	JButton removeButton = new JButton();
	JButton cloneButton = new JButton();

	JPanel contentPanel = new JPanel();

	JScrollPane tableScrollPane = new JScrollPane();
	JTable frameTable = new JTable();

	JPanel titledPanel = new JPanel();
	JLabel titledLabel = new JLabel();

	public FramePane() {
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
		addNewButton.setRequestFocusEnabled(false);
		moveUpButton.setRequestFocusEnabled(false);
		moveDownButton.setRequestFocusEnabled(false);
		removeButton.setRequestFocusEnabled(false);
		cloneButton.setRequestFocusEnabled(false);

		toolBar.setOrientation(JToolBar.HORIZONTAL);
		toolBar.setFloatable(false);
		toolBar.add(addNewButton);
		toolBar.add(moveUpButton);
		toolBar.add(moveDownButton);
		toolBar.add(removeButton);
		toolBar.add(cloneButton);

		titledLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		titledPanel.setBackground(SystemColor.info);
		titledPanel.setLayout(new BorderLayout());
		titledPanel.add(titledLabel, BorderLayout.NORTH);

		tableScrollPane.getViewport().add(frameTable);
		AniFrame.setTable(frameTable);

		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(toolBar, BorderLayout.NORTH);
		contentPanel.add(tableScrollPane, BorderLayout.CENTER);
		contentPanel.setDebugGraphicsOptions(0);

		setEnabled(true);
		setLayout(new BorderLayout());
		add(titledPanel, BorderLayout.NORTH);
		add(contentPanel, BorderLayout.CENTER);
	}

	@Override
	public void updateLocalization() {
		titledLabel.setText("Frame");

		useToolTipText(addNewButton, "addNewButton_ToolTip");
		useToolTipText(moveUpButton, "moveUpButton_ToolTip");
		useToolTipText(moveDownButton, "moveDownButton_ToolTip");
		useToolTipText(removeButton, "removeButton_ToolTip");
		useToolTipText(cloneButton, "cloneButton_ToolTip");

		useIcon(addNewButton, "addNewButton_Icon");
		useIcon(moveUpButton, "moveUpButton_Icon");
		useIcon(moveDownButton, "moveDownButton_Icon");
		useIcon(removeButton, "removeButton_Icon");
		useIcon(cloneButton, "cloneButton_Icon");
	}

	/**
	 * 初始化监听器, 注册了帧表条目的添加, 上移, 下移, 移除, 复制,<br>
	 * 以及当帧序列表选择改变则帧表选择也相应改变
	 */
	private void listenerInit() {
		addNewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AniFrame.getModel().addRow();
				JScrollBar scrollBar = tableScrollPane.getVerticalScrollBar();
				scrollBar.setValue(scrollBar.getMaximum());
			}

		});
		moveUpButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AniFrame.getModel().moveUp();
			}

		});
		moveDownButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AniFrame.getModel().moveDown();
			}

		});
		removeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AniFrame.getModel().remove();
			}

		});
		cloneButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AniFrame.getModel().cloneRow();
				JScrollBar jScrollBar = tableScrollPane.getVerticalScrollBar();
				jScrollBar.setValue(jScrollBar.getMaximum());
			}

		});
		Sequence.getModel().addSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				AniFrame.getModel().getSelectionModel().clearSelection();
				for (Sequence sequence : Sequence.getModel().getSelections()) {
					/**
					 * int id = sequence.aniFrame.getId();
					 * AniFrame.getModel().getSelectionModel()
					 * .addSelectionInterval(id, id);<br>
					 * 下面的写法比上面的更通用一些
					 */
					AniFrame.getModel().addSelectObject(sequence.aniFrame);
				}
			}

		});
	}
}
