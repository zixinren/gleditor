package animation.editor;

import static animation.editor.MainFrame.useText;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;

/**
 * 编辑器配置对话框
 * 
 * @author 段链
 * 
 * @time 2012-9-14
 * 
 */
public class SettingDialog extends JDialog implements Localizable {
	private static final long serialVersionUID = 2392898135171949179L;

	private JPanel contentPane = new JPanel();
	private JPanel northPanel = new JPanel();

	private JButton buttonOK = new JButton();
	private JButton buttonCancel = new JButton();

	private JButton buttonBackgroundColor = new JButton();
	private JButton buttonGridColor = new JButton();
	private JButton buttonModuleBorderColor = new JButton();
	private JSpinner spinnerGridSize = new JSpinner();
	private JButton buttonAxisColor = new JButton();
	private JButton buttonSelectedBorderColor = new JButton();
	private JButton buttonAttackBoxColor = new JButton();
	private JSpinner spinnerTick = new JSpinner();
	private JButton buttonCollisionBoxColor = new JButton();

	private JLabel backgroundColorLabel = new JLabel();
	private JLabel gridColorLabel = new JLabel();
	private JLabel moduleBorderColorLabel = new JLabel();
	private JLabel gridSizeLabel = new JLabel();
	private JLabel axisColorLabel = new JLabel();
	private JLabel selectedBorderColorLabel = new JLabel();
	private JLabel attackBoxColorLabel = new JLabel();
	private JLabel tickLabel = new JLabel();
	private JLabel collisionBoxColorLabel = new JLabel();

	public SettingDialog() {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			jbInit();
			updateLocalization();
			listenerInit();
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化界面组件布局
	 */
	private void jbInit() {
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

		contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		contentPane.setLayout(new BorderLayout());

		northPanel.setLayout(new GridLayout(5, 2, 5, 5));
		addColorLabelAndComponent(backgroundColorLabel, buttonBackgroundColor);
		addColorLabelAndComponent(gridColorLabel, buttonGridColor);
		addColorLabelAndComponent(moduleBorderColorLabel,
				buttonModuleBorderColor);
		addColorLabelAndComponent(axisColorLabel, buttonAxisColor);
		addColorLabelAndComponent(selectedBorderColorLabel,
				buttonSelectedBorderColor);
		addColorLabelAndComponent(attackBoxColorLabel, buttonAttackBoxColor);
		addColorLabelAndComponent(gridSizeLabel, spinnerGridSize);
		addColorLabelAndComponent(collisionBoxColorLabel,
				buttonCollisionBoxColor);
		addColorLabelAndComponent(tickLabel, spinnerTick);

		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2, 5, 5));
		panel.add(buttonOK);
		panel.add(buttonCancel);
		southPanel.add(Box.createHorizontalBox(), BorderLayout.WEST);
		southPanel.add(panel, BorderLayout.EAST);

		contentPane.add(northPanel, BorderLayout.NORTH);
		contentPane.add(southPanel, BorderLayout.SOUTH);
	}

	public void addColorLabelAndComponent(JLabel label, JComponent comp) {
		comp.setPreferredSize(new Dimension(48, 24));
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(label, BorderLayout.WEST);
		panel.add(comp, BorderLayout.EAST);

		northPanel.add(panel);
	}

	@Override
	public void updateLocalization() {
		setTitle(Configuration.resourceBundle.getString("settingDialog_Title"));
		useText(tickLabel, "tick_Label");
		useText(attackBoxColorLabel, "attackBoxColor_Label");
		useText(collisionBoxColorLabel, "collisionBoxColor_Label");
		useText(gridColorLabel, "gridColor_Label");
		useText(axisColorLabel, "axisColor_Label");
		useText(backgroundColorLabel, "backgroundColorLabel");
		useText(gridSizeLabel, "gridSizeLabel");
		useText(selectedBorderColorLabel, "selectedBorderColor_Label");
		useText(moduleBorderColorLabel, "moduleBorderColor_Label");
		useText(buttonOK, "okButton_Text");
		useText(buttonCancel, "cancelButton_Text");
	}

	/**
	 * 初始化界面相关的组件的监听器设定
	 */
	private void listenerInit() {
		buttonBackgroundColor.addActionListener(new ColorChooserActionAdapter(
				buttonBackgroundColor));
		buttonModuleBorderColor
				.addActionListener(new ColorChooserActionAdapter(
						buttonModuleBorderColor));
		buttonSelectedBorderColor
				.addActionListener(new ColorChooserActionAdapter(
						buttonSelectedBorderColor));
		buttonGridColor.addActionListener(new ColorChooserActionAdapter(
				buttonGridColor));
		buttonAxisColor.addActionListener(new ColorChooserActionAdapter(
				buttonAxisColor));
		buttonAttackBoxColor.addActionListener(new ColorChooserActionAdapter(
				buttonAttackBoxColor));
		buttonGridColor.addActionListener(new ColorChooserActionAdapter(
				buttonGridColor));
		buttonAttackBoxColor.addActionListener(new ColorChooserActionAdapter(
				buttonAttackBoxColor));
		buttonCollisionBoxColor
				.addActionListener(new ColorChooserActionAdapter(
						buttonCollisionBoxColor));

		buttonOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onOK();
			}

		});
		buttonCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}

		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				onCancel();
			}

		});

		contentPane.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ESC");
		contentPane.getActionMap().put("ESC", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}

		});
	}

	class ColorChooserActionAdapter implements ActionListener {
		JComponent setBackComp;

		public ColorChooserActionAdapter(JComponent setBackComp) {
			this.setBackComp = setBackComp;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Color bg = JColorChooser.showDialog(null, "Choose Color",
					setBackComp.getBackground());
			if (bg != null) {
				setBackComp.setBackground(bg);
			}
		}

	}

	/**
	 * 确定应用编辑器新设定
	 */
	public void onOK() {
		saveData();
		dispose();
	}

	/**
	 * 取消编辑器设定
	 */
	public void onCancel() {
		dispose();
	}

	/**
	 * 载入编辑器设定数据
	 */
	public void loadData() {
		spinnerGridSize.setValue(Configuration.size_grid);
		spinnerTick.setValue(Configuration.tick);

		buttonBackgroundColor.setBackground(Configuration.color_background);
		buttonModuleBorderColor
				.setBackground(Configuration.color_module_border);
		buttonSelectedBorderColor
				.setBackground(Configuration.color_selected_border);
		buttonGridColor.setBackground(Configuration.color_grid);
		buttonAxisColor.setBackground(Configuration.color_axis);
		buttonAttackBoxColor.setBackground(Configuration.color_attack_box);
		buttonCollisionBoxColor
				.setBackground(Configuration.color_collision_box);
	}

	/**
	 * 保存并应用编辑器设定数据
	 */
	public void saveData() {
		Configuration.size_grid = ((Integer) spinnerGridSize.getValue())
				.intValue();
		Configuration.tick = ((Integer) spinnerTick.getValue()).intValue();

		Configuration.color_background = buttonBackgroundColor.getBackground();
		Configuration.color_module_border = buttonModuleBorderColor
				.getBackground();
		Configuration.color_selected_border = buttonSelectedBorderColor
				.getBackground();
		Configuration.color_grid = buttonGridColor.getBackground();
		Configuration.color_axis = buttonAxisColor.getBackground();
		Configuration.color_attack_box = buttonAttackBoxColor.getBackground();
		Configuration.color_collision_box = buttonCollisionBoxColor
				.getBackground();
		Configuration.firePropertyChange();
	}
}
