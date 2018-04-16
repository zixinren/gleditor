package animation.world;

import static animation.world.Util.getInt;
import static animation.world.Util.round;
import static animation.world.Util.scaleRect;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JTable;

import org.jdom2.Element;

import animation.editor.SelectionTableModel;
import animation.editor.TableTransferHandler;

/**
 * 帧
 * 
 * @author 段链
 * 
 * @time 2012-9-14
 * 
 */
public class AniFrame implements Cloneable, Serializable {
	private static final long serialVersionUID = -6340363085544413173L;

	private static SelectionTableModel<AniFrame> model;

	public String name;
	public ArrayList<Sprite> spriteList;

	public Rectangle attackBox;
	public Rectangle collisionBox;

	public AniFrame(String name) {
		this.name = name;
		spriteList = new ArrayList<Sprite>();
		collisionBox = new Rectangle();
		attackBox = new Rectangle();
	}

	/**
	 * 获取动画的指定帧
	 * 
	 * @param frameID
	 */
	static AniFrame getFrame(int frameID) {
		return Animation.instance().getFrameList().get(frameID);
	}

	/**
	 * 获取动画的帧ID
	 */
	public int getId() {
		return getId(Animation.instance());
	}

	/**
	 * 获取帧在指定动画的帧ID
	 * 
	 * @param ani
	 */
	public int getId(Animation ani) {
		return ani.getFrameList().indexOf(this);
	}

	/**
	 * 获取组成帧的精灵组
	 */
	public ArrayList<Sprite> getSprites() {
		return spriteList;
	}

	/**
	 * 获取点选的精灵
	 * 
	 * @param p
	 */
	public Sprite getSpriteAtPoint(Point p) {
		for (Sprite sprite : spriteList) {
			if (checkPointInSprite(sprite, p))
				return sprite;
		}

		return null;
	}

