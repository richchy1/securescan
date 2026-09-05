package securescan;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import securescan.models.Finding;
import securescan.rules.*;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.out.println("Usage: <Java-file-or-directory>");
            return;
        }

        String path = args[0];
        File input = new File(path);

        List<Rule> rules = List.of(
                new WeakCryptoRule(),
                new HardcodedSecretRule(),
                new SQLInjectionRule()
        );

        List<Finding> allFindings = new ArrayList<>();

        if (input.isFile()) {

            CompilationUnit cu = StaticJavaParser.parse(input);

            for (Rule rule : rules) {
                allFindings.addAll(rule.analyze(cu, input.getPath()));
            }

        } else if (input.isDirectory()) {

            Files.walk(input.toPath())
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(p -> {
                        try {
                            CompilationUnit cu = StaticJavaParser.parse(p);

                            for (Rule rule : rules) {
                                allFindings.addAll(
                                        rule.analyze(cu, p.toString())
                                );
                            }

                        } catch (Exception e) {
                            System.out.println(
                                    "Could not parse: " + p
                            );
                        }
                    });

        } else {
            System.out.println("File or directory not found: " + path);
            return;
        }

        System.out.println("\n== SecureScan Results ==");

        if (allFindings.isEmpty()) {
            System.out.println("No security issues found.");
        } else {
            for (Finding finding : allFindings) {
                System.out.println(
                        finding.getFileName()
                                + ":"
                                + finding.getLine()
                                + " [" + finding.getSeverity() + "] "
                                + finding.getMessage()
                );
            }
        }
    }
}