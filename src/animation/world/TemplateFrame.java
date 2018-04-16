package animation.world;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import animation.editor.SelectionTableModel;
import animation.editor.TableTransferHandler;

/**
 * 模板帧
 * 
 * @author 段链
 * 
 * @time 2012-9-19
 * 
 */
public class TemplateFrame extends AniFrame implements Cloneable, Serializable {
	private static final long serialVersionUID = 2483984386095036856L;

	private static SelectionTableModel<AniFrame> model;

	public TemplateFrame(String name) {
		super(name);
	}

	/**
	 * 获取选择的模板的指定帧
	 * 
	 * @param frameID
	 */
	static AniFrame getFrame(int frameID) {
		Template temp = Template.getModel().getSelection();
		if (temp != null) {
			temp.getAnimation().getFrameList().get(frameID);
		}

		return null;
	}

	public int getId() {
		Template temp = Template.getModel().getSelection();
		if (temp != null) {
			return temp.getAnimation().getFrameList().indexOf(this);
		}

		return -1;
	}

	@Override
	protected boolean checkPointInSprite(Sprite sprite, Point2D p) {
		Point dp = new Point();
		try {
			sprite.inverseTransform(p, dp);
			Rectangle rect = new Rectangle(sprite.module.width,
					sprite.module.height);
			return rect.contains(dp);
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 缩放选择的模板的所有帧
	 * 
	 * @param d
	 *            缩放因子
	 */
	public static void scale(double d) {
		Template temp = Template.getModel().getSelection();
		if (temp != null) {
			for (AniFrame frame : temp.getAnimation().getFrameList()) {
				scaleFrame(d, frame);
			}
		}
	}

	public static SelectionTableModel<AniFrame> getModel() {
		if (model == null) {
			model = new FrameTableModel();
		}

		return model;
	}

	public static void setTable(JTable table) {
		getModel().setTable(table);
	}

	static class FrameTableModel extends SelectionTableModel<AniFrame> {
		private static final long serialVersionUID = -4088566814990175140L;

		static final String[] columnNames = { "Name", "ID" };
		static final Class<?>[] columnClasses = { String.class, Integer.class };
		static final boolean[] columnEditables = new boolean[2];

		public static final int COLUMN_NAME = 0;
		public static final int COLUMN_ID = 1;

		public FrameTableModel() {
			super(null);
			Template.getModel().addSelectionListener(
					new ListSelectionListener() {

						@Override
						public void valueChanged(ListSelectionEvent e) {
							changeSelectedTemplate();
						}
					});
			Sprite.getModel().addSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {
					changeSelectedInnerTemplate();
				}
			});
		}

		private void changeSelectedTemplate() {
			Template temp = Template.getModel().getSelection();
			if (temp != null) {
				setDataArrayList(temp.getAnimation().getFrameList());
				selectNext();
			} else {
				setDataArrayList(null);
			}
		}

		private void changeSelectedInnerTemplate() {
			for (Sprite sprite : Sprite.getModel().getSelections()) {
				if (sprite.module == null) {
					Template temp = sprite.templateWind;
					int index = temp.getId();
					Template.getModel().getSelectionModel()
							.setSelectionInterval(index, index);
					TemplateFrame
							.getModel()
							.getSelectionModel()
							.setSelectionInterval(sprite.frameIDWind,
									sprite.frameIDWind);
					break;
				}
			}
		}

		@Override
		public void setTable(JTable table) {
			super.setTable(table);
			table.setTransferHandler(new TableTransferHandler(
					TableTransferHandler.TEMPLATE_FRAME_FLAVOR, null) {
				private static final long serialVersionUID = 5073022886867188307L;

				@Override
				public boolean canImport(TransferSupport support) {
					return false;
				}

				@Override
				protected Transferable createTransferable(JComponent c) {
					ArrayList<Integer> v = new ArrayList<Integer>();
					v.add(Template.getModel().getSelectionID());
					v.add(TemplateFrame.getModel().getSelectionID());
					return new TableTransferHandler.ArrayListTransferable(v,
							TableTransferHandler.TEMPLATE_FRAME_FLAVOR);
				}

			});
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Template temp = Template.getModel().getSelection();
			if (temp != null) {
				switch (columnIndex) {
				case COLUMN_NAME:
					return temp.getAnimation().getFrameList().get(rowIndex).name;
				case COLUMN_ID:
					return rowIndex;
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
