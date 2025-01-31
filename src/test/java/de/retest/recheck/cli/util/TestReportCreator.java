package de.retest.recheck.cli.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.rules.TemporaryFolder;

import de.retest.recheck.SuiteAggregator;
import de.retest.recheck.persistence.RecheckSutState;
import de.retest.recheck.persistence.RecheckTestReportUtil;
import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.SuiteReplayResult;
import de.retest.recheck.report.TestReplayResult;
import de.retest.recheck.report.action.ActionReplayData;
import de.retest.recheck.report.action.DifferenceRetriever;
import de.retest.recheck.report.action.WindowRetriever;
import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.Attribute;
import de.retest.recheck.ui.descriptors.Attributes;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.PathAttribute;
import de.retest.recheck.ui.descriptors.RootElement;
import de.retest.recheck.ui.descriptors.StringAttribute;
import de.retest.recheck.ui.descriptors.SutState;
import de.retest.recheck.ui.descriptors.TextAttribute;
import de.retest.recheck.ui.diff.RootElementDifference;
import de.retest.recheck.ui.diff.RootElementDifferenceFinder;

public class TestReportCreator {

	private final static String REPORT_WITHOUT_DIFFS_FILE_NAME = "withoutDiffs.report";
	private final static String REPORT_WITH_DIFFS_FILE_NAME = "withDiffs.report";

	private static SutState sutState;

	public static String createTestReportFileWithoutDiffs( final TemporaryFolder folder ) throws IOException {
		final File result = folder.newFile( REPORT_WITHOUT_DIFFS_FILE_NAME );
		final SuiteReplayResult suite = SuiteAggregator.getInstance().getSuite( "suiteWithoutDiffs" );
		RecheckTestReportUtil.persist( suite, result );
		return result.getPath();
	}

	public static String createTestReportFileWithDiffs( final TemporaryFolder folder ) throws IOException {
		final File result = folder.newFile( REPORT_WITH_DIFFS_FILE_NAME );
		final File recheckFolder = folder.newFolder( "suite_test_check" );

		final List<RootElement> rootElements = getRootElementList();
		final List<RootElementDifference> rootElementDifferenceList = getRootElementDifferenceList( rootElements );

		final SuiteReplayResult suite = SuiteAggregator.getInstance().getSuite( "suite" );
		final TestReplayResult test = new TestReplayResult( "test", 0 );
		suite.addTest( test );

		sutState = RecheckSutState.createNew( recheckFolder, new SutState( rootElements ) );

		final DifferenceRetriever differenceRetriever = DifferenceRetriever.of( rootElementDifferenceList );

		final ActionReplayResult check =
				ActionReplayResult.withDifference( ActionReplayData.withoutTarget( "check", recheckFolder.getName() ),
						WindowRetriever.empty(), differenceRetriever, 0 );

		test.addAction( check );

		RecheckTestReportUtil.persist( suite, result );
		return result.getPath();
	}

	private static List<RootElement> getRootElementList( final String... additinonal ) {
		final RootElement root = new RootElement( "idRoot", new IdentifyingAttributes( getAttributes( additinonal ) ),
				new Attributes(), null, "test", 0, "test" );
		return Collections.singletonList( root );
	}

	private static List<RootElementDifference> getRootElementDifferenceList( final List<RootElement> rootElements ) {
		final RootElementDifferenceFinder finder = new RootElementDifferenceFinder( getDefaultValueFinder() );
		final RootElementDifference difference =
				finder.findDifference( rootElements.get( 0 ), getRootElementList( "diff" ).get( 0 ) );
		return Collections.singletonList( difference );
	}

	private static List<Attribute> getAttributes( final String[] additinonal ) {
		final List<Attribute> attributeList = new ArrayList<>();
		attributeList.add( new StringAttribute( "type", "element" ) );
		attributeList.add( new TextAttribute( "text", "someText" + Arrays.toString( additinonal ) ) );
		attributeList.add( new PathAttribute( Path.fromString( "foo/bar" ) ) );
		return attributeList;
	}

	private static DefaultValueFinder getDefaultValueFinder() {
		return ( identifyingAttributes, attributeKey, attributeValue ) -> false;
	}

	public static SutState getSutState() {
		return sutState;
	}
}
