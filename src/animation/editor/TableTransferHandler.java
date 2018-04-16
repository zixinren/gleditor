package animation.editor;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import animation.world.Animation;

/**
 * 表数据转换处理器
 * 
 * @author 段链
 * @time 2012-9-19
 */
public class TableTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 2028582997240613578L;

	public static final DataFlavor ArrayList_FLAVOR = new DataFlavor(
			ArrayList.class, "ArrayList");
	public static final DataFlavor MODULE_FLAVOR = new DataFlavor(
			ArrayList.class, "Module");
	public static final DataFlavor SPRITE_FLAVOR = new DataFlavor(
			ArrayList.class, "SPRITE");
	public static final DataFlavor FRAME_FLAVOR = new DataFlavor(
			ArrayList.class, "Frame");
	public static final DataFlavor SEQUENCE_FLAVOR = new DataFlavor(
			ArrayList.class, "Sequence");
	public static final DataFlavor ACTION_FLAVOR = new DataFlavor(
			ArrayList.class, "Action");
	public static final DataFlavor TEMPLATE_FRAME_FLAVOR = new DataFlavor(
			ArrayList.class, "TemplateFrame");

	private DataFlavor srcType;
	private DataFlavor dropType;
	private JComponent source;

	public TableTransferHandler(DataFlavor srcType, DataFlavor dropType) {
		this.srcType = srcType;
		this.dropType = dropType;
	}

	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY_OR_MOVE;
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		source = c;

		if (source instanceof JTable) {
			JTable table = (JTable) source;
			return new ArrayListTransferable(table.getSelectedRows(), srcType);
		}

		return null;
	}

	@Override
	public boolean canImport(TransferSupport support) {
		if (Animation.instance().isReadOnly())
			return false;

		for (DataFlavor dataFlavor : support.getDataFlavors()) {
			if (dropType != null
					&& dropType.getHumanPresentableName().equals(
							dataFlavor.getHumanPresentableName()))
				return true;
			if (srcType != null
					&& srcType.getHumanPresentableName().equals(
							dataFlavor.getHumanPresentableName()))
				return true;
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean importData(TransferSupport support) {
		if (!canImport(support))
			return false;

		SelectionTableModel model = null;
		int index = 0;
		Component component = support.getComponent();
		/**
		 * JTable是镶嵌在JScrollPane上, 它们有共同的TransferHandler<br>
		 * 拖拽到JTable, 则插入到最大选择项的前面; 拖拽到JScrollPane, 则追加到表项的后面
		 */
		if (component instanceof JTable) {
			model = (SelectionTableModel) ((JTable) component).getModel();
			index = model.getSelectionModel().getMaxSelectionIndex();
		} else if (component instanceof JPanel) {
			model = (SelectionTableModel) ((JTable) ((JScrollPane) component)
					.getViewport().getComponent(0)).getModel();
			index = model.getRowCount();
		}

		Transferable t = support.getTransferable();

		try {
			if (t.isDataFlavorSupported(srcType)) {
				@SuppressWarnings("unchecked")
				ArrayList<Integer> v = (ArrayList<Integer>) t
						.getTransferData(srcType);
				model.moveRows(ArrayListToIntArray(v), index);
				return true;
			}
			if (t.isDataFlavorSupported(dropType)) {
				@SuppressWarnings("unchecked")
				ArrayList<Integer> v = (ArrayList<Integer>) t
						.getTransferData(dropType);
				dropData(index, ArrayListToIntArray(v));
			}

		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Toolkit.getDefaultToolkit().beep();
		return false;
	}

	/**
	 * 插入数据
	 * 
	 * @param index
	 *            插入的行索引
	 * @param rows
	 *            插入的数据
	 */
	public void dropData(int index, int[] rows) {
		Toolkit.getDefaultToolkit().beep();
	}

	private int[] ArrayListToIntArray(ArrayList<Integer> v) {
		int[] array = new int[v.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = v.get(i);
		}

		return array;
	}

	public static class ArrayListTransferable implements Transferable {
		ArrayList<Integer> data;
		DataFlavor flavor;

		public ArrayListTransferable(ArrayList<Integer> data, DataFlavor type) {
			this.flavor = type;
			this.data = data;
		}

		public ArrayListTransferable(int[] rows, DataFlavor type) {
			data = new ArrayList<Integer>(rows.length);
			for (int row : rows) {
				data.add(row);
			}
			this.flavor = type;
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { flavor };
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return this.flavor.equals(flavor)
					&& flavor.getHumanPresentableName().equals(
							this.flavor.getHumanPresentableName());
		}

		@Override
		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			if (!isDataFlavorSupported(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}

			return data;
		}

	}
}
