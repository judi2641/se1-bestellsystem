package datamodel;

import java.util.ArrayList;

/**
 * Entity class representing a Customer as a person who creates and holds (owns) orders in the system.
 */
public class Customer {
    private Long id;
    private String firstName;
    private String lastName;
    private final ArrayList<String> contacts;

    
    /**
     * Default constructor
     */
    public Customer() {
        this.id = null;
        this.firstName = "";
        this.lastName = "";
        this.contacts = new ArrayList<>();
    }

    /**
     * Constructor with single-String name argument, for example "Eric Meyer" (see method splitName(java.lang.String) for details)
     */
    public Customer(String name) {
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("name empty");
        }
        this.id = null;
        splitName(name);
        this.contacts = new ArrayList<>();
    }

    /**
     * Id getter, returns -1, if id is still unassigned.
     */
    public Long getId() {
        if(id == null){
            return null;
        }
        else{
            return this.id;
        }
    }

    /**
     * Id setter, id can only be set once with a valid id value: id > 0, id is immutable after first assignment, return chainable self-reference
     */
    public Customer setId(Long id) {
        if(id != null && id < 0){
            throw new IllegalArgumentException("invalid id (negative)");
        }
        if(this.id == null && id != null && id > 0){
            this.id = id;
        }
        return this;
    }
    /**
     * LastName getter, return value of lastName attribute, never null.
     */
    public String getLastName() {
        return this.lastName;
    }
    /**
     * FirstName getter, return value of firstName attribute, never null. 
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Setter for first- ("Eric") and lastName ("Meyer") attributes, return chainable self-reference.
     */
    public Customer setName(String first, String last) {
        if(last == null || last.isBlank()){
            throw new IllegalArgumentException("last name empty");
        }
        this.firstName = first;
        this.lastName = last;
        return this;

    }
    /**
     * Setter that splits a single-String name ("Eric Meyer") into first- and lastName parts and assigns parts to the corresponding attributes (see method splitName(java.lang.String) for details).
     */
    public Customer setName(String name) {
        splitName(name);
        return this;
    }

    /**
     * Return the number of contacts.
     */
    public int contactsCount() {
        return contacts.size();
    }

    /**
     * Contacts getter as immutable Iterable<String>.
     */
    public Iterable<String> getContacts() {
        return java.util.List.of(this.contacts.toArray(String[]::new));
    }
    /**
     * Add new Customer contact, only valid contacts (not null, not empty "", at least 6 characters and no duplicate contacts) are added.
     */
    public Customer addContact(String contact) {
        if(contact == null || contact.isBlank()){
            throw new IllegalArgumentException("contact argument is null or empty");
        }
        if(trim(contact).length() < 6){
            throw new IllegalArgumentException("contact less than 6 characters: \"" + contact + "\".");
        }
        if(this.contacts.contains(contact)){
            return this;
        }
        this.contacts.add(trim(contact));
        return this;
    }

    /**
     * Delete the i-th contact with i >= 0 and i smaller contactsCount(), otherwise method has no effect.
     */
    public void deleteContact(int i) {
        if(contactsCount() > i && i >= 0){
            this.contacts.remove(i);
        }
    }

    /**
     * Delete all contacts.
     */
    public void deleteAllContacts() {
        contacts.clear();;
    }

    /**
     * Split single-String name into last- and first name parts according to
     * rules:
     * <ul>
     * <li> if a name contains no seperators (comma or semicolon {@code [,;]}),
     *      the trailing consecutive part is the last name, all prior parts
     *      are first name parts, e.g. {@code "Tim Anton Schulz-Müller"}, splits
     *      into <i>first name:</i> {@code "Tim Anton"} and <i>last name</i>
     *      {@code "Schulz-Müller"}.
     * <li> names with seperators (comma or semicolon {@code [,;]}) split into
     *      a last name part before the seperator and a first name part after
     *      the seperator, e.g. {@code "Schulz-Müller, Tim Anton"} splits into
     *      <i>first name:</i> {@code "Tim Anton"} and <i>last name</i>
     *      {@code "Schulz-Müller"}.
     * <li> leading and trailing white spaces {@code [\s]}, commata {@code [,;]}
     *      and quotes {@code ["']} must be trimmed from names, e.g.
     *      {@code "  'Schulz-Müller, Tim Anton'    "}.
     * <li> interim white spaces between name parts must be trimmed, e.g.
     *      {@code "Schulz-Müller, <white-spaces> Tim <white-spaces> Anton <white-spaces> "}.
     * </ul>
     * <pre>
     * Examples:
     * +------------------------------------+-----------------------+-----------------------+
     * |Single-String name                  |first name parts       |last name parts        |
     * +------------------------------------+-----------------------+-----------------------+
     * |"Eric Meyer"                        |"Eric"                 |"Meyer"                |
     * |"Meyer, Anne"                       |"Anne"                 |"Meyer"                |
     * |"Meyer; Anne"                       |"Anne"                 |"Meyer"                |
     * |"Tim Schulz‐Mueller"                |"Tim"                  |"Schulz‐Mueller"       |
     * |"Nadine Ulla Blumenfeld"            |"Nadine Ulla"          |"Blumenfeld"           |
     * |"Nadine‐Ulla Blumenfeld"            |"Nadine‐Ulla"          |"Blumenfeld"           |
     * |"Khaled Saad Mohamed Abdelalim"     |"Khaled Saad Mohamed"  |"Abdelalim"            |
     * +------------------------------------+-----------------------+-----------------------+
     * 
     * Trim leading, trailing and interim white spaces and quotes:
     * +------------------------------------+-----------------------+-----------------------+
     * |" 'Eric Meyer'  "                   |"Eric"                 |"Meyer"                |
     * |"Nadine     Ulla     Blumenfeld"    |"Nadine Ulla"          |"Blumenfeld"           |
     * +------------------------------------+-----------------------+-----------------------+
     * </pre>
     * @param name single-String name to split into first- and last name parts
     * @throws IllegalArgumentException if name argument is null or empty
     */
    public void splitName(String name) {
        name = trim(name);
    
        if (name.contains(",") || name.contains(";")) {
            String[] parts = name.split("[,;]");
            this.lastName = trim(parts[0]);
            this.firstName = trim(parts[1]);
            return;
        }

        
        String[] parts = name.split("\\s+");

        StringBuilder first = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            first.append(parts[i]).append(" ");
        }
        this.firstName = trim(first.toString());
        this.lastName = parts[parts.length - 1];
    }

    /**
     * Trim leading and trailing white spaces {@code [\s]}, commata {@code [,;]}
     * and quotes {@code ["']} from a String (used for names and contacts).
     * @param s String to trim
     * @return trimmed String
     */
    private String trim(String s) {
        s = s.replaceAll("^[\\s\"',;]*", "");   // trim leading white spaces[\s], commata[,;] and quotes['"]
        s = s.replaceAll( "[\\s\"',;]*$", "");  // trim trailing accordingly
        return s;
    }
}