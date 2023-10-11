package journalViewer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.MapContext;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Element;

class JournalViewerSetUp{
	String FILE_NAME;

	static JexlContext 	previous =  new MapContext()
					 	,current =  new MapContext();

	Text message; 
	JournalViewer journalViewer;
	@Inject EPartService ePartService;
	@Inject MDirtyable dirty;
	@PostConstruct public void postConstruct (Composite parent,MPart mPart, @Named(IServiceConstants.ACTIVE_SELECTION) Element eStudy)   {
		
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

		journalViewer=new JournalViewer(parent);
		fd =new FormData();
		fd.left=new FormAttachment(0);
		fd.top=new FormAttachment(0);
		fd.bottom=new FormAttachment(statusBar);
		fd.right=new FormAttachment(100);


		journalViewer.setLayoutData(fd);
		
		FILE_NAME=eStudy.getAttribute("arg1");
		
	    long start = System.currentTimeMillis();
	    
		journalViewer.setColumns(FILE_NAME+".col");
		journalViewer.setFields(FILE_NAME+".fmt");
		journalViewer.setData(FILE_NAME+".csv");

        long end = System.currentTimeMillis();
	    float sec = (end - start) / 1000F; 
	    System.out.println(sec + " seconds");

		journalViewer.setMessageBox(new IMessageBox() {
			@Override
			public void send(String messageDetail) {
				message.setText(messageDetail);
			}
		});
		
		journalViewer.setModifierNotifier(new IModifierNotifier() {
			@Override
			public void setDirty(boolean status) {
				dirty.setDirty(status);
			}
		});
	}
	@Persist  void saveIt() {
		journalViewer.saveIt(FILE_NAME+".col");
	};
}

