package br.com.unipds;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.jmolecules.archunit.JMoleculesArchitectureRules;

@AnalyzeClasses(packages = "br.com.unipds.cotuba")
public class ArchitectureTest {

    @ArchTest
    static final ArchRule haxagonal = JMoleculesArchitectureRules.ensureHexagonal();

}
