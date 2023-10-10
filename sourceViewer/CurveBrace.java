package sourceViewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;


public class CurveBrace extends Sash {
	static final int startAngle=90,arcAngle=90;
	static int done = 0;
	static final int min=80, max=200;
	static final int radMin=10, radMax=20;
	static int radius=0;

	static final float  a =(float)(radMax-radMin)/(float)(max-min);
	static final float  b = radMax - a * max;

	static final int lineWidth=2;
	private ClauseConditional parent;
	private final Color curveBraceColor;
	private Image collapsed, expanded, status;
	private Cursor defaultCursor, handCursor;	

	public CurveBrace   (ClauseConditional parent) {
		super(parent, SWT.VERTICAL|SWT.SMOOTH|SWT.DOUBLE_BUFFERED);
		this.parent=parent;
		curveBraceColor= getAndDisposeColor(0, 0, 0);

		addListener(SWT.Paint,   new Listener() {
			@Override
			public void handleEvent(Event e) {
				if (parent.isExpanded()) {
					Control control= (Control) e.widget;

					Rectangle r = control.getBounds();

					GC gc = e.gc;

					gc.setForeground(curveBraceColor);
					gc.setLineWidth(lineWidth);

					if (r.height<min) radius=10;
					else if (r.height<max) radius= (int) (a*r.height+b);
					else radius = 20;					

					gc.drawArc(r.width/2, 0					,radius	,radius	,90	 ,90);
					gc.drawArc(r.width/2-radius, r.height/2-radius	,radius	,radius	,270 ,90);
					
					gc.drawArc(r.width/2-radius, r.height/2+1		,radius	,radius	,0	 ,90);
					gc.drawArc(r.width/2, r.height-radius-lineWidth	,radius ,radius	,180 ,90);

					gc.drawPolyline(new int[] {r.width/2	,radius/2				,r.width/2		,r.height/2-radius/2});
					gc.drawPolyline(new int[] {r.width/2	,r.height/2+radius/2	,r.width/2		,r.height-radius/2-lineWidth});

					gc.dispose();
				}
			}
		});
		addListener(SWT.Selection, e -> {

			int delta= e.x-getLocation().x;
			if (delta!=0){
				parent.setWidth(delta+parent.getWidth());
				FormData fd = (FormData) this.getLayoutData();
				fd.left=new FormAttachment(0,parent.getWidth());
				parent.layout();
			}
		});
		
		addListener(SWT.Resize, e ->{
			redraw();
		});
		
	}

	private Color getAndDisposeColor(int r, int g, int b) {
		final Color color = new Color(getDisplay(), r, g, b);
		addDisposeListener(e -> {
			if (!color.isDisposed()) {
				color.dispose();
			}
		});
		return color;
	}

	@Override    
	protected void checkSubclass() {        
	}
}
