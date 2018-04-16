package animation.editor;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import animation.world.AniFrame;
import animation.world.Animation;
import animation.world.Module;

/**
 * 编辑器主窗口
 * 
 * @author 段链
 * 
 * @time 2012-9-14
 * 
 */
public class MainFrame extends JFrame implements Localizable {
	private static final long serialVersionUID = 4976319320270161164L;

	private JMenu menuFile = new JMenu();
	private JMenuItem menuFileNew = new JMenuItem();
	private JMenuItem menuFileExit = new JMenuItem();
	private JMenuItem menuFileOpen = new JMenuItem();
	private JMenuItem menuFileSave = new JMenuItem();
	private JMenuItem menuFileSaveAs = new JMenuItem();

	private JMenu menuEdit = new JMenu();
	private JMenuItem menuEditUndo = new JMenuItem();
	private JMenuItem menuEditRedo = new JMenuItem();
	private JMenuItem menuEditScale = new JMenuItem();
	private JMenuItem menuEditSetting = new JMenuItem();
	/**
	 * 在模块表上, 没有被用到的模块条目被标出
	 */
	private JMenuItem menuUnusedModule = new JMenuItem();
	/**
	 * 在帧表上, 没有被用到的帧条目被标出
	 */
	private JMenuItem menuUnusedFrame = new JMenuItem();

	private JMenu menuHelp = new JMenu();
	private JMenuItem menuHelpAbout = new JMenuItem();

	private JToolBar toolBar = new JToolBar();
	private JButton openButton = new JButton();
	private JButton saveButton = new JButton();
	private JButton aboutButton = new JButton();

	private JLabel statusBar = new JLabel();

	private JPanel workspacePanel = new JPanel();
	private WorkspaceManager manager = new WorkspaceManager(workspacePanel);

	private JMenu menuLanguage = new JMenu();
	private JRadioButtonMenuItem menuLanguageEnglish = new JRadioButtonMenuItem();
	private JRadioButtonMenuItem menuLanguageChinese = new JRadioButtonMenuItem();

	private JMenu menuLayout = new JMenu();
	private JRadioButtonMenuItem menuDefaultLayout = new JRadioButtonMenuItem();
	private JRadioButtonMenuItem menuModuleLayout = new JRadioButtonMenuItem();
	private JRadioButtonMenuItem menuSpriteLayout = new JRadioButtonMenuItem();
	private JRadioButtonMenuItem menuFrameLayout = new JRadioButtonMenuItem();
	private JRadioButtonMenuItem menuActorLayout = new JRadioButtonMenuItem();

