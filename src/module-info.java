/**
 * Modules have been introduced in Java 9 (2017) to compose multiple Java
 * projects. Prior, only packages within one project allowed composability.
 * 
 * <p> {@code module-info.java} describes a <i>modular</i> Java project with
 * the module name: {@link se1.bestellsystem}, other modules required by this
 * module, opened packages and packages exported to other modules.
 * 
 * <p> Opening a package makes it accessible to tools such as compilers or
 * test runners. Exporting a package makes it accessible to other modules.
 *
 * <p>Locations of <i>required</i> modules are provided via {@code MODULEPATH}.
 *
 * @version <code style=color:green>{@value application.package_info#Version}</code>
 * @author <code style=color:blue>{@value application.package_info#Author}</code>
 */
module se1.bestellsystem {

    /*
     * Make this package {@code application} accessible to other modules at
     * compile and runtime (use <i>opens</i> for compile-time access only).
     */
    exports application;
    exports datamodel;

    /* Open package to JUnit test runner. */
    opens application;
    opens datamodel;

    /*
     * Module required by this module (JUnit-5 module for JUnit testing).
     */
    requires org.junit.jupiter.api;
}