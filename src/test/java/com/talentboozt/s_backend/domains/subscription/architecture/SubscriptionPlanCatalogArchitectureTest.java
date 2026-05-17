package com.talentboozt.s_backend.domains.subscription.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.talentboozt.s_backend.domains.subscription")
class SubscriptionPlanCatalogArchitectureTest {

    @ArchTest
    static final ArchRule subscriptionNonServiceNonInfrastructureMustNotDependOnEdu =
            noClasses()
                    .that()
                    .resideInAPackage("..subscription..")
                    .and()
                    .resideOutsideOfPackages("..subscription.infrastructure..", "..subscription.service..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("..edu..");
}
