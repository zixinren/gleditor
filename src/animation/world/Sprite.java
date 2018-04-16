package animation.world;

import static animation.world.Util.getDouble;
import static animation.world.Util.getInt;
import static animation.world.Util.round;

import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdom2.Element;

import animation.editor.SelectionTableModel;
import animation.editor.TableTransferHandler;

/**
 * 精灵
 * 
 * @author 段链
 * @time 2012-9-15
 */
public class Sprite extends AffineTransform implements Cloneable, Serializable {
	private static final long serialVersionUID = 985283296217719571L;

	private static SpriteTableModel model;

	public Module module;

	public Template templateWind;
	public int frameIDWind;

	public Sprite(Module module) {
		this.module = module;
	}

	public Sprite(Template temp, int frameID) {
		templateWind = temp;
		frameIDWind = frameID;
	}

	Sprite(int moduleID, Sprite sprite) {
		this(moduleID);
	}

	private Sprite(int moduleID) {
		module = Animation.instance().getModuleList().get(moduleID);
	}

	private Sprite(double m00, double m10, double m01, double m11, double m02,
			double m12) {
		super(m00, m10, m01, m11, m02, m12);
	}

	/**
	 * 精灵是否X翻转
	 */
	public boolean isFlipX() {
		return (getScaleX() == 0.0D ? removeRotate().getScaleX() : getScaleX()) == -1.0D;
	}

	/**
	 * 精灵是否Y翻转
	 */
	public boolean isFlipY() {
		return (getScaleY() == 0.0D ? removeRotate().getScaleY() : getScaleY()) == -1.0D;
	}

	private AffineTransform removeRotate() {
		AffineTransform at = new AffineTransform(this);
		at.concatenate(AffineTransform.getRotateInstance(-90.0D));
		return at;
	}

	/**
	 * 精灵是否逆90°旋转
	 */
	public boolean isRotate() {
		return getScaleX() == 0.0D;
	}

	/**
	 * 从XML文件中载入一组精灵
	 * 
	 * @param frame
	 */
	static ArrayList<Sprite> loadSpriteFromXml(Element frame) {
		return loadSpriteFromXml(Animation.instance(), frame);
	}

	/**
	 * 从XML文件中载入一组精灵
	 * 
	 * @param ani
	 * @param frame
	 */
	static ArrayList<Sprite> loadSpriteFromXml(Animation ani, Element frame) {
		ArrayList<Sprite> result = new ArrayList<Sprite>();

		for (Element e : frame.getChildren("Sprite")) {
			if ("Sprite".equals(e.getName())) {
				Sprite sprite = new Sprite(getDouble(e, "m00"), getDouble(e,
						"m10"), getDouble(e, "m01"), getDouble(e, "m11"),
						round(getDouble(e, "m02")), round(getDouble(e, "m12")));
				sprite.module = ani.getModuleList().get(getInt(e, "module_id"));
				result.add(sprite);
			} else if ("InnerTemplate".equals(e.getName())) {
				Sprite sprite = new Sprite(1.0D, 0.0D, 0.0D, 1.0D,
						round(getDouble(e, "m02")), round(getDouble(e, "m12")));
				sprite.templateWind = ani.getTemplateList().get(
						getInt(e, "template_id"));
				sprite.frameIDWind = getInt(e, "frame_id");
				result.add(sprite);
			}

		}

		return result;
	}

