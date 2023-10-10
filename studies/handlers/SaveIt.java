package studies.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class SaveIt {
	@CanExecute 
	public boolean canExecute(EPartService ePartService) {
		return (ePartService!=null && !ePartService.getDirtyParts().isEmpty());
	}

	@Execute
	public void execute (EPartService ePartService) {
		ePartService.saveAll(false);
	}
}
