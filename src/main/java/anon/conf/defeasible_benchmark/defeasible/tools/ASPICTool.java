package anon.conf.defeasible_benchmark.defeasible.tools;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.aspic.inference.Engine;
import org.aspic.inference.Reasoner;
import org.aspic.inference.ReasonerException;
import org.aspic.inference.Result;
import org.aspic.inference.Valuator;
import org.aspic.inference.parser.ParseException;

import anon.conf.defeasible_benchmark.core.Approach;
import anon.conf.defeasible_benchmark.defeasible.AbstractRuleBasedReasoningTool;
import anon.conf.defeasible_benchmark.defeasible.KBStructure;
import fr.lirmm.graphik.DEFT.core.DefeasibleKB;
import fr.lirmm.graphik.DEFT.core.DefeasibleRule;
import fr.lirmm.graphik.DEFT.core.Preference;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.Rules;
import fr.lirmm.graphik.util.CPUTimeProfiler;
import fr.lirmm.graphik.util.Profiler;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import net.sf.tweety.commons.ParserException;

public class ASPICTool extends AbstractRuleBasedReasoningTool{
	private final String NAME = "ASPIC";
	
	Profiler profiler;
	
	private LinkedList<Pair<String, String>> queriesString;
	private StringBuilder KBStringBuilder;
	private LinkedList<Pair<String, ? extends Object>> results;
	
	public void initialize() {
		this.profiler = new CPUTimeProfiler();
		this.queriesString = new LinkedList<Pair<String, String>>();
		this.results = new LinkedList<Pair<String, ? extends Object>>();
		this.KBStringBuilder = new StringBuilder();
	}
	
	public ASPICTool() {
		this.initialize();
	}
	
	private void parseQueries(Iterator<Pair<String, Query>> queries) {
		while(queries.hasNext()) {
			this.queriesString.add(parseQuery(queries.next()));
		}
	}
	
	private Pair<String, String> parseQuery(Pair<String, Query> queryEntry) {
		Atom queryAtom = ((ConjunctiveQuery) queryEntry.getValue()).getAtomSet().iterator().next();
		String queryString = parseAtom(queryAtom);
		return new ImmutablePair<String, String>(queryEntry.getKey(), queryString); 
	}
	
	private void parseRules(Iterator<Rule> rules) {
		while(rules.hasNext()) {
			this.KBStringBuilder.append(parseRule(rules.next()));
		}
	}
	
	private String parseRule(Rule rule) {
		return parseRule(rule, "");
	}
	
	private String parseRule(Rule rule, String defeasibleValue) {
		Iterator<Rule> itAtomicRule = Rules.computeAtomicHead(rule).iterator();
			
		String str = "";
		
		if(defeasibleValue.isEmpty() && rule instanceof DefeasibleRule) {
			defeasibleValue = "0.5";
		}
		
		// when there is multiple head!
		while(itAtomicRule.hasNext()) {
			Rule atomicRule = itAtomicRule.next();
			CloseableIteratorWithoutException<Atom> itHead = atomicRule.getHead().iterator();
			while(itHead.hasNext()) {
				str += parseAtom(itHead.next());
			}
			str += " <- ";
			CloseableIteratorWithoutException<Atom> itBody = atomicRule.getBody().iterator();
			if(itBody.hasNext()) str += parseAtom(itBody.next());
			while(itBody.hasNext()) {
				str += ", ";
				str += parseAtom(itBody.next());
			}
			str += " " + defeasibleValue + ".\n";
		}
		return str;
	}
	
