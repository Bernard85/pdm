package sourceViewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Sash;

public class ClauseSash extends Sash {

	public ClauseSash(Composite parent, int style) {
		super(parent, style);

		addMouseTrackListener(new MouseTrackListener() {

			@Override
			public void mouseEnter(MouseEvent e) {
				setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
			}

			@Override
			public void mouseExit(MouseEvent e) {
				setBackground(null);
			}

			@Override
			public void mouseHover(MouseEvent e) {
				setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
			}

		});
	}

	@Override
	protected void checkSubclass() {
	}	
}
