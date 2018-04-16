package animation.world;

import static animation.world.Util.getInt;
import static animation.world.Util.scaleRect;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JTable;

import org.jdom2.Element;

import animation.editor.SelectionTableModel;
import animation.editor.TableTransferHandler;

/**
 * 模块
 * 
 * @author 段链
 * 
 * @time 2012-9-14
 * 
 */
public class Module extends Rectangle implements Cloneable, Serializable {
	private static final long serialVersionUID = 8872137796356857354L;

	private static SelectionTableModel<Module> model;

	public Module(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	/**
	 * 把模块大小限制在图片里
	 */
	public void limitRectInImage() {
		if (x < 0)
			x = 0;
		if (y < 0)
			y = 0;
		ImageProvider imageProvider = Animation.aniImage();
		if (x + width > imageProvider.getWidth())
			x = imageProvider.getWidth() - width;
		if (y + height > imageProvider.getHeight())
			y = imageProvider.getHeight() - height;
	}

	@Override
	public boolean equals(Object obj) {
		/**
		 * 必须写下面这一段, 而不能直接用Rectangle的equals方法, 否则Rectangle对象和Module对象就可能能相等了
		 */
		if (obj instanceof Module) {
			return super.equals(obj);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getId();
	}

	/**
	 * 获取模块ID
	 */
	public int getId() {
		return getId(Animation.instance());
	}

	/**
	 * 获取模块在指定动画中的模块ID
	 * 
	 * @param animation
	 */
	public int getId(Animation animation) {
		return animation.getModuleList().indexOf(this);
	}

	/**
	 * 获取所有模块的边界
	 */
	public static Rectangle getAllModuleBounds() {
		Rectangle result = null;
		for (Module module : Animation.instance().getModuleList()) {
			if (result == null) {
				result = new Rectangle(module);
			} else {
				result = result.union(module);
			}
		}

		return result;
	}

	/**
	 * 缩放模块
	 * 
	 * @param d
	 *            缩放因子
	 */
	public static void scale(double d) {
		for (Module module : Animation.instance().getModuleList()) {
			scaleRect(module, d);
		}
	}

	/**
	 * 平移所有模块
	 * 
	 * @param dx
	 * @param dy
	 */
	public static void moveAllModules(int dx, int dy) {
		for (Module module : Animation.instance().getModuleList()) {
			module.translate(dx, dy);
		}
	}

	/**
	 * 获取没有被动画的帧使用的模块
	 */
	public static ArrayList<Module> getUnusedModules() {
		@SuppressWarnings("unchecked")
		ArrayList<Module> v = (ArrayList<Module>) Animation.instance()
				.getModuleList().clone();
		for (AniFrame aniFrame : Animation.instance().getFrameList()) {
			for (Sprite sprite : aniFrame.getSprites()) {
				v.remove(sprite.module);
			}
		}

		return v;
	}

	/**
	 * 模块是否被动画的帧使用
	 * 
	 * @param module
	 */
	public static boolean isModuleUsed(Module module) {
		for (AniFrame aniFrame : Animation.instance().getFrameList()) {
			for (Sprite sprite : aniFrame.getSprites()) {
				if (sprite.module == module)
					return true;
			}
		}

		return false;
	}

	/**
	 * 这一组模块至少有一个被动画的帧使用
	 * 
	 * @param modules
	 */
	public static boolean isModuleUsed(ArrayList<Module> modules) {
		for (AniFrame aniFrame : Animation.instance().getFrameList()) {
			for (Sprite sprite : aniFrame.getSprites()) {
				if (modules.contains(sprite.module))
					return true;
			}
		}
		return false;
	}

	/**
	 * 删除模块, 并删除使用了这些模块的精灵
	 * 
	 * @param modules
	 */
	public static void safeDelete(ArrayList<Module> modules) {
		for (AniFrame aniFrame : Animation.instance().getFrameList()) {
			Iterator<Sprite> iter = aniFrame.getSprites().iterator();
			while (iter.hasNext()) {
				Sprite sprite = iter.next();
				if (modules.contains(sprite.module)) {
					iter.remove();
				}
			}
		}
		Sprite.getModel().fireTableStructureChanged();
		getModel().fireTableStructureChanged();
	}

	/**
	 * 从XML文件载入一組模块
	 * 
	 * @param root
	 *            XML文件的根节点
	 */
	static void loadModuleFromXml(Element root) {
		loadModuleFromXml(Animation.instance(), root);
	}

	/**
	 * 从XML文件载入一組模块
	 * 
	 * @param ani
	 *            模块所在的动画
	 * @param root
	 *            XML文件的根节点
	 */
	static void loadModuleFromXml(Animation ani, Element root) {
		for (Element e : root.getChild("Modules").getChildren("Module")) {
			Module module = new Module(getInt(e, "x"), getInt(e, "y"), getInt(
					e, "w"), getInt(e, "h"));
			ani.getModuleList().add(module);
		}
		getModel().fireTableDataChanged();
	}

	/**
	 * 把一组模块转换成XML数据
	 */
	static Element saveModuleToXml() {
		Element result = new Element("Modules");

		for (Module module : Animation.instance().getModuleList()) {
			Element e = new Element("Module");
			e.setAttribute("x", Integer.toString(module.x));
			e.setAttribute("y", Integer.toString(module.y));
			e.setAttribute("w", Integer.toString(module.width));
			e.setAttribute("h", Integer.toString(module.height));
			result.addContent(e);
		}

		return result;
	}

	public static SelectionTableModel<Module> getModel() {
		if (model == null) {
			model = new ModuleTableModel();
		}

		return model;
	}

	public static void setTable(JTable table) {
		getModel().setTable(table);
	}

	static class ModuleTableModel extends SelectionTableModel<Module> {
		private static final long serialVersionUID = 1470908588671893006L;

		static final String[] columnNames = { "Name" };
		static final Class<?>[] columnClasses = { String.class };
		static final boolean[] columnEditables = new boolean[1];

		public static final int COLUMN_NAME = 0;

		public ModuleTableModel() {
			super(Animation.instance().getModuleList());
		}

		@Override
		public void setTable(JTable table) {
			super.setTable(table);
			table.setTransferHandler(new TableTransferHandler(
					TableTransferHandler.MODULE_FLAVOR,
					TableTransferHandler.MODULE_FLAVOR));
		}

		@Override
		public void addRow() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
			case COLUMN_NAME:
				return "Module " + rowIndex;
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
