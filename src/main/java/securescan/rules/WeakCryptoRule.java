    package securescan.rules;

    import com.github.javaparser.ast.CompilationUnit;
    import com.github.javaparser.ast.expr.MethodCallExpr;
    import com.github.javaparser.ast.expr.StringLiteralExpr;
    import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
    import securescan.models.Finding;

    import java.util.ArrayList;
    import java.util.List;

    public class WeakCryptoRule implements Rule {

        private static final List<String> WEAK_ALGORITHMS = List.of("MD5", "SHA1", "SHA-1", "DES");

        @Override
        public List<Finding> analyze(CompilationUnit cu, String filename) {
            List<Finding> findings = new ArrayList<>();

            cu.accept(new VoidVisitorAdapter<Void>() {
                @Override
                public void visit(MethodCallExpr n, Void arg) {
                    if (n.getNameAsString().equals("getInstance") && n.getArguments().size()>0 && n.getArgument(0) instanceof StringLiteralExpr) {
                        StringLiteralExpr argument = (StringLiteralExpr) n.getArgument(0);
                        String algo = argument.getValue();
                        if (WEAK_ALGORITHMS.contains(algo)) {
                            Finding finding = new Finding(
                                    "WeakCryptoRule",
                                    filename,
                                    n.getBegin().get().line,
                                    "Weak cryptography algorithm: " + algo,
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