	private void parseAtoms(CloseableIterator<Atom> closeableIterator) {
		try {
		while(closeableIterator.hasNext()) {
			String atomString = parseAtom(closeableIterator.next());
			this.KBStringBuilder.append(atomString);
			this.KBStringBuilder.append('.');
			this.KBStringBuilder.append('\n');
		}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private String parseAtom(Atom atom) {
		String str = atom.getPredicate().getIdentifier().toString();
		str += "(";
		Iterator<Term> itTerms = atom.getTerms().iterator();
		str += itTerms.next();
		while(itTerms.hasNext()) {
			str += "," + itTerms.next();
		}
		str += ")";
		return str;
	}
	
	private void parseNegativeConstraints(Iterator<Rule> negativeContraints) {
		while(negativeContraints.hasNext()) {
			this.KBStringBuilder.append(parseNegativeConstraint(negativeContraints.next()));
			this.KBStringBuilder.append('\n');
		}
	}
	
	private String parseNegativeConstraint(Rule nc) {
		String str = "";
		String value = "";
		if(nc.getLabel() != null && !nc.getLabel().isEmpty()) value = " 0.4";
		CloseableIteratorWithoutException<Atom> itBody = nc.getBody().iterator();
		if(itBody.hasNext()) str += "~" + parseAtom(itBody.next()) + " <- ";
		if(itBody.hasNext()) str += parseAtom(itBody.next());
		str += value + ".";
		return str;
	}
	
	
	private void parsePreferences(Iterator<Preference> preferences) {
		while(preferences.hasNext()) {
			parsePreference(preferences.next());
			//this.KBStringBuilder.append('\n');
		}
	}
	
	private void parsePreference(Preference preference) {
		String str = "";
		String value = "0.6";
		String ruleString = parseRule(preference.getLeftSideRule());
		int index = this.KBStringBuilder.indexOf(ruleString);
		String newRuleString = parseRule(preference.getLeftSideRule(), value) + "\n";
		this.KBStringBuilder.replace(index, index + ruleString.length(), newRuleString);
	}
	
	public void run() {
		//System.out.println("KBDEFTBuilder");
		String kbString = this.getKBString();
		//System.out.println(kbString);
		//System.out.println(this.queriesString.iterator().next().getValue());
		try {
			//this.profiler.start(Approach.TOTAL_TIME);
			// I- Preparation phase
			this.profiler.clear();
			this.profiler.start(Approach.LOADING_TIME);
			
			Engine eng = new Engine(kbString);
			eng.setProperty(Engine.Property.SEMANTICS, Reasoner.GROUNDED);
			eng.setProperty(Engine.Property.VALUATION, Valuator.WEAKEST_LINK);
			eng.setProperty(Engine.Property.TRANSPOSITION, Engine.OnOff.OFF);
			eng.setProperty(Engine.Property.RESTRICTED_REBUTTING, Engine.OnOff.OFF);
			
			Pair<String, String> queryStringPair = this.queriesString.iterator().next();
			
			this.profiler.stop(Approach.LOADING_TIME);
			
			// II- Query answering phase
			this.profiler.start(Approach.EXE_TIME);
			String answer = "No";
			// query
			org.aspic.inference.Query query = eng.createQuery(queryStringPair.getValue());
			Iterator<Result> itResult = query.getResults().iterator();
			if (itResult.hasNext()) answer = String.valueOf(itResult.next().isUndefeated());
			this.profiler.stop(Approach.EXE_TIME);
			
			// Add Query and Answer
			this.results.add(queryStringPair);
			this.results.add(new ImmutablePair<String, Object>(Approach.ANSWER, answer));
			
			//this.profiler.stop(Approach.TOTAL_TIME);
			
			// Add times
			Iterator<Entry<String, Object>> itProfiler = this.profiler.entrySet().iterator();
			while(itProfiler.hasNext()) {
				Entry<String, Object> entry = itProfiler.next();
				this.results.add(new ImmutablePair<String, Object>(entry.getKey(), entry.getValue()));
			}
			
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReasonerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void prepare(Iterator<Pair<String, ? extends Object>> params) {
		KBStructure kb = (KBStructure) params.next().getValue();
		
		this.KBStringBuilder = new StringBuilder();
		//System.out.println(kb.toString());
		
		// 1- Parsing Queries
		this.parseQueries(kb.getQueries());
		
		// 2- Parsing Atoms
		this.parseAtoms(kb.getAtoms());
		
		// 3- Parsing Rules 
		this.parseRules(kb.getRules());
		
		// 4- Parsing Negative Constraints
		this.parseNegativeConstraints(kb.getNegativeConstraints());
		
		// 5- Parsing Preferences
		this.parsePreferences(kb.getPreferences());
	}

	public Iterator<Pair<String, ? extends Object>> getResults() {
		return results.iterator();
	}

	@Override
	public String getKBString() {
		return this.KBStringBuilder.toString();
	}

	public String getName() {
		return NAME;
	}

	@Override
	public String getAnswer(Object answer) {
		int ans = (Integer) answer;
		String answerString = "";
		
		switch(ans) {
			case DefeasibleKB.NOT_ENTAILED: answerString = "No"; break;
			case DefeasibleKB.DEFEASIBLY_ENTAILED: answerString = "Yes"; break;
			case DefeasibleKB.STRICTLY_ENTAILED: answerString = "Yes";
		}

		return answerString;
	}

}
