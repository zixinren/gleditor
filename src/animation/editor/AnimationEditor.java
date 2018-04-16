package animation.editor;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.undo.UndoManager;

/**
 * ¶¯»­±à¼­Æ÷
 * 
 * @author ¶ÎÁ´
 * 
 * @time 2012-9-14
 * 
 */
public class AnimationEditor {
	public static UndoManager undoManager = new UndoManager();

	private boolean packFrame = true;

	public AnimationEditor(String[] args) {
		MainFrame frame = new MainFrame();

		if (packFrame) {
			frame.pack();
		} else {
			frame.validate();
		}

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setSize(1024, 768);
		Dimension frameSize = frame.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		if (args.length > 0) {
			frame.openAnimationFile(args[0]);
		}
	}

	public static void main(final String[] args) {
		Configuration.load();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new AnimationEditor(args);
			}

		});
	}
}
