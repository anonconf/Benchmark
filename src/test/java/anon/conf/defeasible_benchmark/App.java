package anon.conf.defeasible_benchmark;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import fr.lirmm.graphik.DEFT.core.DefeasibleKB;
import fr.lirmm.graphik.DEFT.io.DlgpDEFTParser;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplicationException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.core.Rules;
import fr.lirmm.graphik.util.CPUTimeProfiler;
import fr.lirmm.graphik.util.Profiler;
import net.sf.tweety.arg.delp.DefeasibleLogicProgram;
import net.sf.tweety.arg.delp.DelpReasoner;
import net.sf.tweety.arg.delp.parser.DelpParser;
import net.sf.tweety.arg.delp.semantics.GeneralizedSpecificity;
import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.Negation;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws ParserException, IOException, AtomSetException, HomomorphismException, HomomorphismFactoryException, RuleApplicationException, ChaseException
    {
        System.out.println("Hello World!");
        
		// DEFT
        /*String str = "p(a)." +
        		"q(a)." + 
        		"[DEFTr1] r(X) :- p(X)." +
        		"[DEFTr2] s(X) :- q(X)." +
        		"! :- r(X), s(X).";*/
        /*
        String str = "s(a,c)." +
        		"s(c,b)." +
        		"[DEFTr1] r(X) :- p(X)." +
        		"[DEFTr2] s(X,Y) :- s(X,Z), s(Z,Y).";
        */
        /*str = "s(a,c)." +
        		"s(c,b)." +
        		"[DEFTr2] s(X,Z), s(Z,Y) :- s(X,Y)."; //*/
        
        /*str = "s(a,c)." +
		"s(c,b)." +
		"[DEFTr2] q(X,Y,Z) :- s(X,Y)." +
		"[DEFTr2] s(X,Z) :- q(X,Y,Z)." +
		"[DEFTr2] s(X,Y) :- q(X,Y,Z)."; //*/
        /*
        Rule rule = DlgpDEFTParser.parseRule("[DEFTr2] p0(X,Z), p1(X,Y), p1(Y,Z) :- p0(X,Y), p0(Y,Z).");
        
        Iterator<Rule> itRules = Rules.computeAtomicHead(rule).iterator();
        
        while(itRules.hasNext()) {
        	System.out.println(itRules.next());
        }
        */
        /*String str = "p(a,b)." +
        		//"p(b,c)."; +
        		"[DEFTr1] p(X,Y) :- p(X,Y)."; //stackoverflowError.*/
        
        /*String str = "cat(tom). mouse(jerry). dog(spike)." +
        		"[DEFT] inDanger(Y) :- mouse(Y), cat(X)." +
        		"[DEFT] notInDanger(Y) :- dog(X), mouse(Y)." +
        		"[DEFT] dead(X) :- inDanger(X)." +
        		"! :- inDanger(X), notInDanger(X).";
        */
        String str = "evidenceFor(a,raouf). evidenceAgainst(b,raouf). suspect(raouf)." +
        		"[DEFT] notGuilty(X) :- suspect(X)." +
        		"[DEFT] responsible(Y) :- evidenceFor(X,Y)." +
        		"[DEFT] notResponsible(Y) :- evidenceAgainst(X,Y)." +
        		"[DEFT] guilty(X) :- responsible(X)." +
        		"! :- responsible(X), notResponsible(X)." +
        		"! :- guilty(X), notGuilty(X).";
        
        long startTime = System.currentTimeMillis();
        
        Profiler prof = new CPUTimeProfiler();
        //prof.start("DEFT");
		DefeasibleKB kb = new DefeasibleKB(new StringReader(str));
		//kb.addPreference("[DEFTr2] > [DEFTr1].");
		System.out.println("Je vais raisonner~~~~");
		kb.saturate();
		
		//Atom atom = kb.getAtomsSatisfiyingAtomicQuery("? :- s(a,b).").iterator().next();
		
		//Atom atom = kb.getAtomsSatisfiyingAtomicQuery("? :- t(a).").iterator().next();
		
		System.out.println("looking for atom.");
		
		Atom atom = kb.getAtomsSatisfiyingAtomicQuery("? :- notGuilty(raouf).").iterator().next();
		
		System.out.println("found atom.");
		
		int entailment = kb.EntailmentStatus(atom);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("Answer is: " + readableAnswer(entailment));
		System.out.println("That took " + (endTime - startTime) + " milliseconds");
		
		
		System.out.println("==========================DeLP======================================");
		// DeLP
		long startTime1 = System.currentTimeMillis();
        DelpParser parser = new DelpParser();
        /*DefeasibleLogicProgram delp = parser.parseBeliefBase("p(a)." +
															"q(a)." +
															"r(X) -< p(X)." + 
															"s(X) -< q(X)." +
															"~s(X) -< r(X)."); */
        /*
        String str2 = "s(a,c)." +
        		"s(c,b)." +
        		"q(X,Y,Z) -< s(X,Y)." +
        		"s(X,Z) -< q(X,Y,Z)." +
        		"s(X,Y) -< q(X,Y,Z)."; //*/
        /*
        String str2 = "p(b). j(a)." +
        		"q(X,Y) -< p(X)." +
        		"p(Y) -< q(X,Y)." +
        		"t(X) -< q(X,Y)." +
        		"~t(X) -< p(X)."; //*/
        /*
        String str2 =
        		"p(X) -< p(X)." +
				"p(a)."; //*/
        
        //String str2 = "p -< true. ~p -< true. q -< true. ~q -< p.";
        //String str2 = "p -< true. ~p -< true. q -< p.";
        
        String str2 = "evidenceFor(a, raouf). evidenceAgainst(b,raouf). suspect(raouf)." +
                "~guilty(X) -< suspect(X)." +
                "responsible(Y) -< evidenceFor(X,Y)." +
                "~responsible(Y) -< evidenceAgainst(X,Y)." +
                "guilty(X) -< responsible(X).";
        
        DefeasibleLogicProgram delp = parser.parseBeliefBase(str2);
        
        GeneralizedSpecificity comp = new GeneralizedSpecificity();
        
        DelpReasoner reasoner = new DelpReasoner(delp,comp);
        
        FolParser folParser = new FolParser();
		folParser.setSignature(parser.getSignature());
		
		// query
		String qString = "guilty(raouf)";
		//String qString = "p(a)";
		
		Formula f;
		if(qString.startsWith("~"))
			f = new Negation((FolFormula)folParser.parseFormula(qString.substring(1)));
		else f = folParser.parseFormula(qString);
		
		Answer ans = reasoner.query(f);
		long endTime1 = System.currentTimeMillis();
		
		System.out.println(ans.getText());
		System.out.println("That took " + (endTime1 - startTime1) + " milliseconds");
		

		System.out.println("==========================ASPIC+====================================");
		
    }
    
    
    public static String readableAnswer(int entailment) {
    	switch(entailment) {
	        case DefeasibleKB.NOT_ENTAILED: return "NOT entailed!";
	        case DefeasibleKB.STRICTLY_ENTAILED: return "Strictly entailed!";
	        case DefeasibleKB.DEFEASIBLY_ENTAILED: return "Defeasibly entailed!";
    	}
    	return "don't know";
    }
}
