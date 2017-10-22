import java.util.*;

public class MineSweeper_logic {

    private static Queue<Entry> hasMine = new LinkedList<Entry>();
    private static Queue<Entry> noMine = new LinkedList<Entry>();
    public static void main(String[] args) {
        Entry e = new Entry(1,1);
        Entry e1 = new Entry(1,2);
        Entry e2 = new Entry(1,3);
        List<Entry> le = new ArrayList<Entry>();
        le.add(e);
        le.add(e2);
        le.add(e1);
        List<KnowledgeBase.Clause> lc = getCNF(le, 2);
        KnowledgeBase kb = new KnowledgeBase(lc);

        System.out.println(kb.infer(new Symbol(e, false)));

    }

    public static void logicInfer(KnowledgeBase kb, Entry query) {
        // if kb contains a positive unit clause, it's a mine
        List<Entry> l = kb.getPositiveUnitClause();
        for (Entry e:l) {
            if(!hasMine.contains(e)) hasMine.add(e);
        }
        // if kb contains a negative unit clause, it's a mine
        l = kb.getNegativeUnitClause();
        for (Entry e:l) {
            if(!noMine.contains(e)) noMine.add(e);
        }
        // if (kb & !query) is unsatisfiable for any clause in kb, mark query is a mine
        Boolean inferred = kb.infer(new Symbol(query, false));
        if (inferred && !hasMine.contains(query)) hasMine.add(query);
        // if (kb & query) is unsatisfiable for any clause in kn, mark query isn't a mine
        inferred = kb.infer(new Symbol(query, true));
        if (inferred && !noMine.contains(query)) noMine.add(query);
    }

    public static List<KnowledgeBase.Clause> getCNF(List<Entry> le, int minesNum) {
        // KN(k, n) = [U(k, n)] * [L(k, n)]
        // U(k,n): at most  k of the n square contains a mine
        // L(k,n): at least k of the n square contains a mine
        int squareNum = le.size();
        List<KnowledgeBase.Clause> result = new ArrayList<KnowledgeBase.Clause>();
        if (minesNum < 0 || minesNum > squareNum) {
            System.out.println("Fault: " + minesNum + " mines in " + squareNum + " squares.");
            return null;
        }
        if (squareNum == minesNum) {
            for (Entry e: le) {
                List<Symbol> ls = new ArrayList<Symbol>();
                ls.add(new Symbol(e, true));
                result.add(new KnowledgeBase.Clause(ls));
            }
            return result;
        }
        // U(k,n) <==> for any (k+1) squares outs of n, at least one is not mine
        // all combination of (k+1) elements in entry
        List<KnowledgeBase.Clause> u = getU(le, minesNum+1);
        result.addAll(u);

        // L(k,n) <==> for any n-k+1 squares out of n, at least one is mine
        // from n-k+1 to n, return all combination
        List<KnowledgeBase.Clause> l = getL(le, minesNum);
        result.addAll(l);

        return result;
    }

    private static List<KnowledgeBase.Clause> getL(List<Entry> le, int cNum) {
        List<KnowledgeBase.Clause> result = new ArrayList<KnowledgeBase.Clause>();
        for (int i = le.size()-cNum+1; i <= le.size(); i++) {
            List<List<Entry>> se = getCombination(le, i);
            // convert to clauses
            for (List<Entry> l : se) {
                List<Symbol> ls = new ArrayList<Symbol>();
                for (Entry e : l) {
                    Symbol s = new Symbol(e, true);
                    ls.add(s);
                }
                KnowledgeBase.Clause c = new KnowledgeBase.Clause(ls);
                result.add(c);
            }
        }
        return result;
    }

    private static List<KnowledgeBase.Clause> getU(List<Entry> le, int cNum) {
        List<List<Entry>> se = getCombination(le, cNum);
        List<KnowledgeBase.Clause> result = new ArrayList<KnowledgeBase.Clause>();

        // convert to clauses
        for (List<Entry> l : se) {
            List<Symbol> ls = new ArrayList<Symbol>();
            for (Entry e: l) {
                Symbol s = new Symbol(e, false);
                ls.add(s);
            }
            KnowledgeBase.Clause c = new KnowledgeBase.Clause(ls);
            result.add(c);
        }
        return result;
    }

    private static List<List<Entry>> getCombination(List<Entry> values, int size) {
        if (0 == size)
            return Collections.singletonList(Collections.<Entry> emptyList());
        if (values.isEmpty()) {
            return Collections.emptyList();
        }

        List<List<Entry>> combination = new ArrayList<List<Entry>>();
        Entry actual = values.iterator().next();

        List<Entry> subSet = new ArrayList<Entry>(values);
        subSet.remove(actual);

        List<List<Entry>> subSetCombination = getCombination(subSet, size - 1);

        for (List<Entry> set: subSetCombination) {
            List<Entry> newSet = new ArrayList<>(set);
            newSet.add(0, actual);
            combination.add(newSet);
        }
        combination.addAll(getCombination(subSet, size));
        return combination;
    }
}
