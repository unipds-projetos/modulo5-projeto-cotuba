package br.com.unipds;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.jmolecules.archunit.JMoleculesArchitectureRules;
import org.jmolecules.archunit.JMoleculesDddRules;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "br.com.unipds.cotuba")
public class ArchitectureTest {

    @ArchTest
    static final ArchRule haxagonal = JMoleculesArchitectureRules.ensureHexagonal();

    @ArchTest
    static final ArchRule ddd = JMoleculesDddRules.all();

    @ArchTest
    static final ArchRule noCycles = slices().matching("br.com.unipds.cotuba.(*)..").should().beFreeOfCycles();

}
