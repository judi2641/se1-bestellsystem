package application;

import java.util.Arrays;
import java.util.Optional;

/**
 * Class with {@code main()} - method that prints command line arguments.
 *
 * @version <code style=color:green>{@value application.package_info#Version}</code>
 * @author <code style=color:blue>{@value application.package_info#Author}</code>
 */
public class Application implements Runner {

    /**
     * Private default constructor prevents instance creation outside this class.
     */
    private Application() { }


    /**
     * Static {@code main()} - method as entry point for the Java VM.
     * @param args arguments passed from the command line
     */
    public static void main(String[] args) {
        // 
        // create singleton {@link Runner} instance
        Runner application = new Application_C12();
        // 
        // print greeting for modular or none-modular project (moduleName: null)
        String className = application.getClass().getSimpleName();
        String moduleName = Application.class.getModule().getName();
        System.out.println(Optional.ofNullable(moduleName)
            .map(m -> String.format("Hello, %s (%s, modular)", moduleName, className))
            .orElse(String.format("Hello, se1-bestellsystem (%s)", className)));
        // 
        // invoke run() at the {@link Runner} instance
        application.run(args);
    }

    /**
     * Run application code on a {@link Runner} instance rather than in the
     * {@code static main();} method.
     */
    @Override
    public void run(String[] args) {
        Arrays.stream(args)
            .map(arg -> String.format(" - arg: %s", arg))
            .forEach(System.out::println);
    }
}