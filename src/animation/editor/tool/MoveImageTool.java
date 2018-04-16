package animation.editor.tool;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.undo.CompoundEdit;

import animation.editor.AnimationEditor;
import animation.editor.ScalableViewer;
import animation.world.Animation;
import animation.world.Module;

/**
 * 移动切块工具, 多用于整理切好的图块
 * 
 * <p>
 * <li>鼠标右键单击, 取消选择的图块</li>
 * <li>鼠标左键拖拽或者按方向键, 移动图块</li>
 * <li>方向键, 移动图块</li>
 * <li>Delete键, 删除图块</li>
 * 
 * @author 段链
 * @time 2012-9-15
 */
public class MoveImageTool extends ToolAdapter {
	/**
	 * 选择模块工具
	 */
	private SelectModuleTool tool;
	/**
	 * 上一次鼠标位置
	 */
	private Point lastMousePoint;
	/**
	 * 复合可撤销编辑
	 */
	private CompoundEdit compoundEdit;

	public MoveImageTool(ScalableViewer viewer) {
		super(viewer);
		tool = new SelectModuleTool(getViewer());
	}

	@Override
	public void endSession() {
		Animation.aniImage().flatImage();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		tool.mouseClicked(e);
		if (e.getButton() == MouseEvent.BUTTON3) {
			Module.getModel().getSelectionModel().clearSelection();
			getViewer().repaint();
		}
		lastMousePoint = e.getPoint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		tool.mousePressed(e);
		lastMousePoint = e.getPoint();
		if (compoundEdit == null) {
			compoundEdit = new CompoundEdit();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (compoundEdit != null && compoundEdit.isSignificant()) {
			compoundEdit.end();
			AnimationEditor.undoManager.addEdit(compoundEdit);
		}
		compoundEdit = null;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int dx = e.getX() - lastMousePoint.x;
		int dy = e.getY() - lastMousePoint.y;
		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0
				&& Module.getModel().getSelection() != null) {
			moveImage(dx, dy);
		}
		lastMousePoint = e.getPoint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (Module.getModel().getSelection() == null)
			return;

		int dx = 0;
		int dy = 0;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_DELETE:
			getCompoundEdit().addEdit(
					Animation.aniImage().removeImage(
							Module.getModel().getSelections()));
			return;
		case KeyEvent.VK_UP:
			dy = -1;
			break;
		case KeyEvent.VK_DOWN:
			dy = 1;
			break;
		case KeyEvent.VK_LEFT:
			dx = -1;
			break;
		case KeyEvent.VK_RIGHT:
			dx = 1;
			break;
		default:
			return;
		}
		moveImage(dx, dy);
		e.consume();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		mouseReleased(null);
	}

	/**
	 * 复合可撤销编辑
	 */
	private CompoundEdit getCompoundEdit() {
		if (compoundEdit == null) {
			compoundEdit = new CompoundEdit();
		}

		return compoundEdit;
	}

	/**
	 * 移动图片
	 * 
	 * @param dx
	 * @param dy
	 */
	private void moveImage(int dx, int dy) {
		getCompoundEdit().addEdit(
				Animation.aniImage().moveImage(
						Module.getModel().getSelections(), dx, dy));
	}
}
