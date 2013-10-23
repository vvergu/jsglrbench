package bench;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.jsglr.client.Asfix2TreeBuilder;
import org.spoofax.jsglr.client.Disambiguator;
import org.spoofax.jsglr.client.FilterException;
import org.spoofax.jsglr.client.imploder.TermTreeFactory;
import org.spoofax.jsglr.client.imploder.TreeBuilder;
import org.spoofax.jsglr.io.SGLR;
import org.spoofax.jsglr.shared.SGLRException;
import org.spoofax.sunshine.Environment;
import org.spoofax.sunshine.parser.model.IParserConfig;
import org.spoofax.terms.attachments.ParentTermFactory;

/**
 * @author Vlad Vergu <v.a.vergu add tudelft.nl>
 */
public class JSGLRI {

	private final IParserConfig config;

	private boolean useRecovery = false;

	private SGLR parser;

	private Disambiguator disambiguator;

	private int cursorLocation = Integer.MAX_VALUE;

	private boolean implodeEnabled = true;

	public void setCursorLocation(int cursorLocation) {
		this.cursorLocation = cursorLocation;
	}

	public JSGLRI(IParserConfig config) {
		assert config != null;
		this.config = config;
		final TermTreeFactory factory = new TermTreeFactory(
				new ParentTermFactory(Environment.INSTANCE().termFactory));
		this.parser = new SGLR(new TreeBuilder(factory), config
				.getParseTableProvider().getParseTable());
		resetState();
	}

	public void setUseRecovery(boolean useRecovery) {
		this.useRecovery = useRecovery;
		parser.setUseStructureRecovery(useRecovery);
	}

	public void setImplodeEnabled(boolean implode) {
		this.implodeEnabled = implode;
		resetState();
	}

	/**
	 * Resets the state of this parser, reinitializing the SGLR instance
	 */
	private void resetState() {
		parser.setTimeout(config.getTimeout());
		if (disambiguator != null)
			parser.setDisambiguator(disambiguator);
		else
			disambiguator = parser.getDisambiguator();
		setUseRecovery(useRecovery);
//		if (!implodeEnabled) {
			parser.setTreeBuilder(new Asfix2TreeBuilder(
					Environment.INSTANCE().termFactory));
//		} else {
//			assert parser.getTreeBuilder() instanceof TreeBuilder;
//			@SuppressWarnings("unchecked")
//			ITreeFactory<IStrategoTerm> treeFactory = ((TreeBuilder) parser
//					.getTreeBuilder()).getFactory();
//			assert ((TermTreeFactory) treeFactory).getOriginalTermFactory() instanceof ParentTermFactory;
//		}
	}

	public IStrategoTerm parse(String input, String filename) {
		resetState();
		IStrategoTerm ast = null;
		try {
			ast = actuallyParse(input, filename);
		} catch (Exception e) {
			throw new RuntimeException("Parse failure", e);
		}
		return ast;
	}

	private IStrategoTerm actuallyParse(String input, String filename)
			throws SGLRException, InterruptedException {
		IStrategoTerm result;
		try {
			result = (IStrategoTerm) parser.parse(input, filename,
					config.getStartSymbol(), false, cursorLocation);
		} catch (FilterException fex) {
//			if (fex.getCause() == null
//					&& parser.getDisambiguator().getFilterPriorities()) {
//				disambiguator.setFilterPriorities(false);
//				try {
//					result = (IStrategoTerm) parser.parse(input, filename,
//							config.getStartSymbol());
//				} finally {
//					disambiguator.setFilterPriorities(true);
//				}
//			} else {
				throw fex;
//			}
		}
		return result;
	}

	public IParserConfig getConfig() {
		return config;
	}

	protected SGLR getParser() {
		return parser;
	}

}
