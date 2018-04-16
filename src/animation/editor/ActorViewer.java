package animation.editor;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import animation.editor.tool.AbstractTool;
import animation.world.AniFrame;
import animation.world.Animation;
import animation.world.Module;
import animation.world.Sequence;
import animation.world.Sprite;
import animation.world.Template;

/**
 * 角色视图
 * 
 * @author 段链
 * @time 2012-9-17
 */
public class ActorViewer extends ScalableViewer implements Localizable {
	private static final long serialVersionUID = -3399534513054136440L;

	private AniFrame aniFrame;
	private JTextField collisionBoxText;
	private JTextField attackBoxText;
	/**
	 * 碰撞框和攻击框文本格式
	 */
	private MessageFormat boxFormat = new MessageFormat(
			"[{0, number, ###}, {1, number, ###}, {2, number, ###}, {3, number, ###}]");
	/**
	 * 是否绘制网格
	 */
	private boolean hasGrid = true;
	/**
	 * 是否显示碰撞框和攻击框
	 */
	private boolean showBox = false;

	public ActorViewer() {
		try {
			jbInit();
			updateLocalization();
			listenerInit();
			setViewScale(Configuration.VIEW_SCALE);
			setOrigin(-160, -160);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化角色视图组件
	 */
	private void jbInit() {
		setDoubleBuffered(false);
		setAutoscrolls(true);
	}

	@Override
	public void updateLocalization() {

	}

	/**
	 * 初始化角色视图监听器
	 * <p>
	 * <li>配置改变重绘角色视图</li>
	 * <li>图片改变重绘角色视图</li>
	 * <li>帧表选择改变则重新设置角色视图的帧属性</li>
	 * <li>模板表数据改变则重绘角色视图</li>
	 * <li>精灵表选择改变则重绘角色视图</li>
	 * <li>精灵表数据改变则重绘角色视图</li>
	 * <li>帧序列表改变则重新设置角色视图的帧属性</li>
	 * <li>支持Drop, 接收MODULE_FLAVOR和TEMPLATE_FRAME_FLAVOR</li>
	 */
	private void listenerInit() {
		Configuration.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				repaint();
			}

		});
		Animation.aniImage().addPropertyChangeListener(
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						repaint();
					}

				});
		AniFrame.getModel().addSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				setFrame(AniFrame.getModel().getSelection());
			}

		});
		Template.getModel().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				repaint();
			}
		});
		Sprite.getModel().addSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				repaint();
			}
		});
		Sprite.getModel().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				repaint();
			}
		});
		Sequence.getModel().addSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				Sequence sequence = Sequence.getModel().getSelection();
				setFrame(sequence == null ? null : sequence.getFrame());
			}
		});
		setDropTarget(new DropTarget(this, new DropTargetAdapter() {

			@SuppressWarnings("unchecked")
			@Override
			public void drop(DropTargetDropEvent dtde) {
				Transferable t = dtde.getTransferable();
				if (t.isDataFlavorSupported(TableTransferHandler.MODULE_FLAVOR)) {
					try {
						Point p = transformCoordinate(dtde.getLocation());
						ArrayList<Integer> v = (ArrayList<Integer>) t
								.getTransferData(TableTransferHandler.MODULE_FLAVOR);
						for (Integer i : v) {
							Module module = Animation.instance().getModule(i);
							Sprite sprite = new Sprite(module);
							sprite.translate(p.x, p.y);
							Sprite.getModel().insertRow(
									Sprite.getModel().getRowCount(), sprite);
						}
						dtde.dropComplete(true);
						return;
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (t.isDataFlavorSupported(TableTransferHandler.TEMPLATE_FRAME_FLAVOR)) {
					Point p = transformCoordinate(dtde.getLocation());
					try {
						ArrayList<Integer> v = (ArrayList<Integer>) t
								.getTransferData(TableTransferHandler.TEMPLATE_FRAME_FLAVOR);
						Iterator<Integer> iter = v.iterator();
						while (iter.hasNext()) {
							int templateID = iter.next();
							Template template = Template
									.getTemplate(templateID);
							int frameID = iter.next();
							Sprite sprite = new Sprite(template, frameID);
							sprite.translate(p.x, p.y);
							Sprite.getModel().insertRow(
									Sprite.getModel().getRowCount(), sprite);
						}
						dtde.dropComplete(true);
						return;
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				dtde.dropComplete(false);
			}

			@Override
			public void dragEnter(DropTargetDragEvent dtde) {
				if (aniFrame != null) {
					for (DataFlavor flavor : dtde.getCurrentDataFlavorsAsList()) {
						if (flavor.getHumanPresentableName().equals(
								TableTransferHandler.MODULE_FLAVOR
										.getHumanPresentableName())) {
							dtde.acceptDrag(TransferHandler.COPY);
							return;
						}
						if (flavor.getHumanPresentableName().equals(
								TableTransferHandler.TEMPLATE_FRAME_FLAVOR
										.getHumanPresentableName())) {
							dtde.acceptDrag(TransferHandler.COPY);
							return;
						}
					}
				}
				dtde.rejectDrag();
			}
		}));
	}

	@Override
	public Dimension getOriginSize() {
		return new Dimension(320, 320);
	}

	@Override
	public AbstractTool getTool() {
		return aniFrame == null ? null : super.getTool();
	}

	/**
	 * 获取角色视图当前帧
	 */
	public AniFrame getFrame() {
		return aniFrame;
	}

	/**
	 * 设置角色视图当前帧
	 * 
	 * @param frame
	 */
	public void setFrame(AniFrame frame) {
		if (aniFrame != frame) {
			aniFrame = frame;
			loadBoxData();
			repaint();
		}
	}

	/**
	 * 在界面上显示角色当前帧碰撞框和攻击框
	 */
	public void loadBoxData() {
		getCollisonBoxTextField().setText("");
		getAttackBoxTextField().setText("");
		if (aniFrame != null) {
			Rectangle box = aniFrame.collisionBox;
			String text = boxFormat.format(new Integer[] { box.x, box.y,
					box.x + box.width, box.y + box.height });
			collisionBoxText.setText(text);

			box = aniFrame.attackBox;
			text = boxFormat.format(new Integer[] { box.x, box.y,
					box.x + box.width, box.y + box.height });
			attackBoxText.setText(text);
		}
	}

	/**
	 * 获取碰撞框文本域
	 */
	public JTextField getCollisonBoxTextField() {
		if (collisionBoxText == null) {
			collisionBoxText = buildBoxText();
		}

		return collisionBoxText;
	}

	/**
	 * 获取攻击框文本域
	 */
	public JTextField getAttackBoxTextField() {
		if (attackBoxText == null) {
			attackBoxText = buildBoxText();
		}

		return attackBoxText;
	}

	/**
	 * 创建一个框文本域
	 */
	private JTextField buildBoxText() {
		JTextField boxText = new JTextField();
		boxText.setPreferredSize(new Dimension(100, 24));
		boxText.setText("");
		boxText.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				saveBoxData();
			}

		});
		boxText.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				saveBoxData();
			}

		});

		return boxText;
	}

	/**
	 * 保存角色当前帧碰撞框和攻击框数据
	 */
	private void saveBoxData() {
		if (aniFrame == null)
			return;
		saveBoxData(collisionBoxText, aniFrame.collisionBox);
		saveBoxData(attackBoxText, aniFrame.attackBox);
		repaint();
	}

	/**
	 * 保存框数据
	 * 
	 * @param boxText
	 *            框文本域
	 * @param box
	 *            框
	 */
	private void saveBoxData(JTextField boxText, Rectangle box) {
		if (boxText.getText().length() <= 0) {
			boxText.setText(boxFormat.format(new Integer[] { 0, 0, 0, 0 }));
		}
		try {
			Object[] array = boxFormat.parse(boxText.getText());
			box.x = Integer.parseInt(array[0].toString());
			box.y = Integer.parseInt(array[1].toString());
			box.width = Integer.parseInt(array[0].toString()) - box.x;
			box.height = Integer.parseInt(array[0].toString()) - box.y;
		} catch (ParseException e) {
			Toolkit.getDefaultToolkit().beep();
		}
	}

	/**
	 * 对选中的精灵进行X轴翻转
	 */
	public void flipX() {
		if (Animation.instance().isReadOnly())
			return;
		AffineTransform at = new AffineTransform();
		at.scale(-1.0D, 1.0D);
		AnimationEditor.undoManager.addEdit(transform(at));
	}

	/**
	 * 对选中的精灵进行Y轴翻转
	 */
	public void flipY() {
		if (Animation.instance().isReadOnly())
			return;

		AffineTransform at = new AffineTransform();
		at.scale(1.0D, -1.0D);
		AnimationEditor.undoManager.addEdit(transform(at));
	}

	/**
	 * 对选中的精灵进行90°逆时针旋转
	 */
	public void rotate() {
		if (Animation.instance().isReadOnly())
			return;

		AffineTransform at = new AffineTransform();
		at.rotate(Math.toRadians(90.0D));
		AnimationEditor.undoManager.addEdit(transform(at));
	}

	/**
	 * 获取仿射变换的可撤销编辑
	 * 
	 * @param at
	 */
	private UndoableEdit transform(AffineTransform at) {
		TransformSpriteUndoableEdit edit = new TransformSpriteUndoableEdit(
				Sprite.getModel().getSelections(), at);
		edit.transform(at);

		return edit;
	}

	class TransformSpriteUndoableEdit extends AbstractUndoableEdit {
		private static final long serialVersionUID = -248988277191366932L;

		ArrayList<Sprite> spriteList;
		AffineTransform at;

		@SuppressWarnings("unchecked")
		public TransformSpriteUndoableEdit(ArrayList<Sprite> spriteList,
				AffineTransform at) {
			this.spriteList = (ArrayList<Sprite>) spriteList.clone();
			this.at = at;
		}

		/**
		 * 对精灵集合进行变换
		 * 
		 * @param at
		 */
		public void transform(AffineTransform at) {
			for (Sprite sprite : spriteList) {
				sprite.concatenate(at);
				Sprite.getModel().fireTableCellUpdated(
						aniFrame.getSprites().indexOf(sprite),
						Sprite.SpriteTableModel.COLUMN_TRANS);
			}
			repaint();
		}

		@Override
		public void undo() throws CannotUndoException {
			super.undo();
			try {
				transform(at.createInverse());
			} catch (NoninvertibleTransformException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void redo() throws CannotRedoException {
			super.redo();
			transform(at);
		}
	}

	/**
	 * 设置角色碰撞框和攻击框是否显示
	 * 
	 * @param b
	 */
	public void setShowBox(boolean b) {
		this.showBox = b;
		repaint();
	}

	/**
	 * 设置是否显示网格
	 * 
	 * @param enable
	 */
	public void setEnableGird(boolean enable) {
		this.hasGrid = enable;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		g2.setColor(Configuration.color_background);
		g2.fill(g2.getClipBounds());

		g2.setStroke(new BasicStroke(1.0F));
		if (hasGrid) {
			paintGrid(g2);
		}

		if (aniFrame != null && !Animation.aniImage().isEmpty()) {
			paintFrame(g2);
			if (showBox) {
				paintCollisionBox(g2, aniFrame, getTransform());
				paintAttackBox(g2, aniFrame, getTransform());
			}
		}
		paintAxis(g2);
	}

	/**
	 * 绘制网格
	 * 
	 * @param g2
	 */
	private void paintGrid(Graphics2D g2) {
		int w = 160;
		int h = 160;

		g2.setColor(Configuration.color_grid);
		for (int x = 0; x <= w; x += Configuration.size_grid) {
			g2.draw(getTransform().createTransformedShape(
					new Line2D.Double(-x, -h, -x, h)));
			g2.draw(getTransform().createTransformedShape(
					new Line2D.Double(x, -h, x, h)));
		}
		for (int y = 0; y <= h; y += Configuration.size_grid) {
			g2.draw(getTransform().createTransformedShape(
					new Line2D.Double(-w, -y, w, -y)));
			g2.draw(getTransform().createTransformedShape(
					new Line2D.Double(-w, y, w, y)));
		}
	}

	/**
	 * 绘制角色当前帧
	 * 
	 * @param g2
	 */
	private void paintFrame(Graphics2D g2) {
		int id = 0;
		for (Sprite sprite : aniFrame.getSprites()) {
			boolean xor = Sprite.getModel().getSelectionModel()
					.isSelectedIndex(id);
			if (sprite.module != null) {
				Animation.aniImage().render(g2, sprite.module, sprite,
						getTransform(), xor);
			} else {
				Animation ani = sprite.templateWind.getAnimation();
				ani.getImageProvider().renderTemplateFrame(g2, getTransform(),
						ani.getFrameList().get(sprite.frameIDWind), ani,
						sprite, xor);
			}
			++id;
		}
	}

	/**
	 * 绘制角色指定帧所有碰撞框
	 * 
	 * @param g2
	 * @param aniFrame
	 * @param trans
	 */
	private void paintCollisionBox(Graphics2D g2, AniFrame aniFrame,
			AffineTransform trans) {
		if (trans == null)
			return;
		g2.setColor(Configuration.color_collision_box);

		Shape shape = trans.createTransformedShape(aniFrame.collisionBox);
		if (shape != null) {
			g2.draw(shape);
		}

		for (Sprite sprite : aniFrame.getSprites()) {
			if (sprite.module == null) {
				AffineTransform newTrans = new AffineTransform(trans);
				newTrans.translate(sprite.getTranslateX(),
						sprite.getTranslateY());
				paintCollisionBox(g2, sprite.templateWind.getAnimation()
						.getFrameList().get(sprite.frameIDWind), newTrans);
			}
		}
	}

	/**
	 * 绘制角色指定帧所有攻击框
	 * 
	 * @param g2
	 * @param aniFrame
	 * @param trans
	 */
	private void paintAttackBox(Graphics2D g2, AniFrame aniFrame,
			AffineTransform trans) {
		if (trans == null)
			return;
		g2.setColor(Configuration.color_attack_box);

		Shape shape = trans.createTransformedShape(aniFrame.attackBox);
		if (shape != null) {
			g2.draw(shape);
		}

		for (Sprite sprite : aniFrame.getSprites()) {
			if (sprite.module == null) {
				AffineTransform newTrans = new AffineTransform(trans);
				newTrans.translate(sprite.getTranslateX(),
						sprite.getTranslateY());
				paintAttackBox(g2, sprite.templateWind.getAnimation()
						.getFrameList().get(sprite.frameIDWind), newTrans);
			}
		}
	}

	/**
	 * 绘制坐标轴
	 * 
	 * @param g2
	 */
	private void paintAxis(Graphics2D g2) {
		g2.setColor(Configuration.color_axis);
		g2.draw(getTransform().createTransformedShape(
				new Line2D.Double(-160.0D, 0.0D, 160.0D, 0.0D)));
		g2.draw(getTransform().createTransformedShape(
				new Line2D.Double(0.0D, -160.0D, 0.0D, 160.0D)));
	}
}
