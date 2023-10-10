package sourceViewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class ExpandButton extends Canvas {
	private Image collapsed, expanded, status;
	private Cursor handCursor;
	public ExpandButton(ClauseConditional parent) {
		super(parent, SWT.NONE);
		expanded = getAndDisposeImage("/icons/expanded.png");
		collapsed = getAndDisposeImage("/icons/collapsed.png");
		status=parent.isExpanded()?expanded:collapsed;
		setCursor(getAndDisposeCursor(SWT.CURSOR_HAND));
		addPaintListener(e ->{
			paintControl(e.gc);
		});
		addListener(SWT.MouseDown, e-> {
			parent.changeState();
			status=parent.isExpanded()?expanded:collapsed;
			parent.getParent().layout();
			redraw();
			
			Display.getCurrent().setCursorLocation(toDisplay(8,8));


		});


	}
	private void paintControl(GC gc) {
		gc.setAdvanced(true);
		gc.setAntialias(SWT.ON);
		gc.drawImage(status, 0, 0);
	}





	private Image getAndDisposeImage(String path) {
		Image image= new Image(Display.getCurrent(),CurveBrace.class.getResourceAsStream(path));
		addDisposeListener(e -> {
			if (!image.isDisposed()) {
				image.dispose();
			}
		});
		return image;
	}
	private Cursor getAndDisposeCursor(int cursorID) {
		Cursor cursor= new Cursor(Display.getCurrent(), cursorID);
		addDisposeListener(e -> {
			if (!cursor.isDisposed()) {
				cursor.dispose();
			}
		});
		return cursor;
	}


}
