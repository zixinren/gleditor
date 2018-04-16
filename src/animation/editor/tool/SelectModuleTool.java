package animation.editor.tool;

import java.awt.event.MouseEvent;

import animation.editor.ScalableViewer;
import animation.world.Animation;
import animation.world.Module;

/**
 * 选择模块工具
 * 
 * @author 段链
 * 
 * @time 2012-9-20
 * 
 */
public class SelectModuleTool extends ToolAdapter {

	public SelectModuleTool(ScalableViewer viewer) {
		super(viewer);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		selectModule(e, true);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		selectModule(e, false);
	}

	/**
	 * 选择模块
	 * 
	 * @param e
	 * @param change
	 *            强制刷新选择的模块
	 */
	public void selectModule(MouseEvent e, boolean change) {
		Module module = Module.getModel().getSelection();
		if (module == null || change || !module.contains(e.getPoint())) {
			module = Animation.instance().getModuleAtPoint(e.getPoint());
		}
		/**
		 * 按住CTRL键是多选, 不按是单选
		 */
		if (!e.isControlDown()) {
			Module.getModel().getSelectionModel().clearSelection();
		}
		if (module != null) {
			int id = module.getId();
			Module.getModel().getSelectionModel().addSelectionInterval(id, id);
		}
	}
}
