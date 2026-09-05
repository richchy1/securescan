    package securescan.rules;
    import com.github.javaparser.ast.CompilationUnit;
    import securescan.models.Finding;
    import java.util.List;

    public interface Rule {
        List<Finding> analyze(CompilationUnit cu, String fileName);
    }