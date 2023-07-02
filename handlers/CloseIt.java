package p.d.m.studies.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;
import org.w3c.dom.Element;

import p.d.m.studies.Studies;


public class CloseIt {
		@CanExecute	public boolean canExecute (@Optional @Named(IServiceConstants.ACTIVE_SELECTION) Element element) throws Throwable {

			if (element.hasAttribute("open")) return Boolean.valueOf(element.getAttribute("open"));
			
			return false;
		}

		@Execute public void execute(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) Element element) throws Throwable{
			if (element.hasAttribute("open")) element.setAttribute("open", "false");	
			
			Studies.refreshIt();
		}	
}
