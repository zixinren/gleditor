package animation.editor.modulesmap;

import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import animation.editor.ModulePane;
import animation.editor.ScalableViewer;
import animation.editor.tool.ToolAdapter;
import animation.world.Animation;
import animation.world.Module;

/**
 * 模块映射选择工具
 * 
 * @author 段链
 * @time 2012-9-15
 */
public class ModuleMapSelectTool extends ToolAdapter {
	private static boolean isSelect = false;
	/**
	 * 当前选定的原始模块
	 */
	public static int currentModuleID = 0;

	public ModuleMapSelectTool(ScalableViewer viewer) {
		super(viewer);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (e.getClickCount() == 1) {
				int id = e.getX() / ModuleSrcViewer.MODULE_WIDTH;
				Module module = Animation.instance().getModuleList().get(id);
				Module.getModel().selectObject(module);
				getViewer().repaint();
				isSelect = true;
				currentModuleID = id;
			} else if (e.getClickCount() == 2) {
				/**
				 * 移除对选定模块的映射
				 */
				int id = e.getX() / ModuleSrcViewer.MODULE_WIDTH;
				ModulesMapData modulesMapData = ModulesStylesData
						.getModulesMapData();
				for (int i = 0; i < modulesMapData.getModulesMapSize(); ++i) {
					if (id != modulesMapData.getModulesMapSrcData(i))
						continue;
					modulesMapData.removeMapData(i);
				}
				ModulePane.frameJScrollPane.repaint();
				getViewer().repaint();
			} else {
				isSelect = false;
			}
		}
	}

	/**
	 * 是否选择一个模块
	 */
	public static boolean isSelect() {
		return isSelect;
	}
}
