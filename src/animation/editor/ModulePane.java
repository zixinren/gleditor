package animation.editor;

import static animation.editor.MainFrame.useIcon;
import static animation.editor.MainFrame.useToolTipText;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import animation.editor.modulesmap.FrameViewer;
import animation.editor.modulesmap.ModuleMapSelectTool;
import animation.editor.modulesmap.ModuleMapViewer;
import animation.editor.modulesmap.ModuleSelectTool;
import animation.editor.modulesmap.ModuleSrcViewer;
import animation.editor.modulesmap.ModulesMapPane;
import animation.editor.tool.AbstractTool;
import animation.editor.tool.DragModuleTool;
import animation.editor.tool.MoveImageTool;
import animation.editor.tool.MoveModuleTool;
import animation.editor.tool.PencilTool;
import animation.world.Animation;

/**
 * 模块面板
 * 
 * @author 段链
 * @time 2012-9-15
 */
public class ModulePane extends JPanel implements Localizable {
	private static final long serialVersionUID = 7615646544811932313L;

	JScrollPane scrollPane = new JScrollPane();
	ModuleViewer viewer = new ModuleViewer();

	JPanel modulesMapViewer = new JPanel();
	JScrollPane frameViewer = new JScrollPane();

	JToolBar toolBar = new JToolBar();
	JToggleButton dragButton = new JToggleButton();
	JToggleButton moveModuleButton = new JToggleButton();
	JToggleButton moveImageButton = new JToggleButton();
	JToggleButton pencilButton = new JToggleButton();
	JButton zoomButton = new JButton();
	JButton selectImageButton = new JButton();
	JToggleButton modulesMapButton = new JToggleButton();
	JButton expandImageButton = new JButton();
	JButton collapseImageButton = new JButton();

	private AbstractTool[] tools = new AbstractTool[] {
			new DragModuleTool(viewer), new MoveModuleTool(viewer),
			new MoveImageTool(viewer), new PencilTool(viewer) };

	public static final int DRAG_MODULE_TOOL = 0;
	public static final int MOVE_MODULE_TOOL = 1;
	public static final int MOVE_IMAGE_TOOL = 2;
	public static final int PENCIL_TOOL = 3;

	public static JScrollPane frameJScrollPane = new JScrollPane();
	public static JScrollPane mapJScrollPane = new JScrollPane();