	/**
	 * 检测点是否在精灵中
	 * 
	 * @param sprite
	 * @param p
	 */
	protected boolean checkPointInSprite(Sprite sprite, Point2D p) {
		try {
			if (sprite.module != null) {
				Point2D dp = sprite.inverseTransform(p, null);
				Rectangle rect = new Rectangle(sprite.module.width,
						sprite.module.height);
				return rect.contains(dp);
			} else {
				for (Sprite innerSprite : sprite.templateWind.getAnimation()
						.getFrameList().get(sprite.frameIDWind).getSprites()) {
					/**
					 * 把p变换到sprite坐标系下
					 */
					Point2D dp = new Point(
							(int) (p.getX() - sprite.getTranslateX()),
							(int) (p.getY() - sprite.getTranslateY()));
					if (checkPointInSprite(innerSprite, dp))
						return true;

				}
			}
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 缩放所有帧
	 * 
	 * @param d
	 *            缩放因子
	 */
	public static void scale(double d) {
		for (AniFrame frame : Animation.instance().getFrameList()) {
			scaleFrame(d, frame);
		}
	}

	/**
	 * 缩放指定帧
	 * 
	 * @param d
	 * @param frame
	 */
	protected static void scaleFrame(double d, AniFrame frame) {
		scaleRect(frame.collisionBox, d);
		scaleRect(frame.attackBox, d);
		for (Sprite sprite : frame.getSprites()) {
			sprite.translate(
					round(sprite.getTranslateX() * d) - sprite.getTranslateX(),
					round(sprite.getTranslateY() * d) - sprite.getTranslateY());
		}
	}

	/**
	 * 获取没有被动画动作使用的帧
	 */
	public static ArrayList<AniFrame> getUnusedFrames() {
		@SuppressWarnings("unchecked")
		ArrayList<AniFrame> result = (ArrayList<AniFrame>) Animation.instance()
				.getFrameList().clone();
		for (AniAction action : Animation.instance().getActionList()) {
			for (Sequence sequence : action.getSequences()) {
				result.remove(sequence.getFrame());
			}
		}

		return result;
	}

	/**
	 * 帧是否被动画使用
	 * 
	 * @param frame
	 */
	public static boolean isFrameUsed(AniFrame frame) {
		for (AniAction action : Animation.instance().getActionList()) {
			for (Sequence sequence : action.getSequences()) {
				if (sequence.getFrame() == frame)
					return true;
			}
		}

		return false;
	}

	/**
	 * 从XML文件载入一组帧
	 * 
	 * @param root
	 */
	static void loadFrameFromXml(Element root) {
		loadFrameFromXml(Animation.instance(), root);
	}

	/**
	 * 从XML文件载入一组帧
	 * 
	 * @param ani
	 * @param root
	 */
	static void loadFrameFromXml(Animation ani, Element root) {
		for (Element e : root.getChild("Frames").getChildren("Frame")) {
			AniFrame frame = new AniFrame(e.getAttributeValue("name"));
			frame.collisionBox = loadBoxFromXml(frame.collisionBox, e, "cl",
					"cl", "cr", "cb");
			frame.attackBox = loadBoxFromXml(frame.attackBox, e, "al", "al",
					"ar", "ab");
			frame.spriteList.addAll(Sprite.loadSpriteFromXml(ani, e));
			ani.getFrameList().add(frame);
		}
		getModel().fireTableDataChanged();
	}

	/**
	 * 从XML文件中载入矩形框
	 * 
	 * @param box
	 * @param e
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	private static Rectangle loadBoxFromXml(Rectangle box, Element e,
			String left, String top, String right, String bottom) {
		if (box == null) {
			box = new Rectangle();
		}

		box.x = getInt(e, left);
		box.y = getInt(e, top);
		box.width = getInt(e, right) - box.x;
		box.height = getInt(e, bottom) - box.y;

		return box;
	}

	/**
	 * 把一组帧转化成XML数据
	 */
	static Element saveFrameToXml() {
		Element result = new Element("Frames");

		for (AniFrame frame : Animation.instance().getFrameList()) {
			Element e = new Element("frame");
			e.setAttribute("name", frame.name);
			saveBoxToXml(frame.collisionBox, e, "cl", "ct", "cr", "cb");
			saveBoxToXml(frame.attackBox, e, "al", "at", "ar", "ab");
			e.addContent(Sprite.saveSpriteToXml(frame.getSprites()));
			result.addContent(e);
		}

		return result;
	}

	/**
	 * 把矩形框转化成XML数据
	 * 
	 * @param rect
	 * @param e
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	private static void saveBoxToXml(Rectangle rect, Element e, String left,
			String top, String right, String bottom) {
		e.setAttribute(left, Integer.toString(rect.x));
		e.setAttribute(top, Integer.toString(rect.y));
		e.setAttribute(right, Integer.toString(rect.x + rect.width));
		e.setAttribute(bottom, Integer.toString(rect.y + rect.height));
	}

	public static SelectionTableModel<AniFrame> getModel() {
		if (model == null) {
			model = new FrameTableModel(new Prototype<AniFrame>() {
				private int count = 1;

				@Override
				public AniFrame clone() {
					AniFrame result = new AniFrame("Untitled" + count);
					++count;
					return result;
				}

				@Override
				public AniFrame clone(AniFrame frame) {
					AniFrame result = new AniFrame("copy of " + frame.name);
					result.attackBox = (Rectangle) frame.attackBox.clone();
					result.collisionBox = (Rectangle) frame.collisionBox
							.clone();
					for (Sprite sprite : frame.spriteList) {
						Sprite newSprite;
						if (sprite.module != null) {
							newSprite = new Sprite(sprite.module);
						} else {
							newSprite = new Sprite(sprite.templateWind,
									sprite.frameIDWind);
						}
						newSprite.setTransform(sprite);
						result.spriteList.add(newSprite);
					}
					return result;
				}
			});
		}

		return model;
	}

	public static void setTable(JTable table) {
		getModel().setTable(table);
	}

	static class FrameTableModel extends SelectionTableModel<AniFrame> {
		private static final long serialVersionUID = -7133446528328260052L;

		static final String[] columnNames = { "Name", "ID" };
		static final Class<?>[] columnClasses = { String.class, Integer.class };
		static final boolean[] columnEditables = { true, false };

		public static final int COLUMN_NAME = 0;
		public static final int COLUMN_ID = 1;

		public FrameTableModel(Prototype<AniFrame> prototype) {
			super(Animation.instance().getFrameList(), prototype);
		}

		@Override
		public void setTable(JTable table) {
			super.setTable(table);
			table.setTransferHandler(new TableTransferHandler(
					TableTransferHandler.FRAME_FLAVOR,
					TableTransferHandler.MODULE_FLAVOR) {
				private static final long serialVersionUID = -2192409020963704736L;

				@Override
				public void dropData(int index, int[] rows) {
					for (int row : rows) {
						Module module = Animation.instance().getModuleList()
								.get(row);
						Sprite.getModel().addRow(module);
					}
				}

			});
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			switch (columnIndex) {
			case COLUMN_NAME:
				Animation.instance().getFrameList().get(rowIndex).name = (String) aValue;
				fireTableCellUpdated(rowIndex, columnIndex);
				break;
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
			case COLUMN_NAME:
				return Animation.instance().getFrameList().get(rowIndex).name;
			case COLUMN_ID:
				return rowIndex;
			}

			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (Animation.instance().isReadOnly())
				return false;

			return columnEditables[columnIndex];
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return columnClasses[columnIndex];
		}
	}
}
