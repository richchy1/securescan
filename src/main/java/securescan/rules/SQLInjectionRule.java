package securescan.rules;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import securescan.models.Finding;

import java.util.ArrayList;
import java.util.List;

public class SQLInjectionRule implements Rule {

    private static final List<String> SQL_METHODS =
            List.of("executeQuery", "executeUpdate", "execute");

    @Override
    public List<Finding> analyze(CompilationUnit cu, String filename) {
        List<Finding> findings = new ArrayList<>();

        cu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodCallExpr n, Void arg) {

                // Check method name and make sure it has an argument
                if (SQL_METHODS.contains(n.getNameAsString())
                        && n.getArguments().size() > 0
                        && n.getArgument(0) instanceof BinaryExpr) {

                    // Get the first argument as a BinaryExpr
                    BinaryExpr binary =
                            (BinaryExpr) n.getArgument(0);

                    // Check that the binary operation is +
                    if (binary.getOperator() == BinaryExpr.Operator.PLUS) {

                        Expression left = binary.getLeft();
                        Expression right = binary.getRight();

                        // Check if at least one side is not a string literal
                        if (!(left instanceof StringLiteralExpr)
                                || !(right instanceof StringLiteralExpr)) {

                            Finding finding = new Finding(
                                    "SqlInjectionRule",
                                    filename,
                                    n.getBegin().get().line,
                                    "Possible SQL injection: SQL query is built using string concatenation",
                                    "HIGH"
                            );

                            findings.add(finding);
                        }
                    }
                }

                super.visit(n, arg);
            }
        }, null);

        return findings;
    }
}