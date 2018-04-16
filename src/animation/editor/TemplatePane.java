package animation.editor;

import static animation.editor.MainFrame.useIcon;
import static animation.editor.MainFrame.useToolTipText;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;

import animation.world.Animation;
import animation.world.Template;

/**
 * 模板面板
 * 
 * @author 段链
 * @time 2012-9-15
 */
public class TemplatePane extends JPanel implements Localizable {
	private static final long serialVersionUID = 1193993289350688761L;

	JToolBar toolBar = new JToolBar();
	JButton reflushButton = new JButton();
	JButton pathButton = new JButton();
	JButton addButton = new JButton();
	JButton moveUpButton = new JButton();
	JButton moveDownButton = new JButton();
	JButton removeButton = new JButton();

	JScrollPane tableScrollPane = new JScrollPane();
	JTable templateTable = new JTable();

	JLabel titledLabel = new JLabel();
	JPanel titledPanel = new JPanel();

	JPanel contentPanel = new JPanel();

	public TemplatePane() {
		try {
			jbInit();
			updateLocalization();
			listenerInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化模板面板组件
	 */
	private void jbInit() {
		pathButton.setRequestFocusEnabled(false);
		reflushButton.setRequestFocusEnabled(false);
		addButton.setRequestFocusEnabled(false);
		moveUpButton.setRequestFocusEnabled(false);
		moveDownButton.setRequestFocusEnabled(false);
		removeButton.setRequestFocusEnabled(false);

		toolBar.setOrientation(JToolBar.HORIZONTAL);
		toolBar.setFloatable(false);
		toolBar.add(reflushButton);
		toolBar.add(addButton);
		toolBar.add(moveUpButton);
		toolBar.add(moveDownButton);
		toolBar.add(removeButton);

		titledLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		titledPanel.setBackground(SystemColor.info);
		titledPanel.setLayout(new BorderLayout());
		titledPanel.add(titledLabel, BorderLayout.NORTH);

		tableScrollPane.getViewport().add(templateTable);
		Template.setTable(templateTable);

		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(toolBar, BorderLayout.NORTH);
		contentPanel.add(tableScrollPane, BorderLayout.CENTER);

		setLayout(new BorderLayout());
		add(titledPanel, BorderLayout.NORTH);
		add(contentPanel, BorderLayout.CENTER);
	}

	@Override
	public void updateLocalization() {
		titledLabel.setText("Templates");

		useToolTipText(pathButton, "selectTemplate_ToolTip");
		useToolTipText(reflushButton, "reflushButton_ToolTip");
		useToolTipText(addButton, "addNewButton_ToolTip");
		useToolTipText(moveUpButton, "moveUpButton_ToolTip");
		useToolTipText(moveDownButton, "moveDownButton_ToolTip");
		useToolTipText(removeButton, "removeButton_ToolTip");

		useIcon(pathButton, "selectImageButton_Icon");
		useIcon(reflushButton, "reflushButton_Icon");
		useIcon(addButton, "addNewButton_Icon");
		useIcon(moveUpButton, "moveUpButton_Icon");
		useIcon(moveDownButton, "moveDownButton_Icon");
		useIcon(removeButton, "removeButton_Icon");
	}

	/**
	 * 初始化监听器
	 */
	private void listenerInit() {
		pathButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectTemplate();
			}

		});
		reflushButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Template selectedTemp = Template.getModel().getSelection();
				int rowIndex = Template.getModel().getSelectionID();

				if (selectedTemp != null) {
					Template.getModel().fireTableDataChanged();
					Template.getModel().table.changeSelection(rowIndex, 0,
							false, false);
				}
			}

		});
		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Template.getModel().addRow(
						new Template("Untitled"
								+ Template.getModel().getRowCount(), ""));
				JScrollBar verticalScrollBar = tableScrollPane
						.getVerticalScrollBar();
				verticalScrollBar.setValue(verticalScrollBar.getMaximum());
			}

		});
		moveUpButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Template.getModel().moveUp();
			}

		});
		moveDownButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Template.getModel().moveDown();
			}

		});
		removeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Template.getModel().remove();
			}

		});
		contentPanel.setTransferHandler(templateTable.getTransferHandler());
	}

	/**
	 * 选择模板XML文件
	 */
	private void selectTemplate() {
		if (Animation.instance().isReadOnly())
			return;

		Template selectedTemp = Template.getModel().getSelection();
		int rowIndex = Template.getModel().getSelectionID();
		if (selectedTemp != null) {
			JFileChooser chooser = new JFileChooser(
					Configuration.default_filePath);
			chooser.setFileFilter(new MyFileFilter("xml", "XML File"));
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				selectedTemp
						.reload(chooser.getSelectedFile().getAbsolutePath());
				Template.getModel().fireTableDataChanged();
				Template.getModel().table.changeSelection(rowIndex, 0, false,
						false);
			}
		}
	}
}
