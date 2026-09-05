package securescan;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class PrintCallsVisitor extends VoidVisitorAdapter<Void> {
    @Override
    public void visit(MethodCallExpr n, Void arg) {
        System.out.println("Method call: "+ n.getNameAsString() + " at line " + n.getBegin().get().line);

        super.visit(n,arg);
    }
}