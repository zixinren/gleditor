package animation.world;

import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JTable;

import org.jdom2.Element;

import animation.editor.SelectionTableModel;
import animation.editor.TableTransferHandler;

/**
 * 动作
 * 
 * @author 段链
 * @time 2012-9-16
 */
public class AniAction implements Cloneable, Serializable {
	private static final long serialVersionUID = 6748837012046995858L;

	private static SelectionTableModel<AniAction> model;

	public String name;
	public ArrayList<Sequence> sequenceList;
	public MechModel mechModel;

	public AniAction(String name) {
		this.name = name;
		sequenceList = new ArrayList<Sequence>();
		mechModel = new MechModel();
	}

	/**
	 * 获取动作ID
	 */
	public int getId() {
		return Animation.instance().getActionList().indexOf(this);
	}

	/**
	 * 设置力学模型
	 * 
	 * @param mechModel
	 */
	public void setMechModel(MechModel mechModel) {
		this.mechModel = mechModel;
	}

	/**
	 * 获取力学模型
	 */
	public MechModel getMechModel() {
		return mechModel;
	}

	/**
	 * 获取一组帧序列
	 */
	public ArrayList<Sequence> getSequences() {
		return sequenceList;
	}

	/**
	 * 对所有动作的运动模型进行缩放
	 * 
	 * @param d
	 *            缩放因子
	 */
	public static void scale(double d) {
		double d2 = d * d;
		for (AniAction action : Animation.instance().getActionList()) {
			if (action.mechModel != null) {
				action.mechModel.speedX *= d;
				action.mechModel.speedY *= d;
				action.mechModel.accelerationX *= d2;
				action.mechModel.accelerationY *= d2;
			}
		}
	}

	/**
	 * 从XML载入一组动作
	 */
	static void loadActionFromXml(Element root) {
		loadActionFromXml(Animation.instance(), root);
	}

	/**
	 * 从XML载入一组动作
	 * 
	 * @param ani
	 */
	static void loadActionFromXml(Animation ani, Element root) {
		for (Element e : root.getChild("Actions").getChildren("Action")) {
			AniAction action = new AniAction(e.getAttributeValue("name"));
			action.sequenceList.addAll(Sequence.loadSequenceFromXml(ani, e));
			action.mechModel = MechModel.loadMechModelFromXml(e);
			ani.getActionList().add(action);
		}
		getModel().fireTableDataChanged();
	}

	/**
	 * 把一组动作转换成XML数据
	 */
	static Element saveActionToXml() {
		Element result = new Element("Actions");

		for (AniAction action : Animation.instance().getActionList()) {
			Element e = new Element("Action");
			e.setAttribute("name", action.name);
			e.addContent(Sequence.saveSequenceToXml(action.sequenceList));
			e.addContent(MechModel.saveMechModelToXml(action.mechModel));
			result.addContent(e);
		}

		return result;
	}

	public static SelectionTableModel<AniAction> getModel() {
		if (model == null) {
			model = new ActionTableModel(new Prototype<AniAction>() {
				private int count = 1;

				@Override
				public AniAction clone() {
					AniAction result = new AniAction("Untitled" + count);
					++count;

					return result;
				}

				@Override
				public AniAction clone(AniAction action) {
					AniAction result = new AniAction("copy of " + action.name);
					result.mechModel = (MechModel) action.mechModel.clone();
					for (Sequence sequence : action.sequenceList) {
						result.sequenceList
								.add(new Sequence(sequence.aniFrame));
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

	static class ActionTableModel extends SelectionTableModel<AniAction> {
		private static final long serialVersionUID = -5881567035807957458L;

		static final String[] columnNames = { "Name", "ID" };
		static final Class<?>[] columnClasses = { String.class, Integer.class };
		static final boolean[] columnEditables = { true, false };

		public static final int COLUMN_NAME = 0;
		public static final int COLUMN_ID = 1;

		public ActionTableModel(Prototype<AniAction> prototype) {
			super(Animation.instance().getActionList(), prototype);
		}

		@Override
		public void setTable(JTable table) {
			super.setTable(table);
			table.setTransferHandler(new TableTransferHandler(
					TableTransferHandler.ACTION_FLAVOR,
					TableTransferHandler.FRAME_FLAVOR) {
				private static final long serialVersionUID = -7729625271440612846L;

				@Override
				public void dropData(int index, int[] rows) {
					for (int row : rows) {
						AniFrame frame = Animation.instance().getFrameList()
								.get(row);
						Sequence.getModel().addRow(frame);
					}
				}

			});
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			switch (columnIndex) {
			case COLUMN_NAME:
				Animation.instance().getActionList().get(rowIndex).name = (String) aValue;
				fireTableCellUpdated(rowIndex, columnIndex);
				break;
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
			case COLUMN_NAME:
				return Animation.instance().getActionList().get(rowIndex).name;
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
