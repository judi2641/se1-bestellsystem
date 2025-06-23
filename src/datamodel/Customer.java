package datamodel;

import java.util.*;
import java.util.function.BiConsumer;


/**
 * Entity class representing a {@link Customer} as a person who creates
 * and holds (owns) orders in the system.
 * 
 * @version <code style=color:green>{@value application.package_info#Version}</code>
 * @author <code style=color:blue>{@value application.package_info#Author}</code>
 */
public final class Customer {

    /**
     * Immutable Customer id attribute set once when the object is created.
     */
    private final long id;

    /**
     * Customer's surname attribute set when the object is created. Can be
     * updated by the {@link Validator} {@code updateName()} method.
     */
    private String lastName;

    /**
     * Customer's oone-surname name parts set when the object is created. Can
     * be updated by the {@link Validator} {@code updateName()} method.
     */
    private String firstName;

    /**
     * Customer contact information with multiple entries of email addresses
     * and/or phone numbers. Contacts can be update by {@link Validator}
     * {@code addContact()} and {@code removeContact()} methods.
     */
    protected final List<String> contacts;

    /**
     * None-public accessor to update first- and lastName attributes by the
     * {@link Validator} {@code updateName()} method.
     */
    protected final BiConsumer<String, String> nameUpdater =
        (first, last) -> { firstName=first; lastName=last; };

    /**
     * Non-public constructor used by {@link DataFactory} that also provides
     * the <i>id</i> attribute. {@link Customer} objects cannot be created
     * outside this package.
     */
    protected Customer(long id, String lastName, String firstName, List<String> contacts) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.contacts = contacts==null? new ArrayList<>() : contacts;
    }

    /**
     * Public <i>id</i> getter.
     * @return customer <i>id</i> attribute of {@link Customer} object
     */
    public long id() {
        return id;
    }

    /**
     * Public getter of the <i>lastName</i> attribute that always holds
     * a valid last name (never null, not empty).
     * @return value of lastName attribute, never null, not empty
     */
    public String lastName() {
        return lastName;
    }

    /**
     * Public getter of the <i>firstName</i> attribute that always holds
     * a valid first name (never null, may be empty if unknown).
     * @return value of firstName attribute, never null, may be empty
     */
    public String firstName() {
        return firstName;
    }

    /**
     * Return the number of contacts.
     * @return number of contacts
     */
    public int contactsCount() {
        return contacts.size();
    }

    /**
     * Public <i>contacts</i> getter as immutable {@code Iterable<String>}.
     * @return contacts as immutable {@code Iterable<String>}
     */
    public Iterable<String> contacts() {
        return contacts;
    }
}