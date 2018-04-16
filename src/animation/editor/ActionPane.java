package animation.editor;

import static animation.editor.MainFrame.useIcon;
import static animation.editor.MainFrame.useToolTipText;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import animation.world.AniAction;
import animation.world.Sequence;

/**
 * 动作面板
 * 
 * @author 段链
 * @time 2012-9-15
 */
public class ActionPane extends JPanel implements Localizable, Runnable {
	private static final long serialVersionUID = 4257788290439164541L;

	JToolBar toolBar = new JToolBar();
	JButton addNewButton = new JButton();
	JButton moveUpButton = new JButton();
	JButton moveDownButton = new JButton();
	JButton removeButton = new JButton();
	JButton cloneButton = new JButton();

	JPanel contentPanel = new JPanel();

	JScrollPane tableScrollPane = new JScrollPane();
	JTable actionTable = new JTable();

	JPanel titledPanel = new JPanel();
	JLabel titledLabel = new JLabel();

	JToolBar toolBar2 = new JToolBar();
	/**
	 * 显示动作的下一帧
	 */
	JButton stepButton = new JButton();
	/**
	 * 循环播放动作<br>
	 * 触发播放后, 按钮显示停止图标<br>
	 * 触发停止后, 按钮显示播放图标<br>
	 */
	JButton playButton = new JButton();

	private int delay = 0;
	private boolean isRunning;
	private Thread thread;

	public ActionPane() {
		try {
			jbInit();
			updateLocalization();
			listenerInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化动作面板组件
	 */
	private void jbInit() {
		addNewButton.setRequestFocusEnabled(false);
		moveUpButton.setRequestFocusEnabled(false);
		moveDownButton.setRequestFocusEnabled(false);
		removeButton.setRequestFocusEnabled(false);
		cloneButton.setRequestFocusEnabled(false);

		toolBar.setOrientation(JToolBar.HORIZONTAL);
		toolBar.setFloatable(false);
		toolBar.add(addNewButton);
		toolBar.add(moveUpButton);
		toolBar.add(moveDownButton);
		toolBar.add(removeButton);
		toolBar.add(cloneButton);

		titledLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		titledPanel.setBackground(SystemColor.info);
		titledPanel.setLayout(new BorderLayout());
		titledPanel.add(titledLabel, BorderLayout.NORTH);

		tableScrollPane.getViewport().add(actionTable);
		AniAction.setTable(actionTable);

		playButton.setRequestFocusEnabled(false);
		stepButton.setRequestFocusEnabled(false);

		playButton.setMnemonic(KeyEvent.VK_P);
		stepButton.setMnemonic(KeyEvent.VK_S);

		toolBar2.setOrientation(JToolBar.HORIZONTAL);
		toolBar2.setFloatable(false);
		toolBar2.add(playButton);
		toolBar2.add(stepButton);

		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(toolBar, BorderLayout.NORTH);
		contentPanel.add(tableScrollPane, BorderLayout.CENTER);
		contentPanel.add(toolBar2, BorderLayout.SOUTH);
		contentPanel.setDebugGraphicsOptions(0);

		setEnabled(true);
		setLayout(new BorderLayout());
		add(titledPanel, BorderLayout.NORTH);
		add(contentPanel, BorderLayout.CENTER);
	}

	@Override
	public void updateLocalization() {
		titledLabel.setText("Action");

		useToolTipText(addNewButton, "addNewButton_ToolTip");
		useToolTipText(moveUpButton, "moveUpButton_ToolTip");
		useToolTipText(moveDownButton, "moveDownButton_ToolTip");
		useToolTipText(removeButton, "removeButton_ToolTip");
		useToolTipText(cloneButton, "cloneButton_ToolTip");
		useToolTipText(playButton, "playButton_ToolTip");
		useToolTipText(stepButton, "stepButton_ToolTip");

		useIcon(addNewButton, "addNewButton_Icon");
		useIcon(moveUpButton, "moveUpButton_Icon");
		useIcon(moveDownButton, "moveDownButton_Icon");
		useIcon(removeButton, "removeButton_Icon");
		useIcon(cloneButton, "cloneButton_Icon");
		useIcon(playButton, "playButton_Icon");
		useIcon(stepButton, "stepButton_Icon");
	}

	private void listenerInit() {
		addNewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AniAction.getModel().addRow();
				JScrollBar scrollBar = tableScrollPane.getVerticalScrollBar();
				scrollBar.setValue(scrollBar.getMaximum());
			}

		});
		moveUpButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AniAction.getModel().moveUp();
			}

		});
		moveDownButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AniAction.getModel().moveDown();
			}

		});
		removeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AniAction.getModel().remove();
			}

		});
		cloneButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AniAction.getModel().cloneRow();
				JScrollBar jScrollBar = tableScrollPane.getVerticalScrollBar();
				jScrollBar.setValue(jScrollBar.getMaximum());
			}

		});

		playButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (isRunning) {
					useToolTipText(playButton, "playButton_ToolTip");
					useIcon(playButton, "playButton_Icon");
					stop();
				} else {
					useToolTipText(playButton, "stopButton_ToolTip");
					useIcon(playButton, "stopButton_Icon");
					play();
				}
			}

		});
		stepButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				stop();
				nextFrame();
			}

		});
	}

	@Override
	public void run() {
		isRunning = true;
		Sequence.getModel().getSelectionModel().clearSelection();
		while (isRunning) {

			Runnable doRun = new Runnable() {

				@Override
				public void run() {
					--delay;
					if (delay <= 0) {
						nextFrame();
						Sequence sequence = Sequence.getModel().getSelection();
						if (sequence != null) {
							delay = sequence.duration;
						}
					}
				}

			};

			if (SwingUtilities.isEventDispatchThread()) {
				doRun.run();
			} else {
				SwingUtilities.invokeLater(doRun);
			}

			try {
				Thread.sleep(Configuration.tick);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 动作帧序列表选择下一帧
	 */
	public void nextFrame() {
		Sequence.getModel().selectNext();
	}

	/**
	 * 开始播放, 如果之前有播放线程执行则要等到这个线程死亡后才继续
	 */
	public void play() {
		if (isRunning) {
			isRunning = false;
			while (thread.isAlive()) {
				try {
					Thread.sleep(Configuration.tick);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		thread = new Thread(this);
		thread.start();
	}

	/**
	 * 停止播放
	 */
	public void stop() {
		isRunning = false;
		thread = null;
	}
}
