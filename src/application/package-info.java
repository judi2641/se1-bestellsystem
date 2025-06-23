/**
 * {@code package-info.java} has been introduced with <i>Modules</i> in Java 9
 * (2017) to provide package-level documentation and project-wide variables
 * that can be used in <i>Javadoc</i>.
 *
 * @version <code style=color:green>{@value application.package_info#Version}</code>
 * @author <code style=color:blue>{@value application.package_info#Author}</code>
 */
package application;


/**
 * Class {@code package_info} of the {@link application} package provides
 * project-wide variables used in <i>Javadoc</i>.
 */
class package_info {

    /**
     * Author attribute to appear in javadoc.
     */
    static final String Author = "jdittrich";   // <-- adjust with your name

    /**
     * Version attribute to appear in javadoc.
     */
    static final String Version = "D12-1.0.0-SNAPSHOT";
}