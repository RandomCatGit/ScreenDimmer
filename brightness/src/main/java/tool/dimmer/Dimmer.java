package tool.dimmer;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;

/**
 * Dimmer is simple class which provides an adjustable semi-transparent layer to filter light below minimum monitor
 * brightness level.
 *
 * @author RandomCatGit
 */
public class Dimmer {

	static JDialog filter = new JDialog();

	static JFrame settings = new JFrame();

	public static void main(String[] args) throws AWTException {
		setupSettings();

		MenuItem exit = new MenuItem("Exit");
		MenuItem adj = new MenuItem("Adjust Brightness");
		final PopupMenu popup = new PopupMenu();
		final TrayIcon trayIcon = new TrayIcon(new ImageIcon("dimmer.png").getImage(), "Brightness Settings");
		final SystemTray tray = SystemTray.getSystemTray();

		adj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				settings.setVisible(true);
				settings.setState(Frame.NORMAL);
			}
		});

		exit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		popup.add(adj);
		popup.addSeparator();
		popup.add(exit);
		trayIcon.setPopupMenu(popup);
		tray.add(trayIcon);

		settings.addWindowFocusListener(new WindowFocusListener() {

			public void windowLostFocus(WindowEvent e) {
				settings.setVisible(false);
			}

			public void windowGainedFocus(WindowEvent e) {
			}
		});

		settings.addWindowListener(new WindowListener() {

			public void windowOpened(WindowEvent e) {
			}

			public void windowIconified(WindowEvent e) {
				settings.setVisible(false);
			}

			public void windowDeiconified(WindowEvent e) {
			}

			public void windowDeactivated(WindowEvent e) {
			}

			public void windowClosing(WindowEvent e) {
			}

			public void windowClosed(WindowEvent e) {
				tray.remove(trayIcon);
			}

			public void windowActivated(WindowEvent e) {
			}
		});

		trayIcon.addMouseListener(new MouseListener() {

			public void mouseReleased(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					settings.setVisible(true);
					settings.setState(Frame.NORMAL);
					settings.toFront();
				}
			}
		});

		addSlider();

		showFilter();
	}

	private static void setupSettings() {
		settings.setIconImage(new ImageIcon("dimmer.png").getImage());
		Rectangle d = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		settings.setBounds((int) d.getWidth() - 240, (int) d.getHeight() - 100, 240, 100);
		settings.setTitle("Screen Brightness Settings");
		settings.setExtendedState(JFrame.ICONIFIED);
		settings.setLayout(null);
		settings.setResizable(false);
		settings.setAutoRequestFocus(true);
		settings.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		settings.setAlwaysOnTop(true);
	}

	private static void addSlider() {
		final JSlider sl = new JSlider(SwingConstants.HORIZONTAL, 0, 99, 50);
		sl.setBounds(20, 0, 200, 60);
		settings.add(sl);

		sl.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				filter.setBackground(
						new Color(0.0f, 0.0f, 0.0f, Float.parseFloat(String.valueOf(sl.getValue())) / 100));
			}
		});

	}

	private static void showFilter() {
		filter.setTitle("Screen Brightness Regulator");
		filter.setBounds(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		filter.setUndecorated(true);
		filter.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.5f));
		filter.setAlwaysOnTop(true);
		filter.setVisible(true);
		setTransparent(filter);
	}

	private static void setTransparent(Component w) {
		WinDef.HWND hwnd = getHWnd(w);
		int wl = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
		wl = wl | WinUser.WS_EX_LAYERED | WinUser.WS_EX_TRANSPARENT;
		User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);
	}

	private static HWND getHWnd(Component w) {
		HWND hwnd = new HWND();
		hwnd.setPointer(Native.getComponentPointer(w));
		return hwnd;
	}
}