	/**
	 * 把一组精灵转化成XML数据
	 * 
	 * @param spriteList
	 * @return
	 */
	static List<Element> saveSpriteToXml(ArrayList<Sprite> spriteList) {
		List<Element> result = new ArrayList<Element>();

		for (Sprite sprite : spriteList) {
			if (sprite.module != null) {
				Element e = new Element("Sprite");
				e.setAttribute("m00", Double.toString(sprite.getScaleX()));
				e.setAttribute("m10", Double.toString(sprite.getShearY()));
				e.setAttribute("m01", Double.toString(sprite.getShearX()));
				e.setAttribute("m11", Double.toString(sprite.getScaleY()));
				e.setAttribute("m02",
						Double.toString(round(sprite.getTranslateX())));
				e.setAttribute("m12",
						Double.toString(round(sprite.getTranslateY())));
				e.setAttribute("module_id",
						Integer.toString(sprite.module.getId()));
				result.add(e);
			} else {
				Element e = new Element("InnerTemplate");
				e.setAttribute("m02",
						Double.toString(round(sprite.getTranslateX())));
				e.setAttribute("m12",
						Double.toString(round(sprite.getTranslateY())));
				e.setAttribute("template_id",
						Integer.toString(sprite.templateWind.getId()));
				e.setAttribute("frame_id", Integer.toString(sprite.frameIDWind));
				result.add(e);
			}
		}

		return result;
	}

	public static SpriteTableModel getModel() {
		if (model == null) {
			model = new SpriteTableModel();
		}

		return model;
	}

	public static void setTable(JTable table) {
		getModel().setTable(table);
	}

	public static class SpriteTableModel extends SelectionTableModel<Sprite> {
		private static final long serialVersionUID = 2442876284787046280L;

		static final String[] columnNames = { "Module", "Trans", "ID" };
		static final Class<?>[] columnClasses = { Integer.class, String.class,
				Integer.class };
		static final boolean[] columnEditables = new boolean[3];

		public static final int COLUMN_MODULE = 0;
		public static final int COLUMN_TRANS = 1;
		public static final int COLUMN_ID = 2;

		private AniFrame aniFrame;

		public SpriteTableModel() {
			super(null);
			AniFrame.getModel().addSelectionListener(
					new ListSelectionListener() {

						@Override
						public void valueChanged(ListSelectionEvent e) {
							changeSelectedFrame();
						}
					});
		}

		/**
		 * 改变了选择帧, 则相应的改变精灵表数据
		 */
		private void changeSelectedFrame() {
			aniFrame = AniFrame.getModel().getSelection();
			if (aniFrame != null) {
				setDataArrayList(aniFrame.getSprites());
			} else {
				setDataArrayList(null);
			}
		}

		@Override
		public void setTable(JTable table) {
			super.setTable(table);
			table.setTransferHandler(new TableTransferHandler(
					TableTransferHandler.SPRITE_FLAVOR,
					TableTransferHandler.MODULE_FLAVOR) {
				private static final long serialVersionUID = -6470977773252514788L;

				@Override
				public void dropData(int index, int[] rows) {
					if (aniFrame != null) {
						for (int i = 0; i < rows.length; i++) {
							Sprite.getModel().insertRow(index + i,
									new Sprite(rows[i], null));
						}
					}
				}

			});
		}

		@Override
		public void addRow() {
			throw new UnsupportedOperationException();
		}

		/**
		 * 不可撤销
		 * 
		 * @param module
		 */
		public void addRow(Module module) {
			if (aniFrame == null)
				return;
			Sprite sprite = new Sprite(module);
			aniFrame.getSprites().add(sprite);
			int row = aniFrame.getSprites().size() - 1;
			fireTableRowsInserted(row, row);
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (aniFrame != null) {
				Sprite sprite = aniFrame.getSprites().get(rowIndex);
				if (sprite.module != null) {
					switch (columnIndex) {
					case COLUMN_MODULE:
						return sprite.module.getId();
					case COLUMN_TRANS:
						StringBuilder trans = new StringBuilder();
						if (sprite.isFlipX()) {
							trans.append('X');
						}
						if (sprite.isFlipY()) {
							trans.append('Y');
						}
						if (sprite.isRotate()) {
							trans.append('R');
						}
						return trans.toString();
					case COLUMN_ID:
						return rowIndex;
					}
				} else {
					switch (columnIndex) {
					case COLUMN_MODULE:
						return sprite.templateWind.getId();
					case COLUMN_TRANS:
						return sprite.templateWind.getName() + " "
								+ sprite.frameIDWind;
					case COLUMN_ID:
						return rowIndex;
					}
				}
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
