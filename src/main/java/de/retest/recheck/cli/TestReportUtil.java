package de.retest.recheck.cli;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.persistence.Persistence;
import de.retest.recheck.persistence.bin.KryoPersistence;
import de.retest.recheck.report.TestReport;

public class TestReportUtil {

	private static final Logger logger = LoggerFactory.getLogger( TestReportUtil.class );

	private TestReportUtil() {}

	public static TestReport load( final File testReportFile ) throws IOException {
		logger.info( "Checking test report in path '{}'.", testReportFile.getPath() );
		final Persistence<TestReport> persistence = new KryoPersistence<>();
		return persistence.load( testReportFile.toURI() );
	}

	public static void print( final TestReport testReport, final File testReportFile ) {
		logger.info( "Test report '{}' has {} differences in {} tests.", testReportFile.getName(),
				testReport.getDifferencesCount(), testReport.getNumberOfTestsWithChanges() );
	}
}
