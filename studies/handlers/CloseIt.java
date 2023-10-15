package studies.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.w3c.dom.Element;

import studies.StudiesSetUp;


public class CloseIt {
		@CanExecute	public boolean canExecute (@Optional @Named(IServiceConstants.ACTIVE_SELECTION) Element element) throws Throwable {

			if (element.hasAttribute("open")) return Boolean.valueOf(element.getAttribute("open"));
			
			return false;
		}

		@Execute public void execute(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) Element element) throws Throwable{
			if (element.hasAttribute("open")) element.setAttribute("open", "false");	
			
			StudiesSetUp.refreshIt();
		}	
}
