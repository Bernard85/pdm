package p.d.m.journalViewer;


import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.MapContext;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import p.d.m.project1.IExo;

class Jrn2 implements IExo{
	final String FILE_NAME = "C:\\Users\\Kingdel\\eclipse-workspace\\p.d.m\\file\\jrnSample";

	static JexlContext 	previous =  new MapContext()
					 	,current =  new MapContext();

	Text message;
	JrnViewer jrnViewer;
	@Inject EPartService ePartService;
	@Inject MDirtyable dirty;
	@PostConstruct public void postConstruct (Composite parent)   {

		parent.setLayout(new FormLayout());

		Composite statusBar = new Composite(parent, SWT.BORDER);
		FormData fd=new FormData();
		fd.left = new FormAttachment(0);
		fd.right = new FormAttachment(100);
		fd.bottom = new FormAttachment(100);
		fd.height = 20;
		statusBar.setLayoutData(fd);
		statusBar.setLayout(new FillLayout());

		message =new Text(statusBar, SWT.BORDER);
		message.setEditable(false);

		jrnViewer=new JrnViewer(parent);
		fd =new FormData();
		fd.left=new FormAttachment(0);
		fd.top=new FormAttachment(0);
		fd.bottom=new FormAttachment(statusBar);
		fd.right=new FormAttachment(100);


		jrnViewer.setLayoutData(fd);
	    long start = System.currentTimeMillis();
	    
		jrnViewer.setColumns(FILE_NAME+".col");
		jrnViewer.setFields(FILE_NAME+".fmt");
		jrnViewer.setData(FILE_NAME+".csv");

        long end = System.currentTimeMillis();
	    float sec = (end - start) / 1000F; 
	    System.out.println(sec + " seconds");

		jrnViewer.setMessageBox(new IMessageBox() {
			@Override
			public void send(String messageDetail) {
				message.setText(messageDetail);
			}

		});
		jrnViewer.setModifierNotifier(new IModifierNotifier() {
			@Override
			public void setDirty(boolean status) {
				dirty.setDirty(status);
			}
		});
	}

	@Override
	public void closeIt() throws Throwable {
		System.out.println("closeIt called");
	}

	@Persist  void saveIt() {
		jrnViewer.saveIt(FILE_NAME+".col");
	};
}