	public MainFrame() {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			if (Configuration.resourceBundle.getClass().equals(
					Configuration.ENGLISH_RESOURCE_BUNDLE_NAME)) {
				menuLanguageEnglish.setSelected(true);
			} else {
				menuLanguageChinese.setSelected(true);
			}
			jbInit();
			updateLocalization();
			listenerInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化界面组件布局
	 */
	private void jbInit() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setFocusable(false);
		setLayout(new BorderLayout());

		menuFileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				InputEvent.CTRL_DOWN_MASK));
		menuFileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_DOWN_MASK));
		menuFile.add(menuFileNew);
		menuFile.add(menuFileOpen);
		menuFile.add(menuFileSave);
		menuFile.add(menuFileSaveAs);
		menuFile.addSeparator();
		menuFile.add(menuFileExit);

		/**
		 * 这个是全局快捷键
		 */
		menuEditUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				InputEvent.CTRL_DOWN_MASK));
		menuEditRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,
				InputEvent.CTRL_DOWN_MASK));
		menuEdit.add(menuEditUndo);
		menuEdit.add(menuEditRedo);
		menuEdit.addSeparator();
		menuEdit.add(menuUnusedModule);
		menuEdit.add(menuUnusedFrame);
		menuEdit.add(menuEditScale);
		menuEdit.addSeparator();
		menuEdit.add(menuEditSetting);

		menuDefaultLayout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
				InputEvent.CTRL_DOWN_MASK));
		menuModuleLayout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
				InputEvent.CTRL_DOWN_MASK));
		menuSpriteLayout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_DOWN_MASK));
		menuFrameLayout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
				InputEvent.CTRL_DOWN_MASK));
		menuActorLayout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
				InputEvent.CTRL_DOWN_MASK));
		menuDefaultLayout.setSelected(true);
		ButtonGroup layoutButtonGroup = new ButtonGroup();
		layoutButtonGroup.add(menuDefaultLayout);
		layoutButtonGroup.add(menuModuleLayout);
		layoutButtonGroup.add(menuSpriteLayout);
		layoutButtonGroup.add(menuFrameLayout);
		layoutButtonGroup.add(menuActorLayout);
		menuLayout.add(menuDefaultLayout);
		menuLayout.add(menuModuleLayout);
		menuLayout.add(menuSpriteLayout);
		menuLayout.add(menuFrameLayout);
		menuLayout.add(menuActorLayout);

		menuLanguageEnglish.setText("English");
		menuLanguageChinese.setText("Chinese");
		menuLanguage.setText("Language");
		ButtonGroup languageButtonGroup = new ButtonGroup();
		languageButtonGroup.add(menuLanguageEnglish);
		languageButtonGroup.add(menuLanguageChinese);
		menuLanguageEnglish.setSelected(true);
		menuLanguage.add(menuLanguageEnglish);
		menuLanguage.add(menuLanguageChinese);

		menuHelp.add(menuHelpAbout);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menuFile);
		menuBar.add(menuEdit);
		menuBar.add(menuLayout);
		menuBar.add(menuLanguage);
		menuBar.add(menuHelp);
		setJMenuBar(menuBar);

		openButton.setRequestFocusEnabled(false);
		saveButton.setRequestFocusEnabled(false);
		aboutButton.setRequestFocusEnabled(false);
		toolBar.add(openButton);
		toolBar.add(saveButton);
		toolBar.add(aboutButton);
		toolBar.setFloatable(false);

		statusBar.setText("ready");

		add(toolBar, BorderLayout.NORTH);
		add(statusBar, BorderLayout.SOUTH);
		add(workspacePanel, BorderLayout.CENTER);
		manager.layout(WorkspaceManager.DEFAULT_LAYOUT);
	}

	@Override
	public void updateLocalization() {
		setIconImage(new ImageIcon(
				MainFrame.class.getResource(Configuration.resourceBundle
						.getString("application_Icon"))).getImage());
		updateApplicationTitle();
		useIcon(openButton, "openFile_Icon");
		useIcon(saveButton, "saveFile_Icon");
		useIcon(aboutButton, "help_Icon");

		useIcon(menuFileNew, "newFileMenu_Icon");
		useIcon(menuFileOpen, "openFileMenu_Icon");
		useIcon(menuFileSave, "saveFileMenu_Icon");

		useIcon(menuEditUndo, "undoEditMenu_Icon");
		useIcon(menuEditRedo, "redoEditMenu_Icon");
		useIcon(menuEditSetting, "settingEditMenu_Icon");

		useIcon(menuHelpAbout, "aboutHelpMenu_Icon");

		useText(menuFile, "fileMenu_Text");
		useText(menuFileNew, "newFileMenu_Text");
		useText(menuFileOpen, "openFileMenu_Text");
		useText(menuFileSave, "saveFileMenu_Text");
		useText(menuFileSaveAs, "saveAsFileMenu_Text");
		useText(menuFileExit, "exitMenu_Text");

		useText(menuEdit, "editMenu_Text");
		useText(menuEditUndo, "undoEditMenu_Text");
		useText(menuEditRedo, "redoEditMenu_Text");
		useText(menuUnusedModule, "UnusedModuleEditMenu_Text");
		useText(menuUnusedFrame, "UnusedFrameEditMenu_Text");
		useText(menuEditScale, "scaleEditMenu_Text");
		useText(menuEditSetting, "settingEditMenu_Text");

		useText(menuHelp, "helpMenu_Text");
		useText(menuHelpAbout, "aboutMenu_Text");

		useToolTipText(openButton, "openFileMenu_Text");
		useToolTipText(saveButton, "saveFileMenu_Text");
		useToolTipText(aboutButton, "helpMenu_Text");

		useText(menuLayout, "layoutMenu_Text");
		useText(menuDefaultLayout, "defaultLayoutMenu_Text");
		useText(menuModuleLayout, "moduleLayoutMenu_Text");
		useText(menuSpriteLayout, "spriteLayoutMenu_Text");
		useText(menuFrameLayout, "frameLayoutMenu_Text");
		useText(menuActorLayout, "actionLayoutMenu_Text");
	}

	/**
	 * 用编辑器绑定的资源包本地化按钮的图标
	 * 
	 * @param button
	 * @param key
	 */
	public static void useIcon(AbstractButton button, String key) {
		button.setIcon(new ImageIcon(MainFrame.class
				.getResource(Configuration.resourceBundle.getString(key))));
	}

	/**
	 * 用编辑器绑定的资源包本地化标签的图标
	 * 
	 * @param label
	 * @param key
	 */
	public static void useIcon(JLabel label, String key) {
		label.setIcon(new ImageIcon(MainFrame.class
				.getResource(Configuration.resourceBundle.getString(key))));
	}

	/**
	 * 更新编辑器的标题
	 */
	private void updateApplicationTitle() {
		String title = Configuration.resourceBundle
				.getString("applicationTitle");
		if (Animation.instance().getFilePath() == null) {
			setTitle(title);
		} else {
			setTitle(title + "-" + Animation.instance().getFilePath());
		}
	}

	/**
	 * 用编辑器绑定的资源包本地化按钮的文件
	 * 
	 * @param button
	 * @param key
	 */
	public static void useText(AbstractButton button, String key) {
		button.setText(Configuration.resourceBundle.getString(key));
	}

	/**
	 * 用编辑器绑定的资源包本地化标签的文本
	 * 
	 * @param label
	 * @param key
	 */
	public static void useText(JLabel label, String key) {
		label.setText(Configuration.resourceBundle.getString(key));
	}

	/**
	 * 用编辑器绑定的资源包本地化按钮的提示信息
	 * 
	 * @param button
	 * @param key
	 */
	public static void useToolTipText(AbstractButton button, String key) {
		button.setToolTipText(Configuration.resourceBundle.getString(key));
	}

	/**
	 * 初始化界面相关的组件的监听器设定
	 */
	private void listenerInit() {
		menuFileNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!checkModify())
					return;
				Animation.clear();
				setTitle("Untitled");
			};
		});
		menuFileOpen.addActionListener(new OpenButton_actionAdapter(this));
		menuFileSave.addActionListener(new SaveButton_actionAdapter(this));
		menuFileSaveAs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAs();
			}
		});
		menuFileExit.addActionListener(new MenuFileExit_actionAdapter(this));

		menuEditUndo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (AnimationEditor.undoManager.canUndo()) {
					AnimationEditor.undoManager.undo();
				}
			}
		});
		menuEditRedo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (AnimationEditor.undoManager.canRedo()) {
					AnimationEditor.undoManager.redo();
				}
			}
		});
		menuUnusedModule.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ListSelectionModel selectionModel = Module.getModel()
						.getSelectionModel();
				selectionModel.clearSelection();
				for (Module module : Module.getUnusedModules()) {
					int id = module.getId();
					selectionModel.addSelectionInterval(id, id);
				}
			}
		});
		menuUnusedFrame.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ListSelectionModel selectionModel = AniFrame.getModel()
						.getSelectionModel();
				selectionModel.clearSelection();
				for (AniFrame frame : AniFrame.getUnusedFrames()) {
					int id = frame.getId();
					selectionModel.addSelectionInterval(id, id);
				}
			}
		});
		menuEditScale.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String value = JOptionPane.showInputDialog("Input scale value");
				if (value != null) {
					try {
						double d = Double.parseDouble(value);
						if (JOptionPane.showConfirmDialog(MainFrame.this,
								"Are you sure?", "Scale to " + d,
								JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
							Animation.instance().scale(d);
						}
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(MainFrame.this,
								"It's not a double value!");
					}
				}
			}
		});
		menuEditSetting.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog dlg = new SettingDialog();
				dlg.setModal(true);
				dlg.pack();
				dlg.setLocationRelativeTo(MainFrame.this);
				dlg.setVisible(true);
			}
		});

		menuDefaultLayout.addActionListener(new ChangeLayout_actionAdapter(
				WorkspaceManager.DEFAULT_LAYOUT));
		menuModuleLayout.addActionListener(new ChangeLayout_actionAdapter(
				WorkspaceManager.MODULE_LAYOUT));
		menuSpriteLayout.addActionListener(new ChangeLayout_actionAdapter(
				WorkspaceManager.SPRITE_LAYOUT));
		menuFrameLayout.addActionListener(new ChangeLayout_actionAdapter(
				WorkspaceManager.FRAME_LAYOUT));
		menuActorLayout.addActionListener(new ChangeLayout_actionAdapter(
				WorkspaceManager.ACTION_LAYOUT));

		menuLanguageEnglish
				.addActionListener(new LanguageMenuItem_actionAdapter(this));
		menuLanguageChinese
				.addActionListener(new LanguageMenuItem_actionAdapter(this));

		menuHelpAbout.addActionListener(new MenuHelpAbout_actionAdapter(this));

		openButton.addActionListener(new OpenButton_actionAdapter(this));
		saveButton.addActionListener(new SaveButton_actionAdapter(this));
		aboutButton.addActionListener(new MenuHelpAbout_actionAdapter(this));
	}

	class OpenButton_actionAdapter implements ActionListener {
		MainFrame delegate;

		public OpenButton_actionAdapter(MainFrame delegate) {
			this.delegate = delegate;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			delegate.openButton_actionPerformed(e);
		}
	}

	class SaveButton_actionAdapter implements ActionListener {
		MainFrame delegate;

		public SaveButton_actionAdapter(MainFrame delegate) {
			this.delegate = delegate;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			delegate.save();
		}

	}

	class MenuFileExit_actionAdapter implements ActionListener {
		MainFrame delegate;

		public MenuFileExit_actionAdapter(MainFrame delegate) {
			this.delegate = delegate;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			delegate.menuFileExit_actionPerformed(e);
		}

	}

	class ChangeLayout_actionAdapter implements ActionListener {
		String layout;

		public ChangeLayout_actionAdapter(String layout) {
			this.layout = layout;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			manager.layout(layout);
		}

	}

	class LanguageMenuItem_actionAdapter implements ActionListener {
		MainFrame delegate;

		public LanguageMenuItem_actionAdapter(MainFrame delegate) {
			this.delegate = delegate;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			delegate.languageMenuItem_actionPerformed(e);
		}

	}

	class MenuHelpAbout_actionAdapter implements ActionListener {
		MainFrame delegate;

		public MenuHelpAbout_actionAdapter(MainFrame delegate) {
			this.delegate = delegate;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			delegate.menuHelpAbout_actionPerformed(e);
		}

	}

	/**
	 * 打开用户指定的动画XML文件
	 * 
	 * @param e
	 */
	public void openButton_actionPerformed(ActionEvent e) {
		if (!checkModify())
			return;
		JFileChooser chooser = new JFileChooser(Configuration.default_filePath);
		chooser.setFileFilter(new MyFileFilter("xml", "Animation XML File"));
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			openAnimationFile(chooser.getSelectedFile().getAbsolutePath());
			Configuration.default_filePath = chooser.getCurrentDirectory()
					.getAbsolutePath();
			updateApplicationTitle();
		}
	}

	/**
	 * 保存动画
	 */
	public void save() {
		if (Animation.instance().getFilePath() == null) {
			saveAs();
		} else {
			Animation.instance().saveAnimationToXml(
					Animation.instance().getFilePath());
		}
	}

	/**
	 * 把动画转换成XML文件, 并保存在用户指定的位置
	 */
	private void saveAs() {
		JFileChooser chooser = new JFileChooser(Configuration.default_filePath);
		MyFileFilter filter = new MyFileFilter("xml", "Animation XML File");
		chooser.setFileFilter(filter);
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			String xmlFile = chooser.getSelectedFile().getAbsolutePath();
			if (filter.getExtension(chooser.getSelectedFile()) == null) {
				xmlFile += ".xml";
			}
			Animation.instance().saveAnimationToXml(xmlFile);
			updateApplicationTitle();
		}
	}

	/**
	 * 退出编辑器
	 * 
	 * @param e
	 */
	public void menuFileExit_actionPerformed(ActionEvent e) {
		System.exit(0);
	}

	/**
	 * 应用选择的语言本地化界面
	 * 
	 * @param e
	 */
	public void languageMenuItem_actionPerformed(ActionEvent e) {
		String resourceBundleName;
		if (menuLanguageEnglish.isSelected()) {
			resourceBundleName = Configuration.ENGLISH_RESOURCE_BUNDLE_NAME;
		} else {
			resourceBundleName = Configuration.CHINESE_RESOURCE_BUNDLE_NAME;
		}
		boolean update = !Configuration.resourceBundle.getClass().getName()
				.equals(resourceBundleName);
		if (update) {
			Configuration.resourceBundle = ResourceBundle
					.getBundle(resourceBundleName);
			updateComponentLocalization(this);
		}
	}

	/**
	 * 用编辑器绑定的资源包本地化组件
	 * 
	 * @param c
	 *            界面组件
	 */
	public static void updateComponentLocalization(Component c) {
		updateComponentLocalization0(c);
		c.invalidate();
		c.validate();
		c.repaint();
	}

	/**
	 * 本地化组件
	 * 
	 * @param c
	 *            界面组件
	 */
	private static void updateComponentLocalization0(Component c) {
		if (c instanceof Localizable) {
			((Localizable) c).updateLocalization();
		}
		Component[] childrens = null;
		if (c instanceof JMenu) {
			childrens = ((JMenu) c).getMenuComponents();
		} else if (c instanceof Container) {
			childrens = ((Container) c).getComponents();
		}

		if (childrens != null) {
			for (Component child : childrens) {
				updateComponentLocalization0(child);
			}
		}
	}

	/**
	 * 弹出关于对话框
	 * 
	 * @param e
	 */
	public void menuHelpAbout_actionPerformed(ActionEvent e) {
		AboutDialog dlg = new AboutDialog(this);

		dlg.setModal(true);
		dlg.pack();
		dlg.setLocationRelativeTo(this);
		dlg.setVisible(true);
	}

	/**
	 * 检测打开中的动画是否被修改过, 有则提示用户是否保存动画
	 * 
	 * @return true动画没有被修改过或者不需要保存修改, false则相反
	 */
	private boolean checkModify() {
		if (Animation.instance().isModified()) {
			int result = JOptionPane.showConfirmDialog(this,
					"Animation is Modified, do you want Save or no?", "Save",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				save();
			} else if (result == JOptionPane.CANCEL_OPTION) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 打开动画XML文件
	 * 
	 * @param filename
	 *            动画XML文件路径
	 */
	public void openAnimationFile(String filename) {
		Animation.clear();
		Animation.instance().loadAnimationFromXml(filename);
		updateApplicationTitle();
	}

	public void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			if (checkModify()) {
				Configuration.save();
			}
		} else if (e.getID() == WindowEvent.WINDOW_ACTIVATED) {
			Animation.instance().checkReadOnly();
		}
		super.processWindowEvent(e);
	}
}
