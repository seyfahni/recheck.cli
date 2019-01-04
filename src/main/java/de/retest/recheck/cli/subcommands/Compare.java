package de.retest.recheck.cli.subcommands;

import java.io.File;

import de.retest.recheck.RecheckAdapter;
import de.retest.recheck.ReplayResultProvider;
import de.retest.recheck.execution.RecheckAdapters;
import de.retest.recheck.execution.RecheckDifferenceFinder;
import de.retest.recheck.persistence.RecheckReplayResultUtil;
import de.retest.recheck.persistence.RecheckSutState;
import de.retest.report.ActionReplayResult;
import de.retest.report.SuiteReplayResult;
import de.retest.report.TestReplayResult;
import de.retest.ui.descriptors.SutState;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command( name = "compare",
		description = "Compare two persisted states to one another. Produces an replay.result file." )
public class Compare implements Runnable {

	@Option( names = { "-h", "--help" }, usageHelp = true, hidden = true )
	private boolean displayHelp;

	@Parameters( index = "0", description = "The first persisted state, being the 'expected' state during comparison." )
	private File expectedState;

	@Parameters( index = "1", description = "The second persisted state, being the 'actual' state during comparison." )
	private File actualState;

	@Parameters( index = "2", defaultValue = "replay.result", description = "Where to persist the result." )
	private File resultFile;

	@Override
	public void run() {
		final SutState expected = RecheckSutState.loadExpected( expectedState );
		// here we treat actual as expected...
		final SutState actual = RecheckSutState.loadExpected( actualState );

		// TODO How do we know which adapter to use if we have both states already converted?
		final RecheckAdapter adapter = RecheckAdapters.findAdapterFor( expectedState );

		final RecheckDifferenceFinder finder = new RecheckDifferenceFinder( adapter.getDefaultValueFinder(),
				expectedState.getName(), expectedState.getPath() );

		final SuiteReplayResult suite = ReplayResultProvider.getInstance().getSuite( "Direct file comparison" );
		final TestReplayResult replayResult = new TestReplayResult( "Comparing " + adapter.toString() + " files", 0 );
		suite.addTest( replayResult );
		final ActionReplayResult actionReplayResult = finder.findDifferences( actual, expected );
		replayResult.addAction( actionReplayResult );
		RecheckReplayResultUtil.persist( suite, resultFile );
	}

}
