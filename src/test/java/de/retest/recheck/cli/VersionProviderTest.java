package de.retest.recheck.cli;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class VersionProviderTest {

	@Test
	void versions_should_be_provided() throws Exception {
		final VersionProvider cut = new VersionProvider();

		final String[] versions = cut.getVersion();

		// logo + recheck.cli + recheck + Java
		assertThat( versions ).hasSize( 4 );

		final String recheckCliVersion = versions[1];
		final String recheckVersion = versions[2];
		final String javaVersion = versions[3];

		assertThat( recheckCliVersion ).isEqualTo( "recheck CLI version n/a" );
		assertThat( recheckVersion ).startsWith( "recheck version" );
		assertThat( javaVersion ).startsWith( "Java version" );
	}

}
