package datamodel;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;


/**
 * Class with <i>factory</i> methods to create objects of type {@code T}.
 * 
 * Factory centralizes object creation (e.g. {@code T:} {@link Customer}) from
 * validated parameters (e.g. valid <i>names</i> and <i>contacts</i>) at a
 * central location.
 * 
 * Objects are created only from valid parameters only provided with
 * {@code create} methods. Object {@code id} are assigned by the factory to
 * avoid duplication.
 * 
 * @version <code style=color:green>{@value application.package_info#Version}</code>
 * @author <code style=color:blue>{@value application.package_info#Author}</code>
 */
public final class DataFactory {

    /*
     * {@link Validator} instance used by {@link DataFactory}.
     */
    private final Validator validator = new Validator();

    /**
     * Inner class of a pool of unique <i>id</i> of type {@code ID}
     * assigned to objects created by the factory.
     */
    private class IdPool<ID> {
        private final List<ID> ids;
        private final BiConsumer<List<ID>, Integer> idExpander;
        private int current = 0;

        /**
         * Constructor.
         * @param initial initial set of <i>id</i> stored in pool
         * @param expander call-out to expand <i>id</i> pool by given size
         */
        IdPool(List<ID> initial, BiConsumer<List<ID>, Integer> expander) {
            this.ids = new ArrayList<>(initial==null? List.of() : initial);
            this.idExpander = expander;
        }

        /**
         * Retrieve next <i>id</i> from pool. Expand, if exhausted.
         * @return next <i>id</i> from pool
         */
        ID next() {
            if(current >= ids.size()) {
                idExpander.accept(ids, 25);
            }
            return ids.get(current++);
        }
    }

    /*
     * {@link IdPool} for class {@link Customer} with set of initial <i>id</i>
     * of type {@code Long} that is expanded if exhausted.
     */
    private final IdPool<Long> customerIdPool = new IdPool<>(
        List.of( // initial pool <i>id</i>
            892474L, 643270L, 286516L, 412396L, 456454L, 651286L
        ),
        (idPool, expandBy) -> // <i>id</i> pool expander:
            Stream.generate(() -> (long)((Math.random() * (999999 - 100000)) + 100000))
                .filter(id -> ! idPool.contains(id))
                .limit(expandBy)
                .forEach(idPool::add));

    
    private static final DataFactory factory = new DataFactory();

    /*
     * Private constructor is part of the singleton pattern.
     */
    private DataFactory() { }

    public static DataFactory getInstance(){
        return factory;
    }

    public Runnable create(String name) {
        Runnable newInstance = new Runnable() {
            @Override
            public void run() {
                System.out.println(
                    String.format("new Runnable instance, \"%s\"", name));
            }
        };
        return newInstance;
    }
    /**
     * <i>Factory</i> method to create {@link Customer} object from arguments
     * as mix of name parts and contacts. No object is created from invalid
     * name parts or contacts.
     * <p>
     * Examples:
     * <pre>
     * - createCustomer("Eric", "Meyer", "eric98@yahoo.com", "eric98@yahoo.com", "(030) 3945-642298")
     * - createCustomer("Bayer, Anne", "anne24@yahoo.de", "(030) 3481-23352", "fax: (030)23451356")
     * - createCustomer(" Tim ", " Schulz-Mueller ", "tim2346@gmx.de")
     * - createCustomer("Nadine-Ulla Blumenfeld", "+49 152-92454")
     * - createCustomer("Khaled Saad Mohamed Abdelalim", "+49 1524-12948210")
     * </pre>
     * @param args name parts and contacts to create a {@link Customer} object
     * @return {@link Customer} object with valid name and contacts or empty
     */
    public Optional<Customer> createCustomer(String... args) {
        /*
         * separate contacts from names in args
         */
        final StringBuilder flatName = new StringBuilder();
        final List<String> contacts = Arrays.stream(args)
            .map(arg -> {
                var contact = validator.validateContact(arg);
                if(contact.isEmpty()) {
                    flatName.append(arg).append(" ");
                }
                return contact;
            })
            .flatMap(Optional::stream)
            .toList();
        // 
        return validator.validateName(flatName.toString())
            .map(np -> new Customer(customerIdPool.next(), np.lastName(), np.firstName(), contacts));
    }
}