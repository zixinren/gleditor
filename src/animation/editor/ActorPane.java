package animation.editor;

import static animation.editor.MainFrame.useIcon;
import static animation.editor.MainFrame.useToolTipText;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import animation.editor.tool.AbstractTool;
import animation.editor.tool.BoxEditTool;
import animation.editor.tool.MoveSpriteTool;
import animation.world.AniFrame;

/**
 * 角色面板
 * 
 * @author 段链
 * @time 2012-9-15
 */
public class ActorPane extends JPanel implements Localizable {
	private static final long serialVersionUID = 800750853334964384L;

	JToolBar toolBar = new JToolBar();
	JButton zoomButton = new JButton();
	JButton flipXButton = new JButton();
	JButton flipYButton = new JButton();
	JButton rotateButton = new JButton();
	/**
	 * 编辑角色碰撞框和攻击框的按钮<br>
	 * 按钮按下时, {@link #showBoxButton}不可用, 碰撞框和攻击框可见, 当前编辑工具为
	 * {@link #BOX_EDIT_TOOL}<br>
	 * 按钮弹出时, {@link #showBoxButton}可用, 当前编辑工具为{@link #MOVE_SPRITE_TOOL}<br>
	 */
	JToggleButton editBoxButton = new JToggleButton();
	JToggleButton gridButton = new JToggleButton();
	JToggleButton showBoxButton = new JToggleButton();

	JScrollPane scrollPane = new JScrollPane();
	ActorViewer viewer = new ActorViewer();

	JToolBar toolBar2 = new JToolBar();
	JTextField collisionBoxText = new JTextField();
	JTextField attackBoxText = new JTextField();
	JLabel collisionBoxLabel = new JLabel();
	JLabel attackBoxLabel = new JLabel();

	private AbstractTool[] tools = new AbstractTool[] {
			new MoveSpriteTool(viewer), new BoxEditTool(viewer) };

	public static final int MOVE_SPRITE_TOOL = 0;
	public static final int BOX_EDIT_TOOL = 1;

	/**
	 * 角色碰撞框和攻击框信息格式
	 */
	private MessageFormat format = new MessageFormat(
			"[{0,number,###}, {1,number,###}, {2,number,###}, {3,number,###}]");

