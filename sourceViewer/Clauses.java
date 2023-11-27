package sourceViewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Clauses extends Composite {
	Composite parent;
	Element element;
	Slider slider;
	Composite clauses2;
	public Clauses(Composite parent,Element element) {
		super(parent, SWT.NONE);
		this.parent=parent;
		this.element= element;
		setLayout(new FormLayout());

		slider= new Slider(this, SWT.VERTICAL);
		FormData fd = new FormData();
		fd.top=new FormAttachment(0);
		fd.left=new FormAttachment(0);
		fd.bottom=new FormAttachment(100);
		fd.width=20;
		slider.setLayoutData(fd);

		slider.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				clauses2.layout();
			}
		});
		
		clauses2 = new Clauses2(this,element);
		fd = new FormData();
		fd.left=new FormAttachment(slider);
		fd.top=new FormAttachment(0);
		fd.right=new FormAttachment(100);
		fd.bottom=new FormAttachment(100);
		clauses2.setLayoutData(fd);
	}
	/**
	 * @return the clauses2
	 */
	public Composite getClauses2() {
		return clauses2;
	}
}
