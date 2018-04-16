package animation.editor.tool;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import animation.editor.AnimationEditor;
import animation.editor.ScalableViewer;
import animation.world.AniFrame;
import animation.world.Animation;
import animation.world.ImageProvider;
import animation.world.Module;

/**
 * 移动模块工具, 多用于调整各个模块
 * <p>
 * <li>鼠标右键单击, 取消选择模块</li>
 * <li>鼠标左键拖拽, 移动模块</li>
 * <li>方向键, 移动模块</li>
 * <li>Delete键, 删除模块</li>
 * <li>ALT+鼠标左键拖拽, 未选择模块可创建模块</li>
 * <li>ALT+鼠标左键拖拽, 已选择模块可改变模块大小</li>
 * <li>ALT+方向键, 未选择模块可创建模块</li>
 * <li>ALT+方向键, 已选择模块可改变模块大小</li>
 * 
 * @author 段链
 * @time 2012-9-15
 */
public class MoveModuleTool extends ToolAdapter {
	/**
	 * 选择模块工具
	 */
	private SelectModuleTool tool;
	/**
	 * 上一次鼠标的位置
	 */
	private Point lastMousePoint;
	/**
	 * 复合可撤销编辑
	 */
	private CompoundEdit compoundEdit;

	public MoveModuleTool(ScalableViewer viewer) {
		super(viewer);
		tool = new SelectModuleTool(getViewer());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		tool.mouseClicked(e);
		/**
		 * 单击右键, 取消选择
		 */
		if (e.getButton() == MouseEvent.BUTTON3) {
			Module.getModel().getSelectionModel().clearSelection();
			getViewer().repaint();
		}
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
		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
			if (e.isShiftDown()) {
				/**
				 * 按住SHFIT键, 如果已选择模块则改变模块大小; 如果未选择模块, 则创建宽高都为1的模块并选中该模块,
				 * 随后就执行了已选择模块的逻辑
				 */
				if (Module.getModel().getSelection() == null) {
					getCompoundEdit().addEdit(
							newModule(lastMousePoint.x, lastMousePoint.y, dx,
									dy));
				}
				/**
				 * 选择多个模块则无法改变大小
				 */
				if (Module.getModel().getSelections().size() > 1)
					return;
				shiftModule(dx, dy);
			} else if (Module.getModel().getSelection() != null) {
				moveModule(dx, dy);
			}
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
			ArrayList<Module> moduleList = Module.getModel().getSelections();
			if (Module.isModuleUsed(moduleList)
					&& JOptionPane.showConfirmDialog(null,
							"This Module is Used, do you want delete it?",
							"Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
				return;
			getCompoundEdit().addEdit(removeModule(moduleList));
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

		if (e.isShiftDown()) {
			if (Module.getModel().getSelections().size() > 1)
				return;
			shiftModule(dx, dy);
		} else {
			moveModule(dx, dy);
		}
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
	 * 创建新模块
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	private UndoableEdit newModule(int x, int y, int width, int height) {
		Module module = new Module(x, y, width, height);
		Module.getModel().addRow(module);
		Module.getModel().addSelectObject(module);
		NewModuleUndoableEdit edit = new NewModuleUndoableEdit(module);
		getViewer().repaint();
		return edit;
	}

	class NewModuleUndoableEdit extends AbstractUndoableEdit {
		private static final long serialVersionUID = -3874959312262093646L;

		private Module module;

		public NewModuleUndoableEdit(Module module) {
			this.module = module;
		}

		@Override
		public void undo() throws CannotUndoException {
			super.undo();
			Animation.instance().getModuleList().remove(module);
			getViewer().repaint();
		}

		@Override
		public void redo() throws CannotRedoException {
			super.redo();
			Animation.instance().getModuleList().add(module);
			getViewer().repaint();
		}

	}

	/**
	 * 改变模块的大小
	 * 
	 * @param dx
	 * @param dy
	 */
	private void shiftModule(int dx, int dy) {
		Module module = Module.getModel().getSelection();
		ImageProvider imageProvider = Animation.aniImage();
		if (module.x + module.width + dx <= imageProvider.getWidth()
				&& module.y + module.height + dy <= imageProvider.getHeight()) {
			getCompoundEdit().addEdit(shiftModule(module, dx, dy));
		}
	}

	/**
	 * 改变模块的大小
	 * 
	 * @param module
	 * @param dx
	 * @param dy
	 */
	private UndoableEdit shiftModule(Module module, int dx, int dy) {
		if (module.width + dx < 0) {
			dx = -module.width;
		}
		if (module.height + dy < 0) {
			dy = -module.height;
		}
		ImageProvider imageProvider = Animation.aniImage();
		if (module.x + module.width + dx > imageProvider.getWidth()) {
			dx = imageProvider.getWidth() - module.x - module.width;
		}
		if (module.y + module.height + dy > imageProvider.getHeight()) {
			dy = imageProvider.getHeight() - module.y - module.height;
		}
		module.width += dx;
		module.height += dy;
		module.limitRectInImage();
		getViewer().repaint();
		ShiftModuleUndoableEdit edit = new ShiftModuleUndoableEdit(module, dx,
				dy);
		return edit;
	}

	class ShiftModuleUndoableEdit extends AbstractUndoableEdit {
		private static final long serialVersionUID = -371500140310539619L;

		private Module module;
		private int dx;
		private int dy;

		public ShiftModuleUndoableEdit(Module module, int dx, int dy) {
			this.module = module;
			this.dx = dx;
			this.dy = dy;
		}

		@Override
		public void undo() throws CannotUndoException {
			super.undo();
			shiftMove(-dx, -dy);
		}

		@Override
		public void redo() throws CannotRedoException {
			super.redo();
			shiftModule(dx, dy);
		}

		private void shiftMove(int dx, int dy) {
			module.width += dx;
			module.height += dy;
			module.limitRectInImage();
			Animation.aniImage().firePropertyChange("image");
			getViewer().repaint();
		}
	}

	/**
	 * 移动模块
	 * 
	 * @param dx
	 * @param dy
	 */
	private void moveModule(int dx, int dy) {
		getCompoundEdit().addEdit(
				moveModule(Module.getModel().getSelections(), dx, dy));
	}

	/**
	 * 移动模块
	 * 
	 * @param selectedModule
	 * @param dx
	 * @param dy
	 * @return
	 */
	private UndoableEdit moveModule(ArrayList<Module> selectedModule, int dx,
			int dy) {
		MoveModuleUndoableEdit edit = new MoveModuleUndoableEdit(
				selectedModule, dx, dy);
		edit.moveModule();
		return edit;
	}

	class MoveModuleUndoableEdit extends AbstractUndoableEdit {
		private static final long serialVersionUID = -5303693534990104130L;

		private ArrayList<Module> moduleList;
		private int dx;
		private int dy;

		@SuppressWarnings("unchecked")
		public MoveModuleUndoableEdit(ArrayList<Module> moduleList, int dx,
				int dy) {
			ImageProvider imageProvider = Animation.aniImage();
			for (Module module : moduleList) {
				if (module.x + dx < 0) {
					dx = -module.x;
				}
				if (module.y + dy < 0) {
					dy = -module.y;
				}
				if (module.x + module.width + dx > imageProvider.getWidth()) {
					dx = imageProvider.getWidth() - module.x - module.width;
				}
				if (module.y + module.height + dy > imageProvider.getHeight()) {
					dy = imageProvider.getHeight() - module.y - module.height;
				}
			}
			this.moduleList = (ArrayList<Module>) moduleList.clone();
			this.dx = dx;
			this.dy = dy;
		}

		@Override
		public void undo() throws CannotUndoException {
			super.undo();
			moveModule(-dx, -dy);
		}

		@Override
		public void redo() throws CannotRedoException {
			super.redo();
			moveModule(dx, dy);
		}

		void moveModule() {
			moveModule(dx, dy);
		}

		void moveModule(int dx, int dy) {
			for (Module module : moduleList) {
				module.translate(dx, dy);
				module.limitRectInImage();
			}
			Animation.aniImage().firePropertyChange("image");
			viewer.repaint();
		}
	}

	/**
	 * 移除模块
	 * 
	 * @param selectedModule
	 */
	private UndoableEdit removeModule(ArrayList<Module> selectedModule) {
		RemoveModuleUndoableEdit edit = new RemoveModuleUndoableEdit(
				selectedModule);
		Module.safeDelete(selectedModule);
		selectedModule.clear();
		getViewer().repaint();

		return edit;
	}

	class RemoveModuleUndoableEdit extends AbstractUndoableEdit {
		private static final long serialVersionUID = 2554169320379711698L;

		private ArrayList<Module> moduleList;
		private ArrayList<AniFrame> backupData;

		@SuppressWarnings("unchecked")
		public RemoveModuleUndoableEdit(ArrayList<Module> moduleList) {
			this.moduleList = (ArrayList<Module>) moduleList.clone();
			backupData = (ArrayList<AniFrame>) Animation.instance()
					.getFrameList().clone();
		}

		@Override
		public void undo() throws CannotUndoException {
			super.undo();
			Animation.instance().getModuleList().addAll(moduleList);
			Animation.instance().getFrameList().clear();
			Animation.instance().getFrameList().addAll(backupData);
			AniFrame.getModel().fireTableDataChanged();
			getViewer().repaint();
		}

		@Override
		public void redo() throws CannotRedoException {
			super.redo();
			Module.safeDelete(moduleList);
		}
	}
}