	public ActorPane() {
		try {
			jbInit();
			updateLocalization();
			listenerInit();
			viewer.setTool(tools[MOVE_SPRITE_TOOL]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化角色面板组件
	 */
	private void jbInit() {
		gridButton.setSelected(true);
		zoomButton.setRequestFocusEnabled(false);
		flipXButton.setRequestFocusEnabled(false);
		flipYButton.setRequestFocusEnabled(false);
		rotateButton.setRequestFocusEnabled(false);
		editBoxButton.setRequestFocusEnabled(false);
		gridButton.setRequestFocusEnabled(false);
		showBoxButton.setRequestFocusEnabled(false);

		flipXButton.setMnemonic(KeyEvent.VK_X);
		flipYButton.setMnemonic(KeyEvent.VK_Y);
		rotateButton.setMnemonic(KeyEvent.VK_R);
		editBoxButton.setMnemonic(KeyEvent.VK_B);
		gridButton.setMnemonic(KeyEvent.VK_G);

		toolBar.setOrientation(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		toolBar.add(editBoxButton);
		toolBar.addSeparator();
		toolBar.add(zoomButton);
		toolBar.addSeparator();
		toolBar.add(flipXButton);
		toolBar.add(flipYButton);
		toolBar.add(rotateButton);
		toolBar.addSeparator();
		toolBar.add(showBoxButton);
		toolBar.add(gridButton);

		collisionBoxText.setPreferredSize(new Dimension(100, 24));
		attackBoxText.setPreferredSize(new Dimension(100, 24));

		/**
		 * 使用JToolBar来排布非按钮的界面组件, 是为了它的addSeparator功能
		 */
		toolBar2.setFloatable(false);
		toolBar2.add(viewer.getLocationLabel());
		toolBar2.addSeparator();
		toolBar2.add(collisionBoxLabel);
		toolBar2.addSeparator();
		toolBar2.add(collisionBoxText);
		toolBar2.addSeparator();
		toolBar2.add(attackBoxLabel);
		toolBar2.addSeparator();
		toolBar2.add(attackBoxText);

		scrollPane.getViewport().add(viewer);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		setDebugGraphicsOptions(0);
		setLayout(new BorderLayout());
		add(toolBar, BorderLayout.WEST);
		add(scrollPane, BorderLayout.CENTER);
		add(toolBar2, BorderLayout.SOUTH);
	}

	@Override
	public void updateLocalization() {
		useToolTipText(zoomButton, "zoomButton_ToolTip");
		useToolTipText(flipXButton, "flipXButton_ToolTip");
		useToolTipText(flipYButton, "flipYButton_ToolTip");
		useToolTipText(rotateButton, "rotateButton_ToolTip");
		useToolTipText(editBoxButton, "editBoxButton_ToolTip");
		useToolTipText(gridButton, "gridButton_ToolTip");
		useToolTipText(showBoxButton, "showBoxButton_ToolTip");

		useIcon(zoomButton, "zoomButton_Icon");
		useIcon(flipXButton, "flipXButton_Icon");
		useIcon(flipYButton, "flipYButton_Icon");
		useIcon(rotateButton, "rotateButton_Icon");
		useIcon(editBoxButton, "editBoxButton_Icon");
		useIcon(gridButton, "gridButton_Icon");
		useIcon(showBoxButton, "showBoxButton_Icon");

		collisionBoxLabel.setText("CollisionBox: ");
		attackBoxLabel.setText("AttackBox: ");
	}

	/**
	 * 初始化界面监听器
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
		editBoxButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (editBoxButton.isSelected()) {
					viewer.setTool(tools[BOX_EDIT_TOOL]);
					viewer.setShowBox(true);
					showBoxButton.setEnabled(false);
				} else {
					viewer.setTool(tools[MOVE_SPRITE_TOOL]);
					showBoxButton.setEnabled(true);
				}
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
		flipXButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				viewer.flipX();
			}

		});
		flipYButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				viewer.flipY();
			}

		});
		rotateButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				viewer.rotate();
			}

		});

		/**
		 * 如果帧表的选择改变, 则相应的改变框文本域的显示
		 */
		AniFrame.getModel().addSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateBox();
			}

		});

		attackBoxText.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (viewer.getFrame() == null)
					return;
				if (viewer.getFrame().attackBox == null) {
					viewer.getFrame().attackBox = new Rectangle();
					updateBox();
					return;
				}

				Rectangle box = viewer.getFrame().attackBox;
				try {
					Object[] array = format.parse(attackBoxText.getText());
					viewer.getFrame().attackBox = new Rectangle();
					box = viewer.getFrame().attackBox;
					box.x = Integer.parseInt(array[0].toString());
					box.y = Integer.parseInt(array[1].toString());
					box.width = Integer.parseInt(array[2].toString()) - box.x;
					box.height = Integer.parseInt(array[3].toString()) - box.y;
					viewer.repaint();
				} catch (ParseException ex) {
					Toolkit.getDefaultToolkit().beep();
				}
			}

		});
		collisionBoxText.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (viewer.getFrame() == null)
					return;
				if (viewer.getFrame().collisionBox == null) {
					viewer.getFrame().collisionBox = new Rectangle();
					updateBox();
					return;
				}

				Rectangle box = viewer.getFrame().collisionBox;
				try {
					Object[] array = format.parse(collisionBoxText.getText());
					viewer.getFrame().collisionBox = new Rectangle();
					box = viewer.getFrame().collisionBox;
					box.x = Integer.parseInt(array[0].toString());
					box.y = Integer.parseInt(array[1].toString());
					box.width = Integer.parseInt(array[2].toString()) - box.x;
					box.height = Integer.parseInt(array[3].toString()) - box.y;
					viewer.repaint();
				} catch (ParseException ex) {
					Toolkit.getDefaultToolkit().beep();
				}
			}

		});
	}

	/**
	 * 更新角色面板上的碰撞框和攻击框文本域
	 */
	public void updateBox() {
		if (viewer.getFrame() != null) {
			Rectangle box = viewer.getFrame().collisionBox;
			String text = format.format(new Integer[] { box.x, box.y,
					box.x + box.width, box.y + box.height });
			collisionBoxText.setText(text);

			box = viewer.getFrame().attackBox;
			text = format.format(new Integer[] { box.x, box.y,
					box.x + box.width, box.y + box.height });
			attackBoxText.setText(text);
		}
	}
}
