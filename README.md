## Synopsis

Defeasible benchmark that can be used to classify defeasible reasoning tools based on their semantics (ambiguity propagating vs ambiguity blocking), expressiveness (existential rules, negation, cyclic rules, priority relation) and performance.

## How to Run?

It is recommended to run the benchmark using command line, it is also possible to run it directly. All details are in [anon.conf.defeasible_benchmark.defeasible.BenchLaucher.java](https://github.com/anonconf/Benchmark/blob/master/src/main/java/anon/conf/defeasible_benchmark/defeasible/BenchLauncher.java). 

## Test Theories

* **Chain Theory:** Test performance when the GRD is acyclic.
* **Existential Test Theory:** Test if the system understands existential rules, and test performance in presence of large number of constants.
* **Chain FES Theory:** Test performance when faced with FES rules.
* **Chain FUS Theory:** Test performance with FUS rules.
* **Chain GBTS Theory:** Test performance with GBTS rules.
* **AmbiguityTest Theory:** Test if the system has an ambiguity blocking or an ambiguity propagating behavior, and test performance when faced with conflicting arguments with long derivations.
* **Circle Theory:** Test performance when the GRD is cyclic.
* **Negation Chain Theory:** Test performance in the absence of conflict but with rules containing negated atoms in their body.
* **Levels Theory:** Test performance when faced with large number of arguments with small derivations.
* **Prioritized Levels Theory:** Test performance w.r.t. a priority relation.
* **Teams Theory:** Test performance w.r.t. a sizable number of conflicts.
* **Tree Theory:** Test performance w.r.t. a large number of linked arguments.
* **Directed Acyclic Graph Theory:** Test performance when faced with many arguments for the same atom.

## How To Test Other Tools?

Implement the interface [AbstractRuleBasedReasoningTool](https://github.com/anonconf/Benchmark/blob/master/src/main/java/anon/conf/defeasible_benchmark/defeasible/AbstractRuleBasedReasoningTool.java). Example: [DeLPTool](https://github.com/anonconf/Benchmark/blob/master/src/main/java/anon/conf/defeasible_benchmark/defeasible/tools/DeLPTool.java).


## Contributors

This benchmark is currently under review, the list of contributors (and who to contact in case of problem) will be disclosed afterwards.

## License

his project is licensed under the MIT License.