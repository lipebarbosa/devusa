package com.devusa.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.devusa", importOptions = ImportOption.DoNotIncludeTests.class)
public class ModularArchitectureTest {

    @ArchTest
    static final ArchRule orderShouldNotDependOnPaymentDirectly =
            noClasses().that().resideInAPackage("..order..")
                    .should().dependOnClassesThat().resideInAPackage("..payment..");

    @ArchTest
    static final ArchRule cartShouldNotDependOnPayment =
            noClasses().that().resideInAPackage("..cart..")
                    .should().dependOnClassesThat().resideInAPackage("..payment..");
}

