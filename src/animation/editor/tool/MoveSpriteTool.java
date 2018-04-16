package animation.editor.tool;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import animation.editor.ActorViewer;
import animation.editor.AnimationEditor;
import animation.world.AniFrame;
import animation.world.Sprite;

/**
 * 移动精灵工具
 * 
 * @author 段链
 * @time 2012-9-17
 */
public class MoveSpriteTool extends ToolAdapter {
	/**
	 * 上一次鼠标的位置
	 */
	private Point lastMousePoint;
	/**
	 * 复合可撤销编辑
	 */
	private CompoundEdit compoundEdit;
	/**
	 * 角色视图
	 */
	private ActorViewer viewer;

	public MoveSpriteTool(ActorViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		lastMousePoint = e.getPoint();
		if (viewer.getFrame() == null)
			return;
		if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
			Sprite sprite = viewer.getFrame().getSpriteAtPoint(e.getPoint());
			if (!e.isControlDown()) {
				Sprite.getModel().getSelectionModel().clearSelection();
			}
			if (sprite != null) {
				Sprite.getModel().addSelectObject(sprite);
			}

			if (compoundEdit == null) {
				compoundEdit = new CompoundEdit();
			}

		} else {
			Sprite.getModel().getSelectionModel().clearSelection();
		}
		viewer.repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int dx = e.getX() - lastMousePoint.x;
		int dy = e.getY() - lastMousePoint.y;
		if (dx == 0 && dy == 0)
			return;
		if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0
				&& viewer.getFrame() != null
				&& Sprite.getModel().getSelection() != null) {
			getCompoundEdit().addEdit(moveSprite(dx, dy));
		}
		lastMousePoint = e.getPoint();
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
	public void keyPressed(KeyEvent e) {
		if (viewer.getFrame() == null
				|| Sprite.getModel().getSelection() == null)
			return;
		int dx = 0;
		int dy = 0;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_DELETE:
			getCompoundEdit().addEdit(removeSprite());
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
		getCompoundEdit().addEdit(moveSprite(dx, dy));
		e.consume();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		mouseReleased(null);
	}

	/**
	 * 移动精灵
	 * 
	 * @param dx
	 * @param dy
	 */
	private UndoableEdit moveSprite(int dx, int dy) {
		MoveSpriteUndoableEdit edit = new MoveSpriteUndoableEdit(Sprite
				.getModel().getSelections(), dx, dy);
		edit.moveSprite();

		return edit;
	}

	class MoveSpriteUndoableEdit extends AbstractUndoableEdit {
		private static final long serialVersionUID = 8797861142595661474L;

		private ArrayList<Sprite> spriteList;
		private int dx;
		private int dy;

		@SuppressWarnings("unchecked")
		public MoveSpriteUndoableEdit(ArrayList<Sprite> spriteList, int dx,
				int dy) {
			this.spriteList = (ArrayList<Sprite>) spriteList.clone();
			this.dx = dx;
			this.dy = dy;
		}

		void moveSprite() {
			moveSprite(dx, dy);
		}

		void moveSprite(int dx, int dy) {
			for (Sprite sprite : spriteList) {
				AffineTransform at = new AffineTransform();
				at.translate(dx, dy);
				sprite.preConcatenate(at);
			}
			viewer.repaint();
		}

		@Override
		public void undo() throws CannotUndoException {
			super.undo();
			moveSprite(-dx, -dy);
		}

		@Override
		public void redo() throws CannotRedoException {
			super.redo();
			moveSprite(dx, dy);
		}
	}

	/**
	 * 移除精灵
	 */
	private UndoableEdit removeSprite() {
		RemoveSpriteUndoableEdit edit = new RemoveSpriteUndoableEdit(
				viewer.getFrame(), Sprite.getModel().getSelections());
		Sprite.getModel().remove();
		viewer.repaint();

		return edit;
	}

	class RemoveSpriteUndoableEdit extends AbstractUndoableEdit {
		private static final long serialVersionUID = 2360925602645264483L;

		private AniFrame frame;
		private ArrayList<Sprite> spriteList;
		private ArrayList<Sprite> selectList;

		@SuppressWarnings("unchecked")
		public RemoveSpriteUndoableEdit(AniFrame frame,
				ArrayList<Sprite> selectList) {
			this.frame = frame;
			spriteList = (ArrayList<Sprite>) frame.getSprites().clone();
			selectList = (ArrayList<Sprite>) selectList.clone();
		}

		@Override
		public void undo() throws CannotUndoException {
			super.undo();
			frame.getSprites().clear();
			frame.getSprites().addAll(spriteList);
			Sprite.getModel().fireTableDataChanged();
			viewer.repaint();
		}

		@Override
		public void redo() throws CannotRedoException {
			super.redo();
			frame.getSprites().removeAll(selectList);
			Sprite.getModel().fireTableDataChanged();
			viewer.repaint();
		}
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

}
