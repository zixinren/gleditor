package animation.world;

import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.List;

import javax.swing.JTable;

import org.jdom2.Element;

import animation.editor.SelectionTableModel;

/**
 * 模板
 * 
 * @author 段链
 * 
 * @time 2012-9-18
 * 
 */
public class Template extends AffineTransform implements Cloneable,
		Serializable {
	private static final long serialVersionUID = -2657536501453425552L;

	private static SelectionTableModel<Template> model;

	public String name;
	public String path;
	public Animation animation;

	public Template(String name, String path) {
		this.name = name;
		this.path = path;
		if (path != null && path.length() > 0) {
			animation = new Animation(path);
		} else {
			animation = new Animation();
		}
	}

	/**
	 * 获取模板使用的动画
	 */
	public Animation getAnimation() {
		return animation;
	}

	/**
	 * 获取模板名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 获取模板所使用的动画的路径
	 */
	public String getPath() {
		return path;
	}

	/**
	 * 获取模板
	 * 
	 * @param templateID
	 *            模板ID
	 */
	public static Template getTemplate(int templateID) {
		return Animation.instance().getTemplateList().get(templateID);
	}

	/**
	 * 获取模板ID
	 */
	public int getId() {
		return Animation.instance().getTemplateList().indexOf(this);
	}

	/**
	 * 重新载入模板
	 * 
	 * @param path
	 *            路径
	 */
	public void reload(String path) {
		this.path = path;
		animation.reload(path);
	}

	/**
	 * 刷新模板
	 */
	public void refresh() {
		animation.reload(path);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Template) {
			Template temp = (Template) obj;
			if (name.equals(temp.name) && path.equals(temp.path))
				return super.equals(obj);
		}

		return false;
	}

	/**
	 * 从XML文件载入一组模板
	 * 
	 * @param root
	 */
	static void loadTemplateFromXml(Element root) {
		loadTemplateFromXml(Animation.instance(), root);
	}

	/**
	 * 从XML文件载入一组模板
	 * 
	 * @param ani
	 * @param root
	 */
	static void loadTemplateFromXml(Animation ani, Element root) {
		Element child;
		List<Element> children;
		if ((child = root.getChild("Templates")) != null
				&& (children = child.getChildren("Template")) != null) {
			for (Element e : children) {
				ani.getTemplateList().add(
						new Template(e.getAttributeValue("name"), e
								.getAttributeValue("path")));
			}
			getModel().fireTableDataChanged();
		}
	}

	/**
	 * 把一组模板转换成XML数据
	 */
	static Element saveTemplateToXml() {
		Element result = new Element("Templates");

		for (Template template : Animation.instance().getTemplateList()) {
			Element e = new Element("Template");
			e.setAttribute("name", template.name);
			e.setAttribute("path", template.path);
			result.addContent(e);
		}

		return result;
	}

	public static SelectionTableModel<Template> getModel() {
		if (model == null) {
			model = new TemplateTableModel();
		}

		return model;
	}

	public static void setTable(JTable table) {
		getModel().setTable(table);
	}

	static class TemplateTableModel extends SelectionTableModel<Template> {
		private static final long serialVersionUID = -1931448687697260523L;

		static final String[] columnNames = { "Template", "Path", "ID" };
		static final Class<?>[] columnClasses = { String.class, String.class,
				Integer.class };
		static final boolean[] columnEditables = { true, false, false };

		public static final int COLUMN_NAME = 0;
		public static final int COLUMN_PATH = 1;
		public static final int COLUMN_ID = 2;

		public TemplateTableModel() {
			super(Animation.instance().getTemplateList());
		}

		@Override
		public void addRow() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			switch (columnIndex) {
			case COLUMN_NAME:
				Animation.instance().getTemplateList().get(rowIndex).name = (String) aValue;
				fireTableCellUpdated(rowIndex, columnIndex);
				break;
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Template temp = Animation.instance().getTemplateList()
					.get(rowIndex);
			switch (columnIndex) {
			case COLUMN_NAME:
				return temp.name;
			case COLUMN_PATH:
				return temp.path;
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
