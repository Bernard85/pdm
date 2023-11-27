package sourceViewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Element;

public class Clause extends AClause {
	ClauseSash h1,v1;
	Composite parent;
	public Clause(Composite parent, Element element) {
		super(parent, element);
		this.parent=parent; 
		setLayout(new FormLayout());
		//
		FormData  fd = new FormData();
		fd.top=new FormAttachment(0);
		fd.left=new FormAttachment(0);
		fd.right=new FormAttachment(100);
		fd.bottom=new FormAttachment(100);
		clauseText.setLayoutData(fd);
		//
		h1= new ClauseSash(parent,SWT.HORIZONTAL);
		h1.addListener(SWT.Selection, e -> {
			int delta =e.y-h1.getLocation().y;
			setHeight(getHeight()+delta);
			parent.layout();
			clauseText.redraw();
		});
		//
		v1 = new ClauseSash(parent, SWT.VERTICAL);
		v1.addListener(SWT.Selection, e -> {
			int delta =e.x-v1.getLocation().x;
			setWidth(getWidth()+delta);
			parent.layout();
			clauseText.redraw();
		});
		
	}
	
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(getWidth(),getHeight());
	}

	@Override
	protected int getRealWidth() {
		return getWidth();
	}

	@Override
	protected int getRealHeight() {
		return getHeight();
	}
}
