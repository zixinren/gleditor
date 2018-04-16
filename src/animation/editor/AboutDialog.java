package animation.editor;

import static animation.editor.MainFrame.useIcon;
import static animation.editor.MainFrame.useText;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 编辑器关于对话框
 * 
 * @author 段链
 * 
 * @time 2012-9-14
 * 
 */
public class AboutDialog extends JDialog implements ActionListener, Localizable {
	private static final long serialVersionUID = -536991081874201593L;

	private JButton buttonOK = new JButton();
	private JLabel label = new JLabel();
	/**
	 * product标签
	 */
	private JLabel label1 = new JLabel();
	/**
	 * version标签
	 */
	private JLabel label2 = new JLabel();
	/**
	 * build date标签
	 */
	private JLabel label3 = new JLabel();
	/**
	 * copyright标签
	 */
	private JLabel label4 = new JLabel();
	/**
	 * comments标签
	 */
	private JLabel label5 = new JLabel();

	private String product = "Animation Editor";
	private String copyright = "内部交流,严禁外泄";
	private String comments = "Editor of Animation XML";

	public AboutDialog(Frame parent) {
		super(parent);
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			jbInit();
			updateLocalization();
			listenerInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化界面组件布局
	 */
	private void jbInit() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JPanel insertsPanel = new JPanel();
		insertsPanel.setLayout(new FlowLayout());
		insertsPanel.add(buttonOK);

		JPanel panel2 = new JPanel();
		panel2.setLayout(new BorderLayout());

		JPanel insertsPanel2 = new JPanel();
		insertsPanel2.setLayout(new FlowLayout());
		insertsPanel2
				.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		insertsPanel2.add(label);

		JPanel insertsPanel3 = new JPanel();
		insertsPanel3.setLayout(new GridLayout(5, 1));
		insertsPanel3
				.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		insertsPanel3.add(label1);
		insertsPanel3.add(label2);
		insertsPanel3.add(label3);
		insertsPanel3.add(label4);
		insertsPanel3.add(label5);

		panel2.add(insertsPanel2, BorderLayout.WEST);
		panel2.add(insertsPanel3, BorderLayout.CENTER);

		panel.add(panel2, BorderLayout.NORTH);
		panel.add(insertsPanel, BorderLayout.SOUTH);
		add(panel);
		setResizable(true);
	}

	@Override
	public void updateLocalization() {
		useIcon(label, "about_Icon");
		label1.setText(product);
		useText(label2, "version");
		useText(label3, "build_Date");
		label4.setText(copyright);
		label5.setText(comments);
		useText(buttonOK, "okButton_Text");
	}

	/**
	 * 初始化界面相关的组件的监听器设定
	 */
	private void listenerInit() {
		buttonOK.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonOK) {
			cancel();
		}
	}

	@Override
	public void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			cancel();
		}
		super.processWindowEvent(e);
	}

	/**
	 * 退出编辑器关于
	 */
	public void cancel() {
		dispose();
	}
}
