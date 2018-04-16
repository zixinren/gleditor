package animation.editor.tool;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.TransferHandler;

import animation.editor.ScalableViewer;
import animation.world.Animation;
import animation.world.Module;

/**
 * 拖拽模块工具
 * 
 * @author 段链
 * @time 2012-9-15
 */
public class DragModuleTool extends ToolAdapter {
	/**
	 * 选择模块工具
	 */
	private SelectModuleTool tool;
	/**
	 * 鼠标按下的位置, 用于选择模块
	 */
	private Point lastMousePoint;

	public DragModuleTool(ScalableViewer viewer) {
		super(viewer);
		tool = new SelectModuleTool(getViewer());
	}

	@Override
	public void beginSession() {
		/**
		 * 开始拖拽模块到角色视图中, 这时模块视图不能自动滚动
		 */
		viewer.setAutoscrolls(false);
	}

	@Override
	public void endSession() {
		viewer.setAutoscrolls(true);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		tool.mouseClicked(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		tool.mousePressed(e);
		lastMousePoint = e.getPoint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (Module.getModel().getSelection() != null
				&& Animation.instance().getModuleAtPoint(lastMousePoint) != null) {
			viewer.getTransferHandler().exportAsDrag(getViewer(), e,
					TransferHandler.COPY);
		}
	}
}
