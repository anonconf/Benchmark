package anon.conf.defeasible_benchmark.defeasible.circle;

import java.util.Iterator;

import anon.conf.defeasible_benchmark.defeasible.AbstractKBStructureGenerator;
import anon.conf.defeasible_benchmark.defeasible.AbstractKBStructureGeneratorIterable;
import anon.conf.defeasible_benchmark.defeasible.DefeasibleBenchDataSet;
import anon.conf.defeasible_benchmark.defeasible.KBStructure;
import fr.lirmm.graphik.DEFT.io.DlgpDEFTParser;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.Rule;

public class CircleBenchDataSet extends DefeasibleBenchDataSet {
	private static final String NAME = "CircleBench";
	
	private int[] sizes;
	private Iterable<? extends Object> KBGeneratorIterable;
	
	public CircleBenchDataSet(int[] sizes, int nbrAtoms, int nbrTerms) {
		this.sizes = sizes;
		this.KBGeneratorIterable = new CircleKBStructureGeneratorIterable(sizes, nbrAtoms, nbrTerms);
	}
	
	public CircleBenchDataSet(int[] sizes) {
		this(sizes, 1, 1);
	}
	
	@Override
	protected int[] getSizes() {
		return this.sizes;
	}
	
	class CircleKBStructureGenerator extends AbstractKBStructureGenerator {

		public CircleKBStructureGenerator(int[] sizes) {
			super(sizes);
		}
		
		public CircleKBStructureGenerator(int[] sizes, int nbrAtoms, int nbrTerms) {
			super(sizes, nbrAtoms, nbrTerms);
		}
		
		@Override
		protected KBStructure generate(int n) {
			KBStructure kb = new KBStructure();
			// Generate the KB
			final String P = "p";
			final String R = "r";
			String defeasible = "DEFT";
			String predicate = P;
			// Add first atoms
			for(int j=0; j < this.getNbrAtoms(); j++) {
				String atomString = predicate + "1" + "_" + j + this.getTermsString("a") + ".";
				Atom atom = DlgpDEFTParser.parseAtom(atomString);
				kb.addAtom(atom);
			}
		
			// Add rules
			for(int i=1; i <= n; i++) {
				String label = "["+ defeasible + R + i + "] ";
				String body = predicate + (i) + "_0" + this.getTermsString("X");
				for(int j=1; j < this.getNbrAtoms(); j++) {
					body += ", ";
					body += predicate + (i) + "_" + j + this.getTermsString("X");
				}
				
				String head = predicate + (((i) % n) +1) + "_0" + this.getTermsString("X");
				for(int j=1; j < this.getNbrAtoms(); j++) {
					head += ", ";
					head += predicate + (((i) % n) +1) + "_" + j + this.getTermsString("X");
				}
				
				String ruleString = label + head + " :- " + body + ".";
				Rule rule = DlgpDEFTParser.parseRule(ruleString);
				kb.addRule(rule);
			}
		
			// Add query
			Query query = DlgpDEFTParser.parseQuery("? :- " + P + n + "_0" + this.getTermsString("a") + ".");
			kb.addQuery("Atom n?", query);
			return kb;
		}
		
	}
	
	class CircleKBStructureGeneratorIterable extends AbstractKBStructureGeneratorIterable {
		private int[] sizes;
		private int nbrAtoms;
		private int nbrTerms;
		
		CircleKBStructureGeneratorIterable(int[] sizes, int nbrAtoms, int nbrTerms) {
			this.sizes = sizes;
			this.nbrTerms = nbrTerms;
			this.nbrAtoms = nbrAtoms;
		}

		@Override
		public int[] getSizes() {
			return this.sizes;
		}

		@Override
		public Iterator<KBStructure> iterator() {
			return new CircleKBStructureGenerator(sizes, nbrAtoms, nbrTerms);
		}
		
		
	}

	@Override
	protected Iterable<? extends Object> getKBGeneratorIterable() {
		return this.KBGeneratorIterable;
	}

	public String getName() {
		return NAME;
	}
}
