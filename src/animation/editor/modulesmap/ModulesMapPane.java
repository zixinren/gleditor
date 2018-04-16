package animation.editor.modulesmap;

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

import animation.editor.Localizable;
import animation.editor.ModulePane;

;

/**
 * 模块映射面板
 * 
 * @author 段链
 * 
 * @time 2012-9-20
 * 
 */
public class ModulesMapPane extends JPanel implements Localizable {
	private static final long serialVersionUID = 3185023615073146143L;

	JToolBar toolBar = new JToolBar();
	JButton addNewButton = new JButton();
	JButton moveUpButton = new JButton();
	JButton moveDownButton = new JButton();
	JButton removeButton = new JButton();
	JButton cloneButton = new JButton();

	JScrollPane tableScrollPane = new JScrollPane();
	JTable mapTable = new JTable();

	JLabel titledLabel = new JLabel();
	JPanel titledPanel = new JPanel();

	JPanel contentPanel = new JPanel();

	public ModulesMapPane() {
		try {
			jbInit();
			updateLocalization();
			listenerInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化模块映射面板组件
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

		tableScrollPane.getViewport().add(mapTable);
		ModulesStylesData.setTable(mapTable);

		contentPanel.setDebugGraphicsOptions(0);
		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(toolBar, BorderLayout.NORTH);
		contentPanel.add(tableScrollPane, BorderLayout.CENTER);

		setEnabled(true);
		setLayout(new BorderLayout());
		add(titledPanel, BorderLayout.NORTH);
		add(contentPanel, BorderLayout.CENTER);
	}

	@Override
	public void updateLocalization() {
		titledLabel.setText("Modules Map");

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
	 * 初始化监听器
	 */
	private void listenerInit() {
		ModulesStylesData.getModel().addSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						ModulesStylesData.setModulesStyles(mapTable
								.getSelectedRow());
						ModulePane.mapJScrollPane.repaint();
						ModulePane.frameJScrollPane.repaint();
					}
				});
		addNewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ModulesStylesData.getModel().addRow();
				JScrollBar scrollBar = tableScrollPane.getVerticalScrollBar();
				scrollBar.setValue(scrollBar.getMaximum());
			}

		});
		moveUpButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ModulesStylesData.getModel().moveUp();
			}

		});
		moveDownButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ModulesStylesData.getModel().moveDown();
			}

		});
		removeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ModulesStylesData.removeModulesStyles(mapTable.getSelectedRow());
				ModulesStylesData.getModel().remove();
				ModulesStylesData.setModulesStyles(0);
			}

		});
		cloneButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ModulesStylesData.getModel().cloneRow();
				JScrollBar scrollBar = tableScrollPane.getVerticalScrollBar();
				scrollBar.setValue(scrollBar.getMaximum());
			}

		});
	}
}
