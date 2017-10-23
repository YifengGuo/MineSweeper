import java.util.*;

public class MineSweeper_logic {

    private static Queue<Entry> hasMine = new LinkedList<Entry>();
    private static Queue<Entry> noMine = new LinkedList<Entry>();
    public static void main(String[] args) {

        // Generate Board
        int width = 5;
        int len = 5;
        Board_logic board = new Board_logic(width, len);
        board.print();

        KnowledgeBase kb = new KnowledgeBase();
        // Start Game
        while (!board.fatal) {
            if (board.isSolved()) {
                System.out.println("Success!");
                break;
            }
            boolean[][] t = board.getCleared();

            // choose a node to reveal
            Entry e = getNextStep(board);
            // update information in board
            int value = board.revealNode(e.x, e.y);
            board.printG();
            if (value == -1) {
                System.out.println("Fail! " + e.toString() + "is a mine!");
                return;
            }
            List<Entry> le = board.getAdjNodes(e.x, e.y);
            // get CNF of new clue
            List<KnowledgeBase.Clause> lc = getCNF(le, value);
            for (KnowledgeBase.Clause c: lc) {
//                System.out.println(c.toString());
                kb.addClause(c);
            }
//            for (int i = 0; i < width; i++) {
//                for (int j = 0 ; j< len; j++) {
//                    System.out.print(t[i][j] + " ");
//                }
//                System.out.println();
//            }
//            System.out.println("UPD KB");
            // Update knowledge base
            kb.update(board, hasMine, noMine);
//            System.out.println("UPD BD");
            // Update board
            board.update(hasMine);
//            System.out.println("LOG INF");
//            kb.print();
            // logic inference for the uncleared nodes
            List<Entry> lue = board.getAdjUncNodes(e.x, e.y);
            for (Entry query : lue) {
                if (!kb.isEmpty())
                    logicInfer(kb, query);

            }
            kb.update(board, hasMine, noMine);
//            kb.print();
        }


/*
        Entry e = new Entry(1,1);
        Entry e1 = new Entry(1,2);
        Entry e2 = new Entry(1,3);
        Entry e3 = new Entry(1,4);
        List<Entry> le = new ArrayList<Entry>();
        le.add(e);
        le.add(e2);
        le.add(e1);
        le.add(e3);
        List<KnowledgeBase.Clause> lc = getCNF(le, 1);
        KnowledgeBase kb = new KnowledgeBase();
        for (KnowledgeBase.Clause c : lc) {
            System.out.println(c.toString());
            kb.addClause(c);
        }
        System.out.println(!kb.infer(new Symbol(e2, false)));
*/
    }

    public static Entry getNextStep(Board_logic board) {
        if (!noMine.isEmpty()){
            List<Entry> le = new ArrayList<Entry>(noMine);
            for (Entry et : le) System.out.print(et.toString());
            System.out.println();
            Entry e = noMine.poll();
            System.out.println("I take :" + e.toString());
            return e;
        }
        System.out.print("F**k! I guess: ");
        Entry eg = board.randomUnclearedEntry();
        System.out.println(eg.toString());
        return eg;
    }

    public static void logicInfer(KnowledgeBase kb, Entry query) {
        System.out.println("Infer kb: " + kb.toSting());
        // if kb contains a positive unit clause, it's a mine
        List<Entry> l = kb.getPositiveUnitClause();
        for (Entry e:l) {
            if(!hasMine.contains(e)) {
                hasMine.add(e);
                System.out.println("unit pos: " + e.toString());
            }
        }
        // if kb contains a negative unit clause, it's a mine
        l = kb.getNegativeUnitClause();
        for (Entry e:l) {
            if(!noMine.contains(e)) {
                noMine.add(e);
                System.out.println("unit neg: " + e.toString());
            }
        }
        // if (kb & !query) is unsatisfiable for any clause in kb, mark query is a mine
        Boolean inferred = kb.infer(new Symbol(query, false));
        if (!inferred && !hasMine.contains(query)) {
            System.out.println("inferred as a mine: " + query.toString());
            hasMine.add(query);
        }
        // if (kb & query) is unsatisfiable for any clause in kn, mark query isn't a mine
        inferred = kb.infer(new Symbol(query, true));
        if (!inferred && !noMine.contains(query)) {
            System.out.println("inferred as no mine: " + query.toString());
            noMine.add(query);
        }
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
