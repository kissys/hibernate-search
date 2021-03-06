/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2013, Red Hat, Inc. and/or its affiliates or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat, Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.search.test.performance.task;

import static org.hibernate.search.test.performance.scenario.TestContext.ASSERT_QUERY_RESULTS;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.Query;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.test.performance.model.Book;
import org.hibernate.search.test.performance.scenario.TestContext;

/**
 * @author Tomas Hradec
 */
public class QueryBooksBySummaryTask extends AbstractTask {

	private static final String[] PHRASES = new String[] {
			"software development career",
			"agile software craftsmanship",
			"When programming teams buy into TDD",
			"Lisa Crispin",
			"time-consuming process",
			"continuous integration",
			"Linus Torvalds",
			"configuration management tool Puppet",
			"Ruby",
			"the core model-view-controller (MVC) architectural",
			"JavaScript-based platform",
			"server-side development with Node",
			"ASP.NET MVC 4",
			"Pro C# 5.0",
			"Windows Presentation Foundation",
			"Visual Studio 2012 Professional",
			"Windows Phone, WindowsRT",
			"jQuery Mobile",
			"Spring Framework",
			"Jason Van Zyl",
			"Ant as the build tool",
			"Subversion",
			"1970s",
			"bash shell",
			"Pocket Reference",
			"most experienced system administrators",
			"GNU Emacs",
			"Perl bible",
			"Python's fundamentals",
	};

	public QueryBooksBySummaryTask(TestContext ctx) {
		super( ctx );
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void execute(FullTextSession fts) {
		long bookCount = ctx.bookIdCounter.get();
		String phrase = PHRASES[(int) ( bookCount % PHRASES.length )];

		Query q = fts.getSearchFactory()
				.buildQueryBuilder()
				.forEntity( Book.class )
				.get()
				.phrase()
				.withSlop( 3 )
				.onField( "summary" )
				.sentence( phrase )
				.createQuery();

		List<Book> result = fts.createFullTextQuery( q, Book.class ).list();

		if ( ASSERT_QUERY_RESULTS ) {
			assertResult( result, phrase );
			assertResultSize( result, phrase, bookCount );
		}
	}

	private void assertResult(List<Book> result, String phrase) {
		for ( Book book : result ) {
			assertTrue( "QueryBooksBySummaryTask: phrase=" + phrase + ", summary=" + StringUtils.substring( book.getSummary(), 0, 50 ),
					StringUtils.containsIgnoreCase( book.getSummary(), phrase ) );
		}
	}

	private void assertResultSize(List<Book> result, String phrase, long estimatedBooksCount) {
		long estimatedBooksCountPerSummary = estimatedBooksCount / InsertBookTask.SUMMARIES.length;
		long tolerance = 2 * TestContext.THREADS_COUNT;

		if ( result.size() < ( estimatedBooksCountPerSummary - tolerance )
				|| result.size() > ( estimatedBooksCountPerSummary + tolerance ) ) {
			fail( "QueryBooksBySummaryTask: phrase=" + phrase + ", actual=" + result.size() + ", estimate=" + estimatedBooksCountPerSummary );
		}
	}

}
