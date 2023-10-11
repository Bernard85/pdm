package sourceViewer;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Sash;
import org.w3c.dom.Element;

public class Expandable extends AClause {
	private static final int HMARGIN = 0;
	CurveBrace curveBrace;
	ExpandButton expandButton;
	Clauses clauses; 

	ClauseSash h1,h2;
	Composite parent;
	int clausesPosition=0;
	public Expandable(Composite clauses2, Element element) {
		super(clauses2, element);
		this.parent=clauses2; 
	
		setLayout(new FormLayout());
		//
		curveBrace = new CurveBrace(this);
		FormData fd = new FormData();
		fd.top=new FormAttachment(0);
		fd.left=new FormAttachment(0,getWidth());
		fd.width=18;
		fd.bottom=new FormAttachment(100);
		curveBrace.setLayoutData(fd);
		//
		expandButton = new ExpandButton(this);
		fd = new FormData();
		fd.top=new FormAttachment(50,-7);
		fd.right=new FormAttachment(curveBrace);
		fd.width=14;
		fd.height=14;
		expandButton.setLayoutData(fd);
		//
		clauses = new Clauses(this, element);
		fd = new FormData();
		fd.top=new FormAttachment(0);
		fd.left=new FormAttachment(curveBrace);
		fd.bottom=new FormAttachment(100);
		fd.right=new FormAttachment(100);
		clauses.setLayoutData(fd);
		//
		h1 = new ClauseSash(this,SWT.HORIZONTAL);
		fd = new FormData();
		fd.left= new FormAttachment(0, HMARGIN);
		fd.right=new FormAttachment(expandButton);
		fd.bottom=new FormAttachment(100,-4);
		h1.setLayoutData(fd);
		//
		h1.addListener(SWT.Selection, e -> {
			int delta =e.y-h1.getLocation().y;
			setHeight(delta+getHeight());
			parent.layout();
		});

		h2=new ClauseSash(this,SWT.HORIZONTAL);
		fd=new FormData();
		fd.left= new FormAttachment(0,HMARGIN);
		fd.right=new FormAttachment(expandButton);
		fd.top=new FormAttachment(50,getHeightCond()/2);
		h2.setLayoutData(fd);
		h2.addListener(SWT.Selection, e -> {
			int delta =e.y-h2.getLocation().y;
			setHeightCond(getHeightCond()+delta);
			FormData fd2=(FormData)h2.getLayoutData();
			fd2.top.offset+=delta/2;		
			h2.redraw();
			
			FormData fd3 = (FormData)clauseText.getLayoutData();
			fd3.height=getHeightCond();
			layout();
		});

		clauseText.setSize(SWT.DEFAULT, getHeightCond());
		fd = new FormData();
		fd.left= new FormAttachment(0,HMARGIN);
		fd.height=getHeightCond();
		fd.right=new FormAttachment(expandButton);
		fd.bottom=new FormAttachment(h2,-2,SWT.TOP);
		clauseText.setLayoutData(fd);
		deductFromState();
	}

	public void changeState() {
		setExpanded(!isExpanded());
		deductFromState();
	}
	public void deductFromState() {
		h1.setVisible(isExpanded());
		h1.setEnabled(isExpanded());
		clauses.setVisible(isExpanded());
	};

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(parent.getSize().x,(isExpanded()?getHeight():getHeightCond())+2*HMARGIN);
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
		return isExpanded()?getHeight():getHeightCond();
	}
}
