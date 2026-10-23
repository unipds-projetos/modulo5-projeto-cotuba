package br.com.unipds.cotuba;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.jmolecules.archunit.JMoleculesArchitectureRules;
import org.jmolecules.archunit.JMoleculesDddRules;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "br.com.unipds.cotuba")
public class ArchitectureTest {
    @ArchTest //verifica as anotações entre pacotes e classes
    static final ArchRule hexagonal = JMoleculesArchitectureRules.ensureHexagonal();

    @ArchTest
    static final ArchRule all = JMoleculesDddRules.all();

    @ArchTest
    static final ArchRule noCycles = slices().matching("br.com.unipds.cotuba.(*)..").should().beFreeOfCycles();

}
