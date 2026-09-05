package securescan.rules;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import securescan.models.Finding;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HardcodedSecretRule implements Rule {

    private static final List<String> SUSPICIOUS_NAMES =
            List.of("password", "secret", "key", "token", "credential","pass");

    @Override
    public List<Finding> analyze(CompilationUnit cu, String filename) {
        List<Finding> findings = new ArrayList<>();

        cu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(VariableDeclarator n, Void arg) {

                String name = n.getNameAsString().toLowerCase();

                boolean suspicious = SUSPICIOUS_NAMES.stream()
                        .anyMatch(name::contains);

                Optional<Expression> initializer = n.getInitializer();

                if (suspicious
                        && initializer.isPresent()
                        && initializer.get() instanceof StringLiteralExpr) {
                    String value = ((StringLiteralExpr) initializer.get()).getValue();

                    if (value.length() > 3) {
                        Finding finding = new Finding(
                                "HardcodedSecretRule",
                                filename,
                                n.getBegin().get().line,
                                "Possible hardcoded secret in variable: " + n.getNameAsString(),
                                "HIGH"
                        );
                        findings.add(finding);
                    }
                }

                super.visit(n, arg);
            }
        }, null);

        return findings;
    }
}