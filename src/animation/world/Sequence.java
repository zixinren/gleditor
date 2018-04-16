package animation.world;

import static animation.world.Util.getInt;

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
 * 帧序列
 * 
 * @author 段链
 * @time 2012-9-15
 */
public class Sequence implements Cloneable, Serializable {
	private static final long serialVersionUID = 8201046211729823110L;

	private static SequenceTableModel model;

	public AniFrame aniFrame;
	public int duration;
	public int flag;

	public Sequence(AniFrame frame) {
		aniFrame = frame;
		duration = 1;
	}

	public Sequence(int frameID) {
		this(AniFrame.getFrame(frameID));
	}

	public Sequence(Animation ani, int frameID) {
		this(ani.getFrameList().get(frameID));
	}

	/**
	 * 获取帧序列使用的帧
	 */
	public AniFrame getFrame() {
		return aniFrame;
	}

	/**
	 * 从XML文件载入一组帧序列
	 * 
	 * @param action
	 */
	static ArrayList<Sequence> loadSequenceFromXml(Element action) {
		return loadSequenceFromXml(Animation.instance(), action);
	}

	/**
	 * 从XML文件载入指定动画的一组帧序列
	 * 
	 * @param ani
	 * @param action
	 */
	static ArrayList<Sequence> loadSequenceFromXml(Animation ani, Element action) {
		ArrayList<Sequence> result = new ArrayList<Sequence>();

		for (Element e : action.getChildren("Sequence")) {
			Sequence sequence = new Sequence(getInt(e, "id"));
			sequence.flag = getInt(e, "flag");
			sequence.duration = getInt(e, "duration");
			result.add(sequence);
		}

		return result;
	}

	/**
	 * 把一组帧序列转换成XML数据
	 * 
	 * @param sequenceList
	 */
	static List<Element> saveSequenceToXml(ArrayList<Sequence> sequenceList) {
		List<Element> result = new ArrayList<Element>();

		for (Sequence sequence : sequenceList) {
			Element e = new Element("Sequence");
			e.setAttribute("id", Integer.toString(sequence.aniFrame.getId()));
			e.setAttribute("flag", Integer.toString(sequence.flag));
			e.setAttribute("duration", Integer.toString(sequence.duration));
			result.add(e);
		}

		return result;
	}

	public static SequenceTableModel getModel() {
		if (model == null) {
			model = new SequenceTableModel();
		}

		return model;
	}

	public static void setTable(JTable table) {
		getModel().setTable(table);
	}

	public static class SequenceTableModel extends
			SelectionTableModel<Sequence> {
		private static final long serialVersionUID = 7631951259585907716L;

		static final String[] columnNames = { "Frame", "Frame ID", "Duration",
				"Flag" };
		static final Class<?>[] columnClasses = { String.class, Integer.class,
				Integer.class, Integer.class };
		static final boolean[] columnEditables = { false, false, true, true };

		public static final int COLUMN_FRAME = 0;
		public static final int COLUMN_FRAME_ID = 1;
		public static final int COLUMN_DURATION = 2;
		public static final int COLUMN_FLAG = 3;

		private AniAction aniAction;

		public SequenceTableModel() {
			super(null);
			AniAction.getModel().addSelectionListener(
					new ListSelectionListener() {

						@Override
						public void valueChanged(ListSelectionEvent e) {
							changeSelectedAction();
						}
					});
		}

		/**
		 * 改变了选择的动作, 则相应改变帧序列表数据
		 */
		private void changeSelectedAction() {
			aniAction = AniAction.getModel().getSelection();
			if (aniAction != null) {
				setDataArrayList(aniAction.getSequences());
				/**
				 * 纠正一下表的选择行索引, 并刷新一下相关视图
				 */
				selectNext();
			} else {
				setDataArrayList(null);
			}
		}

		@Override
		public void setTable(JTable table) {
			super.setTable(table);
			table.setTransferHandler(new TableTransferHandler(
					TableTransferHandler.SEQUENCE_FLAVOR,
					TableTransferHandler.FRAME_FLAVOR) {
				private static final long serialVersionUID = 1473672589759385388L;

				@Override
				public void dropData(int index, int[] rows) {
					if (aniAction == null)
						return;
					for (int i = 0; i < rows.length; i++) {
						Sequence.getModel().insertRow(index + i,
								new Sequence(rows[i]));
					}
				}
			});
		}

		@Override
		public void addRow() {
			throw new UnsupportedOperationException();
		}

		/**
		 * 追加一行帧序列
		 * 
		 * @param frame
		 */
		public void addRow(AniFrame frame) {
			if (aniAction == null)
				return;

			Sequence sequence = new Sequence(frame);
			aniAction.getSequences().add(sequence);
			int row = aniAction.getSequences().size() - 1;
			fireTableRowsInserted(row, row);
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if (aniAction == null)
				return;

			switch (columnIndex) {
			case COLUMN_DURATION:
				aniAction.getSequences().get(rowIndex).duration = (Integer) aValue;
				fireTableCellUpdated(rowIndex, columnIndex);
				break;
			case COLUMN_FLAG:
				aniAction.getSequences().get(rowIndex).flag = (Integer) aValue;
				fireTableCellUpdated(rowIndex, columnIndex);
				break;
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (aniAction == null)
				return null;

			Sequence sequence = aniAction.getSequences().get(rowIndex);
			switch (columnIndex) {
			case COLUMN_FRAME:
				return sequence.aniFrame.name;
			case COLUMN_FRAME_ID:
				return sequence.aniFrame.getId();
			case COLUMN_DURATION:
				return sequence.duration;
			case COLUMN_FLAG:
				return sequence.flag;
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
