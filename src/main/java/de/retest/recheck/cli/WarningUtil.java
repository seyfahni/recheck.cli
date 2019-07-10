package de.retest.recheck.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.ui.diff.AttributeDifference;
import de.retest.recheck.ui.diff.ElementIdentificationWarning;
import de.retest.recheck.ui.review.ReviewResult;

public class WarningUtil {

	private static final Logger logger = LoggerFactory.getLogger( WarningUtil.class );

	private WarningUtil() {
	}

	public static void logWarnings( final ReviewResult reviewResult ) {
		reviewResult.getAttributeDifferences().stream()
				.filter( attributeDifference -> attributeDifference.getElementIdentificationWarning() != null )
				.forEach( attributeDifference -> logger.warn( getWarningMessage( attributeDifference,
						attributeDifference.getElementIdentificationWarning() ) ) );
	}

	private static String getWarningMessage( final AttributeDifference attributeDifference,
			final ElementIdentificationWarning warning ) {
		final String title = "*************** recheck warning ***************\n";
		final String elementIdentifier = attributeDifference.getKey();
		final String expectedValue = attributeDifference.getExpectedToString();
		final String actualValue = attributeDifference.getActualToString();
		final String elementIdentifierInfo =
				String.format( "The HTML %s attribute used for element identification changed from %s to %s.\n",
						elementIdentifier, expectedValue, actualValue );
		final String info = "recheck identified the element based on the persisted Golden Master.\n";
		final String testClassName = warning.getTestClassName();
		final String onApplyChangesInfo = //
				String.format( "If you apply these changes to the Golden Master, your test %s will break.",
						testClassName );
		return title + elementIdentifierInfo + info + onApplyChangesInfo;
	}
}
