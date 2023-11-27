package sourceViewer;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Element;

public class Expandable extends AClause {
	CurveBrace curveBrace;
	ExpandButton expandButton;
	Clauses clauses; 

	ClauseSash h1,h2,v1;
	FormData fdClauseText, fdH2, fdV1;
	Composite parent;
	int clausesPosition=0;
	public Expandable(Composite clauses2, Element element) {
		super(clauses2, element);
		this.parent=clauses2; 
	
		setLayout(new FormLayout());
		// child control creation  
		curveBrace = new CurveBrace(this);
		expandButton = new ExpandButton(this);
		clauses = new Clauses(this, element);
		h1= new ClauseSash(parent,SWT.HORIZONTAL);
		v1= new ClauseSash(this,SWT.VERTICAL);
		h2=new ClauseSash(this,SWT.HORIZONTAL);
		// Layout
		layoutControl();
		//
		deductFromState();
		// add Listeners
		addListeners();
	}
	private void layoutControl() {
		
		fdV1 = new FormData();
		fdV1.height=getHeightCond();
		fdV1.left=new FormAttachment(0,getWidth());
		fdV1.top=new FormAttachment(50,-getHeightCond()/2);
		v1.setLayoutData(fdV1);

    	fdClauseText = new FormData();
		fdClauseText.left= new FormAttachment(0);
		fdClauseText.height=getHeightCond();
		fdClauseText.right=new FormAttachment(v1);
		fdClauseText.bottom=(isExpanded())?new FormAttachment(h2):new FormAttachment(100);
		clauseText.setLayoutData(fdClauseText);
		
		FormData fdExpandButton = new FormData();
		fdExpandButton.top=new FormAttachment(50,-7);
		fdExpandButton.left=new FormAttachment(v1,-2);
		fdExpandButton.width=14;
		fdExpandButton.height=14;
		expandButton.setLayoutData(fdExpandButton);
		
		FormData fdCurveBrace = new FormData();
		fdCurveBrace.top=new FormAttachment(0);
		fdCurveBrace.left=new FormAttachment(expandButton);
		fdCurveBrace.width=18;
		fdCurveBrace.bottom=new FormAttachment(100);
		curveBrace.setLayoutData(fdCurveBrace);

		FormData fdClauses = new FormData();
		fdClauses.top=new FormAttachment(0);
		fdClauses.left=new FormAttachment(curveBrace);
		fdClauses.bottom=new FormAttachment(100);
		fdClauses.right=new FormAttachment(100);
		clauses.setLayoutData(fdClauses);

		fdH2=new FormData();
		fdH2.left= new FormAttachment(0);
		fdH2.right=new FormAttachment(v1);
		fdH2.top=new FormAttachment(50, getHeightCond()/2);
		h2.setLayoutData(fdH2);
	}

	private void addListeners() {
		v1.addListener(SWT.Selection, e -> {
			int delta =e.x-v1.getLocation().x;
			setWidth(getWidth()+delta);
			fdV1.left=new FormAttachment(0,getWidth());
			fdV1.height=getHeightCond();
			fdV1.top=new FormAttachment(50,-getHeightCond()/2);
			layout();
			parent.layout();
		});
		
		curveBrace.addListener(SWT.Selection, e -> {
			int delta =e.x-curveBrace.getLocation().x;
			setWidth(getWidth()+delta);
			fdV1.left=new FormAttachment(0,getWidth());
			layout();
			parent.layout();
		});
		//
		clauses.addListener(SWT.Resize, e ->{
			clauses.slider.setThumb(clauses.clauses2.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		});
		//
		h1.addListener(SWT.Selection, e -> {
			modifierNotifier.setDirty(true);
			int delta =e.y-h1.getLocation().y;
			setRealHeight(getRealHeight()+delta);
			
			System.out.println("height/heightcond:"+element.getAttribute("height")+"/"+element.getAttribute("heightcond"));
			
			h1.getParent().layout();
		});
		
		h2.addListener(SWT.Selection, e -> {
			modifierNotifier.setDirty(true);
			int delta =e.y-h2.getLocation().y;
			setHeightCond(getHeightCond()+delta*2);
			fdH2.top.offset+=delta;
			fdClauseText.height=getHeightCond();

			fdV1.height=getHeightCond();
			fdV1.top=new FormAttachment(50,-getHeightCond()/2);

			layout();
		});
		
	}

	public void changeState() {
		modifierNotifier.setDirty(true);
		setExpanded(!isExpanded());
		deductFromState();
	}
	public void deductFromState() {
		h2.setVisible(isExpanded());
		h2.setEnabled(isExpanded());
		clauses.setVisible(isExpanded());
		fdClauseText.bottom=(isExpanded())?new FormAttachment(h2):new FormAttachment(100);
		layout();
	};

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(parent.getSize().x,(isExpanded()?getHeight():getHeightCond()));
	}

	/**
	 * @param expanded the expanded to set
	 */
	public void setExpanded(boolean expanded) {
		element.setAttribute("expanded",String.valueOf(expanded));
	}	
	/**
	 * @return the expanded
	 */
	public Boolean isExpanded() {
		return Boolean.valueOf(element.getAttribute("expanded"));
	}
	/**
	 * @return the heightCond
	 */
	public int getHeightCond() {
		return Integer.valueOf(element.getAttribute("heightcond"));
	}


	/**
	 * @param heightCond the heightCond to set
	 */
	public void setHeightCond(int heightCond) {
		element.setAttribute("heightcond",String.valueOf(heightCond));
	}

	/**
	 * @param append the user input
	 */
	public void appendUserInput(int start, int end, String text) {
		element.setAttribute("userinput",getUserinput()+String.valueOf(start) + "-" + String.valueOf(end) +"\"" +text +"\"|");
	}

	/**
	 * @return the userinput
	 */	

	private String getUserinput() {
		return element.getAttribute("userinput");
	}


	public Clauses getClauses() {
		return clauses;
	}

	/**
	 * @return the clausesPosition
	 */
	public int getClausesPosition() {
		return clausesPosition;
	}

	/**
	 * @param clausesPosition the clausesPosition to set
	 */
	public void setClausesPosition(int clausesPosition) {
		this.clausesPosition = clausesPosition;
	}

	@Override
	protected int getRealWidth() {
		return parent.getClientArea().width;
	}

	@Override
	protected int getRealHeight() {
		return (isExpanded()?getHeight():getHeightCond());
	}
	
	protected void setRealHeight(int realHeight) {
		element.setAttribute((isExpanded())?"height":"heightcond",String.valueOf(realHeight) );
	}
}
