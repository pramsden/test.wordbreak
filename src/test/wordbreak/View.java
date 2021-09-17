package test.wordbreak;

import java.net.URL;

import javax.inject.Inject;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;

import test.wordbreak.report.ReportUtil;

public class View extends ViewPart {
	public static final String ID = "test.wordbreak.view";

	@Inject IWorkbench workbench;

	private Browser browser;
	
	@Override
	public void createPartControl(Composite parent) {
		browser = new Browser(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager toolBar = actionBars.getToolBarManager();
		Action action = new Action("Create Report") {
			@Override
			public void run() {
				try {
					URL url = new ReportUtil().createPDFReport();
					browser.setUrl(url.toExternalForm());
				} catch (Exception e) {
					browser.setText(e.getMessage());
				}
			}
		};
		toolBar.add(action);
		
	}

	

	@Override
	public void setFocus() {
		browser.setFocus();
	}
}