package test.wordbreak.report;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.IEngineTask;
import org.eclipse.birt.report.engine.api.IPDFRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.eclipse.birt.report.engine.api.ReportEngine;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;

public class ReportUtil {

	public URL createPDFReport() throws Exception {
		final File tmpFile = File.createTempFile("TestReport_", ".pdf"); //$NON-NLS-1$ //$NON-NLS-2$
		tmpFile.deleteOnExit();
		String absolutePath = tmpFile.getAbsolutePath();
		PDFRenderOption renderOptions = null;
		renderOptions = new PDFRenderOption();
		renderOptions.setOutputFormat(IPDFRenderOption.OUTPUT_FORMAT_PDF);
		renderOptions.setOption(IPDFRenderOption.PAGE_OVERFLOW, IPDFRenderOption.OUTPUT_TO_MULTIPLE_PAGES);
		renderOptions.setOption(IPDFRenderOption.PDF_TEXT_WRAPPING, true);
		renderOptions.setOption(IPDFRenderOption.PDF_WORDBREAK, true);
		renderOptions.setOutputFileName(absolutePath);
		createReport(getReportUrl(), renderOptions);

//		final File tmpFileA = File.createTempFile("atfReport_", ".pdf"); //$NON-NLS-1$ //$NON-NLS-2$
//		tmpFile.deleteOnExit();
//		PdfHelper.convertToPdfa(tmpFile, tmpFileA, new PdfAOptions().author("ATF").subject(reportName).language(locale)
//				.conformance(ConformanceLevel.PDF_A_1B));

//		return tmpFileA.toURI().toURL();
		
		return tmpFile.toURI().toURL();
	}

	URL getReportUrl() throws IOException {
		Bundle bundle = Platform.getBundle("test.wordbreak");

		URL url = bundle.getEntry("/birt/report.rptdesign");

		url = FileLocator.toFileURL(url);

		return url;
	}

	void createReport(URL reportDesign, RenderOption renderOption) {
		IReportEngine engine = null;
		EngineConfig config = null;
		IReportRunnable design = null;
		IRunAndRenderTask task;
		try {
			InputStream fs = null;
			try {
				fs = reportDesign.openStream();
			} catch (MalformedURLException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			Map<String, Object> parameters = new HashMap<>();

			config = new EngineConfig();

//			config.setResourceLocator(locator);

			Bundle bundle = FrameworkUtil.getBundle(ReportUtil.class);
			BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
			ClassLoader loader = bundleWiring.getClassLoader();

			// loader = ReportUtil.class.getClassLoader();
			config.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, loader);
			config.getAppContext().put(EngineConstants.APPCONTEXT_DATASET_CACHE_OPTION, Boolean.FALSE.toString());
			config.getAppContext().put(EngineConstants.REFRESH_DATA, Boolean.TRUE.toString());

			engine = new ReportEngine(config);
			design = engine.openReportDesign(fs);
			task = engine.createRunAndRenderTask(design);
			for (String key : parameters.keySet()) {
				Object object = parameters.get(key);
				task.setParameterValue(key, object);
			}
			task.setRenderOption(renderOption);
			task.setLocale(Locale.US);
			task.run();
			if (task.getStatus() == IEngineTask.STATUS_FAILED) {
				System.err.println("task failed");
			}
			task.close();
			engine.destroy();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