	public ModulePane() {
		try {
			jbInit();
			updateLocalization();
			listenerInit();
			viewer.setTool(tools[DRAG_MODULE_TOOL]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化模块面板组件
	 */
	private void jbInit() {
		dragButton.setSelected(true);

		dragButton.setRequestFocusEnabled(false);
		moveModuleButton.setRequestFocusEnabled(false);
		moveImageButton.setRequestFocusEnabled(false);
		pencilButton.setRequestFocusEnabled(false);
		zoomButton.setRequestFocusEnabled(false);
		expandImageButton.setRequestFocusEnabled(false);
		collapseImageButton.setRequestFocusEnabled(false);
		selectImageButton.setRequestFocusEnabled(false);

		dragButton.setMnemonic(KeyEvent.VK_D);
		moveModuleButton.setMnemonic(KeyEvent.VK_M);
		moveImageButton.setMnemonic(KeyEvent.VK_I);
		pencilButton.setMnemonic(KeyEvent.VK_L);

		ButtonGroup toolButtonGroup = new ButtonGroup();
		toolButtonGroup.add(dragButton);
		toolButtonGroup.add(moveModuleButton);
		toolButtonGroup.add(moveImageButton);
		toolButtonGroup.add(pencilButton);

		toolBar.setOrientation(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		toolBar.add(dragButton);
		toolBar.add(moveModuleButton);
		toolBar.add(moveImageButton);
		toolBar.add(pencilButton);
		toolBar.addSeparator();
		toolBar.add(zoomButton);
		toolBar.addSeparator();
		toolBar.add(selectImageButton);
		toolBar.add(modulesMapButton);
		toolBar.addSeparator();
		toolBar.add(expandImageButton);
		toolBar.add(collapseImageButton);

		scrollPane.getViewport().add(viewer);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		setLayout(new BorderLayout());
		add(toolBar, BorderLayout.WEST);
		add(scrollPane, BorderLayout.CENTER);

		buildModulesMapViewer();
	}

	/**
	 * 创建模块映射视图
	 */
	private void buildModulesMapViewer() {
		ScalableViewer moduleSourceViewer = new ModuleSrcViewer();
		moduleSourceViewer.setTool(new ModuleSelectTool(moduleSourceViewer));

		JScrollPane srcJScrollPane = new JScrollPane();
		srcJScrollPane.getViewport().add(moduleSourceViewer);
		srcJScrollPane.setBorder(new TitledBorder(new EtchedBorder(),
				"Modules Viewer"));
		srcJScrollPane.getHorizontalScrollBar().setUnitIncrement(16);

		ScalableViewer moduleDistinationViewer = new ModuleMapViewer();
		moduleDistinationViewer.setTool(new ModuleMapSelectTool(
				moduleDistinationViewer));

		mapJScrollPane.getViewport().add(moduleDistinationViewer);
		mapJScrollPane.setBorder(new TitledBorder(new EtchedBorder(),
				"Modules Map Viewer"));
		mapJScrollPane.getHorizontalScrollBar().setUnitIncrement(16);

		JPanel topPane = new JPanel();
		topPane.setLayout(new BorderLayout());
		topPane.add(srcJScrollPane, BorderLayout.NORTH);
		topPane.add(mapJScrollPane, BorderLayout.SOUTH);

		ScalableViewer frameViewer = new FrameViewer();

		frameJScrollPane.getViewport().add(frameViewer);
		frameJScrollPane.setBorder(new TitledBorder(new EtchedBorder(),
				"Frame Viewer"));
		frameJScrollPane.getHorizontalScrollBar().setUnitIncrement(16);

		JPanel bottomPane = new JPanel();
		bottomPane.setLayout(new BorderLayout());
		bottomPane.add(frameJScrollPane, BorderLayout.NORTH);
		bottomPane.add(new ModulesMapPane(), BorderLayout.CENTER);

		modulesMapViewer.setLayout(new BorderLayout());
		modulesMapViewer.add(topPane, BorderLayout.NORTH);
		modulesMapViewer.add(bottomPane, BorderLayout.CENTER);
	}

	@Override
	public void updateLocalization() {
		useToolTipText(dragButton, "dragButton_ToolTip");
		useToolTipText(moveModuleButton, "moveModuleButton_ToolTip");
		useToolTipText(moveImageButton, "moveImageButton_ToolTip");
		useToolTipText(pencilButton, "pencilButton_ToolTip");
		useToolTipText(zoomButton, "zoomButton_ToolTip");
		useToolTipText(expandImageButton, "expandImageButton_ToolTip");
		useToolTipText(collapseImageButton, "collapseImageButton_ToolTip");
		useToolTipText(selectImageButton, "selectImageButton_ToolTip");
		useToolTipText(modulesMapButton, "modulesMapButton_ToolTip");

		useIcon(dragButton, "dragButton_Icon");
		useIcon(moveModuleButton, "moveModuleButton_Icon");
		useIcon(moveImageButton, "moveImageButton_Icon");
		useIcon(pencilButton, "pencilButton_Icon");
		useIcon(zoomButton, "zoomButton_Icon");
		useIcon(expandImageButton, "expandImageButton_Icon");
		useIcon(collapseImageButton, "collapseImageButton_Icon");
		useIcon(selectImageButton, "selectImageButton_Icon");
		useIcon(modulesMapButton, "modulesMapButton_Icon");
	}

	/**
	 * 初始化监听器
	 */
	private void listenerInit() {
		dragButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				viewer.setTool(tools[DRAG_MODULE_TOOL]);
				viewer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

		});
		moveModuleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				viewer.setTool(tools[MOVE_MODULE_TOOL]);
				viewer.setCursor(Cursor
						.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});
		moveImageButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				viewer.setTool(tools[MOVE_IMAGE_TOOL]);
				viewer.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

		});
		pencilButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				viewer.setTool(tools[PENCIL_TOOL]);
				viewer.setCursor(Cursor
						.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			}

		});
		selectImageButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectImageFile();
			}

		});
		expandImageButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Animation.aniImage().expandImage();
			}

		});
		collapseImageButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Animation.aniImage().shrinkImage();
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
		modulesMapButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (modulesMapButton.isSelected()) {
					viewer.remove(scrollPane);
					viewer.add(modulesMapViewer, BorderLayout.CENTER);
				} else {
					viewer.remove(modulesMapViewer);
					viewer.add(scrollPane, BorderLayout.CENTER);
				}
				viewer.revalidate();
				viewer.repaint();
			}

		});
	}

	/**
	 * 打开文件选择对话框, 选择模块使用的图片
	 */
	public void selectImageFile() {
		if (Animation.instance().isReadOnly())
			return;
		JFileChooser chooser = new JFileChooser(Configuration.default_filePath);
		chooser.setFileFilter(new MyFileFilter(".png", "PNG File"));
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				Animation.aniImage().loadImageFile(
						chooser.getSelectedFile().getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
