import java.util.*;

public class MineSweeper_logic {

    private static Queue<Entry> hasMine = new LinkedList<Entry>();
    private static Queue<Entry> noMine = new LinkedList<Entry>();
    public static void main(String[] args) {

        // Generate Board
        int width = 10;
        int len = 10;
        Board_logic board = new Board_logic(width, len);
        System.out.println("The Game Board: ");
        board.print();
        System.out.println("MinesNum = " + board.getMinesNum());

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
                kb.addClause(c);
            }
            // Update knowledge base
            kb.update(board, hasMine, noMine);
            // Update board
            board.update(hasMine);
            // logic inference for the uncleared nodes
            List<Entry> lue = board.getAdjUncNodes(e.x, e.y);
            for (Entry query : lue) {
                if (!kb.isEmpty())
                    logicInfer(kb, query);

            }
            kb.update(board, hasMine, noMine);
        }


    }

    public static Entry getNextStep(Board_logic board) {
        System.out.println("<================================>");
        if (!noMine.isEmpty()){
            List<Entry> le = new ArrayList<Entry>(noMine);

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
        // if kb contains a positive unit clause, it's a mine
        List<Entry> l = kb.getPositiveUnitClause();
        for (Entry e:l) {
            if(!hasMine.contains(e)) {
                hasMine.add(e);
            }
        }
        // if kb contains a negative unit clause, it's a mine
        l = kb.getNegativeUnitClause();
        for (Entry e:l) {
            if(!noMine.contains(e)) {
                noMine.add(e);
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
        // if k == n, return all positive unit clause
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
